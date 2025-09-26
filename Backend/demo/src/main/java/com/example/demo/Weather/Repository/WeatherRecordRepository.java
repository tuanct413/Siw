package com.example.demo.Weather.Repository;


import com.example.demo.Weather.model.WeatherRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherRecordRepository extends JpaRepository<WeatherRecord, Long> {
    // Lấy bản ghi mới nhất theo city
    Optional<WeatherRecord> findTopByCityOrderByCreatedAtDesc(String city);

    //kiểm tra số lượng bản ghi
    List<WeatherRecord> findByCityOrderByCreatedAtDesc(String city);

    void deleteByCityAndCreatedAtBefore(String city, LocalDateTime cutoff);
}
