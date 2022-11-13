package pl.com.seremak.billsplaning.messageQueue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.com.seremak.simplebills.commons.dto.queue.CategoryEventDto;

import static pl.com.seremak.billsplaning.config.RabbitMQConfig.CATEGORY_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sentCategoryDeletionMessage(final CategoryEventDto categoryEventDto) {
        rabbitTemplate.convertAndSend(CATEGORY_QUEUE, categoryEventDto);
        log.info("Message sent: queue={}, message={}", CATEGORY_QUEUE, categoryEventDto);
    }
}
