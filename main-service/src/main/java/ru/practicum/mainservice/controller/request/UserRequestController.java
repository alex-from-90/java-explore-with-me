package ru.practicum.mainservice.controller.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.request.RequestDTO;
import ru.practicum.mainservice.service.RequestService;

import javax.validation.constraints.PositiveOrZero;
import javax.websocket.server.PathParam;
import java.util.List;

@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/users/{userId}/requests")
@RequiredArgsConstructor
@Validated
public class UserRequestController {

    private final RequestService requestService;

    @GetMapping
    public List<RequestDTO> getUserRequests(@PathVariable @PositiveOrZero int userId) {
        log.info("Запрос на получение заявок пользователя userId={}", userId);
        List<RequestDTO> requests = requestService.getUserRequests(userId);
        log.info("Найдено {} заявок для пользователя userId={}", requests.size(), userId);
        return requests;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDTO createRequest(
            @PathVariable @PositiveOrZero int userId,
            @PathParam(value = "eventId") @PositiveOrZero int eventId
    ) {
        log.info("Запрос на создание заявки на участие в событии userId={}, eventId={}", userId, eventId);
        RequestDTO request = requestService.createRequest(userId, eventId);
        log.info("Заявка на участие в событии userId={}, eventId={}, request={}", userId, eventId, request);
        return request;
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDTO cancelRequest(
            @PathVariable @PositiveOrZero int userId,
            @PathVariable @PositiveOrZero int requestId
    ) {
        log.info("Запрос на отмену заявки userId={}, requestId={}", userId, requestId);
        RequestDTO request = requestService.cancelRequest(userId, requestId);
        log.info("Запрос на участие отменен userId={}, requestId={}, request={}", userId, requestId, request);
        return request;
    }
}
