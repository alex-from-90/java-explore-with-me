package ru.practicum.mainservice.service;

import ru.practicum.mainservice.dto.comment.CommentDTO;
import ru.practicum.mainservice.dto.comment.CreateCommentDTO;
import ru.practicum.mainservice.dto.filter.PageFilterDTO;
import ru.practicum.mainservice.model.Comment;

import java.util.List;

public interface CommentService {
    CommentDTO addComment(CreateCommentDTO dto, Integer userId, Integer eventId);

    CommentDTO editComment(CreateCommentDTO dto, Integer userId, Integer commentId);

    CommentDTO getById(Integer userId, Integer commentId);

    Comment getCommentById(Integer commentId);

    List<CommentDTO> getAllUserComments(Integer userId, Integer from, Integer size);

    void deleteCommentById(Integer userId, Integer commentId);

    CommentDTO editCommentAdmin(Integer commentId, CreateCommentDTO dto);

    void deleteCommentAdmin(Integer commentId);

    List<CommentDTO> getAllCommentsByEvent(Integer eventId, PageFilterDTO pageFilter);
}