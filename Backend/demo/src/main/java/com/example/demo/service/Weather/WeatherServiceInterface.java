package com.example.demo.service.Weather;

import com.example.demo.DTO.ApiResponse;
import com.example.demo.DTO.LocationRequest;
import com.example.demo.DTO.WeatherForecastDTO;
import com.example.demo.DTO.WeatherSummary;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface WeatherServiceInterface {
    default  WeatherSummary getWeather(String city)
    {
        throw new UnsupportedOperationException("Not implemented");
    };
    default  ResponseEntity<Map<String, Object>> createHistoryWeather(LocationRequest request)
    {
        throw new UnsupportedOperationException("Not implemented");
    };
    default  ResponseEntity<Map<String, Object>> getallhistory(Long userId)
    {
        throw new UnsupportedOperationException("Not implemented");
    };
    default  Map<String, Object> deleteWeather(Long id,Long userId)
    {
        throw new UnsupportedOperationException("Not implemented");
    };
    default  List<WeatherForecastDTO> get7dayForecast(String city)
    {
        throw new UnsupportedOperationException("Not implemented");
    };
    default ApiResponse<List<LocationRequest>> getFavoriteLocations(Long userId)
    {
        throw new UnsupportedOperationException("Not implemented");
    };
    default  ApiResponse<Map<String, Object>> setWeatherAlert(Long userId, String city, String condition)
    {
        throw new UnsupportedOperationException("Not implemented");
    };
    default  ApiResponse<Map<String, Object>> compareWeather(String city1, String city2)
    {
        throw new UnsupportedOperationException("Not implemented");
    };
}
