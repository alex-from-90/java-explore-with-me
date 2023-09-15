package ru.practicum.mainservice.controller.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.compilation.CompilationDTO;
import ru.practicum.mainservice.dto.filter.PageFilterDTO;
import ru.practicum.mainservice.mapper.CompilationMapper;
import ru.practicum.mainservice.model.Compilation;
import ru.practicum.mainservice.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
public class CompilationController {
    private final CompilationService compilationService;
    private final CompilationMapper compilationMapper;

    @GetMapping
    public List<CompilationDTO> getCompilations(
            @Valid PageFilterDTO pageFilter,
            @RequestParam(required = false) Boolean pinned
    ) {
        log.info("Запрос на список подборок pinned={} page={}", pinned, pageFilter);
        List<Compilation> compilations = compilationService.getCompilations(
                pinned,
                pageFilter.getFrom(),
                pageFilter.getSize()
        );
        log.info("Найдено подборок {}", compilations.size());
        return compilations.stream().map(compilationMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/{compId}")
    public CompilationDTO getCompilationById(@PathVariable @PositiveOrZero int compId) {
        log.info("Запрос на получение подборки compId={}", compId);
        Compilation compilation = compilationService.getCompilationById(compId);
        log.info("Найдена подборка {}", compilation);
        return compilationMapper.toDto(compilation);
    }
}
