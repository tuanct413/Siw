package com.example.demo.controller;
import com.example.demo.DTO.WeatherSummary;
import com.example.demo.Implementation.WeatherServiceImp;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/weather")
public class WeatherController {
    private final WeatherServiceImp weatherServiceImp;

    public WeatherController(WeatherServiceImp weatherServiceImp ) {
        this.weatherServiceImp = weatherServiceImp;
    }
    @GetMapping("/find")
    public WeatherSummary findUserByLocal(@RequestParam("local") String local) {
        System.out.println("🔥 Controller nhận request với local = " + local);
        return weatherServiceImp.getWeather(local);
    }

}
