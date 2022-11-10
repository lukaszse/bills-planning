package pl.com.seremak.billsplaning.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pl.com.seremak.billsplaning.dto.TransactionEventDto;
import pl.com.seremak.billsplaning.model.Category;
import pl.com.seremak.billsplaning.model.CategoryUsageLimit;
import pl.com.seremak.billsplaning.repository.CategoryRepository;
import pl.com.seremak.billsplaning.repository.CategoryUsageLimitRepository;
import pl.com.seremak.billsplaning.repository.CategoryUsageLimitSearchRepository;
import pl.com.seremak.billsplaning.utils.CollectionUtils;
import pl.com.seremak.billsplaning.utils.VersionedEntityUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static java.util.Objects.isNull;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static pl.com.seremak.billsplaning.converter.CategoryUsageLimitConverter.categoryUsageLimitOf;
import static pl.com.seremak.billsplaning.model.Category.TransactionType.EXPENSE;
import static pl.com.seremak.billsplaning.utils.DateUtils.toYearMonthString;
import static pl.com.seremak.billsplaning.utils.TransactionBalanceUtils.updateBalance;

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
                        .collectList();
        return total ?
                categoriesUsageLimitsMono.map(CategoryUsageLimitService::extractTotalUsageLimit) :
                categoriesUsageLimitsMono;
    }

    public Mono<CategoryUsageLimit> updateCategoryUsageLimitAfterNewTransaction(final TransactionEventDto transactionEventDto) {
        return categoryUsageLimitRepository.findByUsernameAndCategoryNameAndYearMonth(transactionEventDto.getUsername(),
                        transactionEventDto.getCategoryName(), YearMonth.now().toString())
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
        if (isNull(category.getLimit())) {
            log.info("New CategoryUsageLimit will not be created for categoryName={} since category limit is not set", category.getName());
            return Mono.empty();
        }
        return Mono.just(categoryUsageLimitOf(category, transactionYearMonth))
                .map(VersionedEntityUtils::setMetadata)
                .flatMap(categoryUsageLimitRepository::save);
    }

    private Mono<Category> getLimitForNewCategoryUsageLimit(final String username, final String categoryName) {
        return categoryRepository.findCategoriesByUsernameAndName(username, categoryName)
                .collectList()
                .map(CollectionUtils::getSoleElementOrThrowException);
    }

    private Mono<CategoryUsageLimit> updateCategoryUsageLimitAfterNewTransaction(final CategoryUsageLimit categoryUsageLimit,
                                                                                 final TransactionEventDto transactionEventDto) {
        final BigDecimal updatedLimitUsage = updateBalance(categoryUsageLimit.getUsage(), transactionEventDto);
        categoryUsageLimit.setUsage(updatedLimitUsage);
        return categoryUsageLimitSearchRepository.updateCategoryUsageLimit(categoryUsageLimit);
    }

    private Mono<CategoryUsageLimit> updateCategoryUsageLimit(final CategoryUsageLimit categoryUsageLimit, final BigDecimal newLimit) {
        categoryUsageLimit.setLimit(newLimit);
        return categoryUsageLimitSearchRepository.updateCategoryUsageLimit(categoryUsageLimit);
    }

    private static List<CategoryUsageLimit> extractTotalUsageLimit(final List<CategoryUsageLimit> categoryUsageLimits) {
        final Optional<Pair<BigDecimal, BigDecimal>> totalUsageAndLimitOpt = categoryUsageLimits.stream()
                .map(categoryUsageLimit -> Pair.of(categoryUsageLimit.getUsage(), categoryUsageLimit.getLimit()))
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
}
