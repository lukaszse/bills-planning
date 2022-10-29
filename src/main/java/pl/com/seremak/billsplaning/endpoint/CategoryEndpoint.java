package pl.com.seremak.billsplaning.endpoint;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.billsplaning.model.Category;
import pl.com.seremak.billsplaning.service.CategoryService;
import pl.com.seremak.billsplaning.utils.EndpointUtils;
import pl.com.seremak.billsplaning.utils.JwtExtractionHelper;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryEndpoint {

    public static final String CATEGORY_URI_PATTERN = "/categories/%s";
    public static final String JWT_TOKEN_VALIDATION_ERROR_MSG = "Username validation failed. Jwt token not match.";
    private final CategoryService categoryService;
    private final JwtExtractionHelper jwtExtractionHelper;

    @PostMapping(produces = TEXT_PLAIN_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> createCategory(final JwtAuthenticationToken principal, @Valid @RequestBody final Category category) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        validateUser(username, category.getUsername());
        log.info("Category creation request received: {}", category);
        return categoryService.createCategory(category)
                .map(Category::getName)
                .doOnSuccess(createdCategoryName -> log.info("Category with name={} successfully created", createdCategoryName))
                .map(categoryName -> EndpointUtils.createResponse(CATEGORY_URI_PATTERN, categoryName));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Category>>> getAllCategories(final JwtAuthenticationToken principal) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Finding categories for user with name={}", username);
        return categoryService.getAllCategories(username)
                .collectList()
                .doOnSuccess(categories -> log.info("{} categories for username={} found.", categories.size(), username))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "{categoryName}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Category>> getCategoryByName(final JwtAuthenticationToken principal, @PathVariable final String categoryName) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Looking for category with name={} and username={}", categoryName, username);
        return categoryService.getCategory(username, categoryName)
                .doOnSuccess(category -> log.info("Category with name={} for username={} successfully found.", category.getName(), category.getUsername()))
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(value = "{categoryName}")
    private Mono<ResponseEntity<String>> deleteCategoryName(final JwtAuthenticationToken principal, @PathVariable final String categoryName) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Deleting category with name={} and username={}", categoryName, username);
        return categoryService.deleteCategory(username, categoryName)
                .doOnSuccess(category -> log.info("Category with name={} and username={} deleted.", category.getName(), category.getUsername()))
                .map(Category::getName)
                .map(ResponseEntity::ok);
    }

    private static void validateUser(final String tokenUsername, final String requestBodyUsername) {
        if (!Objects.equals(tokenUsername, requestBodyUsername)) {
            throw new AuthenticationServiceException(JWT_TOKEN_VALIDATION_ERROR_MSG);
        }
    }
}
