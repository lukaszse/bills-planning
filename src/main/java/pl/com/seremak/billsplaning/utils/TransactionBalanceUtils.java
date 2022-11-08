package pl.com.seremak.billsplaning.utils;

import pl.com.seremak.billsplaning.dto.TransactionEventDto;

import java.math.BigDecimal;

public class TransactionBalanceUtils {

    public static BigDecimal updateBalance(final BigDecimal currentBalance, final TransactionEventDto transactionEventDto) {
        return switch (transactionEventDto.getType()) {
            case CREATION -> currentBalance.add(transactionEventDto.getAmount().abs());
            case DELETION -> currentBalance.subtract(transactionEventDto.getAmount().abs());
            case UPDATE -> currentBalance.add(transactionEventDto.getAmount());
        };
    }
}
