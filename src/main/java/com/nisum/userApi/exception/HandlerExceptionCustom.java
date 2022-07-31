package com.nisum.userApi.exception;

import com.nisum.userApi.model.dto.ErrorDto;
import io.jsonwebtoken.SignatureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class HandlerExceptionCustom {

    @ExceptionHandler({UserApiBussinesException.class})
    public ResponseEntity<Object> handleExceptionBussiness(UserApiBussinesException e) {
        return new ResponseEntity<>(new ErrorDto(e.getMessage(), e.getStatus()), new HttpHeaders(), e.getStatus());
    }

    @ExceptionHandler({UsernameNotFoundException.class})
    public ResponseEntity<Object> handleExceptionUserNotFound(UsernameNotFoundException e) {
        return new ResponseEntity<>(new ErrorDto(e.getMessage(), HttpStatus.NOT_FOUND), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({SignatureException.class})
    public ResponseEntity<Object> handleExceptionTokenInvalid(SignatureException e) {
        return new ResponseEntity<>(new ErrorDto(e.getMessage(), HttpStatus.NOT_FOUND), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

}