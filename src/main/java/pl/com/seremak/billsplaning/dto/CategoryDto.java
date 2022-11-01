package pl.com.seremak.billsplaning.dto;

import com.mongodb.lang.Nullable;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class CategoryDto {

    @NotBlank(message = "Name of Category cannot be blank")
    private String name;

    @Nullable
    private BigDecimal limit;
}
