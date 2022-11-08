package pl.com.seremak.billsplaning.converter;

import pl.com.seremak.billsplaning.dto.TransactionDto;
import pl.com.seremak.billsplaning.model.CategoryUsageLimit;
import pl.com.seremak.billsplaning.utils.DateUtils;

import java.math.BigDecimal;

public class CategoryUsageLimitConverter {

    public static CategoryUsageLimit categoryUsageLimitOf(final TransactionDto transactionDto, final BigDecimal categoryLimit) {
        return CategoryUsageLimit.builder()
                .username(transactionDto.getUsername())
                .categoryName(transactionDto.getCategoryName())
                .limit(categoryLimit)
                .usage(BigDecimal.ZERO)
                .yearMonth(DateUtils.toYearMonth(transactionDto.getDate()).orElseThrow())
                .build();
    }
}
