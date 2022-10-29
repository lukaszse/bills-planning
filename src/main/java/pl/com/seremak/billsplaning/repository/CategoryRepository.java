package pl.com.seremak.billsplaning.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.com.seremak.billsplaning.model.Category;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CategoryRepository extends ReactiveCrudRepository<Category, String> {

    Flux<Category> getCategoriesByUsername(final String username);
    Flux<Category> getCategoriesByUsernameAndName(final String username, final String name);
    Mono<Category> deleteCategoryByUsernameAndName(final String username, final String name);
}
