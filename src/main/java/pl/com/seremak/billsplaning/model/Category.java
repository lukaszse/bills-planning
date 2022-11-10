package pl.com.seremak.billsplaning.model;

import com.mongodb.lang.Nullable;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@Document
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category extends VersionedEntity {

    public enum Type {
        STANDARD, CUSTOM, UNDEFINED
    }

    public enum TransactionType {
        INCOME, EXPENSE
    }

    @NotNull(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Name of Category cannot be blank")
    private String name;

    @Nullable
    private Type type;

    @NotNull
    private TransactionType transactionType;

    @Nullable
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal limit;

    private BigDecimal usageOfLimit;
}
