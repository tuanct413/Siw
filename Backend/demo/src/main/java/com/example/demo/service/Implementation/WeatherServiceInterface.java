package com.example.demo.service.Implementation;

import com.example.demo.DTO.LocationRequest;
import com.example.demo.DTO.WeatherSummary;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface WeatherServiceInterface {
    public WeatherSummary getWeather(String city);
    ResponseEntity<Map<String, Object>> createHistoryWeather(LocationRequest request);
    ResponseEntity<Map<String, Object>> getallhistory(Long userId);
    Map<String, Object> deleteWeather(Long id,Long userId);


}
