package pl.com.seremak.billsplaning.model;


import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@Setter
@Builder
@Document
@NoArgsConstructor
@AllArgsConstructor
public class CategoryUsageLimit extends VersionedEntity {

    private String username;
    private String categoryName;
    private BigDecimal limit;
    private BigDecimal usage;
    private YearMonth yearMonth;
}
