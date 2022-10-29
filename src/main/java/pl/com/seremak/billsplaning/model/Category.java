package pl.com.seremak.billsplaning.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;

@Data
@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Category {

    private String username;

    @NotBlank(message = "Name of Category cannot be blank")
    private String name;
}
