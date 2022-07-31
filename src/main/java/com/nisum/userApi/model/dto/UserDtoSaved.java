package com.nisum.userApi.model.dto;

import com.nisum.userApi.model.entity.User;
import lombok.Data;

import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@Data
public class UserDtoSaved {
    private UUID id;
    private String created;
    private String modified;
    private String lastLogin;
    private String token;
    private Boolean isActive;

    public UserDtoSaved() {

    }

    public UserDtoSaved(UUID id, String created, String modified, String lastLogin, String token, Boolean isActive) {
        this.id = id;
        this.created = created;
        this.modified = modified;
        this.lastLogin = lastLogin;
        this.token = token;
        this.isActive = isActive;
    }

    public UserDtoSaved(User user) {
        this.id = user.getId();
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
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserDtoSaved that = (UserDtoSaved) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
