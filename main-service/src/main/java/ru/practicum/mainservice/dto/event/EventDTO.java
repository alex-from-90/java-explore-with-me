package ru.practicum.mainservice.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.mainservice.dto.category.CategoryDTO;
import ru.practicum.mainservice.dto.location.LocationDTO;
import ru.practicum.mainservice.dto.user.UserDTO;
import ru.practicum.mainservice.enums.EventState;

import java.time.LocalDateTime;

@Data
public class EventDTO {
    private int id;
    private String annotation;
    private CategoryDTO category;
    private int confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdOn;
    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;
    private UserDTO initiator;
    private LocationDTO location;
    private boolean paid;
    private int participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime publishedOn;
    private boolean requestModeration;
    private EventState state;
    private String title;
    private int views;
}
