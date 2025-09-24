package com.example.demo.service.Weather;

import com.example.demo.DTO.WeatherSummary;
import com.example.demo.Model.WeatherRecord;
import com.example.demo.repository.LocationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.WeatherRecordRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Primary
@Service
public class WeatherServiceDatabase implements WeatherServiceInterface {

    private final WeatherService weatherService; // service gọi API ngoài
    private final WeatherRecordRepository repository;

    public WeatherServiceDatabase(WeatherService weatherService, WeatherRecordRepository repository) {
        this.weatherService = weatherService;
        this.repository = repository;
    }

    @Override
    public WeatherSummary getWeather(String city) {

        // Xác định cutoff 10 ngày trước
        LocalDateTime cutoff = LocalDateTime.now().minusDays(10);
        // Xóa các record cũ hơn 10 ngày
        repository.deleteByCityAndCreatedAtBefore(city, cutoff);

        // Lấy bản ghi mới nhất (nếu có)
        return repository.findTopByCityOrderByCreatedAtDesc(city)
                .map(this::mapToSummary) // nếu đã có bản ghi mới nhất thì trả về
                .orElseGet(() -> {
                    // Nếu chưa có bản ghi trong vòng 10 ngày → gọi API ngoài
                    WeatherSummary fetched = weatherService.getWeather(city);
                    // Lưu bản ghi mới vào DB
                    repository.save(mapToRecord(fetched));
                    return fetched;
                });
    }

    private WeatherSummary mapToSummary(WeatherRecord record) {
        WeatherSummary summary = new WeatherSummary();
        summary.setCity(record.getCity());
        summary.setCountry(record.getCountry());
        summary.setTemperatureC(record.getTemperatureC());
        summary.setCondition(record.getCondition());
        summary.setHumidity(record.getHumidity());
        summary.setWindKph(record.getWindKph());
        summary.setVisibilityKm(record.getVisibilityKm());
        summary.setUvIndex(record.getUvIndex());
        return summary;
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

