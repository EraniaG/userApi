package com.nisum.userApi.model.dto;

import com.nisum.userApi.model.entity.Phone;
import lombok.Data;

import javax.persistence.Column;

@Data
public class PhoneDto {
    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "citycode", nullable = false)
    private String citycode;

    @Column(name = "countrycode", nullable = false)
    private String countrycode;

    public PhoneDto() {

    }
    public PhoneDto(Phone phone) {
        this.number = phone.getNumber();
        this.citycode = phone.getCitycode();
        this.countrycode = phone.getCitycode();
    }
}
