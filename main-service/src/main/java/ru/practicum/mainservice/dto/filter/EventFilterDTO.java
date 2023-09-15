package ru.practicum.mainservice.dto.filter;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.mainservice.enums.EventSort;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class EventFilterDTO extends PageFilterDTO {
    private String text;
    private List<Integer> categories;
    private Boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private Boolean onlyAvailable;
    private EventSort sort;
}
