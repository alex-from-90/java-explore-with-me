package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.Compilation;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.repository.CompilationRepository;
import ru.practicum.mainservice.util.OffsetBasedPageRequest;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    @Override
    @Transactional
    public Compilation createCompilation(List<Integer> eventIds, Compilation compilation) {
        List<Event> events = eventIds.isEmpty() ? Collections.emptyList() : eventService.findAllByIds(eventIds);
        Compilation newCompilation = new Compilation();
        newCompilation.setTitle(compilation.getTitle());
        newCompilation.setPinned(compilation.isPinned());
        newCompilation.setEvents(events);
        return compilationRepository.save(newCompilation);
    }

    @Override
    @Transactional(readOnly = true)
    public Compilation getCompilationById(int compilationId) {
        return compilationRepository.findById(compilationId)
                .orElseThrow(() -> new NotFoundException(String.format(
                        "Compilation with id=%s was not found", compilationId
                )));
    }

    @Override
    @Transactional
    public void deleteCompilation(int compilationId) {
        Compilation compilation = getCompilationById(compilationId);
        compilationRepository.delete(compilation);
    }

    @Override
    @Transactional
    public Compilation updateCompilation(int compilationId, List<Integer> eventIds, Compilation compilation) {
        Compilation compilationFromDB = getCompilationById(compilationId);
        List<Event> events = eventIds.isEmpty() ? Collections.emptyList() : eventService.findAllByIds(eventIds);
        compilationFromDB.setEvents(events);
        compilationFromDB.setTitle(compilation.getTitle());
        compilationFromDB.setPinned(compilation.isPinned());
        return compilationFromDB;
    }

    @Override
    public List<Compilation> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = new OffsetBasedPageRequest(from, size);
        if (Objects.nonNull(pinned))
            return compilationRepository.findAllByPinned(pinned, pageable);
        return compilationRepository.findAll(pageable).getContent();
    }
}
