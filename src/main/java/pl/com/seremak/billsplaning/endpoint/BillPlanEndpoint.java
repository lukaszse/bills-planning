package pl.com.seremak.billsplaning.endpoint;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.billsplaning.dto.BillPlanDto;
import pl.com.seremak.billsplaning.model.BillPlan;
import pl.com.seremak.billsplaning.service.BillPlanService;
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
public class BillPlanEndpoint {

    public static final String BILL_PLAN_URI_PATTERN = "/billPlans/%s";
    private final BillPlanService billPlanService;
    private final JwtExtractionHelper jwtExtractionHelper;

    @PostMapping(produces = TEXT_PLAIN_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> createBillPlan(final JwtAuthenticationToken principal,
                                                       @Valid @RequestBody final BillPlanDto billPlanDto) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Bill plan creation request for username={} and categoryName={} received.", username, billPlanDto.getCategoryName());
        return billPlanService.createBillPlan(username, billPlanDto)
                .doOnSuccess(createdBillPlan ->
                        log.info("Bill plan with username={} and categoryName={} successfully created.", createdBillPlan.getUsername(), createdBillPlan.getCategoryName()))
                .map(BillPlan::getCategoryName)
                .map(categoryName -> EndpointUtils.createResponse(BILL_PLAN_URI_PATTERN, categoryName));
    }

    @GetMapping
    public Mono<ResponseEntity<List<BillPlan>>> getBillPlans(final JwtAuthenticationToken principal) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Looking for all bill plans for username={}.", username);
        return billPlanService.getAllBillPlans(username)
                .doOnSuccess(billPlans -> log.info("{} bill plans for username={} found.", billPlans.size(), username))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "{categoryName}")
    public Mono<ResponseEntity<BillPlan>> getBillPlan(final JwtAuthenticationToken principal, @PathVariable final String categoryName) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Looking for all bill plans for username={}.", username);
        return billPlanService.getBillPlanByCategoryName(username, categoryName)
                .doOnSuccess(billPlans -> log.info("A bill plan for username={} and categoryName={} found.", billPlans, categoryName))
                .map(ResponseEntity::ok);
    }

    @PatchMapping(value = "{categoryName}")
    private Mono<ResponseEntity<String>> updateCategoryBillPlan(final JwtAuthenticationToken principal,
                                                                @Valid @RequestBody final BillPlanDto billPlanDto) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Updating billPlan with username={} and categoryName={}", username, billPlanDto.getCategoryName());
        return billPlanService.update(username, billPlanDto)
                .doOnSuccess(billPlan -> log.info("Bill plan with username={} and categoryName={} updated.", billPlan.getUsername(), billPlan.getCategoryName()))
                .map(BillPlan::getCategoryName)
                .map(__ -> ResponseEntity.noContent().build());
    }

    @DeleteMapping(value = "{categoryName}")
    private Mono<ResponseEntity<String>> deleteCategoryBillPlan(final JwtAuthenticationToken principal, @PathVariable final String categoryName) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Deleting billPlan with username={} and categoryName={}", username, categoryName);
        return billPlanService.deleteBillPlan(username, categoryName)
                .doOnSuccess(billPlan -> log.info("Bill plan with username={} and categoryName={} deleted.", billPlan.getUsername(), billPlan.getCategoryName()))
                .map(BillPlan::getCategoryName)
                .map(__ -> ResponseEntity.noContent().build());
    }
}
