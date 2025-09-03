package com.example.demo.controller;
import com.example.demo.DTO.LocationRequest;
import com.example.demo.DTO.WeatherSummary;
import com.example.demo.Implementation.WeatherServiceImp;
import com.example.demo.entity.Location;
import com.example.demo.entity.User;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherServiceImp weatherServiceImp;

    public WeatherController(WeatherServiceImp weatherServiceImp ) {
        this.weatherServiceImp = weatherServiceImp;
    }
    @GetMapping("/find")
    public WeatherSummary findUserByLocal(@RequestParam("local") String local ) {
        System.out.println("🔥 Controller nhận request với local = " + local);
        return weatherServiceImp.getWeather(local);

    }
    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> locationcreate(
            @Valid @RequestBody LocationRequest request,
            BindingResult result) {

        Map<String, Object> response = new HashMap<>();

        // Kiểm tra lỗi validate
        if (result.hasErrors()) {
            response.put("message", result.getAllErrors().get(0).getDefaultMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        ResponseEntity<Map<String, Object>> serviceResponse =
                weatherServiceImp.createHistoryWeather(request);

        return serviceResponse;
    }

}
