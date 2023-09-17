package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.dto.request.RequestDTO;
import ru.practicum.mainservice.dto.request.UpdateRequestDTO;
import ru.practicum.mainservice.dto.request.UpdateRequestResultDTO;
import ru.practicum.mainservice.enums.EventState;
import ru.practicum.mainservice.enums.StatusRequest;
import ru.practicum.mainservice.exception.APIException;
import ru.practicum.mainservice.mapper.RequestMapper;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.Request;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.RequestRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventService eventService;
    private final UserService userService;
    private final RequestMapper requestMapper;

    @Override
    @Transactional(readOnly = true)
    public Request getRequestById(int requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new APIException(
                        HttpStatus.NOT_FOUND,
                        String.format("Request with id=%s was not found", requestId),
                        "The required object was not found."
                ));
    }

    @Override
    @Transactional
    public RequestDTO createRequest(int userId, int eventId) {
        Event event = eventService.getEventById(eventId);
        if (!EventState.PUBLISHED.equals(event.getState()))
            throw new APIException(
                    HttpStatus.CONFLICT,
                    "Event not published",
                    "For the requested operation the conditions are not met."
            );
        if (event.getInitiator().getId().equals(userId))
            throw new APIException(
                    HttpStatus.CONFLICT,
                    "initiator cant create request",
                    "For the requested operation the conditions are not met."
            );
        if (event.getParticipantLimit() != 0) {
            long eventRequests = requestRepository.getEventRequestCountByStatus(eventId, StatusRequest.CONFIRMED);
            if (event.getParticipantLimit() <= eventRequests)
                throw new APIException(
                        HttpStatus.CONFLICT,
                        "event participant limit",
                        "For the requested operation the conditions are not met."
                );
        }
        User requester = userService.getUserById(userId);
        if (requestRepository.countByEventAndRequesterAndStatusIn(event, requester, Arrays.asList(StatusRequest.CONFIRMED, StatusRequest.PENDING)) > 0)
            throw new APIException(
                    HttpStatus.CONFLICT,
                    "User already has pending request",
                    "For the requested operation the conditions are not met."
            );
        Request request = new Request();
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(StatusRequest.PENDING);
        } else {
            request.setStatus(StatusRequest.CONFIRMED);
        }
        request.setEvent(event);
        request.setRequester(requester);
        return requestMapper.toDto(requestRepository.save(request));
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDTO> getUserRequests(int userId) {
        User requester = userService.getUserById(userId);
        return requestRepository.findAllByRequester(requester).stream()
                .map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RequestDTO cancelRequest(int userId, int requestId) {
        Request request = getRequestById(requestId);
        request.setStatus(StatusRequest.CANCELED);
        return requestMapper.toDto(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RequestDTO> getUserEventRequests(int userId, int eventId) {
        Event event = eventService.getEventById(eventId);
        return requestRepository.findAllByEvent(event).stream().map(requestMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UpdateRequestResultDTO updateRequests(int userId, int eventId, UpdateRequestDTO dto) {
        Event event = eventService.getEventById(eventId);
        long eventConfirmedRequests = requestRepository.getEventRequestCountByStatus(eventId, StatusRequest.CONFIRMED);
        if (
                StatusRequest.CONFIRMED.equals(dto.getStatus()) && event.getParticipantLimit() > 0
                        && event.getParticipantLimit() <= eventConfirmedRequests
        )
            throw new APIException(
                    HttpStatus.CONFLICT,
                    "The participant limit has been reached",
                    "For the requested operation the conditions are not met."
            );
        List<Request> requests = requestRepository.findAllById(dto.getRequestIds());
        if (requests.stream().anyMatch(request -> !request.getStatus().equals(StatusRequest.PENDING)))
            throw new APIException(
                    HttpStatus.CONFLICT,
                    "Request must have status PENDING",
                    "For the requested operation the conditions are not met."
            );
        for (Request request : requests) {
            if (
                    StatusRequest.CONFIRMED.equals(dto.getStatus())
                            && (event.getParticipantLimit() < 1 || event.getParticipantLimit() > eventConfirmedRequests++)
            )
                request.setStatus(StatusRequest.CONFIRMED);
            else
                request.setStatus(StatusRequest.REJECTED);
        }
        UpdateRequestResultDTO.UpdateRequestResultDTOBuilder builder = UpdateRequestResultDTO.builder();
        for (Request request : requests) {
            if (StatusRequest.CONFIRMED.equals(request.getStatus()))
                builder.confirmedRequest(requestMapper.toDto(request));
            else
                builder.rejectedRequest(requestMapper.toDto(request));
        }
        return builder.build();
    }
}