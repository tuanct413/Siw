package com.example.demo.service;

import com.example.demo.DTO.WeatherSummary;
import com.example.demo.Implementation.WeatherServiceImp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WeatherService implements WeatherServiceImp {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Override
    public WeatherSummary getWeather(String city) {
        String url = apiUrl + "?key=" + apiKey + "&q=" + city + "&aqi=no";

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null) {
            throw new RuntimeException("Không lấy được dữ liệu thời tiết");
        }

        Map<String, Object> location = (Map<String, Object>) response.get("location");
        Map<String, Object> current = (Map<String, Object>) response.get("current");
        Map<String, Object> condition = (Map<String, Object>) current.get("condition");

        WeatherSummary summary = new WeatherSummary();
        summary.setCity((String) location.get("name"));
        summary.setTemperatureC(((Number) current.get("temp_c")).doubleValue());
        summary.setCondition((String) condition.get("text"));
        summary.setHumidity(((Number) current.get("humidity")).intValue());
        summary.setWindKph(((Number) current.get("wind_kph")).doubleValue());
        summary.setVisibilityKm(((Number) current.get("vis_km")).doubleValue());
        summary.setUvIndex(((Number) current.get("uv")).intValue());

        return summary;
    }

}
