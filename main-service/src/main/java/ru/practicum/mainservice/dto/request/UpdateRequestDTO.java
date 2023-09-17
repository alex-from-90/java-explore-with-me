package ru.practicum.mainservice.dto.request;

import lombok.Data;
import ru.practicum.mainservice.enums.StatusRequest;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UpdateRequestDTO {
    private List<Integer> requestIds;
    @NotNull
    private StatusRequest status;
}
