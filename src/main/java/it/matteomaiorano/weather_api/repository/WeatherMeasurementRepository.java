package it.matteomaiorano.weather_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import it.matteomaiorano.weather_api.entity.WeatherMeasurement;

public interface WeatherMeasurementRepository
        extends JpaRepository<WeatherMeasurement, Long> {

    List<WeatherMeasurement> findByCityId(Long cityId);
}