package ru.practicum.mainservice.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.compilation.CompilationDTO;
import ru.practicum.mainservice.dto.compilation.CreateCompilationDTO;
import ru.practicum.mainservice.dto.compilation.UpdateCompilationDTO;
import ru.practicum.mainservice.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
public class AdminCompilationController {

    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDTO createCompilation(@RequestBody @Valid CreateCompilationDTO dto) {
        log.info("Получен запрос на создание подборки data={}", dto);
        CompilationDTO compilation = compilationService.createCompilation(dto);
        log.info("Подборка успешно создана data={}", compilation);
        return compilation;
    }

    @DeleteMapping("/{compilationId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable @PositiveOrZero int compilationId) {
        compilationService.deleteCompilation(compilationId);
    }

    @PatchMapping("/{compId}")
    public CompilationDTO updateCompilation(
            @PathVariable @PositiveOrZero int compId,
            @RequestBody(required = false) @Valid UpdateCompilationDTO dto
    ) {
        log.info("Получен запрос на изменение подборки data={}", dto);
        CompilationDTO compilation = compilationService.updateCompilation(compId, dto);
        log.info("Подборка успешно изменена data={}", compilation);
        return compilation;
    }
}
