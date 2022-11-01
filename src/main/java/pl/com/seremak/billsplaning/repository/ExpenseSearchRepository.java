package pl.com.seremak.billsplaning.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import pl.com.seremak.billsplaning.model.Expense;
import reactor.core.publisher.Mono;

import java.util.Map;

import static pl.com.seremak.billsplaning.utils.VersionedEntityUtils.updateMetadata;

@Repository
@RequiredArgsConstructor
public class ExpenseSearchRepository {

    private final ReactiveMongoTemplate mongoTemplate;
    private final ObjectMapper objectMapper;

    public Mono<Expense> updateBillPlan(final Expense expense) {
        return mongoTemplate.findAndModify(
                prepareFindBillQuery(expense.getUsername(), expense.getCategoryName()),
                preparePartialUpdateQuery(expense),
                new FindAndModifyOptions().returnNew(true),
                Expense.class);
    }

    private static Query prepareFindBillQuery(final String username, final String categoryName) {
        return new Query()
                .addCriteria(Criteria.where("username").is(username))
                .addCriteria(Criteria.where("categoryName").is(categoryName));
    }

    @SuppressWarnings({"unchecked"})
    private Update preparePartialUpdateQuery(final Expense expense) {
        final Update update = new Update();
        final Map<String, Object> fieldsMap = objectMapper.convertValue(expense, Map.class);
        fieldsMap.entrySet().stream()
                .filter(field -> field.getValue() != null)
                .forEach(field -> update.set(field.getKey(), field.getValue()));
        return updateMetadata(update);
    }
}
