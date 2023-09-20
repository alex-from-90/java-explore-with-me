package ru.practicum.mainservice.dto.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CreateCommentDTO {
    @NotBlank
    @Size(min = 5, max = 1000)
    private String text;
}
