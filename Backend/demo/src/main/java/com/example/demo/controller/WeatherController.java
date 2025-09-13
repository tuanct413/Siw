package com.example.demo.controller;
import com.example.demo.DTO.CustomUserDetails;
import com.example.demo.DTO.LocationRequest;
import com.example.demo.DTO.WeatherSummary;
import com.example.demo.service.Implementation.WeatherServiceInterface;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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



}
