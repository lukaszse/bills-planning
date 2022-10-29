package pl.com.seremak.billsplaning.endpoint;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.billsplaning.model.Category;
import pl.com.seremak.billsplaning.service.CategoryService;
import pl.com.seremak.billsplaning.utils.EndpointUtils;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryEndpoint {

    public static final String CATEGORY_URI_PATTERN = "/categories/%s";
    private final CategoryService categoryService;

    @PostMapping(produces = TEXT_PLAIN_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<String>> createCategory(@Valid @RequestBody final Category category) {
        // todo add authorization and token handling
        log.info("Category creation request received: {}", category);
        return categoryService.createCategory(category)
                .map(createdCategory -> category.getName())
                .doOnSuccess(createdCategoryName -> log.info("Category with name={} successfully created", createdCategoryName))
                .map(categoryName -> EndpointUtils.createResponse(CATEGORY_URI_PATTERN, categoryName));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Category>>> getAllCategories(final String username) {
        log.info("Finding categories for user wiyh name={}", username);
        return categoryService.getAllCategories(username)
                .collectList()
                .doOnSuccess(categories -> log.info("{} categories for username={} found.", categories.size(), username))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "{categoryName}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Category>> getCategoryByName(final String username, @PathVariable final String categoryName) {
        log.info("Looking for category with name={} and username={}", categoryName, username);
        return categoryService.getCategory(username, categoryName)
                .doOnSuccess(category -> log.info("Category with name={} for username={} successfully found.", category.getName(), category.getUsername()))
                .map(ResponseEntity::ok);
    }
}
