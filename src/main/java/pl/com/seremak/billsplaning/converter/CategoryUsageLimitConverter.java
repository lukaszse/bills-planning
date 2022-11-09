package pl.com.seremak.billsplaning.converter;

import pl.com.seremak.billsplaning.dto.TransactionEventDto;
import pl.com.seremak.billsplaning.model.CategoryUsageLimit;

import java.math.BigDecimal;
import java.time.Instant;

import static pl.com.seremak.billsplaning.utils.DateUtils.toYearMonthString;

public class CategoryUsageLimitConverter {

    public static CategoryUsageLimit categoryUsageLimitOf(final TransactionEventDto transactionEventDto, final BigDecimal categoryLimit) {
        return CategoryUsageLimit.builder()
                .username(transactionEventDto.getUsername())
                .categoryName(transactionEventDto.getCategoryName())
                .limit(categoryLimit)
                .usage(BigDecimal.ZERO)
                .yearMonth(toYearMonthString(transactionEventDto.getDate()).orElseThrow())
                .build();
    }

    public static CategoryUsageLimit categoryUsageLimitOf(final String username, final String categoryName, final BigDecimal categoryLimit) {
        return CategoryUsageLimit.builder()
                .username(username)
                .categoryName(categoryName)
                .limit(categoryLimit)
                .usage(BigDecimal.ZERO)
                .yearMonth(toYearMonthString(Instant.now()).orElseThrow())
                .build();
    }
}
