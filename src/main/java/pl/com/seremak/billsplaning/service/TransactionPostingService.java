package pl.com.seremak.billsplaning.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.seremak.billsplaning.dto.TransactionEventDto;
import pl.com.seremak.billsplaning.model.Balance;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionPostingService {

    private final BalanceService balanceService;
    private final CategoryUsageLimitService categoryUsageLimitService;

    public Mono<Balance> postTransaction(final TransactionEventDto transactionEventDto) {
        return categoryUsageLimitService.updateCategoryUsageLimitAfterNewTransaction(transactionEventDto)
                .then(balanceService.updateBalance(transactionEventDto));
    }
}
