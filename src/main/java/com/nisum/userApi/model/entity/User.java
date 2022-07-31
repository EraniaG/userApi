package com.nisum.userApi.model.entity;

import com.nisum.userApi.model.dto.UserDto;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "USER")
@Data
public class User {

    public User() {
    }

    public User(UUID id, LocalDateTime created, LocalDateTime modified, LocalDateTime lastLogin, String token, Boolean isActive) {
        this.id = id;
        this.created = created;
        this.modified = modified;
        this.lastLogin = lastLogin;
        this.token = token;
        this.isActive = isActive;
    }

    public User(UUID id, String name, String email, String password, LocalDateTime created, LocalDateTime modified,
                LocalDateTime lastLogin, String token, Boolean isActive, Set<Phone> phones) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.created = created;
        this.modified = modified;
        this.lastLogin = lastLogin;
        this.token = token;
        this.isActive = isActive;
        this.phones = phones;
    }

    public User(UserDto user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.token = user.getToken();
        this.isActive = true;
        this.phones = new HashSet<>();
        if (user.getPhones() != null) {
            user.getPhones().forEach(x -> phones.add(new Phone(this, x)));
        }
    }

    @Id
    @GenericGenerator(name = "uuid_user", strategy = "org.hibernate.id.UUIDGenerator")
    @GeneratedValue(generator = "uuid_user")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "created", nullable = false)
    private LocalDateTime created;

    @Column(name = "modified")
    private LocalDateTime modified;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "token")
    private String token;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @OneToMany(
            mappedBy = "userByUUID",
            cascade = CascadeType.ALL,
            fetch = FetchType.LAZY
    )
    @LazyCollection(LazyCollectionOption.FALSE)
    private Set<Phone> phones;

    @ManyToMany(
            cascade = CascadeType.ALL,
            fetch = FetchType.EAGER
    )
    @JoinTable(name = "USER_HAS_ROLE",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    )
    private Set<Role> roles;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id) && Objects.equals(name, user.name)
                && Objects.equals(email, user.email) && Objects.equals(password, user.password)
                && Objects.equals(token, user.token)
                && Objects.equals(isActive, user.isActive);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, email, password, created, modified, lastLogin, token, isActive);
    }
}
