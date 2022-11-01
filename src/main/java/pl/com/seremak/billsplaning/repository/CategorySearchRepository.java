package pl.com.seremak.billsplaning.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import pl.com.seremak.billsplaning.model.Category;
import pl.com.seremak.billsplaning.utils.MongoQueryHelper;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class CategorySearchRepository {

    private final ReactiveMongoTemplate mongoTemplate;
    private final MongoQueryHelper mongoQueryHelper;


    public Mono<Category> updateCategory(final Category category) {
        return mongoTemplate.findAndModify(
                prepareFindBillQuery(category.getUsername(), category.getName()),
                mongoQueryHelper.preparePartialUpdateQuery(category),
                new FindAndModifyOptions().returnNew(true),
                Category.class);
    }

    private static Query prepareFindBillQuery(final String username, final String categoryName) {
        return new Query()
                .addCriteria(Criteria.where("username").is(username))
                .addCriteria(Criteria.where("categoryName").is(categoryName));
    }
}
