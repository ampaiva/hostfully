package com.ampaiva.hostfully.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PatchException extends RuntimeException {

    public PatchException(String message) {
        super(message);
    }
}
