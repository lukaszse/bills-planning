package pl.com.seremak.billsplaning.messageQueue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.com.seremak.billsplaning.messageQueue.queueDto.CategoryDeletionMessage;

import static pl.com.seremak.billsplaning.config.RabbitMQConfig.CATEGORY_DELETION_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sentCategoryDeletionMessage(final CategoryDeletionMessage categoryDeletionMessage) {
        rabbitTemplate.convertAndSend(CATEGORY_DELETION_QUEUE, categoryDeletionMessage);
        log.info("Message sent: queue={}, message={}", CATEGORY_DELETION_QUEUE, categoryDeletionMessage);
    }
}
