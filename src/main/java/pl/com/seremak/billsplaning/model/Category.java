package pl.com.seremak.billsplaning.model;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@AllArgsConstructor(staticName = "of")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category extends VersionedEntity {

    public enum Type {
        STANDARD, CUSTOM
    }

    @NotNull(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Name of Category cannot be blank")
    private String name;

    @Nullable
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal limit;

    @Nullable
    private Type type;

    public static Category of(final String username, final String name, final Type type) {
        return of(username, name, null, type);
    }

    public static Category of(final String username, final String name, final BigDecimal limit) {
        return of(username, name, limit, Type.CUSTOM);
    }

    public static Category of(final String username, final String name) {
        return of(username, name, null, Type.CUSTOM);
    }
}
