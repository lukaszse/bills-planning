package pl.com.seremak.billsplaning.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pl.com.seremak.billsplaning.dto.TransactionDto;
import pl.com.seremak.billsplaning.model.Category;
import pl.com.seremak.billsplaning.model.CategoryUsageLimit;
import pl.com.seremak.billsplaning.repository.CategoryUsageLimitRepository;
import pl.com.seremak.billsplaning.repository.CategoryUsageLimitSearchRepository;
import pl.com.seremak.billsplaning.utils.ListUtils;
import pl.com.seremak.billsplaning.utils.VersionedEntityUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static pl.com.seremak.billsplaning.converter.CategoryUsageLimitConverter.categoryUsageLimitOf;
import static pl.com.seremak.billsplaning.utils.DateUtils.toYearMonthString;
import static pl.com.seremak.billsplaning.utils.TransactionBalanceUtils.updateBalance;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryUsageLimitService {

    private final CategoryUsageLimitRepository categoryUsageLimitRepository;
    private final CategoryUsageLimitSearchRepository categoryUsageLimitSearchRepository;
    private final CategoryService categoryService;


    public Mono<List<CategoryUsageLimit>> findAllCategoryUsageLimits(final String username, final String yearMonth) {
        final String yearMonthToSearch = defaultIfNull(yearMonth, toYearMonthString(Instant.now()).orElseThrow());
        return categoryUsageLimitRepository.findByUsernameAndYearMonth(username, yearMonthToSearch)
                .collectList()
                .map(CategoryUsageLimitService::addTotalUsageLimit);
    }

    public Mono<CategoryUsageLimit> updateCategoryUsageLimit(final TransactionDto transactionDto) {
        return categoryUsageLimitRepository.findByUsernameAndCategoryNameAndYearMonth(transactionDto.getUsername(),
                        transactionDto.getCategoryName(), YearMonth.now().toString())
                .switchIfEmpty(createNewCategoryUsageLimit(transactionDto))
                .map(categoryUsageLimit -> updateCategoryUsageLimit(categoryUsageLimit, transactionDto))
                .flatMap(categoryUsageLimitSearchRepository::updateCategoryUsageLimit)
                .doOnSuccess(updatedCategoryUsageLimit ->
                        log.info("Usage limit for category={} updated.", updatedCategoryUsageLimit.getCategoryName()));
    }

    private Mono<CategoryUsageLimit> createNewCategoryUsageLimit(final TransactionDto transactionDto) {
        return getCategoryLimit(transactionDto.getUsername(), transactionDto.getCategoryName())
                .map(categoryLimit -> categoryUsageLimitOf(transactionDto, categoryLimit))
                .map(VersionedEntityUtils::setMetadata)
                .flatMap(categoryUsageLimitRepository::save);
    }

    private Mono<BigDecimal> getCategoryLimit(final String username, final String categoryName) {
        return categoryService.findCategory(username, categoryName)
                .map(Category::getLimit);
    }

    private static CategoryUsageLimit updateCategoryUsageLimit(final CategoryUsageLimit categoryUsageLimit,
                                                               final TransactionDto transactionDto) {
        final BigDecimal updatedLimitUsage = updateBalance(categoryUsageLimit.getUsage(), transactionDto);
        categoryUsageLimit.setUsage(updatedLimitUsage);
        return categoryUsageLimit;
    }

    private static List<CategoryUsageLimit> addTotalUsageLimit(final List<CategoryUsageLimit> categoryUsageLimits) {
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
                .map(total -> ListUtils.addAndReturn(categoryUsageLimits, total))
                .orElse(categoryUsageLimits);
    }
}
