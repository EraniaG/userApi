package com.nisum.userApi.model.dto;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class ErrorDto {
    private String mensaje;
    private HttpStatus status;

    public ErrorDto(String mensaje) {
        this.mensaje = mensaje;
    }

    public ErrorDto(String mensaje, HttpStatus status) {
        this.mensaje = mensaje;
        this.status = status;
    }
}
