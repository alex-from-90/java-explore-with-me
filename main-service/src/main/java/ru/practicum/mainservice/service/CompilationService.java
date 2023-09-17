package ru.practicum.mainservice.service;

import ru.practicum.mainservice.dto.compilation.CompilationDTO;
import ru.practicum.mainservice.dto.compilation.CreateCompilationDTO;
import ru.practicum.mainservice.dto.compilation.UpdateCompilationDTO;
import ru.practicum.mainservice.model.Compilation;

import java.util.List;

public interface CompilationService {
    CompilationDTO createCompilation(CreateCompilationDTO compilation);

    CompilationDTO getCompilationById(int compilationId);

    Compilation getById(int compilationId);

    void deleteCompilation(int compilationId);

    CompilationDTO updateCompilation(int compilationId, UpdateCompilationDTO compilation);

    List<CompilationDTO> getCompilations(Boolean pinned, int from, int size);
}
