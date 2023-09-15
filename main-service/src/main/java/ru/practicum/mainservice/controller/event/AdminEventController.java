package ru.practicum.mainservice.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.event.EventDTO;
import ru.practicum.mainservice.dto.event.UpdateEventDTO;
import ru.practicum.mainservice.dto.filter.AdminEventFilterDTO;
import ru.practicum.mainservice.mapper.EventMapper;
import ru.practicum.mainservice.mapper.FilterMapper;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
public class AdminEventController {
    private final EventService eventService;
    private final EventMapper eventMapper;
    private final FilterMapper filterMapper;

    @GetMapping
    public List<EventDTO> getEvents(@Valid AdminEventFilterDTO eventFilter) {
        log.info("Поиск событий по фильтру {}, from={}, size={}", eventFilter, eventFilter.getFrom(), eventFilter.getSize());
        List<Event> events = eventService.findEvents(filterMapper.eventFilter(eventFilter));
        log.info("Найдено {} событий", events.size());
        Map<Integer, Integer> views = eventService.getViews(events);
        Map<Integer, Integer> confirmedRequests = eventService.getConfirmedRequests(events);
        return events.stream().map(event -> eventMapper.toDto(
                        event,
                        views.getOrDefault(event.getId(), 0),
                        confirmedRequests.getOrDefault(event.getId(), 0)
                ))
                .collect(Collectors.toList());
    }

    @PatchMapping("/{eventId}")
    public EventDTO editEventById(@PathVariable @PositiveOrZero int eventId, @RequestBody @Valid UpdateEventDTO dto) {
        log.info("Запрос на редактирование события eventId={}, data={}", eventId, dto);
        Event event = eventMapper.toModel(dto);
        event.setId(eventId);
        event = eventService.updateAdminEvent(dto.getCategory(), event, dto.getStateAction());
        Map<Integer, Integer> views = eventService.getViews(Collections.singletonList(event));
        Map<Integer, Integer> confirmedRequests = eventService.getConfirmedRequests(Collections.singletonList(event));
        return eventMapper.toDto(event, views.getOrDefault(eventId, 0), confirmedRequests.getOrDefault(eventId, 0));
    }
}
