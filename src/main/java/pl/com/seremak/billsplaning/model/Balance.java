package pl.com.seremak.billsplaning.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Getter
@Setter
@Document
@NoArgsConstructor
@AllArgsConstructor
public class Balance {

    @Id
    @NotBlank(message = "Balance user cannot be blank")
    private String username;

    @NotNull
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal balance;
}
