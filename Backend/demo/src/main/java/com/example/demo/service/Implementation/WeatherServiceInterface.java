package com.example.demo.service.Implementation;

import com.example.demo.DTO.ApiResponse;
import com.example.demo.DTO.LocationRequest;
import com.example.demo.DTO.WeatherForecastDTO;
import com.example.demo.DTO.WeatherSummary;
import org.springframework.http.ResponseEntity;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public interface WeatherServiceInterface {
    WeatherSummary getWeather(String city);
    ResponseEntity<Map<String, Object>> createHistoryWeather(LocationRequest request);
    ResponseEntity<Map<String, Object>> getallhistory(Long userId);
    Map<String, Object> deleteWeather(Long id,Long userId);
    List<WeatherForecastDTO> get7dayForecast(String city);
    ApiResponse<List<LocationRequest>> getFavoriteLocations(Long userId);
    ApiResponse<Map<String, Object>> setWeatherAlert(Long userId, String city, String condition);
    ApiResponse<Map<String, Object>> compareWeather(String city1, String city2);
}
