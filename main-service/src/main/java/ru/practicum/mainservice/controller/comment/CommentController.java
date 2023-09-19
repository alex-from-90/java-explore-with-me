package ru.practicum.mainservice.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.mainservice.dto.comment.CommentDTO;
import ru.practicum.mainservice.dto.filter.PageFilterDTO;
import ru.practicum.mainservice.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/events/{eventId}/comments")
@RequiredArgsConstructor
@Validated
public class CommentController {
    private final CommentService commentService;

    @GetMapping
    public List<CommentDTO> getAllCommentsForEvent(
            @PathVariable @PositiveOrZero Integer eventId,
            @Valid PageFilterDTO pageFilter
    ) {
        log.info("Запрос на получение всех комментариев о событии eventId={}", eventId);
        return commentService.getAllCommentsByEvent(eventId, pageFilter);
    }
}