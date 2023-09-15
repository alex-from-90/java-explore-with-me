package ru.practicum.mainservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.mainservice.dto.compilation.CompilationDTO;
import ru.practicum.mainservice.dto.compilation.CreateCompilationDTO;
import ru.practicum.mainservice.dto.compilation.UpdateCompilationDTO;
import ru.practicum.mainservice.model.Compilation;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {

    private final EventMapper eventMapper;

    public Compilation fromDto(CreateCompilationDTO dto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(dto.getPinned());
        compilation.setTitle(dto.getTitle());
        return compilation;
    }

    public CompilationDTO toDto(Compilation compilation) {
        CompilationDTO dto = new CompilationDTO();
        dto.setId(compilation.getId());
        dto.setEvents(compilation.getEvents().stream().map(eventMapper::toShortDto).collect(Collectors.toList()));
        dto.setPinned(compilation.isPinned());
        dto.setTitle(compilation.getTitle());
        return dto;
    }

    public Compilation fromDto(UpdateCompilationDTO dto) {
        Compilation compilation = new Compilation();
        compilation.setPinned(dto.getPinned());
        compilation.setTitle(dto.getTitle());
        return compilation;
    }
}
