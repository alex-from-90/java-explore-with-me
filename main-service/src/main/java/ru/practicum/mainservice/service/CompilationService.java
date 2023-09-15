package ru.practicum.mainservice.service;

import ru.practicum.mainservice.model.Compilation;

import java.util.List;

public interface CompilationService {
    Compilation createCompilation(List<Integer> eventIds, Compilation compilation);

    Compilation getCompilationById(int compilationId);

    void deleteCompilation(int compilationId);

    Compilation updateCompilation(int compilationId, List<Integer> eventIds, Compilation compilation);

    List<Compilation> getCompilations(Boolean pinned, int from, int size);
}
