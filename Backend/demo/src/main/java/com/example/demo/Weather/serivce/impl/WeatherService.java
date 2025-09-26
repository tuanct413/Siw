package com.example.demo.Weather.serivce.impl;

import com.example.demo.dto.*;
import com.example.demo.Weather.model.Location;
import com.example.demo.User.model.User;
import com.example.demo.Weather.Repository.LocationRepository;
import com.example.demo.User.repository.UserRepository;
import com.example.demo.Weather.DTO.LocationRequest;
import com.example.demo.Weather.DTO.WeatherForecastDTO;
import com.example.demo.Weather.DTO.WeatherSummary;
import com.example.demo.Weather.serivce.Interface.WeatherGetWeather;
import com.example.demo.Weather.serivce.Interface.WeatherServiceInterface;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.*;

@Service
public class WeatherService implements WeatherServiceInterface, WeatherGetWeather {

    private final RestTemplate restTemplate = new RestTemplate();

    private final LocationRepository  locationRepository;
    private UserRepository userRepository;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.api.url1}")
    private String apiurl7Day;

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

    @Override
    public ApiResponse<Map<String, Object>> setWeatherAlert(Long userId, String city, String condition) {
        WeatherSummary weather = getWeather(city);
        boolean alertweather = false;
        boolean alerttemperature = false;
        String weatherMessage = "";
        String temperatureMessage="";
        double temp = weather.getTemperatureC(); // lấy thời tiết hiện tại

        if(temp < 10){
            alerttemperature = true;
            temperatureMessage= "thời tiết đang rất Lạnh nên mắc áo ấm " +city ;
        }
        else if(temp > 30 ){
            alerttemperature = true;
            temperatureMessage= "thời tiết đang khá nóng hạn chế ra đường " +city ;
        }
        else if (temp >= 20 && temp <= 30 ){
            alerttemperature = true;
            temperatureMessage= "thời tiết quá đẹp " +city ;
        }
        // Cảnh báo điều kiện thời tiết
        if(weather.getCondition().toLowerCase().contains(condition.toLowerCase())){
            alertweather = true;
            weatherMessage = "Cảnh báo: Thời tiết tại " + city + " hiện tại có " + condition;
        }
        else {
            weatherMessage = "Chưa có cảnh báo. Điều kiện '" + condition + "' chưa xảy ra ở " + city;
        }
        Map<String, Object> data = new HashMap<>();
        data.put("city", weather.getCity());
        data.put("country", weather.getCountry());
        data.put("temperature", weather.getTemperatureC());
        data.put("condition", weather.getCondition());
        data.put("alertweather", alertweather );
        data.put("weatherMessage",weatherMessage);
        data.put("temperatureMessage",temperatureMessage);
        data.put("alertTemperature",alerttemperature);
        return new ApiResponse<>(
                "lấy thành công",
                data
        );

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

    public List<WeatherForecastDTO> get7dayForecast(String city) {
        if (city == null || city.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "City không được để trống!");
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            String url = apiurl7Day + "?key=" + apiKey + "&q=" + city + "&days=14";
            JsonNode root = restTemplate.getForObject(url, JsonNode.class);

            // Debug xem trả về gì

            JsonNode forecastDays = root.path("forecast").path("forecastday");
            if (forecastDays.isMissingNode() || !forecastDays.isArray()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không có dữ liệu forecast cho city: " + city);
            }

            List<WeatherForecastDTO> result = new ArrayList<>();
            int index = 0;
            for (JsonNode node : forecastDays) {
                if (index == 0) { // bỏ qua ngày hôm nay
                    index++;
                    continue;
                }
                WeatherForecastDTO dto = new WeatherForecastDTO();
                dto.setDate(node.path("date").asText());
                dto.setMaxtemp_c(node.path("day").path("maxtemp_c").asDouble());
                dto.setMintemp_c(node.path("day").path("mintemp_c").asDouble());
                dto.setAvgtemp_c(node.path("day").path("avgtemp_c").asDouble());
                dto.setDaily_chance_of_rain(node.path("day").path("daily_chance_of_rain").asInt());
                dto.setTotalprecip_mm(node.path("day").path("totalprecip_mm").asDouble());
                result.add(dto);
            }
            return result;

        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Không lấy được dữ liệu thời tiết: " + e.getMessage());
        }
    }
    public ApiResponse<List<LocationRequest>> getFavoriteLocations(Long userId) {
        List<Location> locations = locationRepository.findByUserId(userId);

        // convert entity -> DTO
        List<LocationRequest> dtoList = locations.stream()
                .map(loc -> new LocationRequest(loc.getCityName(), loc.getCountry()))
                .toList();

        return new ApiResponse<>(
                "Lấy danh sách thành công",
                dtoList
        );
    }
    @Override
    public ApiResponse<Map<String, Object>> compareWeather(String city1, String city2){
        WeatherSummary weather1 = getWeather(city1);
        WeatherSummary weather2 = getWeather(city2);

        ArrayList<WeatherSummary> cities = new ArrayList<>();
        ArrayList<String> rainingCities = new ArrayList<>();
        cities.add(weather1);
        cities.add(weather2);
        String messger = "";
        String messgerCondition = "";
        for (WeatherSummary  w  : cities){
            if (w.getCondition().toLowerCase().contains("rain")) {
                rainingCities.add(w.getCity());
            }
        }
        for(int i = 0 ; i < cities.size() - 1 ;i++ ){
            WeatherSummary index1 = cities.get(i);
            WeatherSummary index2 = cities.get(i + 1);
            double temp1 = index1.getTemperatureC();
            double temp2 = index2.getTemperatureC();
            if(temp1 > temp2){
                messger=( index1.getCity() + " nóng hơn " + index2.getCity());
            }
            else if (temp1 < temp2) {
                messger=( index2.getCity() + " nóng hơn " + index1.getCity());
            } else {
                System.out.println(index2.getCity() + " và " + index1.getCity() + " có cùng nhiệt độ");
            }
        }
        if (!rainingCities.isEmpty()) {
            messgerCondition=("Các thành phố đang mưa: " + String.join(", ", rainingCities));
        }
        Map<String, Object> data = new HashMap<>();
           data.put("messgersTemperatureC",messger);
           data.put("messgerCondition",messgerCondition);
           return new  ApiResponse<>(
                   "lấy thành công",
                   data
           );
    }

}
