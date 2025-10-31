package com.example.demo.Weather.Repository;

import com.example.demo.User.model.User;
import com.example.demo.Weather.model.WeatherAlert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherAlertRepository extends JpaRepository<WeatherAlert, Long> {
    @Query("SELECT wa FROM WeatherAlert wa WHERE wa.active = true AND (wa.lastSent IS NULL OR wa.lastSent < :threshold)")
    List<WeatherAlert> findAlertsToSend(LocalDateTime threshold);
    Optional<WeatherAlert> findByUserAndCityAndCondition(User user, String city, String condition);
    List<WeatherAlert> findByActiveTrue();
}
