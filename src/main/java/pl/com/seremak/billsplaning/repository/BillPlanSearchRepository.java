/*
package pl.com.seremak.billsplaning.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import pl.com.seremak.billsplaning.model.BillPlan;
import reactor.core.publisher.Mono;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BillPlanSearchRepository {

    private final ReactiveMongoTemplate reactiveMongoTemplate;
    private final ObjectMapper objectMapper;

    private Mono<BillPlan> updateBillPlan(final String username, final String categoryName) {

    }

    private static Query prepareFindBillQuery(final String username, final String categoryName) {
        return new Query()
                .addCriteria(Criteria.where(USER_FIELD).is(user))
                .addCriteria(Criteria.where(BILL_NUMBER_FIELD).is(billNumber));
    }

    @SuppressWarnings({"unchecked"})
    private Update preparePartialUpdateQuery(final BillPlan billPlan) {
        final Update update = new Update();
        final Map<String, Object> fieldsMap = objectMapper.convertValue(billPlan, Map.class);
        fieldsMap.entrySet().stream()
                .filter(field -> field.getValue() != null)
                .forEach(field -> update.set(field.getKey(), field.getValue()));
        return updateMetadata(update);
    }
}
*/
