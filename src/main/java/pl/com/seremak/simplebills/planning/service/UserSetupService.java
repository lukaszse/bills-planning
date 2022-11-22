package pl.com.seremak.simplebills.planning.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserSetupService {

    private final CategoryService categoryService;
    private final BalanceService balanceService;

    public void setupUser(final String username) {
        categoryService.createStandardCategoriesForUserIfNotExists(username).block();
        balanceService.createNewClearBalance(username).block();
    }
}
