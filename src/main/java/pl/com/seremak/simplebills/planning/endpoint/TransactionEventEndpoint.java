package pl.com.seremak.simplebills.planning.endpoint;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.simplebills.commons.dto.queue.TransactionEventDto;
import pl.com.seremak.simplebills.commons.model.Balance;
import pl.com.seremak.simplebills.commons.utils.JwtExtractionHelper;
import pl.com.seremak.simplebills.planning.service.TransactionPostingService;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/transactionsEvents")
@RequiredArgsConstructor
public class TransactionEventEndpoint {

    private final TransactionPostingService transactionPostingService;


    @PostMapping(produces = TEXT_PLAIN_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Balance>> postTransaction(final JwtAuthenticationToken principal,
                                                         @Valid @RequestBody final TransactionEventDto transactionEventDto) {
        final String username = JwtExtractionHelper.extractUsername(principal);
        JwtExtractionHelper.validateUsername(username, transactionEventDto.getUsername());
        log.info("Transaction request for username={} and categoryName={} received.", username, transactionEventDto.getCategoryName());
        return transactionPostingService.postTransaction(transactionEventDto)
                .map(ResponseEntity::ok);
    }
}
