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
import ru.practicum.mainservice.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;

    @GetMapping
    public List<ShortEventDTO> getEvents(@Valid EventFilterDTO eventFilter, HttpServletRequest request) {
        log.info("Поиск событий по фильтру {}", eventFilter);
        eventService.addStatistic(request);
        List<ShortEventDTO> events = eventService.findEvents(eventFilter);
        log.info("Найдено {} событий", events.size());
        return events;
    }

    @GetMapping("/{eventId}")
    public EventDTO getEventById(@PathVariable @PositiveOrZero int eventId, HttpServletRequest request) {
        log.info("Получение публичного события по id={}", eventId);
        eventService.addStatistic(request);
        EventDTO event = eventService.getPublishedEventById(eventId);
        log.info("Найдено событие {}", event);
        return event;
    }
}
