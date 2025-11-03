package com.example.demo.Weather.serivce.scheduler;

import com.example.demo.Weather.DTO.WeatherSummary;
import com.example.demo.Weather.model.WeatherRecord;
import com.example.demo.Weather.Repository.WeatherRecordRepository;
import com.example.demo.Weather.serivce.impl.WeatherService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class WeatherScheduler {

    private final WeatherService weatherService;
    private final RestTemplate restTemplate = new RestTemplate();


    private final WeatherRecordRepository repository;
    public WeatherScheduler(WeatherService weatherService, WeatherRecordRepository repository) {
        this.weatherService = weatherService;
        this.repository = repository;
    }
    // Chạy mỗi ngày 1 lần 1 tiếng 1 lần
    @Scheduled(cron = "0 0 * * * *")
    public void fetchDailyWeather() {
        // Giả sử  có danh sách các city cần theo dõi
        String[] cities = {"Hanoi", "Yen Bai", "Ho Chi Minh"};

        for (String city : cities) {
            WeatherSummary fetched = weatherService.getWeather(city);
            repository.save(mapToRecord(fetched));
        }
    }
    private WeatherRecord mapToRecord(WeatherSummary summary) {
        return WeatherRecord.builder()
                .city(summary.getCity())
                .country(summary.getCountry())
                .temperatureC(summary.getTemperatureC())
                .condition(summary.getCondition())
                .humidity(summary.getHumidity())
                .windKph(summary.getWindKph())
                .visibilityKm(summary.getVisibilityKm())
                .uvIndex(summary.getUvIndex())
                .build();
    }
    @Scheduled(fixedRate = 120000)
    public void pingSelf() {
        try {
            String response = restTemplate.getForObject("https://siw-backend.onrender.com/ping", String.class);
            System.out.println("Ping response: " + response);
        } catch (Exception e) {
            System.err.println("Ping failed: " + e.getMessage());
        }
    }



}
