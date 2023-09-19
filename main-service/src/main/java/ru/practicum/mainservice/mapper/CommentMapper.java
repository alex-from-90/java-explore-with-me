package ru.practicum.mainservice.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.mainservice.dto.comment.CommentDTO;
import ru.practicum.mainservice.model.Comment;

@Component
@RequiredArgsConstructor
public class CommentMapper {

    private final UserMapper userMapper;
    private final EventMapper eventMapper;

    public CommentDTO toDto(Comment comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthor(userMapper.toDto(comment.getAuthor()));
        dto.setEvent(eventMapper.toShortDto(comment.getEvent()));
        dto.setCreateDate(comment.getCreated());
        return dto;
    }
}
