package pl.com.seremak.billsplaning.databasePrePopulation;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.com.seremak.billsplaning.service.CategoryService;

import java.util.List;

import static pl.com.seremak.billsplaning.service.CategoryService.findAllMissingCategories;
import static pl.com.seremak.billsplaning.utils.BillPlanConstants.MASTER_USER;

@Slf4j
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "custom-properties")
public class StandardCategoriesCreation {

    private final CategoryService categoryService;

    @Setter
    private List<String> categories;

    @EventListener
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        createStandardCategoriesForMasterUserIfNotExists();
    }

    private void createStandardCategoriesForMasterUserIfNotExists() {
        log.info("Looking for missing standard categories...");
        categoryService.findStandardCategoriesForUser(MASTER_USER)
                .collectList()
                .map(masterUserCategories -> findAllMissingCategories(MASTER_USER, masterUserCategories, categories))
                .flatMapMany(categoryService::createAllCategories)
                .collectList()
                .doOnSuccess(CategoryService::logMissingCategoryAddingSummary)
                .block();
    }
}
