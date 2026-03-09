package com.example.demo.Weather.serivce.impl;

import com.example.demo.Weather.DTO.WeatherInOneDay;
import com.example.demo.Weather.Repository.WeatherAlertRepository;
import com.example.demo.Weather.model.WeatherAlert;
import com.example.demo.Weather.serivce.Interface.WeatherInOneDayInterface;
import com.example.demo.common.mail.MailService;
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
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WeatherService implements WeatherServiceInterface, WeatherGetWeather , WeatherInOneDayInterface {

    private final RestTemplate restTemplate = new RestTemplate();

    private final LocationRepository  locationRepository;
    private  final WeatherAlertRepository weatherAlertRepository;
    private final  WeatherNotificationService weatherNotificationService;

    private UserRepository userRepository;

    @Value("${weather.api.key}")
    private String apiKey;

    @Value("${weather.api.url}")
    private String apiUrl;

    @Value("${weather.api.url1}")
    private String apiurl7Day;



    public WeatherService(LocationRepository locationRepository  , UserRepository userRepository
    ,WeatherAlertRepository weatherAlertRepository,WeatherNotificationService weatherNotificationService) {
        this.locationRepository = locationRepository;
        this.userRepository = userRepository;
        this.weatherNotificationService = weatherNotificationService;
        this.weatherAlertRepository = weatherAlertRepository;
    }

    @Override
    public WeatherSummary getWeather(String city) {
        String url = apiUrl + "/current.json?key=" + apiKey + "&q=" + city + "&aqi=no";

        try {
            return fetchWeather(url);
        } catch (Exception e) {
            // fallback về Hanoi
            String defaultCity = "hanoi";
            String fallbackUrl = apiUrl + "/current.json?key=" + apiKey + "&q=" + defaultCity + "&aqi=no";
            return fetchWeather(fallbackUrl);
        }
    }

    @Override

    public ApiResponse<Map<String, Object>> setWeatherAlert(Long userId, String city, String condition) {
        // Lấy thông tin thời tiết
        WeatherSummary weather = getWeather(city);

        boolean alertWeather = false;
        boolean alertTemperature = false;
        String weatherMessage = "";
        String temperatureMessage = "";

        double temp = weather.getTemperatureC(); // Nhiệt độ hiện tại

        // Cảnh báo theo nhiệt độ
        if (temp < 10) {
            alertTemperature = true;
            temperatureMessage = "🌡️ Thời tiết ở " + city + " đang rất lạnh, hãy mặc áo ấm nhé!";
        } else if (temp > 30) {
            alertTemperature = true;
            temperatureMessage = "🔥 Thời tiết ở " + city + " đang khá nóng, nên hạn chế ra ngoài.";
        } else if (temp >= 20 && temp <= 30) {
            alertTemperature = true;
            temperatureMessage = "☀️ Thời tiết ở " + city + " hôm nay thật tuyệt vời!";
        }

        // Cảnh báo điều kiện thời tiết (mưa, gió, sấm, v.v.)
        if (weather.getCondition().toLowerCase().contains(condition.toLowerCase())) {
            alertWeather = true;
            weatherMessage = "⚠️ Cảnh báo: Thời tiết tại " + city + " hiện có " + condition + ".";
        } else {
            weatherMessage = "✅ Không có cảnh báo: Điều kiện '" + condition + "' chưa xảy ra tại " + city + ".";
        }
        // Chuẩn bị dữ liệu trả về
        Map<String, Object> data = new HashMap<>();
        data.put("city", city); // ⚠️ DÙNG city từ request, KHÔNG dùng weather.getCity()
        data.put("country", weather.getCountry());
        data.put("temperature", weather.getTemperatureC());
        data.put("condition", weather.getCondition());
        data.put("alertWeather", alertWeather);
        data.put("alertTemperature", alertTemperature);
        data.put("weatherMessage", weatherMessage);
        data.put("temperatureMessage", temperatureMessage);

        // ⚡ Gửi mail bất đồng bộ (chạy song song)
        if (alertTemperature || alertWeather) {
            String subject = "🌧️ Cảnh báo thời tiết cho khu vực " + city;
            String content = (alertTemperature ? temperatureMessage + "<br><br>" : "") + (alertWeather ? weatherMessage : "");
            weatherNotificationService.sendWeatherAlertAsync(userId, city, subject, content);
        }
        return new ApiResponse<>(
                "Lấy dữ liệu cảnh báo thành công",
                data
        );
    }

    public void saveOrUpdateAlert(Long userId, String city, String condition) {
        User user = userRepository.findById(userId).orElseThrow();

        // Kiểm tra xem user này đã có cảnh báo cho city + condition chưa
        Optional<WeatherAlert> existingAlertOpt =
                weatherAlertRepository.findByUserAndCityAndCondition(user, city, condition);

        WeatherAlert alert;

        if (existingAlertOpt.isPresent()) {
            // 🔁 Nếu đã tồn tại thì cập nhật lại thông tin (vd: bật lại cảnh báo, reset thời gian)
            alert = existingAlertOpt.get();
            alert.setActive(true);
            alert.setLastSent(LocalDateTime.now());
            System.out.println("♻️ Đã cập nhật lại cảnh báo (đã tồn tại)");
        } else {
            // 🆕 Nếu chưa có, tạo mới
            alert = WeatherAlert.builder()
                    .user(user)
                    .city(city)
                    .condition(condition)
                    .active(true)
                    .lastSent(LocalDateTime.now())
                    .build();
            System.out.println("🆕 Tạo mới cảnh báo thời tiết");
        }

        weatherAlertRepository.save(alert);
        System.out.println("💾 Đã lưu/cập nhật cảnh báo vào DB cho userId: " + userId);
    }


private WeatherSummary fetchWeather(String url) {
    Map<String, Object> response = restTemplate.getForObject(url, Map.class);

    if (response == null) {
        throw new RuntimeException("Không lấy được dữ liệu thời tiết");
    }

    // kiểm tra API error
    if (response.containsKey("error")) {
        Map<String, Object> error = (Map<String, Object>) response.get("error");
        throw new RuntimeException("Weather API error: " + error.get("message"));
    }

    Map<String, Object> location = (Map<String, Object>) response.get("location");
    Map<String, Object> current = (Map<String, Object>) response.get("current");

    if (location == null || current == null) {
        throw new RuntimeException("Thiếu dữ liệu location hoặc current từ API");
    }

    Map<String, Object> condition = (Map<String, Object>) current.get("condition");

    WeatherSummary summary = new WeatherSummary();
    summary.setCity((String) location.get("name"));
    summary.setCountry((String) location.get("country"));
    summary.setTemperatureC(((Number) current.get("temp_c")).doubleValue());
    summary.setCondition((String) condition.get("text"));
    summary.setHumidity(((Number) current.get("humidity")).intValue());
    summary.setWindKph(((Number) current.get("wind_kph")).doubleValue());
    summary.setVisibilityKm(((Number) current.get("vis_km")).doubleValue());
    summary.setUvIndex(((Number) current.get("uv")).intValue());
    summary.setIcon((String) condition.get("icon"));

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
    @Override
    public List<WeatherInOneDay> getAllWeatherInOneday(String city){
        String url = apiurl7Day + "?key=" + apiKey + "&q=" + city + "&days=1";
        try{
            // chạy hàm DTO dữ liệu và trả về
            return  fetchAndMapWeather(url);
        }
        catch (Exception e){
            String defaultCity = "hanoi";
            String fallbackUrl = apiUrl + "?key=" + apiKey + "&q=" + defaultCity + "&days=1";
            // chạy hàm DTO dữ liệu và trả về
            return fetchAndMapWeather(fallbackUrl);
        }
    }
    //
    private List<WeatherInOneDay> fetchAndMapWeather(String url) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        // lấy dữ liệu trong JSON: forecast.forecastday[0].hour
        List<Map<String, Object>> forecastDays =
                (List<Map<String, Object>>) ((Map<String, Object>) response.get("forecast")).get("forecastday");
        List<Map<String, Object>> hours = (List<Map<String, Object>>) forecastDays.get(0).get("hour");

        LocalDateTime now = LocalDateTime.now();

        return hours.stream()
                .map(h -> {
                    WeatherInOneDay  dto = new WeatherInOneDay();
                    dto.setMaxtemp_c(((Number) h.get("temp_c")).doubleValue());
                    dto.setMintemp_c(((Number) h.get("temp_c")).doubleValue());

                    // convert time string -> LocalDateTime
                    String timeStr = (String) h.get("time"); // "2025-10-01 14:00"
                    LocalDateTime time = LocalDateTime.parse(timeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    dto.setCreatedAt(time);

                    Map<String, Object> condition = (Map<String, Object>) h.get("condition");
                    dto.setConditionText((String) condition.get("text"));
                    dto.setConditionIcon((String) condition.get("icon"));

                    return dto;
                })
                // chỉ lấy từ thời điểm hiện tại trở đi
                .filter(dto -> dto.getCreatedAt().isAfter(now))
                //chỉ lấy giờ lẻ
                .limit(6)
                .collect(Collectors.toList());
    }

}
