package ru.practicum.mainservice.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.mainservice.dto.category.CategoryDTO;
import ru.practicum.mainservice.dto.user.UserDTO;

import java.time.LocalDateTime;

@Data
public class ShortEventDTO {
    private int id;
    private String annotation;
    private CategoryDTO category;
    private int confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserDTO initiator;
    private boolean paid;
    private String title;
    private int views;
}
