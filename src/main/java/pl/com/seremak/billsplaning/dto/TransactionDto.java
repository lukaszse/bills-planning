package pl.com.seremak.billsplaning.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

@Data
public class TransactionDto implements Serializable {

    @NotBlank(message = "Username cannot be blank")
    private String username;

    @NotBlank(message = "Name of Category cannot be blank")
    private String categoryName;

    @NotBlank(message = "Amount cannot be blank")
    private BigDecimal amount;

    private ActionType type;

    private Instant date;


    public enum ActionType {
        CREATION, DELETION, UPDATE
    }
}
