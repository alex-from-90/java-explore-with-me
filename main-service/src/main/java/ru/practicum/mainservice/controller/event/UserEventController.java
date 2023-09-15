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
import ru.practicum.mainservice.enums.StatusRequest;
import ru.practicum.mainservice.mapper.EventMapper;
import ru.practicum.mainservice.mapper.RequestMapper;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.Request;
import ru.practicum.mainservice.service.EventService;
import ru.practicum.mainservice.service.RequestService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Validated
public class UserEventController {

    private final EventService eventService;
    private final RequestService requestService;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;

    @GetMapping
    public List<ShortEventDTO> getEvents(@PathVariable @PositiveOrZero int userId, @Valid PageFilterDTO pageableData) {
        log.info("Запрос на получение событий пользователя id={} pageable={}", userId, pageableData);
        List<Event> events = eventService.getAll(userId, pageableData.getFrom(), pageableData.getSize());
        log.info("Найдено {} событий для id={} pageable={}", events.size(), userId, pageableData);
        return events.stream().map(eventMapper::toShortDto).collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventDTO createEvent(@PathVariable @PositiveOrZero int userId, @RequestBody @Valid CreateEventDTO dto) {
        log.info("Получен запрос на создание мероприятия userId={} data={}", userId, dto);
        Event event = eventMapper.toModel(dto);
        event = eventService.createEvent(userId, dto.getCategory(), event);
        log.info("Мероприятие {} успешно создано", event);
        return eventMapper.toDto(event, 0, 0);
    }

    @GetMapping("/{eventId}")
    public EventDTO getUserEventById(@PathVariable @PositiveOrZero int userId, @PathVariable @PositiveOrZero int eventId) {
        log.info("Запрос на получение события eventId={}, userId={}", eventId, userId);
        Event event = eventService.getByInitiatorAndId(userId, eventId);
        log.info("Событие по запросу eventId={}, userId={} = {}", eventId, userId, event);
        Map<Integer, Integer> views = eventService.getViews(Collections.singletonList(event));
        Map<Integer, Integer> confirmedRequests = eventService.getConfirmedRequests(Collections.singletonList(event));
        return eventMapper.toDto(event, views.getOrDefault(eventId, 0), confirmedRequests.getOrDefault(eventId, 0));
    }

    @PatchMapping("/{eventId}")
    public EventDTO updateUserEvent(
            @PathVariable @PositiveOrZero int userId,
            @PathVariable @PositiveOrZero int eventId,
            @RequestBody @Valid UpdateEventDTO dto
    ) {
        log.info("Запрос на редактирование события eventId={}, userId={}, data={}", eventId, userId, dto);
        Event event = eventMapper.toModel(dto);
        event.setId(eventId);
        event = eventService.updateEvent(userId, dto.getCategory(), event, dto.getStateAction());
        log.info("Событие eventId={}, userId={} успешно отредактировано data={}", eventId, userId, event);
        Map<Integer, Integer> views = eventService.getViews(Collections.singletonList(event));
        Map<Integer, Integer> confirmedRequests = eventService.getConfirmedRequests(Collections.singletonList(event));
        return eventMapper.toDto(event, views.getOrDefault(eventId, 0), confirmedRequests.getOrDefault(eventId, 0));
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDTO> getEventRequests(
            @PathVariable @PositiveOrZero int userId,
            @PathVariable @PositiveOrZero int eventId
    ) {
        log.info("Получен запрос на получение списка заявок userId={}, eventId={}", userId, eventId);
        List<Request> requests = requestService.getUserEventRequests(userId, eventId);
        log.info("Найдено запросов {} userId={}, eventId={}", requests.size(), userId, eventId);
        return requests.stream().map(requestMapper::toDto).collect(Collectors.toList());
    }

    @PatchMapping("/{eventId}/requests")
    public Map<String, List<RequestDTO>> updateEventRequests(
            @PathVariable @PositiveOrZero int userId,
            @PathVariable @PositiveOrZero int eventId,
            @RequestBody @Valid UpdateRequestDTO dto
    ) {
        log.info("Получен запрос на обновление заявок userId={}, eventId={}, data={}", userId, eventId, dto);
        List<Request> requests = requestService.updateRequests(userId, eventId, dto.getRequestIds(), dto.getStatus());
        Map<String, List<RequestDTO>> result = new HashMap<>(4);
        for (Request request : requests) {
            if (StatusRequest.CONFIRMED.equals(request.getStatus()))
                result.computeIfAbsent("confirmedRequests", key -> new LinkedList<>())
                        .add(requestMapper.toDto(request));
            else
                result.computeIfAbsent("rejectedRequests", key -> new LinkedList<>())
                        .add(requestMapper.toDto(request));
        }
        log.info("Результат обновления заявок {}", result);
        return result;
    }
}
