package com.nisum.userApi.model.dto;

import com.nisum.userApi.model.entity.User;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class UserDto {

    private UUID id;
    private String name;
    private String email;
    private String password;
    private String created;
    private String modified;
    private String lastLogin;
    private String token;
    private Boolean isActive;
    private List<PhoneDto> phones;

    public UserDto() {

    }

    public UserDto(String name, String email, String password, List<PhoneDto> phones, String token) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.token = token;
        this.phones = phones;
    }

    public UserDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.created = user.getCreated() == null
                ? ""
                : user.getCreated().format(DateTimeFormatter.ISO_DATE_TIME);
        this.modified = user.getModified() == null
                ? ""
                : user.getModified().format(DateTimeFormatter.ISO_DATE_TIME);
        this.lastLogin = user.getLastLogin() == null
                ? (user.getCreated() == null
                ? ""
                : user.getCreated().format(DateTimeFormatter.ISO_DATE_TIME))
                : user.getLastLogin().format(DateTimeFormatter.ISO_DATE_TIME);
        this.token = user.getToken();
        this.isActive = user.getIsActive();
        this.phones = new ArrayList<>();
        if (user.getPhones() != null) {
            user.getPhones().forEach(x -> phones.add(new PhoneDto(x)));
        }
    }
}
