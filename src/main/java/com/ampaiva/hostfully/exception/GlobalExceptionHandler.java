package com.ampaiva.hostfully.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static HttpStatus getResponseStatusValue(Class<? extends Throwable> exceptionClass) {
        ResponseStatus responseStatus = exceptionClass.getAnnotation(ResponseStatus.class);

        if (responseStatus != null) {
            return HttpStatus.valueOf(responseStatus.value().value());
        } else {
            // Default to internal server error if @ResponseStatus is not present
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    private ResponseEntity<Object> handleBadRequest(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return handleBadRequest(ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleRuntimeException(Exception ex) {
        return new ResponseEntity<>(ex.getMessage(), getResponseStatusValue(ex.getClass()));
    }
}
