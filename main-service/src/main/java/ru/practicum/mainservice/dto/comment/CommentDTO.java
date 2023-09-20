package ru.practicum.mainservice.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import ru.practicum.mainservice.dto.event.ShortEventDTO;
import ru.practicum.mainservice.dto.user.UserDTO;

import java.time.LocalDateTime;

@Data
public class CommentDTO {
    private Integer id;
    private String text;
    private UserDTO author;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    private ShortEventDTO event;
}
