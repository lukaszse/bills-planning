package pl.com.seremak.simplebills.planning.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.com.seremak.simplebills.commons.model.CategoryUsageLimit;
import reactor.core.publisher.Mono;

import static pl.com.seremak.simplebills.commons.utils.MongoQueryHelper.preparePartialUpdateQuery;

@Repository
@RequiredArgsConstructor
public class CategoryUsageLimitSearchRepository {

    private final ReactiveMongoTemplate mongoTemplate;


    public Mono<CategoryUsageLimit> updateCategoryUsageLimit(final CategoryUsageLimit categoryUsageLimit) {
        return mongoTemplate.findAndModify(
                prepareFindBillQuery(categoryUsageLimit.getUsername(), categoryUsageLimit.getCategoryName(), categoryUsageLimit.getYearMonth()),
                preparePartialUpdateQuery(categoryUsageLimit, CategoryUsageLimit.class),
                new FindAndModifyOptions().returnNew(true),
                CategoryUsageLimit.class);
    }

    private static Query prepareFindBillQuery(final String username, final String categoryName, final String yearMonth) {
        return new Query()
                .addCriteria(Criteria.where("username").is(username))
                .addCriteria(Criteria.where("categoryName").is(categoryName))
                .addCriteria(Criteria.where("yearMonth").is(yearMonth));
    }
}
