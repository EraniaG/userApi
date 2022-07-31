package com.nisum.userApi.model.entity;

import com.nisum.userApi.model.dto.PhoneDto;
import lombok.Data;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "PHONE")
@Data
public class Phone {

    @Id
    @GeneratedValue
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User userByUUID;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "citycode", nullable = false)
    private String citycode;

    @Column(name = "countrycode", nullable = false)
    private String countrycode;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    public Phone() {

    }

    public Phone(User userByUUID, PhoneDto phoneDto) {
        this.userByUUID = userByUUID;
        this.number = phoneDto.getNumber();
        this.citycode = phoneDto.getCitycode();
        this.countrycode = phoneDto.getCountrycode();
        this.isActive = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phone phone = (Phone) o;
        return Objects.equals(number, phone.number) && Objects.equals(citycode, phone.citycode)
                && Objects.equals(countrycode, phone.countrycode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number, citycode, countrycode);
    }
}
