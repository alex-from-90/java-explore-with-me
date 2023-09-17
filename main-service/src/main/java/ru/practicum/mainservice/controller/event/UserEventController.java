package ru.practicum.mainservice.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.event.CreateEventDTO;
import ru.practicum.mainservice.dto.event.EventDTO;
import ru.practicum.mainservice.dto.event.ShortEventDTO;
import ru.practicum.mainservice.dto.event.UpdateEventDTO;
import ru.practicum.mainservice.dto.filter.PageFilterDTO;
import ru.practicum.mainservice.dto.request.RequestDTO;
import ru.practicum.mainservice.dto.request.UpdateRequestDTO;
import ru.practicum.mainservice.dto.request.UpdateRequestResultDTO;
import ru.practicum.mainservice.service.EventService;
import ru.practicum.mainservice.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class UserEventController {

    private final EventService eventService;
    private final RequestService requestService;

    @GetMapping
    public List<ShortEventDTO> getEvents(@PathVariable @PositiveOrZero int userId, @Valid PageFilterDTO pageableData) {
        log.info("Запрос на получение событий пользователя id={} pageable={}", userId, pageableData);
        List<ShortEventDTO> events = eventService.getAll(userId, pageableData.getFrom(), pageableData.getSize());
        log.info("Найдено {} событий для id={} pageable={}", events.size(), userId, pageableData);
        return events;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDTO createEvent(@PathVariable @PositiveOrZero int userId, @RequestBody @Valid CreateEventDTO dto) {
        log.info("Получен запрос на создание мероприятия userId={} data={}", userId, dto);
        EventDTO event = eventService.createEvent(userId, dto);
        log.info("Мероприятие {} успешно создано", event);
        return event;
    }

    @GetMapping("/{eventId}")
    public EventDTO getUserEventById(@PathVariable @PositiveOrZero int userId, @PathVariable @PositiveOrZero int eventId) {
        log.info("Запрос на получение события eventId={}, userId={}", eventId, userId);
        EventDTO event = eventService.getByInitiatorAndId(userId, eventId);
        log.info("Событие по запросу eventId={}, userId={} = {}", eventId, userId, event);
        return event;
    }

    @PatchMapping("/{eventId}")
    public EventDTO updateUserEvent(
            @PathVariable @PositiveOrZero int userId,
            @PathVariable @PositiveOrZero int eventId,
            @RequestBody @Valid UpdateEventDTO dto
    ) {
        log.info("Запрос на редактирование события eventId={}, userId={}, data={}", eventId, userId, dto);
        EventDTO event = eventService.updateEvent(userId, eventId, dto);
        log.info("Событие eventId={}, userId={} успешно отредактировано data={}", eventId, userId, event);
        return event;
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDTO> getEventRequests(
            @PathVariable @PositiveOrZero int userId,
            @PathVariable @PositiveOrZero int eventId
    ) {
        log.info("Получен запрос на получение списка заявок userId={}, eventId={}", userId, eventId);
        List<RequestDTO> requests = requestService.getUserEventRequests(userId, eventId);
        log.info("Найдено запросов {} userId={}, eventId={}", requests.size(), userId, eventId);
        return requests;
    }

    @PatchMapping("/{eventId}/requests")
    public UpdateRequestResultDTO updateEventRequests(
            @PathVariable @PositiveOrZero int userId,
            @PathVariable @PositiveOrZero int eventId,
            @RequestBody @Valid UpdateRequestDTO dto
    ) {
        log.info("Получен запрос на обновление заявок userId={}, eventId={}, data={}", userId, eventId, dto);
        UpdateRequestResultDTO result = requestService.updateRequests(userId, eventId, dto);
        log.info("Результат обновления заявок {}", result);
        return result;
    }
}