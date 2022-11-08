package pl.com.seremak.billsplaning.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.seremak.billsplaning.dto.TransactionDto;
import pl.com.seremak.billsplaning.model.Category;
import pl.com.seremak.billsplaning.model.CategoryUsageLimit;
import pl.com.seremak.billsplaning.repository.CategoryUsageLimitRepository;
import pl.com.seremak.billsplaning.repository.CategoryUsageLimitSearchRepository;
import pl.com.seremak.billsplaning.utils.VersionedEntityUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.YearMonth;

import static pl.com.seremak.billsplaning.converter.CategoryUsageLimitConverter.categoryUsageLimitOf;
import static pl.com.seremak.billsplaning.utils.TransactionBalanceUtils.updateBalance;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryUsageLimitService {

    private final CategoryUsageLimitRepository categoryUsageLimitRepository;
    private final CategoryUsageLimitSearchRepository categoryUsageLimitSearchRepository;
    private final CategoryService categoryService;

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

    private static CategoryUsageLimit updateCategoryUsageLimit(final CategoryUsageLimit categoryUsageLimit,
                                                               final TransactionDto transactionDto) {
        final BigDecimal updatedLimitUsage = updateBalance(categoryUsageLimit.getUsage(), transactionDto);
        categoryUsageLimit.setUsage(updatedLimitUsage);
        return categoryUsageLimit;
    }

    private Mono<BigDecimal> getCategoryLimit(final String username, final String categoryName) {
        return categoryService.findCategory(username, categoryName)
                .map(Category::getLimit);
    }
}
