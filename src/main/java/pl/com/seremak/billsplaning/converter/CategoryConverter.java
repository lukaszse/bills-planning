package pl.com.seremak.billsplaning.converter;

import pl.com.seremak.billsplaning.dto.CategoryDto;
import pl.com.seremak.billsplaning.model.Category;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryConverter {

    public static Category toCategory(final String username, final CategoryDto categoryDto, final List<Category> existingCategoryList) {
        return existingCategoryList.isEmpty() ?
                Category.builder()
                        .username(username)
                        .transactionType(toTransactionTypeEnum(categoryDto))
                        .name(categoryDto.getName())
                        .limit(categoryDto.getLimit())
                        .build() : null;
    }

    public static Category toCategory(final String username,
                                      final String categoryName,
                                      final Category.TransactionType transactionType) {
        return Category.builder()
                .username(username)
                .transactionType(transactionType)
                .name(categoryName)
                .type(Category.Type.STANDARD)
                .build();
    }

    public static Category toCategory(final String username, final CategoryDto categoryDto) {
        return Category.builder()
                .username(username)
                .transactionType(toTransactionTypeEnum(categoryDto))
                .name(categoryDto.getName())
                .limit(categoryDto.getLimit())
                .build();
    }

    public static CategoryDto toCategoryDto(final Category category) {
        return CategoryDto.of(category.getName(), category.getTransactionType().toString(), category.getLimit());
    }

    public static CategoryDto toCategoryDto(final String categoryName, final Category.TransactionType transactionType) {
        return CategoryDto.of(categoryName, transactionType.toString().toUpperCase(), null);
    }

    public static List<Category> toCategories(final String username,
                                              final List<String> categoryNames,
                                              final Category.TransactionType transactionType) {
        return categoryNames.stream()
                .map(categoryName -> toCategory(username, categoryName, transactionType))
                .collect(Collectors.toList());
    }

    public static Category.TransactionType toTransactionTypeEnum(final CategoryDto categoryDto) {
        return Category.TransactionType.valueOf(categoryDto.getTransactionType().toUpperCase());
    }
}
