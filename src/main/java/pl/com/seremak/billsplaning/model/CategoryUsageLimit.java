package pl.com.seremak.billsplaning.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@Setter
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
