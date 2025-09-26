package com.example.demo.Weather.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "weather_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WeatherRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city", nullable = false, length = 100)
    private String city;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "temperature_c")
    private double temperatureC;   // Nhiệt độ °C

    @Column(name = "weather_condition") // đổi tên tránh từ khóa MySQL
    private String condition;      // Mưa / Nắng / Mây

    @Column(name = "humidity")
    private int humidity;          // Độ ẩm %

    @Column(name = "wind_kph")
    private double windKph;        // Gió km/h

    @Column(name = "visibility_km")
    private double visibilityKm;   // Tầm nhìn km

    @Column(name = "uv_index")
    private int uvIndex;           // Chỉ số UV

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;  // Thời điểm lưu bản ghi
}
