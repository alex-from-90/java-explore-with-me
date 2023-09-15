package ru.practicum.mainservice.service;

import ru.practicum.mainservice.enums.StatusRequest;
import ru.practicum.mainservice.model.Request;

import java.util.List;

public interface RequestService {
    Request getRequestById(int requestId);

    Request createRequest(int userId, int eventId);

    List<Request> getUserRequests(int userId);

    Request cancelRequest(int userId, int requestId);

    List<Request> getUserEventRequests(int userId, int eventId);

    List<Request> updateRequests(int userId, int eventId, List<Integer> requestIds, StatusRequest status);
}
