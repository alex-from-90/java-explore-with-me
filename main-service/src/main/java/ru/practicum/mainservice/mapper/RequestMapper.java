package ru.practicum.mainservice.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.mainservice.dto.request.RequestDTO;
import ru.practicum.mainservice.model.Request;

@Component
public class RequestMapper {
    public RequestDTO toDto(Request request) {
        RequestDTO dto = new RequestDTO();
        dto.setId(request.getId());
        dto.setRequester(request.getRequester().getId());
        dto.setCreated(request.getCreated());
        dto.setStatus(request.getStatus());
        dto.setEvent(request.getEvent().getId());
        return dto;
    }
}
