package ru.practicum.mainservice.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.mainservice.enums.StatusRequest;

import java.time.LocalDateTime;

@Data
public class RequestDTO {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    private int event;
    private int id;
    private int requester;
    private StatusRequest status;
}
