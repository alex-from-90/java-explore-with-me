package ru.practicum.mainservice.exception;

import org.springframework.http.HttpStatus;

public class BadRequestException extends APIException {
    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message, "Incorrectly made request.");
    }
}
