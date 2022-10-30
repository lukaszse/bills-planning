package pl.com.seremak.billsplaning.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;

@Getter
@Setter
@Document
@AllArgsConstructor(staticName = "of")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BillPlan extends VersionedEntity {

    @NotNull(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Name of Category cannot be blank")
    private String categoryName;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount cannot be negative")
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal amount;
}
