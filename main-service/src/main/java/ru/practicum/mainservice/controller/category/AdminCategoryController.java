package ru.practicum.mainservice.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.category.CategoryDTO;
import ru.practicum.mainservice.dto.category.CreateCategoryDTO;
import ru.practicum.mainservice.mapper.CategoryMapper;
import ru.practicum.mainservice.model.Category;
import ru.practicum.mainservice.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Validated
public class AdminCategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO createCategory(@RequestBody @Valid CreateCategoryDTO dto) {
        log.info("Получен запрос на создание категории {}", dto);
        Category category = categoryService.create(categoryMapper.toModel(dto));
        log.info("Сатегория id={} успешно создана", category.getId());
        return categoryMapper.toDto(category);
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable @PositiveOrZero int categoryId) {
        log.info("Получен запрос на удаление категории id={}", categoryId);
        categoryService.delete(categoryId);
        log.info("Категория id={} успешно удалено", categoryId);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDTO updateCategory(@PathVariable @PositiveOrZero int categoryId, @RequestBody @Valid CreateCategoryDTO dto) {
        log.info("Получен запрос на редактирование категории id={} data={}", categoryId, dto);
        Category category = categoryMapper.toModel(dto);
        category = categoryService.update(categoryId, category);
        log.info("Категория id={} успешно изменена data={}", categoryId, category);
        return categoryMapper.toDto(category);
    }
}
