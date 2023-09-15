package ru.practicum.mainservice.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends APIException {
    public NotFoundException(String message) {
        super(
                HttpStatus.NOT_FOUND,
                message,
                "The required object was not found."
        );
    }
}
