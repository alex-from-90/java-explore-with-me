package ru.practicum.mainservice.dto.request;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

@Builder
@Data
public class UpdateRequestResultDTO {
    @Singular
    private List<RequestDTO> confirmedRequests;
    @Singular
    private List<RequestDTO> rejectedRequests;
}