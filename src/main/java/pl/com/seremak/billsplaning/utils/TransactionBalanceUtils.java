package pl.com.seremak.billsplaning.utils;

import pl.com.seremak.billsplaning.dto.TransactionEventDto;

import java.math.BigDecimal;

public class TransactionBalanceUtils {

    public static BigDecimal updateBalance(final BigDecimal currentBalance, final TransactionEventDto transactionEventDto) {
        return calculateBalanceAfterTransaction(currentBalance, transactionEventDto.getAmount(), transactionEventDto.getType());
    }

    public static BigDecimal updateCategoryUsage(final BigDecimal currentBalance, final TransactionEventDto transactionEventDto) {
        return calculateBalanceAfterTransaction(currentBalance, transactionEventDto.getAmount().negate(), transactionEventDto.getType());
    }

    private static BigDecimal calculateBalanceAfterTransaction(final BigDecimal currentBalance,
                                                               final BigDecimal transactionAmount,
                                                               final TransactionEventDto.ActionType type) {
        return switch (type) {
            case CREATION, UPDATE -> currentBalance.add(transactionAmount);
            case DELETION -> currentBalance.subtract(transactionAmount);
        };
    }
}
