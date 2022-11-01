package pl.com.seremak.billsplaning.dto;

import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.com.seremak.billsplaning.model.Category;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class CategoryDto {

    @NotBlank(message = "Name of Category cannot be blank")
    private String name;

    @Nullable
    private BigDecimal limit;

    public static CategoryDto of(final Category category) {
        return CategoryDto.of(category.getName(), category.getLimit());
    }
}
