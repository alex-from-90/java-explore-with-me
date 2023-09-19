package ru.practicum.mainservice.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.mainservice.dto.comment.CommentDTO;
import ru.practicum.mainservice.dto.comment.CreateCommentDTO;
import ru.practicum.mainservice.dto.filter.PageFilterDTO;
import ru.practicum.mainservice.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@SuppressWarnings("unused")
@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
public class UserCommentController {
    private final CommentService commentService;

    @PostMapping("/events/{eventId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDTO addComment(
            @RequestBody @Valid CreateCommentDTO dto,
            @PathVariable @PositiveOrZero Integer userId,
            @PathVariable @PositiveOrZero Integer eventId
    ) {
        log.info("Запрос на добавление комментария о событии eventId={} от пользователя userId={}, data={}", eventId, userId, dto);
        return commentService.addComment(dto, userId, eventId);
    }

    @PatchMapping("/{commentId}")
    public CommentDTO renewalComment(
            @RequestBody @Valid CreateCommentDTO dto,
            @PathVariable @PositiveOrZero Integer userId,
            @PathVariable @PositiveOrZero Integer commentId
    ) {
        log.info("Запрос на обновление комментария commentId={} от пользователя userId={}", commentId, userId);
        return commentService.editComment(dto, userId, commentId);
    }

    @GetMapping("/{commentId}")
    public CommentDTO getCommentById(
            @PathVariable @PositiveOrZero Integer userId,
            @PathVariable @PositiveOrZero Integer commentId
    ) {
        log.info("Запрос на получение комментария commentId={} от пользователя userId={}", commentId, userId);
        return commentService.getById(userId, commentId);
    }

    @GetMapping
    public List<CommentDTO> getAllUserComments(
            @PathVariable Integer userId,
            @Valid PageFilterDTO pageFilter
    ) {
        log.info("Запрос на списка комментариев от пользователя userId={}", userId);
        return commentService.getAllUserComments(userId, pageFilter.getFrom(), pageFilter.getSize());
    }

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCommentById(@PathVariable Integer userId, @PathVariable Integer commentId) {
        log.info("Запрос на удаление комментария commentId={} от пользователя userId={}", commentId, userId);
        commentService.deleteCommentById(userId, commentId);
    }
}
