package pl.com.seremak.billsplaning.messageQueue;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import pl.com.seremak.billsplaning.service.CategoryService;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    public static final String USER_CREATION_QUEUE = "userCreation";
    private final CategoryService categoryService;


    @RabbitListener(queues = USER_CREATION_QUEUE)
    public void listenUserCreationQueue(final String username) {
        log.info("User creation message received. Username={}", username);
        categoryService.createStandardCategoriesForUserIfNotExists(username);
    }
}
