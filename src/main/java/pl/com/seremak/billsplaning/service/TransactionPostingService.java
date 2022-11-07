package pl.com.seremak.billsplaning.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.seremak.billsplaning.dto.TransactionDto;
import pl.com.seremak.billsplaning.model.Balance;
import pl.com.seremak.billsplaning.repository.BalanceRepository;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionPostingService {

    private final BalanceRepository balanceRepository;

    public Mono<Balance> postTransaction(final String username, final TransactionDto transactionDto) {
        return balanceRepository.findBalanceByUsername(username)
                .defaultIfEmpty(createNewBalanceForUser(username))
                .map(existingBalance -> updateBalance(existingBalance, transactionDto))
                .flatMap(balanceRepository::save)
                .doOnSuccess(updatedBalance -> log.info("Balance for username={} has been updated", username));
    }

    private static Balance createNewBalanceForUser(final String username) {
        return new Balance(username, BigDecimal.ZERO);
    }

    private static Balance updateBalance(final Balance balance, final TransactionDto transactionDto) {
        final BigDecimal balanceAmount = balance.getBalance();
        switch (transactionDto.getType()) {
            case CREATION -> balance.setBalance(balanceAmount.add(transactionDto.getAmount().abs()));
            case DELETION -> balance.setBalance(balanceAmount.subtract(transactionDto.getAmount().abs()));
            case UPDATE -> balance.setBalance(balanceAmount.add(transactionDto.getAmount()));
        }
        return balance;
    }
}
