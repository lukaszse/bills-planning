package pl.com.seremak.billsplaning.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class BillPlanDto {

    @NotBlank(message = "Name of Category cannot be blank")
    private String categoryName;

    @NotBlank(message = "Amount cannot be blank")
    private BigDecimal amount;
}
