package pl.com.seremak.billsplaning.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pl.com.seremak.billsplaning.repository.CategoryRepository;
import pl.com.seremak.billsplaning.repository.CategoryUsageLimitRepository;
import pl.com.seremak.billsplaning.repository.CategoryUsageLimitSearchRepository;
import pl.com.seremak.simplebills.commons.dto.queue.TransactionEventDto;
import pl.com.seremak.simplebills.commons.model.Category;
import pl.com.seremak.simplebills.commons.model.CategoryUsageLimit;
import pl.com.seremak.simplebills.commons.utils.CollectionUtils;
import pl.com.seremak.simplebills.commons.utils.DateUtils;
import pl.com.seremak.simplebills.commons.utils.VersionedEntityUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static pl.com.seremak.simplebills.commons.converter.CategoryUsageLimitConverter.categoryUsageLimitOf;
import static pl.com.seremak.simplebills.commons.model.Category.TransactionType.EXPENSE;
import static pl.com.seremak.simplebills.commons.utils.DateUtils.toYearMonthString;
import static pl.com.seremak.simplebills.commons.utils.TransactionBalanceUtils.updateCategoryUsage;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryUsageLimitService {

    private final CategoryUsageLimitRepository categoryUsageLimitRepository;
    private final CategoryUsageLimitSearchRepository categoryUsageLimitSearchRepository;
    private final CategoryRepository categoryRepository;


    public Mono<List<CategoryUsageLimit>> findAllCategoryUsageLimits(final String username, final String yearMonth, final boolean total) {
        final String yearMonthToSearch = defaultIfNull(yearMonth, toYearMonthString(Instant.now()).orElseThrow());
        final Mono<List<CategoryUsageLimit>> categoriesUsageLimitsMono =
                categoryUsageLimitRepository.findByUsernameAndYearMonth(username, yearMonthToSearch)
                        .filter(categoryUsageLimit -> nonNull(categoryUsageLimit.getUsage())
                                && !categoryUsageLimit.getUsage().equals(ZERO))
                        .collectList();
        return total ?
                categoriesUsageLimitsMono.map(CategoryUsageLimitService::extractTotalUsageLimit) :
                categoriesUsageLimitsMono;
    }

    public Mono<CategoryUsageLimit> updateCategoryUsageLimitAfterNewTransaction(final TransactionEventDto transactionEventDto) {
        final String yearMonth = getTransactionYearMonthOrSetCurrentIfNotExists(transactionEventDto);
        return categoryUsageLimitRepository.findByUsernameAndCategoryNameAndYearMonth(transactionEventDto.getUsername(),
                        transactionEventDto.getCategoryName(), yearMonth)
                .switchIfEmpty(createNewCategoryUsageLimit(transactionEventDto))
                .flatMap(categoryUsageLimit -> updateCategoryUsageLimitAfterNewTransaction(categoryUsageLimit, transactionEventDto))
                .doOnNext(updatedCategoryUsageLimit ->
                        log.info("Usage limit for category={} updated.", updatedCategoryUsageLimit.getCategoryName()));
    }

    public Mono<CategoryUsageLimit> updateCategoryUsageLimit(final String username, final String categoryName, final BigDecimal newLimit) {
        return categoryUsageLimitRepository.findByUsernameAndCategoryNameAndYearMonth(username,
                        categoryName, YearMonth.now().toString())
                .switchIfEmpty(createNewCategoryUsageLimit(username, categoryName))
                .flatMap(categoryUsageLimit -> updateCategoryUsageLimit(categoryUsageLimit, newLimit))
                .doOnSuccess(updatedCategoryUsageLimit ->
                        log.info("Usage limit for category={} updated.", updatedCategoryUsageLimit.getCategoryName()));
    }

    private Mono<CategoryUsageLimit> createNewCategoryUsageLimit(final TransactionEventDto transactionEventDto) {
        return getLimitForNewCategoryUsageLimit(transactionEventDto.getUsername(), transactionEventDto.getCategoryName())
                .flatMap(category -> createCategoryUsageLimitForExpense(category, transactionEventDto.getDate()));
    }

    public Mono<CategoryUsageLimit> createNewCategoryUsageLimit(final String username, final String categoryName) {
        return getLimitForNewCategoryUsageLimit(username, categoryName)
                .flatMap(category -> createCategoryUsageLimitForExpense(category, Instant.now()));
    }

    public Mono<CategoryUsageLimit> deleteCategoryUsageLimit(final String username, final String categoryName) {
        return categoryUsageLimitRepository.deleteByUsernameAndCategoryName(username, categoryName)
                .doOnNext(deletedCategoryUsageLimit -> log.info("CategoryUsageLimit for category={} deleted", deletedCategoryUsageLimit.getCategoryName()));
    }

    private Mono<CategoryUsageLimit> createCategoryUsageLimitForExpense(final Category category, final Instant transactionYearMonth) {
        if (!EXPENSE.equals(category.getTransactionType())) {
            log.info("New CategoryUsageLimit will not be created for transactionType={}", category.getTransactionType());
            return Mono.empty();
        }
        if (Category.Type.UNDEFINED.equals(category.getType())) {
            log.info("New CategoryUsageLimit will not be created for STANDARD category with name={}", category.getName());
            return Mono.empty();
        }
        return Mono.just(categoryUsageLimitOf(category, transactionYearMonth))
                .map(VersionedEntityUtils::setMetadata)
                .flatMap(categoryUsageLimitRepository::save)
                .doOnNext(createdCategoryUsageLimit -> log.info("CategoryUsageLimit for categoryName={} created", createdCategoryUsageLimit.getCategoryName()));
    }

    private Mono<Category> getLimitForNewCategoryUsageLimit(final String username, final String categoryName) {
        return categoryRepository.findCategoriesByUsernameAndName(username, categoryName)
                .collectList()
                .map(CollectionUtils::getSoleElementOrThrowException);
    }

    private Mono<CategoryUsageLimit> updateCategoryUsageLimitAfterNewTransaction(final CategoryUsageLimit categoryUsageLimit,
                                                                                 final TransactionEventDto transactionEventDto) {
        final BigDecimal updatedLimitUsage = updateCategoryUsage(categoryUsageLimit.getUsage(), transactionEventDto);
        categoryUsageLimit.setUsage(updatedLimitUsage);
        return categoryUsageLimitSearchRepository.updateCategoryUsageLimit(categoryUsageLimit);
    }

    private Mono<CategoryUsageLimit> updateCategoryUsageLimit(final CategoryUsageLimit categoryUsageLimit, final BigDecimal newLimit) {
        categoryUsageLimit.setLimit(newLimit);
        return categoryUsageLimitSearchRepository.updateCategoryUsageLimit(categoryUsageLimit);
    }

    private static List<CategoryUsageLimit> extractTotalUsageLimit(final List<CategoryUsageLimit> categoryUsageLimits) {
        final Optional<Pair<BigDecimal, BigDecimal>> totalUsageAndLimitOpt = categoryUsageLimits.stream()
                .map(categoryUsageLimit -> Pair.of(defaultIfNull(categoryUsageLimit.getUsage(), ZERO), defaultIfNull(categoryUsageLimit.getLimit(), ZERO)))
                .reduce((usageAndLimit1, usageAndLimit2) ->
                        Pair.of(usageAndLimit1.getFirst().add(usageAndLimit2.getFirst()), usageAndLimit1.getSecond().add(usageAndLimit2.getSecond())));
        final Optional<CategoryUsageLimit> totalOpt = categoryUsageLimits.stream().findFirst()
                .flatMap(category -> totalUsageAndLimitOpt
                        .map(totalUsageAndLimit -> CategoryUsageLimit.builder()
                                .username(category.getUsername())
                                .categoryName("total")
                                .usage(totalUsageAndLimit.getFirst())
                                .limit(totalUsageAndLimit.getSecond())
                                .yearMonth(category.getYearMonth())
                                .build()));
        return totalOpt
                .map(List::of)
                .orElse(List.of());
    }

    private static String getTransactionYearMonthOrSetCurrentIfNotExists(final TransactionEventDto transactionEventDto) {
        return Optional.ofNullable(transactionEventDto)
                .map(TransactionEventDto::getDate)
                .flatMap(DateUtils::toYearMonthString)
                .orElse(Instant.now().toString());
    }
}
