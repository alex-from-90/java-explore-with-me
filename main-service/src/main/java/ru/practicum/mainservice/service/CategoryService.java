package ru.practicum.mainservice.service;

import ru.practicum.mainservice.model.Category;

import java.util.List;

public interface CategoryService {
    Category create(Category category);

    void delete(int categoryId);

    Category getById(int categoryId);

    Category update(int categoryId, Category category);

    List<Category> getAll(int from, int size);
}
