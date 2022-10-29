package pl.com.seremak.billsplaning.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class Category {

    private String username;

    @NotBlank(message = "Name of Category cannot be blank")
    private String name;
}
