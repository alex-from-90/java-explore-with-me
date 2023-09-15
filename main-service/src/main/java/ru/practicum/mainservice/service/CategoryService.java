package ru.practicum.mainservice.service;

import ru.practicum.mainservice.dto.category.CategoryDTO;
import ru.practicum.mainservice.dto.category.CreateCategoryDTO;
import ru.practicum.mainservice.model.Category;

import java.util.List;

public interface CategoryService {
    CategoryDTO create(CreateCategoryDTO category);

    void delete(int categoryId);

    CategoryDTO getById(int categoryId);

    Category getCategoryById(int categoryId);

    CategoryDTO update(int categoryId, CreateCategoryDTO category);

    List<CategoryDTO> getAll(int from, int size);
}
