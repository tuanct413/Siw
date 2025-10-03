package com.example.demo.Weather.serivce.Interface;

import com.example.demo.Weather.DTO.WeatherInOneDay;

import java.util.List;

public interface WeatherInOneDayInterface {
    List<WeatherInOneDay> getAllWeatherInOneday (String city);
}
