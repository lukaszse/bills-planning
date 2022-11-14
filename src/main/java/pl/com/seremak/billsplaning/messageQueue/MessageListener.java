package pl.com.seremak.billsplaning.messageQueue;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.com.seremak.billsplaning.service.CategoryService;
import pl.com.seremak.billsplaning.service.TransactionPostingService;
import pl.com.seremak.billsplaning.service.UserSetupService;
import pl.com.seremak.simplebills.commons.dto.queue.TransactionEventDto;

import static pl.com.seremak.billsplaning.config.RabbitMQConfig.TRANSACTION_QUEUE;
import static pl.com.seremak.billsplaning.config.RabbitMQConfig.USER_CREATION_QUEUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageListener {

    private final UserSetupService userSetupService;
    private final TransactionPostingService transactionPostingService;
    private final CategoryService categoryService;


    @RabbitListener(queues = USER_CREATION_QUEUE)
    public void receiveUserCreationMessage(final String username) {
        log.info("User creation message received. Username={}", username);
        userSetupService.setupUser(username);
    }

    @RabbitListener(queues = TRANSACTION_QUEUE)
    public void receiveTransactionMessage(final Message<TransactionEventDto> transactionMessage) {
        final TransactionEventDto transaction = transactionMessage.getPayload();
        log.info("Transaction message received: username={}, categoryName={}", transaction.getUsername(), transaction.getCategoryName());
        transactionPostingService.postTransaction(transaction)
                .doOnSuccess(updatedBalance -> log.info("Balance for username={} updated.", updatedBalance.getUsername()))
                .subscribe();
    }
}
