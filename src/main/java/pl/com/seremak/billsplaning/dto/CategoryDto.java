package pl.com.seremak.billsplaning.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryDto {

    @NotBlank(message = "Name of Category cannot be blank")
    private String name;
}
