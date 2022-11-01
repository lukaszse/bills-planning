package pl.com.seremak.billsplaning.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.com.seremak.billsplaning.model.Expense;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ExpenseRepository extends ReactiveCrudRepository<Expense, String> {

    Flux<Expense> findExpenseByUsername(final String username);

    Flux<Expense> findExpenseByUsernameAndCategoryName(final String username, final String categoryName);

    Mono<Expense> deleteExpenseByUsernameAndCategoryName(final String username, final String categoryName);
}
