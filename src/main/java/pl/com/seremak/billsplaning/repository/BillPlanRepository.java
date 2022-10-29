package pl.com.seremak.billsplaning.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import pl.com.seremak.billsplaning.model.BillPlan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface BillPlanRepository extends ReactiveCrudRepository<BillPlan, String> {

    Flux<BillPlan> getBillPlanByUsername(final String username);
    Mono<BillPlan> deleteBillPlanByUsernameAndCategoryName(final String username, final String categoryName);
}
