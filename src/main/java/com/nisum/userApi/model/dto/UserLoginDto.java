package com.nisum.userApi.model.dto;

import lombok.Data;

@Data
public class UserLoginDto {
    private String email;
    private String password;
}
