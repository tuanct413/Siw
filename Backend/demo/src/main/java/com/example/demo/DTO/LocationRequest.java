package com.example.demo.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class LocationRequest {
    @NotBlank(message = "Tên city_name không được để trống")
    private String cityName;

    @NotBlank(message = "Country không được để trống")
    private String country;

    @NotNull(message = "UserId không được để trống")
    private Long userId;


    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }



}
