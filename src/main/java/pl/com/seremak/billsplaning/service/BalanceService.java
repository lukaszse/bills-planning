package pl.com.seremak.billsplaning.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.seremak.billsplaning.exceptions.NotFoundException;
import pl.com.seremak.billsplaning.model.Balance;
import pl.com.seremak.billsplaning.repository.BalanceRepository;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class BalanceService {

    private final BalanceRepository balanceRepository;

    public Mono<Balance> findBalance(final String username) {
        return balanceRepository.findBalanceByUsername(username)
                .switchIfEmpty(Mono.error(new NotFoundException()));
    }
}
