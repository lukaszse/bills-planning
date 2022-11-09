package pl.com.seremak.billsplaning.dto;

import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class CategoryDto {

    @NotBlank(message = "Name of Category cannot be blank")
    private String name;

    @NotNull(message = "Transaction type cannot be null")
    private String transactionType;

    @Nullable
    private BigDecimal limit;
}
