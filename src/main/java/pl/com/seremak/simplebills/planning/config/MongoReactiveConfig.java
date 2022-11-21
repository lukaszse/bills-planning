package pl.com.seremak.simplebills.planning.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

@Slf4j
@Configuration
public class MongoReactiveConfig {

    @Value("${spring.data.mongodb.database}")
    private String planningDatabase;

    @Value("${spring.data.mongodb.uri}")
    private String planningDatabaseUri;


    @Bean
    public ReactiveMongoTemplate reactiveMongoTemplate() {
        return new ReactiveMongoTemplate(mongoClient(), planningDatabase);
    }

    @Bean
    public MongoClient mongoClient() {
        log.info("Creating MongoDb client for URI: {}", planningDatabaseUri);
        return MongoClients.create(planningDatabaseUri);
    }
}