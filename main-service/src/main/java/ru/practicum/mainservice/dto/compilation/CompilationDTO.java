package ru.practicum.mainservice.dto.compilation;

import lombok.Data;
import ru.practicum.mainservice.dto.event.ShortEventDTO;

import java.util.List;

@Data
public class CompilationDTO {
    private int id;
    private List<ShortEventDTO> events;
    private boolean pinned;
    private String title;
}
