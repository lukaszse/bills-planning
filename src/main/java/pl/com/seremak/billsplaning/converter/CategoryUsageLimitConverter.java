package pl.com.seremak.billsplaning.converter;

import pl.com.seremak.billsplaning.model.Category;
import pl.com.seremak.billsplaning.model.CategoryUsageLimit;

import java.math.BigDecimal;
import java.time.Instant;

import static pl.com.seremak.billsplaning.utils.DateUtils.toYearMonthString;

public class CategoryUsageLimitConverter {

    public static CategoryUsageLimit categoryUsageLimitOf(final Category category, final Instant transactionYearMonth) {
        return CategoryUsageLimit.builder()
                .username(category.getUsername())
                .categoryName(category.getName())
                .limit(category.getLimit())
                .usage(BigDecimal.ZERO)
                .yearMonth(toYearMonthString(transactionYearMonth).orElseThrow())
                .build();
    }
}
