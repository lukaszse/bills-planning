package pl.com.seremak.billsplaning.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@EnableRabbit
@Configuration
@RequiredArgsConstructor
public class RabbitMQConfig {

    public static final String USER_CREATION_QUEUE = "userCreation";
    public static final String CATEGORY_DELETION_QUEUE = "categoryDeletionQueue";
    private final CachingConnectionFactory cachingConnectionFactory;


    @Bean
    public RabbitTemplate rabbitTemplate() {
        return new RabbitTemplate(cachingConnectionFactory);
    }

    /**
     * Required for executing administration functions against an AMQP Broker
     */
    @Bean
    public AmqpAdmin rabbitAdmin() {
        return new RabbitAdmin(cachingConnectionFactory);
    }


    @Bean
    public Queue userCreationQueue() {
        return new Queue(USER_CREATION_QUEUE, false);
    }

    @Bean
    public Queue categoryDeletionQueue() {
        return new Queue(CATEGORY_DELETION_QUEUE, false);
    }
}
