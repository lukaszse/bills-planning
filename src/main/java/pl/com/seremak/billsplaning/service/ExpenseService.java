package pl.com.seremak.billsplaning.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.com.seremak.billsplaning.dto.ExpenseDto;
import pl.com.seremak.billsplaning.exceptions.ConflictException;
import pl.com.seremak.billsplaning.model.Expense;
import pl.com.seremak.billsplaning.repository.ExpenseRepository;
import pl.com.seremak.billsplaning.repository.ExpenseSearchRepository;
import pl.com.seremak.billsplaning.utils.CollectionUtils;
import pl.com.seremak.billsplaning.utils.VersionedEntityUtils;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExpenseService {

    public static final String BILL_PLAN_ALREADY_EXISTS = "Bill plan with username=%s and categoryName=%s already exists.";
    private final ExpenseRepository billPlanRepository;
    private final ExpenseSearchRepository expenseSearchRepository;

    public Mono<Expense> createExpense(final String username, final ExpenseDto expenseDto) {
        final Expense expense = Expense.of(username, expenseDto.getCategoryName(), expenseDto.getAmount());
        return billPlanRepository.findExpenseByUsernameAndCategoryName(expense.getUsername(), expense.getCategoryName())
                .collectList()
                .mapNotNull(existingBillPlans -> existingBillPlans.isEmpty() ? VersionedEntityUtils.setMetadata(expense) : null)
                .map(billPlanRepository::save)
                .flatMap(mono -> mono)
                .switchIfEmpty(Mono.error(new ConflictException(BILL_PLAN_ALREADY_EXISTS.formatted(expense.getUsername(), expense.getCategoryName()))));
    }

    public Mono<Expense> findExpenseByCategoryName(final String username, final String categoryName) {
        return billPlanRepository.findExpenseByUsernameAndCategoryName(username, categoryName)
                .collectList()
                .map(CollectionUtils::getSoleElementOrThrowException);
    }

    public Mono<List<Expense>> findAllExpenses(final String username) {
        return billPlanRepository.findExpenseByUsername(username)
                .collectList();
    }

    public Mono<Expense> update(final String username, final ExpenseDto expenseDto) {
        final Expense expense = Expense.of(username, expenseDto.getCategoryName(), expenseDto.getAmount());
        return expenseSearchRepository.updateBillPlan(expense);
    }

    public Mono<Expense> deleteBillPlan(final String username, final String categoryName) {
        return billPlanRepository.deleteExpenseByUsernameAndCategoryName(username, categoryName);
    }
}
