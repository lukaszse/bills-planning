package pl.com.seremak.billsplaning.messageQueue;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import pl.com.seremak.simplebills.commons.dto.queue.CategoryEventDto;

import static pl.com.seremak.simplebills.commons.constants.MessageQueue.CATEGORY_EVENT_QUEUE;


@Slf4j
@Component
@RequiredArgsConstructor
public class MessagePublisher {

    private final RabbitTemplate rabbitTemplate;

    public void sendCategoryEventMessage(final CategoryEventDto categoryEventDto) {
        rabbitTemplate.convertAndSend(CATEGORY_EVENT_QUEUE, categoryEventDto);
        log.info("Message sent: queue={}, message={}", CATEGORY_EVENT_QUEUE, categoryEventDto);
    }
}
