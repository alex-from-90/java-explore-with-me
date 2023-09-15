package ru.practicum.mainservice.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends APIException {
    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message, "For the requested operation the conditions are not met.");
    }
}
