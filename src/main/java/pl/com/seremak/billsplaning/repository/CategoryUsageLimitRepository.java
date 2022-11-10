package pl.com.seremak.billsplaning.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.com.seremak.billsplaning.model.CategoryUsageLimit;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface CategoryUsageLimitRepository extends ReactiveCrudRepository<CategoryUsageLimit, String> {

    Flux<CategoryUsageLimit> findByUsernameAndYearMonth(final String username, final String yearMonth);

    Mono<CategoryUsageLimit> findByUsernameAndCategoryNameAndYearMonth(final String username,
                                                                       final String categoryName,
                                                                       final String yearMonth);

    Mono<CategoryUsageLimit> deleteByUsernameAndCategoryName(final String username, final String categoryName);
}
