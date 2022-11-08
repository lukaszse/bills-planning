package pl.com.seremak.billsplaning.converter;

import pl.com.seremak.billsplaning.dto.TransactionDto;
import pl.com.seremak.billsplaning.model.CategoryUsageLimit;

import java.math.BigDecimal;

import static pl.com.seremak.billsplaning.utils.DateUtils.toYearMonthString;

public class CategoryUsageLimitConverter {

    public static CategoryUsageLimit categoryUsageLimitOf(final TransactionDto transactionDto, final BigDecimal categoryLimit) {
        return CategoryUsageLimit.builder()
                .username(transactionDto.getUsername())
                .categoryName(transactionDto.getCategoryName())
                .limit(categoryLimit)
                .usage(BigDecimal.ZERO)
                .yearMonth(toYearMonthString(transactionDto.getDate()).orElseThrow())
                .build();
    }
}
