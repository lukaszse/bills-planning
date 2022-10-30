package pl.com.seremak.billsplaning.databasePrePopulation;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.com.seremak.billsplaning.model.Category;
import pl.com.seremak.billsplaning.repository.CategoryRepository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "custom-properties")
public class StandardCategoriesCreation {

    private static final String MASTER_USER = "master_user";

    private final CategoryRepository categoryRepository;

    @Setter
    private List<String> categories;

    @EventListener
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        createStandardCategoriesIfNotExists();
    }

    private void createStandardCategoriesIfNotExists() {
        log.info("Looking for missing standard categories...");
        categoryRepository.getCategoriesByUsername(MASTER_USER)
                .collectList()
                .map(this::findAllMissingCategories)
                .flatMapMany(categoryRepository::saveAll)
                .collectList()
                .doOnSuccess(StandardCategoriesCreation::logMissingCategoryAddingSummary)
                .block();
    }

    private Set<Category> findAllMissingCategories(final List<Category> standardCategories) {
        final Set<String> existingStandardCategoryNames = standardCategories.stream()
                .map(Category::getName)
                .collect(Collectors.toSet());
        return categories.stream()
                .filter(categoryName -> !existingStandardCategoryNames.contains(categoryName))
                .map(categoryName -> Category.of(MASTER_USER, categoryName, Category.Type.STANDARD))
                .collect(Collectors.toSet());
    }

    private static void logMissingCategoryAddingSummary(final List<Category> addedCategories) {
        if (addedCategories.isEmpty()) {
            log.info("No missing categories found");

        } else {
            log.info("{} missing categories added: {}", addedCategories.size(), addedCategories);
        }
    }
}
