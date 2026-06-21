package it.matteomaiorano.weather_api.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.matteomaiorano.weather_api.entity.WeatherMeasurement;

public interface WeatherMeasurementRepository
        extends JpaRepository<WeatherMeasurement, Long> {

    List<WeatherMeasurement> findByCityId(Long cityId);

    boolean existsByCity_IdAndMeasuredAt(
            Long cityId,
            LocalDateTime measuredAt);
}