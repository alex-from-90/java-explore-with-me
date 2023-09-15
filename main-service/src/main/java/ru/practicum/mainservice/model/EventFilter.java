package ru.practicum.mainservice.model;

import lombok.Data;
import ru.practicum.mainservice.enums.EventSort;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class EventFilter {
    private String text;
    private List<Integer> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private EventSort sort;
    private int from;
    private int size;
}
