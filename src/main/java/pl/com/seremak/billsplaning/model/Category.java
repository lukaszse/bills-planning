package pl.com.seremak.billsplaning.model;

import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
public class Category extends VersionedEntity {

    public enum Type {
        STANDARD, CUSTOM
    }

    @NotNull(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Name of Category cannot be blank")
    private String name;

    @Nullable
    private Type type;

    @Nullable
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal limit;

    private BigDecimal usageOfLimit;


    public static Category of(final String username, final String name, final Type type) {
        return new Category(username, name, type, null, null);
    }

    public static Category of(final String username, final String name, final BigDecimal limit) {
        return new Category(username, name, Type.CUSTOM, limit, null);
    }

    public static Category of(final String username, final String name) {
        return new Category(username, name, Type.CUSTOM, null, null);
    }
}
