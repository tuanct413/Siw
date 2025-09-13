package com.example.demo.service;

import com.example.demo.DTO.LocationRequest;
import com.example.demo.DTO.WeatherSummary;
import com.example.demo.Model.Location;
import com.example.demo.Model.User;
import com.example.demo.repository.LocationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.Implementation.WeatherServiceInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Service
public class WeatherService implements WeatherServiceInterface {

    private final RestTemplate restTemplate = new RestTemplate();

    private final LocationRepository  locationRepository;
    private UserRepository userRepository;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    public WeatherService(LocationRepository locationRepository  , UserRepository userRepository) {
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public WeatherSummary getWeather(String city) {
        String url = apiUrl + "?key=" + apiKey + "&q=" + city + "&aqi=no";

        try {
            return fetchWeather(url);
        } catch (Exception e) {
            // fallback về Hanoi
            String defaultCity = "hanoi";
            String fallbackUrl = apiUrl + "?key=" + apiKey + "&q=" + defaultCity + "&aqi=no";
            return fetchWeather(fallbackUrl);
        }
    }
    private WeatherSummary fetchWeather(String url) {
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null) {
            throw new RuntimeException("Không lấy được dữ liệu thời tiết");
        }

        Map<String, Object> location = (Map<String, Object>) response.get("location");
        Map<String, Object> current = (Map<String, Object>) response.get("current");
        Map<String, Object> condition = (Map<String, Object>) current.get("condition");


        WeatherSummary summary = new WeatherSummary();
        summary.setCity((String) location.get("name"));
        summary.setCountry((String)location.get("country"));
        summary.setTemperatureC(((Number) current.get("temp_c")).doubleValue());
        summary.setCondition((String) condition.get("text"));
        summary.setHumidity(((Number) current.get("humidity")).intValue());
        summary.setWindKph(((Number) current.get("wind_kph")).doubleValue());
        summary.setVisibilityKm(((Number) current.get("vis_km")).doubleValue());
        summary.setUvIndex(((Number) current.get("uv")).intValue());

        return summary;
    }

    @Override
    public ResponseEntity<Map<String, Object>> createHistoryWeather(LocationRequest request){
        Map<String,Object> response = new HashMap<>();


        Optional<User> optionalUser = userRepository.findById(request.getUserId());
        boolean exitcheckcoutry = locationRepository.existsByUserIdAndCityNameIgnoreCase(request.getUserId(), request.getCityName());
        if (optionalUser.isEmpty()) {
            response.put("message", "Không tồn tại User");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
        if (exitcheckcoutry){
            response.put("message", "Tỉnh thành đã tồn tại nên không lưu");
        }
        else {
            User user = optionalUser.get();
            Location location = new Location();
            location.setCreated_at(LocalDate.now());  // ngày hiện tại
            location.setCountry(request.getCountry());   // <-- lấy từ request
            location.setCityName(request.getCityName()); // <-- lấy từ request
            location.setUser(user);
            locationRepository.save(location);
            response.put("message", "lưu lịch sử tìm kiếm thành công.");
            response.put("data", Map.of("Tỉnh Thành", location.getCityName()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @Override
    public ResponseEntity<Map<String, Object>> getallhistory(Long userId) {

        Map<String, Object> response = new HashMap<>();

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            response.put("message", "Không tồn tại User");
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        List<Location> locations = locationRepository.findByUserId(userId);
        response.put("message", "Lấy lịch sử thành công");
        response.put("data", locations);
        return ResponseEntity.ok(response);
    }
    public Map<String, Object> deleteWeather(Long id, Long userId) {
        Map<String, Object> response = new HashMap<>();

        Optional<Location> locationOpt = locationRepository.findById(id);

        if(locationOpt.isPresent()) {
            Location location = locationOpt.get();

            if(location.getUser().getId().equals(userId)) {
                locationRepository.deleteById(id);
                response.put("message", "Xóa thành công");
            } else {
                response.put("message", "Bạn không có quyền xóa bản ghi này");
            }
        } else {
            response.put("message", "Không tìm thấy bản ghi với ID: " + id);
        }

        return response;
    }



}
