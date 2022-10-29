package pl.com.seremak.billsplaning.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mongodb.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category {

    @NotNull(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Name of Category cannot be blank")
    private String name;

    @Nullable
    private Type type;

    public enum Type {
        STANDARD, CUSTOM
    }
}
