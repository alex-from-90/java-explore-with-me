package ru.practicum.mainservice.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.event.EventDTO;
import ru.practicum.mainservice.dto.event.UpdateEventDTO;
import ru.practicum.mainservice.dto.filter.AdminEventFilterDTO;
import ru.practicum.mainservice.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public List<EventDTO> getEvents(@Valid AdminEventFilterDTO eventFilter) {
        log.info("Поиск событий по фильтру {}, from={}, size={}", eventFilter, eventFilter.getFrom(), eventFilter.getSize());
        List<EventDTO> events = eventService.findEvents(eventFilter);
        log.info("Найдено {} событий", events.size());
        return events;
    }

    @PatchMapping("/{eventId}")
    public EventDTO editEventById(@PathVariable @PositiveOrZero int eventId, @RequestBody @Valid UpdateEventDTO dto) {
        log.info("Запрос на редактирование события eventId={}, data={}", eventId, dto);
        return eventService.updateAdminEvent(eventId, dto);
    }
}
