package pl.com.seremak.billsplaning.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import pl.com.seremak.billsplaning.model.BillPlan;
import reactor.core.publisher.Mono;

import java.util.Map;

import static pl.com.seremak.billsplaning.utils.VersionedEntityUtils.updateMetadata;

@Repository
@RequiredArgsConstructor
public class BillPlanSearchRepository {

    private final ReactiveMongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public Mono<BillPlan> updateBillPlan(final BillPlan billPlan) {
        return mongoTemplate.findAndModify(
                prepareFindBillQuery(billPlan.getUsername(), billPlan.getCategoryName()),
                preparePartialUpdateQuery(billPlan),
                new FindAndModifyOptions().returnNew(true),
                BillPlan.class);
    }

    private static Query prepareFindBillQuery(final String username, final String categoryName) {
        return new Query()
                .addCriteria(Criteria.where("username").is(username))
                .addCriteria(Criteria.where("categoryName").is(categoryName));
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
