package ru.practicum.mainservice.dto.filter;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class PageFilterDTO {
    @Min(0)
    private Integer from = 0;
    @Min(1)
    private Integer size = 10;
}
