package pl.com.seremak.simplebills.planning.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.com.seremak.simplebills.commons.model.Category;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CategoryRepository extends ReactiveCrudRepository<Category, String> {

    Flux<Category> findCategoriesByUsername(final String username);

    Flux<Category> findCategoriesByUsernameAndName(final String username, final String name);

    Flux<Category> findCategoriesByUsernameAndType(final String username, final Category.Type type);

    Mono<Category> deleteCategoryByUsernameAndName(final String username, final String name);
}
