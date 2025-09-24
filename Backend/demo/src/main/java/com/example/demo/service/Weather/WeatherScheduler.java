package com.example.demo.service.Weather;

import com.example.demo.DTO.WeatherSummary;
import com.example.demo.Model.WeatherRecord;
import com.example.demo.repository.WeatherRecordRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WeatherScheduler {

    private final WeatherService weatherService;

    private final WeatherRecordRepository repository;
    public WeatherScheduler(WeatherService weatherService, WeatherRecordRepository repository) {
        this.weatherService = weatherService;
        this.repository = repository;
    }
    // Chạy mỗi ngày 1 lần lúc 7h sáng
    @Scheduled(cron = "0 0 7 * * *")
    public void fetchDailyWeather() {
        // Giả sử bạn có danh sách các city cần theo dõi
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


}
