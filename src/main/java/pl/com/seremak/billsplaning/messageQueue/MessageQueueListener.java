package pl.com.seremak.billsplaning.messageQueue;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageQueueListener {

    public static final String USER_CREATION_QUEUE = "userCreation";


    @RabbitListener(queues = USER_CREATION_QUEUE)
    public static void listenUserCreationQueue(final String username) {
        log.info("User creation message received. Username={}", username);
    }
}
