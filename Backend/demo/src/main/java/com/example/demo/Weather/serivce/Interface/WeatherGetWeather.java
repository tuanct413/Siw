package com.example.demo.Weather.serivce.Interface;

import com.example.demo.Weather.DTO.WeatherSummary;

public interface  WeatherGetWeather {
    WeatherSummary getWeather(String city);
}
