package ru.practicum.mainservice.dto;

import lombok.Data;

@Data
public class StatDTO {
    private String app;
    private String uri;
    private Integer hits;
}
