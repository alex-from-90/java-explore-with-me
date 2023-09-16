package ru.practicum.mainservice.service;

import ru.practicum.mainservice.dto.request.RequestDTO;
import ru.practicum.mainservice.dto.request.UpdateRequestDTO;
import ru.practicum.mainservice.dto.request.UpdateRequestResultDTO;
import ru.practicum.mainservice.model.Request;

import java.util.List;

public interface RequestService {
    Request getRequestById(int requestId);

    RequestDTO createRequest(int userId, int eventId);

    List<RequestDTO> getUserRequests(int userId);

    RequestDTO cancelRequest(int userId, int requestId);

    List<RequestDTO> getUserEventRequests(int userId, int eventId);

    UpdateRequestResultDTO updateRequests(int userId, int eventId, UpdateRequestDTO dto);
}