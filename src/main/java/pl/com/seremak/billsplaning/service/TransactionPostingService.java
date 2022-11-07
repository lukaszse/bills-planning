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

    private static Balance updateBalance(final Balance existingBalance, final TransactionDto transactionDto) {
        final BigDecimal existingBalanceAmount = existingBalance.getBalance();
        switch (transactionDto.getType()) {
            case CREATION -> existingBalance.setBalance(existingBalanceAmount.add(transactionDto.getAmount().abs()));
            case DELETION ->
                    existingBalance.setBalance(existingBalanceAmount.subtract(transactionDto.getAmount().abs()));
            case UPDATE -> existingBalance.setBalance(existingBalanceAmount.add(transactionDto.getAmount()));
        }
        return existingBalance;
    }
}
