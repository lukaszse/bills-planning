package pl.com.seremak.billsplaning.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.com.seremak.billsplaning.model.CategoryUsageLimit;
import reactor.core.publisher.Mono;

import java.time.YearMonth;

import static pl.com.seremak.billsplaning.utils.MongoQueryHelper.preparePartialUpdateQuery;

@Repository
@RequiredArgsConstructor
public class CategoryUsageLimitSearchRepository {

    private final ReactiveMongoTemplate mongoTemplate;


    public Mono<CategoryUsageLimit> updateCategoryUsageLimit(final CategoryUsageLimit categoryUsageLimit) {
        return mongoTemplate.findAndModify(
                prepareFindBillQuery(categoryUsageLimit.getUsername(), categoryUsageLimit.getCategoryName(),
                        categoryUsageLimit.getYearMonth()),
                preparePartialUpdateQuery(categoryUsageLimit, CategoryUsageLimit.class),
                new FindAndModifyOptions().returnNew(true),
                CategoryUsageLimit.class);
    }

    private static Query prepareFindBillQuery(final String username, final String categoryName, final YearMonth yearMonth) {
        return new Query()
                .addCriteria(Criteria.where("username").is(username))
                .addCriteria(Criteria.where("name").is(categoryName))
                .addCriteria(Criteria.where("yearMonth").is(yearMonth));
    }
}
