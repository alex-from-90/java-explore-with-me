package ru.practicum.mainservice.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.mainservice.dto.ErrorResponseDTO;

@SuppressWarnings("unused")
@Slf4j
@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handle(Exception e) {
        log.warn("handle exception", e);
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponseDTO dto = new ErrorResponseDTO(
                status.name(),
                status.name(),
                e.getMessage()
        );
        return new ResponseEntity<>(dto, status);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<ErrorResponseDTO> handle(APIException e) {
        log.warn("handle exception", e);
        ErrorResponseDTO dto = new ErrorResponseDTO(
                e.getStatus().name(),
                e.getReason(),
                e.getMessage()
        );
        return new ResponseEntity<>(dto, e.getStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handle(MethodArgumentNotValidException e) {
        log.warn("handle exception", e);
        FieldError fieldError = e.getBindingResult().getFieldError();
        ErrorResponseDTO dto = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.name(),
                "Integrity constraint has been violated",
                fieldError == null ? "unknown error" : fieldError.getField() + " " + fieldError.getDefaultMessage()
        );
        return new ResponseEntity<>(dto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponseDTO> handlerDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.warn("handle exception", e);
        ErrorResponseDTO dto = new ErrorResponseDTO(
                HttpStatus.CONFLICT.name(),
                "Integrity constraint has been violated",
                e.getMessage()
        );
        return new ResponseEntity<>(dto, HttpStatus.CONFLICT);
    }

}
