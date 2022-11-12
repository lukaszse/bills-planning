package pl.com.seremak.billsplaning.endpoint;


import com.mongodb.lang.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import pl.com.seremak.billsplaning.converter.CategoryConverter;
import pl.com.seremak.billsplaning.dto.CategoryDto;
import pl.com.seremak.billsplaning.model.Category;
import pl.com.seremak.billsplaning.service.CategoryService;
import pl.com.seremak.billsplaning.utils.EndpointUtils;
import pl.com.seremak.billsplaning.utils.JwtExtractionHelper;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@CrossOrigin
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryEndpoint {

    public static final String CATEGORY_URI_PATTERN = "/categories/%s";
    private final CategoryService categoryService;
    private final JwtExtractionHelper jwtExtractionHelper;

    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<CategoryDto>> createCategory(final JwtAuthenticationToken principal,
                                                            @Valid @RequestBody final CategoryDto categoryDto) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Category creation request received for username={} and categoryName={}", username, categoryDto.getName());
        return categoryService.createCustomCategory(username, categoryDto)
                .doOnSuccess(category -> log.info("Category with name={} and username={} successfully created for", category.getName(), category.getUsername()))
                .map(CategoryConverter::toCategoryDto)
                .map(category -> EndpointUtils.prepareCreatedResponse(CATEGORY_URI_PATTERN, category.getName(), category));
    }

    @GetMapping(produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<Category>>> findAllCategories(final JwtAuthenticationToken principal) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Finding categories for user with name={}", username);
        return categoryService.findAllCategories(username)
                .doOnSuccess(categories -> log.info("{} categories for username={} found.", categories.size(), username))
                .map(ResponseEntity::ok);
    }

    @GetMapping(value = "{categoryName}", produces = APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<Category>> findCategoryByName(final JwtAuthenticationToken principal,
                                                             @PathVariable final String categoryName) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Looking for category with name={} and username={}", categoryName, username);
        return categoryService.findCategory(username, categoryName)
                .doOnSuccess(category -> log.info("Category with name={} for username={} successfully found.", category.getName(), category.getUsername()))
                .map(ResponseEntity::ok);
    }

    @PatchMapping(value = "{name}")
    private Mono<ResponseEntity<CategoryDto>> updateCategory(final JwtAuthenticationToken principal,
                                                             @Valid @RequestBody final CategoryDto categoryDto) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Updating Category with username={} and categoryName={}", username, categoryDto.getName());
        return categoryService.updateCategory(username, categoryDto)
                .doOnSuccess(updatedCategory -> log.info("Category with username={} and categoryName={} updated.", updatedCategory.getUsername(), updatedCategory.getName()))
                .map(CategoryConverter::toCategoryDto)
                .map(ResponseEntity::ok);
    }

    @DeleteMapping(value = "{category}")
    private Mono<ResponseEntity<String>> deleteCategoryName(final JwtAuthenticationToken principal,
                                                            @PathVariable final String category,
                                                            @RequestParam @Nullable final String replacementCategory) {
        final String username = jwtExtractionHelper.extractUsername(principal);
        log.info("Deleting category with name={} and username={}", category, username);
        return categoryService.deleteCategory(username, category, replacementCategory)
                .doOnSuccess(deletedCategory -> log.info("Category with name={} and username={} deleted.", deletedCategory.getName(), deletedCategory.getUsername()))
                .map(Category::getName)
                .map(__ -> ResponseEntity.noContent().build());
    }
}
