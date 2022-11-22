package pl.com.seremak.simplebills.planning.messageQueue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.com.seremak.simplebills.commons.dto.queue.CategoryEventDto;

import static pl.com.seremak.simplebills.commons.constants.MessageQueue.CATEGORY_EVENT_SIMPLE_BILLS_QUEUE;
import static pl.com.seremak.simplebills.commons.constants.MessageQueue.SIMPLE_BILLS_EXCHANGE;


@Slf4j
@Component
@RequiredArgsConstructor
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendCategoryEventMessage(final CategoryEventDto categoryEventDto) {
        rabbitTemplate.convertAndSend(SIMPLE_BILLS_EXCHANGE, CATEGORY_EVENT_SIMPLE_BILLS_QUEUE, categoryEventDto);
        log.info("Message sent: queue={}, message={}", CATEGORY_EVENT_SIMPLE_BILLS_QUEUE, categoryEventDto);
    }
}
