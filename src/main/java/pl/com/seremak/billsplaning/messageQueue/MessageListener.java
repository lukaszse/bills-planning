package pl.com.seremak.billsplaning.messageQueue;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import pl.com.seremak.billsplaning.service.CategoryService;
import pl.com.seremak.billsplaning.service.TransactionPostingService;
import pl.com.seremak.billsplaning.service.UserSetupService;
import pl.com.seremak.simplebills.commons.dto.http.CategoryDto;
import pl.com.seremak.simplebills.commons.dto.queue.TransactionEventDto;

import static pl.com.seremak.billsplaning.config.RabbitMQConfig.*;

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

    @RabbitListener(queues = CATEGORY_CREATION_REQUEST_QUEUE)
    public void receiveCategoryCreationRequestMessage(final Message<CategoryDto> categoryDtoMessage) {
        final CategoryDto categoryDto = categoryDtoMessage.getPayload();
        log.info("CategoryCreationRequestDto message received: username={}, categoryName={}",
                categoryDto.getUsername(), categoryDto.getName());
        categoryService.createCustomCategory(categoryDto)
                .doOnSuccess(createdCategory -> log.info("Category with name={} and username={} created.",
                        createdCategory.getName(), createdCategory.getUsername()))
                .subscribe();
    }
}
