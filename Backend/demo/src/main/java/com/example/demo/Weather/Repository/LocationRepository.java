package com.example.demo.Weather.Repository;

import com.example.demo.Weather.model.Location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByUserId(Long userId);

    boolean existsByUserIdAndCityNameIgnoreCase(Long userId, String cityName);



}
