package pl.com.seremak.billsplaning.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.com.seremak.billsplaning.dto.BillPlanDto;
import pl.com.seremak.billsplaning.exceptions.ConflictException;
import pl.com.seremak.billsplaning.model.BillPlan;
import pl.com.seremak.billsplaning.repository.BillPlanRepository;
import pl.com.seremak.billsplaning.repository.BillPlanSearchRepository;
import pl.com.seremak.billsplaning.utils.CollectionUtils;
import pl.com.seremak.billsplaning.utils.VersionedEntityUtils;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BillPlanService {

    public static final String BILL_PLAN_ALREADY_EXISTS = "Bill plan with username=%s and categoryName=%s already exists.";
    private final BillPlanRepository billPlanRepository;
    private final BillPlanSearchRepository billPlanSearchRepository;

    public Mono<BillPlan> createBillPlan(final String username, final BillPlanDto billPlanDto) {
        final BillPlan billPlan = BillPlan.of(username, billPlanDto.getCategoryName(), billPlanDto.getAmount());
        return billPlanRepository.getBillPlanByUsernameAndCategoryName(billPlan.getUsername(), billPlan.getCategoryName())
                .collectList()
                .mapNotNull(existingBillPlans -> existingBillPlans.isEmpty() ? VersionedEntityUtils.setMetadata(billPlan) : null)
                .map(billPlanRepository::save)
                .flatMap(mono -> mono)
                .switchIfEmpty(Mono.error(new ConflictException(BILL_PLAN_ALREADY_EXISTS.formatted(billPlan.getUsername(), billPlan.getCategoryName()))));
    }

    public Mono<BillPlan> getBillPlanByCategoryName(final String username, final String categoryName) {
        return billPlanRepository.getBillPlanByUsernameAndCategoryName(username, categoryName)
                .collectList()
                .map(CollectionUtils::getSoleElementOrThrowException);
    }

    public Mono<List<BillPlan>> getAllBillPlans(final String username) {
        return billPlanRepository.getBillPlanByUsername(username)
                .collectList();
    }

    public Mono<BillPlan> update(final String username, final BillPlanDto billPlanDto) {
        final BillPlan billPlan = BillPlan.of(username, billPlanDto.getCategoryName(), billPlanDto.getAmount());
        return billPlanSearchRepository.updateBillPlan(billPlan);
    }

    public Mono<BillPlan> deleteBillPlan(final String username, final String categoryName) {
        return billPlanRepository.deleteBillPlanByUsernameAndCategoryName(username, categoryName);
    }
}
