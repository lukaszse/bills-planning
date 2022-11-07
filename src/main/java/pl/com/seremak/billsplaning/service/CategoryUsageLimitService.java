package pl.com.seremak.billsplaning.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.seremak.billsplaning.dto.TransactionDto;
import pl.com.seremak.billsplaning.model.Category;
import pl.com.seremak.billsplaning.model.CategoryUsageLimit;
import pl.com.seremak.billsplaning.repository.CategoryUsageLimitRepository;
import pl.com.seremak.billsplaning.repository.CategoryUsageLimitSearchRepository;
import pl.com.seremak.billsplaning.utils.BalanceUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.YearMonth;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryUsageLimitService {

    private final CategoryUsageLimitRepository categoryUsageLimitRepository;
    private final CategoryUsageLimitSearchRepository categoryUsageLimitSearchRepository;
    private final CategoryService categoryService;

    public Mono<CategoryUsageLimit> updateCategoryUsageLimit(final TransactionDto transactionDto) {
        return categoryUsageLimitRepository.findByUsernameAndCategoryNameAndYearMonth(transactionDto.getUsername(),
                        transactionDto.getCategoryName(), YearMonth.now())
                .switchIfEmpty(getCategoryLimit(transactionDto.getUsername(), transactionDto.getCategoryName())
                        .map(categoryLimit -> CategoryUsageLimitOf(transactionDto, categoryLimit)))
                .map(categoryUsageLimit -> updateCategoryUsageLimit(categoryUsageLimit, transactionDto))
                .flatMap(categoryUsageLimitSearchRepository::updateCategoryUsageLimit)
                .doOnSuccess(updatedCategoryUsageLimit ->
                        log.info("Usage limit for category={} updated.", updatedCategoryUsageLimit.getCategoryName()));
    }

    private static CategoryUsageLimit CategoryUsageLimitOf(final TransactionDto transactionDto, final BigDecimal categoryLimit) {
        return CategoryUsageLimit.builder()
                .username(transactionDto.getUsername())
                .categoryName(transactionDto.getCategoryName())
                .limit(categoryLimit)
                .usage(BigDecimal.ZERO)
                .yearMonth(YearMonth.now())
                .build();
    }

    private static CategoryUsageLimit updateCategoryUsageLimit(final CategoryUsageLimit categoryUsageLimit,
                                                               final TransactionDto transactionDto) {
        final BigDecimal updatedLimitUsage = BalanceUtils.updateBalance(categoryUsageLimit.getUsage(), transactionDto);
        categoryUsageLimit.setUsage(updatedLimitUsage);
        return categoryUsageLimit;
    }

    private Mono<BigDecimal> getCategoryLimit(final String username, final String categoryName) {
        return categoryService.findCategory(username, categoryName)
                .map(Category::getLimit);
    }
}
