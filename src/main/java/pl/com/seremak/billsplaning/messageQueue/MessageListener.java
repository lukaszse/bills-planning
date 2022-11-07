package pl.com.seremak.billsplaning.messageQueue;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.com.seremak.billsplaning.dto.TransactionDto;
import pl.com.seremak.billsplaning.service.CategoryService;
import pl.com.seremak.billsplaning.service.TransactionPostingService;

import static pl.com.seremak.billsplaning.config.RabbitMQConfig.BILL_ACTION_MESSAGE;
import static pl.com.seremak.billsplaning.config.RabbitMQConfig.USER_CREATION_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final CategoryService categoryService;
    private final TransactionPostingService transactionPostingService;


    @RabbitListener(queues = USER_CREATION_QUEUE)
    public void receiveUserCreationMessage(final String username) {
        log.info("User creation message received. Username={}", username);
        categoryService.createStandardCategoriesForUserIfNotExists(username);
    }

    @RabbitListener(queues = BILL_ACTION_MESSAGE)
    public void receiveBillActionMessage(final Message<TransactionDto> transactionMessage) {
        final TransactionDto transaction = transactionMessage.getPayload();
        log.info("Transaction message received. Username={}", transaction);
        transactionPostingService.postTransaction(transaction)
                .subscribe();
    }
}
