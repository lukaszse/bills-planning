package pl.com.seremak.billsplaning.utils;

import pl.com.seremak.billsplaning.dto.TransactionDto;

import java.math.BigDecimal;

public class TransactionBalanceUtils {

    public static BigDecimal updateBalance(final BigDecimal currentBalance, final TransactionDto transactionDto) {
        return switch (transactionDto.getType()) {
            case CREATION -> currentBalance.add(transactionDto.getAmount().abs());
            case DELETION -> currentBalance.subtract(transactionDto.getAmount().abs());
            case UPDATE -> currentBalance.add(transactionDto.getAmount());
        };
    }
}
