package com.org.test.exception;

import com.org.test.exception.custom.Errors;
import com.org.test.exception.custom.InvalidInputException;
import com.org.test.exception.custom.ResourceNotFoundException;
import io.micrometer.tracing.Tracer;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Tracer tracer;

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Errors> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(buildErrors(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(InvalidInputException.class)
    public ResponseEntity<Errors> handleDuplicateData(InvalidInputException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(buildErrors(HttpStatus.CONFLICT, ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Errors> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(buildErrors(HttpStatus.PAYLOAD_TOO_LARGE, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Errors> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<String> errors = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.add(fieldError.getField().concat(" ").concat(fieldError.getDefaultMessage()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(buildErrors(HttpStatus.BAD_REQUEST, String.join(", ", errors)));
    }

    private Errors buildErrors(HttpStatus httpStatus, String description) {
        return Errors.builder()
                .errorCode(String.valueOf(httpStatus.value()))
                .errorMessage(httpStatus.getReasonPhrase())
                .errorDescription(description)
                .traceId(tracer.currentSpan().context().traceId())
                .build();
    }
}
