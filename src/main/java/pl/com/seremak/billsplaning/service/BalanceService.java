package pl.com.seremak.billsplaning.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.seremak.billsplaning.dto.TransactionDto;
import pl.com.seremak.billsplaning.exceptions.NotFoundException;
import pl.com.seremak.billsplaning.model.Balance;
import pl.com.seremak.billsplaning.repository.BalanceRepository;
import pl.com.seremak.billsplaning.utils.TransactionBalanceUtils;
import pl.com.seremak.billsplaning.utils.VersionedEntityUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;

    public Mono<Balance> findBalance(final String username) {
        return balanceRepository.findBalanceByUsername(username)
                .switchIfEmpty(Mono.error(new NotFoundException()));
    }

    public Mono<Balance> updateBalance(final TransactionDto transactionDto) {
        return balanceRepository.findBalanceByUsername(transactionDto.getUsername())
                .defaultIfEmpty(createNewBalanceForUser(transactionDto.getUsername()))
                .map(existingBalance -> updateBalance(existingBalance, transactionDto))
                .flatMap(balanceRepository::save)
                .doOnSuccess(updatedBalance -> log.info("Balance for username={} has been updated", transactionDto.getAmount()));
    }

    private static Balance createNewBalanceForUser(final String username) {
        return VersionedEntityUtils.setMetadata(new Balance(username, BigDecimal.ZERO));
    }

    private static Balance updateBalance(final Balance balance, final TransactionDto transactionDto) {
        final BigDecimal updatedBalanceAmount = TransactionBalanceUtils.updateBalance(balance.getBalance(), transactionDto);
        balance.setBalance(updatedBalanceAmount);
        return (Balance) VersionedEntityUtils.updateMetadata(balance);
    }
}
