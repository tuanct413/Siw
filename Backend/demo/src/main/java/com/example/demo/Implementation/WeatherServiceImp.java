package com.example.demo.Implementation;

import com.example.demo.DTO.LocationRequest;
import com.example.demo.DTO.WeatherSummary;
import com.example.demo.entity.Location;
import com.example.demo.entity.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import java.util.List;

import java.util.Map;

public interface WeatherServiceImp {
    public WeatherSummary getWeather(String city);
    ResponseEntity<Map<String, Object>> createHistoryWeather(LocationRequest request);
    ResponseEntity<Map<String, Object>> getallhistory(Long userId);


}
