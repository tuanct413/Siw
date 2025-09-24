package com.example.demo.controller;
import com.example.demo.DTO.*;
import com.example.demo.service.Weather.WeatherServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherServiceInterface weatherServiceImp;

    public WeatherController(WeatherServiceInterface weatherServiceImp ) {
        this.weatherServiceImp = weatherServiceImp;
    }
    @GetMapping("/find")
    public WeatherSummary findUserByLocal(@RequestParam("local") String local ) {
        System.out.println("🔥 Controller nhận request với local = " + local);
        return weatherServiceImp.getWeather(local);
    }
    @GetMapping("/findby7day/{city}")
    public List<WeatherForecastDTO> get7dayForecast(@PathVariable String city){
        return weatherServiceImp.get7dayForecast(city);
    }
    @DeleteMapping("/remove")
    public Map<String, Object> deleteWeather(@RequestParam("id") Long id,Authentication authentication){
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = customUserDetails.getUserId();
        return weatherServiceImp.deleteWeather(id,userId);
    }
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> locationcreate(
            @Valid @RequestBody LocationRequest request,
            BindingResult result,
            Authentication authentication) { // <-- thêm vào đây

        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            response.put("message", result.getAllErrors().get(0).getDefaultMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }


        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userId = userDetails.getUserId();
        // gán userId vào DTO trước khi truyền xuống service
        request.setUserId(userId);
        ResponseEntity<Map<String, Object>> serviceResponse =
                weatherServiceImp.createHistoryWeather( request);

        return serviceResponse;
    }
    @GetMapping("/getuserFavorite")
    public ResponseEntity<ApiResponse<List<LocationRequest>>> getFavoriteLocations (Authentication authentication){
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userid = userDetails.getUserId();
        ApiResponse<List<LocationRequest>> response =  weatherServiceImp.getFavoriteLocations(userid);
        return ResponseEntity.ok(response);
    }
    @GetMapping("v1/getuserFavorite")
    public ResponseEntity<ApiResponse<Map<String, Object>>> setWeatherAlert(@RequestParam("city") String city
            ,@RequestParam("condition") String condition , Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long userid = userDetails.getUserId();
        ApiResponse<Map<String, Object>> response = weatherServiceImp.setWeatherAlert(userid,city,condition);
        return  ResponseEntity.ok(response);
    }
    @GetMapping("v1/sosanhthanhpho")
    public ResponseEntity<ApiResponse<Map<String, Object>>> compareWeather(@RequestParam("city")String city1,@RequestParam("citynext") String city2){
        ApiResponse<Map<String, Object>> response = weatherServiceImp.compareWeather(city1,city2);
        return ResponseEntity.ok(response);
    }
}
