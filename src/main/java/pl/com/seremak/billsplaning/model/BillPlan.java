package pl.com.seremak.billsplaning.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
public class BillPlan {

    private String username;

    @NotBlank(message = "Name of Category cannot be blank")
    private String categoryName;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount cannot be negative")
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal amount;
}
