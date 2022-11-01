package pl.com.seremak.billsplaning.endpoint;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.billsplaning.dto.ExpenseDto;
import pl.com.seremak.billsplaning.model.Expense;
import pl.com.seremak.billsplaning.service.ExpenseService;
import pl.com.seremak.billsplaning.utils.EndpointUtils;
import pl.com.seremak.billsplaning.utils.JwtExtractionHelper;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/billPlans")
@RequiredArgsConstructor
public class ExpenseEndpoint {

    public static final String BILL_PLAN_URI_PATTERN = "/billPlans/%s";
    private final ExpenseService billPlanService;
    private final JwtExtractionHelper jwtExtractionHelper;

    @PostMapping(produces = TEXT_PLAIN_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> createExpense(final JwtAuthenticationToken principal,
                                                      @Valid @RequestBody final ExpenseDto expenseDto) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Bill plan creation request for username={} and categoryName={} received.", username, expenseDto.getCategoryName());
        return billPlanService.createExpense(username, expenseDto)
                .doOnSuccess(createdExpense ->
                        log.info("Bill plan with username={} and categoryName={} successfully created.", createdExpense.getUsername(), createdExpense.getCategoryName()))
                .map(Expense::getCategoryName)
                .map(categoryName -> EndpointUtils.createResponse(BILL_PLAN_URI_PATTERN, categoryName));
    }

    @GetMapping
    public Mono<ResponseEntity<List<Expense>>> findExpenses(final JwtAuthenticationToken principal) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Looking for all bill plans for username={}.", username);
        return billPlanService.findAllExpenses(username)
                .doOnSuccess(billPlans -> log.info("{} bill plans for username={} found.", billPlans.size(), username))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "{categoryName}")
    public Mono<ResponseEntity<Expense>> findExpense(final JwtAuthenticationToken principal, @PathVariable final String categoryName) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Looking for all bill plans for username={}.", username);
        return billPlanService.findExpenseByCategoryName(username, categoryName)
                .doOnSuccess(billPlans -> log.info("A bill plan for username={} and categoryName={} found.", billPlans, categoryName))
                .map(ResponseEntity::ok);
    }

    @PatchMapping(value = "{categoryName}")
    private Mono<ResponseEntity<String>> updateExpensePlan(final JwtAuthenticationToken principal,
                                                           @Valid @RequestBody final ExpenseDto expenseDto) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Updating billPlan with username={} and categoryName={}", username, expenseDto.getCategoryName());
        return billPlanService.update(username, expenseDto)
                .doOnSuccess(expense -> log.info("Bill plan with username={} and categoryName={} updated.", expense.getUsername(), expense.getCategoryName()))
                .map(Expense::getCategoryName)
                .map(__ -> ResponseEntity.noContent().build());
    }

    @DeleteMapping(value = "{categoryName}")
    private Mono<ResponseEntity<String>> deleteExpensePlan(final JwtAuthenticationToken principal, @PathVariable final String categoryName) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Deleting billPlan with username={} and categoryName={}", username, categoryName);
        return billPlanService.deleteBillPlan(username, categoryName)
                .doOnSuccess(expense -> log.info("Bill plan with username={} and categoryName={} deleted.", expense.getUsername(), expense.getCategoryName()))
                .map(Expense::getCategoryName)
                .map(__ -> ResponseEntity.noContent().build());
    }
}
