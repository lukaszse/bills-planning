package pl.com.seremak.billsplaning.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.com.seremak.simplebills.commons.model.Balance;
import reactor.core.publisher.Mono;

@Repository
public interface BalanceRepository extends ReactiveCrudRepository<Balance, String> {

    Mono<Balance> findBalanceByUsername(final String username);
}
