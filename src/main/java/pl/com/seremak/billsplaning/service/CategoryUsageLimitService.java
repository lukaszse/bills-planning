package pl.com.seremak.billsplaning.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pl.com.seremak.billsplaning.repository.CategoryUsageLimitRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryUsageLimitService {

    private CategoryUsageLimitRepository categoryUsageLimitRepository;
    

}
