package ru.practicum.mainservice.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.mainservice.dto.category.CategoryDTO;
import ru.practicum.mainservice.dto.category.CreateCategoryDTO;
import ru.practicum.mainservice.model.Category;

@Component
public class CategoryMapper {
    public CategoryDTO toDto(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    public Category toModel(CreateCategoryDTO dto) {
        Category category = new Category();
        category.setName(dto.getName());
        return category;
    }
}
