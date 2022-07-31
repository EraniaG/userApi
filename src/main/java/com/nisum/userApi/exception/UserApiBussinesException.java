package com.nisum.userApi.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class UserApiBussinesException extends RuntimeException {
    private HttpStatus status;

    public UserApiBussinesException(String message) {
        super(message);
    }

    public UserApiBussinesException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
