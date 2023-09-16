package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.category.CategoryDTO;
import ru.practicum.mainservice.dto.category.CreateCategoryDTO;
import ru.practicum.mainservice.exception.APIException;
import ru.practicum.mainservice.mapper.CategoryMapper;
import ru.practicum.mainservice.model.Category;
import ru.practicum.mainservice.repository.CategoryRepository;
import ru.practicum.mainservice.util.OffsetBasedPageRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDTO create(CreateCategoryDTO category) {
        Category newCategory = new Category();
        newCategory.setName(category.getName());
        return categoryMapper.toDto(categoryRepository.save(newCategory));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDTO getById(int categoryId) {
        Category category = getCategoryById(categoryId);
        return categoryMapper.toDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public Category getCategoryById(int categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new APIException(HttpStatus.NOT_FOUND, String.format(
                        "Category with id=%s was not found",
                        categoryId
                ), "The required object was not found."));
    }

    @Override
    @Transactional
    public void delete(int categoryId) {
        Category category = getCategoryById(categoryId);
        categoryRepository.delete(category);
    }

    @Override
    @Transactional
    public CategoryDTO update(int categoryId, CreateCategoryDTO category) {
        Category fromDb = getCategoryById(categoryId);
        fromDb.setName(category.getName());
        return categoryMapper.toDto(fromDb);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDTO> getAll(int from, int size) {
        Pageable pageable = new OffsetBasedPageRequest(from, size);
        return categoryRepository.findAll(pageable).getContent().stream()
                .map(categoryMapper::toDto).collect(Collectors.toList());
    }
}