package pl.com.seremak.billsplaning.endpoint;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.com.seremak.billsplaning.model.Balance;
import pl.com.seremak.billsplaning.service.BalanceService;
import pl.com.seremak.billsplaning.utils.JwtExtractionHelper;
import reactor.core.publisher.Mono;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/balance")
@RequiredArgsConstructor
public class BalanceEndpoint {

    private final BalanceService balanceService;
    private final JwtExtractionHelper jwtExtractionHelper;


    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Balance>> findBalance(final JwtAuthenticationToken principal) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        return balanceService.findBalance(username)
                .doOnSuccess(balance -> log.info("Balance for username={} found.", balance.getUsername()))
                .map(ResponseEntity::ok);
    }
}
