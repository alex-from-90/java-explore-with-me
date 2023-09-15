package ru.practicum.mainservice.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.category.CategoryDTO;
import ru.practicum.mainservice.dto.category.CreateCategoryDTO;
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

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDTO createCategory(@RequestBody @Valid CreateCategoryDTO dto) {
        log.info("Получен запрос на создание категории {}", dto);
        CategoryDTO category = categoryService.create(dto);
        log.info("Сатегория id={} успешно создана", category.getId());
        return category;
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
        CategoryDTO category = categoryService.update(categoryId, dto);
        log.info("Категория id={} успешно изменена data={}", categoryId, category);
        return category;
    }
}
