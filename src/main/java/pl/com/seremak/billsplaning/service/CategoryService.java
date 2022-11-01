package pl.com.seremak.billsplaning.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.com.seremak.billsplaning.dto.CategoryDto;
import pl.com.seremak.billsplaning.exceptions.ConflictException;
import pl.com.seremak.billsplaning.model.Category;
import pl.com.seremak.billsplaning.repository.CategoryRepository;
import pl.com.seremak.billsplaning.repository.CategorySearchRepository;
import pl.com.seremak.billsplaning.utils.CollectionUtils;
import pl.com.seremak.billsplaning.utils.VersionedEntityUtils;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    public static final String CATEGORY_ALREADY_EXISTS_ERROR_MSG = "Category with name %s for user with name %s already exists";
    private final CategoryRepository categoryRepository;
    private final CategorySearchRepository categorySearchRepository;

    public Mono<Category> createCustomCategory(final String username, final String categoryName) {
        return categoryRepository.findCategoriesByUsernameAndName(username, categoryName)
                .collectList()
                .mapNotNull(existingCategoryList ->
                        existingCategoryList.isEmpty() ? Category.of(username, categoryName, Category.Type.CUSTOM) : null)
                .map(VersionedEntityUtils::setMetadata)
                .map(categoryRepository::save)
                .flatMap(mono -> mono)
                .switchIfEmpty(Mono.error(new ConflictException(CATEGORY_ALREADY_EXISTS_ERROR_MSG.formatted(username, categoryName))));
    }

    public Mono<List<Category>> findAllCategories(final String username) {
        return categoryRepository.findCategoriesByUsername(username)
                .collectList();
    }

    public Mono<Category> findCategory(final String username, final String categoryName) {
        return categoryRepository.findCategoriesByUsernameAndName(username, categoryName)
                .collectList()
                .map(CollectionUtils::getSoleElementOrThrowException);
    }

    public Mono<Category> updateCategory(final String username, final CategoryDto categoryDto) {
        final Category categoryToUpdate = Category.of(username, categoryDto.getName(), categoryDto.getLimit());
        return categorySearchRepository.updateCategory(categoryToUpdate);
    }

    public Mono<Category> deleteCategory(final String username, final String categoryName) {
        return categoryRepository.deleteCategoryByUsernameAndName(username, categoryName);
    }
}
