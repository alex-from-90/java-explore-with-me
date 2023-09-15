package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.enums.EventState;
import ru.practicum.mainservice.enums.StatusRequest;
import ru.practicum.mainservice.exception.ConflictException;
import ru.practicum.mainservice.exception.NotFoundException;
import ru.practicum.mainservice.model.Event;
import ru.practicum.mainservice.model.Request;
import ru.practicum.mainservice.model.User;
import ru.practicum.mainservice.repository.RequestRepository;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final EventService eventService;
    private final UserService userService;

    @Override
    @Transactional(readOnly = true)
    public Request getRequestById(int requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Request with id=%s was not found", requestId)));
    }

    @Override
    @Transactional
    public Request createRequest(int userId, int eventId) {
        Event event = eventService.getById(eventId);
        if (!EventState.PUBLISHED.equals(event.getState()))
            throw new ConflictException("Event not published");
        if (event.getInitiator().getId().equals(userId))
            throw new ConflictException("initiator cant create request");
        if (event.getParticipantLimit() != 0) {
            long eventRequests = requestRepository.getEventRequestCountByStatus(eventId, StatusRequest.CONFIRMED);
            if (event.getParticipantLimit() <= eventRequests)
                throw new ConflictException("event participant limit");
        }
        User requester = userService.getUserById(userId);
        if (requestRepository.countByEventAndRequesterAndStatusIn(event, requester, Arrays.asList(StatusRequest.CONFIRMED, StatusRequest.PENDING)) > 0)
            throw new ConflictException("User already has pending request");
        Request request = new Request();
        if (event.getRequestModeration() && event.getParticipantLimit() != 0) {
            request.setStatus(StatusRequest.PENDING);
        } else {
            request.setStatus(StatusRequest.CONFIRMED);
        }
        request.setEvent(event);
        request.setRequester(requester);
        return requestRepository.save(request);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> getUserRequests(int userId) {
        User requester = userService.getUserById(userId);
        return requestRepository.findAllByRequester(requester);
    }

    @Override
    @Transactional
    public Request cancelRequest(int userId, int requestId) {
        Request request = getRequestById(requestId);
        request.setStatus(StatusRequest.CANCELED);
        return request;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Request> getUserEventRequests(int userId, int eventId) {
        Event event = eventService.getById(eventId);
        return requestRepository.findAllByEvent(event);
    }

    @Override
    @Transactional
    public List<Request> updateRequests(int userId, int eventId, List<Integer> requestIds, StatusRequest status) {
        Event event = eventService.getById(eventId);
        long eventConfirmedRequests = requestRepository.getEventRequestCountByStatus(eventId, StatusRequest.CONFIRMED);
        if (
                StatusRequest.CONFIRMED.equals(status) && event.getParticipantLimit() > 0
                        && event.getParticipantLimit() <= eventConfirmedRequests
        )
            throw new ConflictException("The participant limit has been reached");
        List<Request> requests = requestRepository.findAllById(requestIds);
        if (requests.stream().anyMatch(request -> !request.getStatus().equals(StatusRequest.PENDING)))
            throw new ConflictException("Request must have status PENDING");
        for (Request request : requests) {
            if (
                    StatusRequest.CONFIRMED.equals(status)
                            && (event.getParticipantLimit() < 1 || event.getParticipantLimit() > eventConfirmedRequests++)
            )
                request.setStatus(StatusRequest.CONFIRMED);
            else
                request.setStatus(StatusRequest.REJECTED);
        }
        return requests;
    }
}
