package ru.practicum.mainservice.model;

import lombok.Data;
import ru.practicum.mainservice.enums.EventState;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AdminEventFilter {
    private List<Integer> users;
    private List<EventState> states;
    private List<Integer> categories;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private int from;
    private int size;
}
