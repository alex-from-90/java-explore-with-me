package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.Category;
import ru.practicum.mainservice.repository.CategoryRepository;
import ru.practicum.mainservice.util.OffsetBasedPageRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Category create(Category category) {
        Category newCategory = new Category();
        newCategory.setName(category.getName());
        return categoryRepository.save(newCategory);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getById(int categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Category with id=%s was not found",
                        categoryId
                )));
    }

    @Override
    @Transactional
    public void delete(int categoryId) {
        Category category = getById(categoryId);
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public Category update(int categoryId, Category category) {
        Category fromDb = getById(categoryId);
        fromDb.setName(category.getName());
        return fromDb;
    }

    @Override
    public List<Category> getAll(int from, int size) {
        Pageable pageable = new OffsetBasedPageRequest(from, size);
        return categoryRepository.findAll(pageable).getContent();
    }
}
