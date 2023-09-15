package ru.practicum.mainservice.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.dto.event.EventDTO;
import ru.practicum.mainservice.dto.event.ShortEventDTO;
import ru.practicum.mainservice.dto.filter.EventFilterDTO;
import ru.practicum.mainservice.mapper.EventMapper;
import ru.practicum.mainservice.mapper.FilterMapper;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;
    private final FilterMapper filterMapper;

    @GetMapping
    public List<ShortEventDTO> getEvents(@Valid EventFilterDTO eventFilter, HttpServletRequest request) {
        log.info("Поиск событий по фильтру {}", eventFilter);
        eventService.addStatistic(request);
        List<Event> events = eventService.findEvents(filterMapper.eventFilter(eventFilter));
        log.info("Найдено {} событий", events.size());
        return events.stream().map(eventMapper::toShortDto).collect(Collectors.toList());
    }

    @GetMapping("/{eventId}")
    public EventDTO getEventById(@PathVariable @PositiveOrZero int eventId, HttpServletRequest request) {
        log.info("Получение публичного события по id={}", eventId);
        eventService.addStatistic(request);
        Event event = eventService.getPublishedEventById(eventId);
        log.info("Найдено событие {}", event);
        Map<Integer, Integer> views = eventService.getViews(Collections.singletonList(event));
        Map<Integer, Integer> confirmedRequests = eventService.getConfirmedRequests(Collections.singletonList(event));
        return eventMapper.toDto(event, views.getOrDefault(eventId, 0), confirmedRequests.getOrDefault(eventId, 0));
    }
}
