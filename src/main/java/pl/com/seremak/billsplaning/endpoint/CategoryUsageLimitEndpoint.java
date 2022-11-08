package pl.com.seremak.billsplaning.endpoint;


import com.mongodb.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.billsplaning.model.CategoryUsageLimit;
import pl.com.seremak.billsplaning.service.CategoryUsageLimitService;
import pl.com.seremak.billsplaning.utils.JwtExtractionHelper;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/category-usage-limit")
@RequiredArgsConstructor
public class CategoryUsageLimitEndpoint {

    private final CategoryUsageLimitService categoryUsageLimitService;
    private final JwtExtractionHelper jwtExtractionHelper;

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<CategoryUsageLimit>>> getAllCategoryUsageLimits(final JwtAuthenticationToken principal,
                                                                                    @Nullable @RequestParam final String yearMonth) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        return categoryUsageLimitService.findAllCategoryUsageLimits(username, yearMonth)
                .doOnSuccess(categoryUsageLimits -> log.info("A list of {} usage of limits for all categories for username={} found.", categoryUsageLimits.size(), username))
                .map(ResponseEntity::ok);
    }
}
