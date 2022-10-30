package pl.com.seremak.billsplaning.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.com.seremak.billsplaning.exceptions.ConflictException;
import pl.com.seremak.billsplaning.model.Category;
import pl.com.seremak.billsplaning.repository.CategoryRepository;
import pl.com.seremak.billsplaning.utils.CollectionUtils;
import pl.com.seremak.billsplaning.utils.VersionedEntityUtils;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    public static final String CATEGORY_ALREADY_EXISTS_ERROR_MSG = "Category with name %s for user with name %s already exists";
    private final CategoryRepository categoryRepository;

    public Mono<Category> createCustomCategory(final String username, final String categoryName) {
        return categoryRepository.getCategoriesByUsernameAndName(username, categoryName)
                .collectList()
                .mapNotNull(existingCategoryList ->
                        existingCategoryList.isEmpty() ? Category.of(username, categoryName, Category.Type.CUSTOM) : null)
                .map(VersionedEntityUtils::setMetadata)
                .map(categoryRepository::save)
                .flatMap(mono -> mono)
                .switchIfEmpty(Mono.error(new ConflictException(CATEGORY_ALREADY_EXISTS_ERROR_MSG.formatted(username, categoryName))));
    }

    public Mono<List<Category>> getAllCategories(final String username) {
        return categoryRepository.getCategoriesByUsername(username)
                .collectList();
    }

    public Mono<Category> getCategory(final String username, final String categoryName) {
        return categoryRepository.getCategoriesByUsernameAndName(username, categoryName)
                .collectList()
                .map(CollectionUtils::getSoleElementOrThrowException);
    }

    public Mono<Category> deleteCategory(final String username, final String categoryName) {
        return categoryRepository.deleteCategoryByUsernameAndName(username, categoryName);
    }
}
