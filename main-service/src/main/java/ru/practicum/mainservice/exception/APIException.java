package ru.practicum.mainservice.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class APIException extends RuntimeException {
    protected HttpStatus status;
    protected String reason;

    public APIException(HttpStatus status, String message, String reason) {
        super(message);
        this.status = status;
        this.reason = reason;
    }
}
