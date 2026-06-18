package it.matteomaiorano.weather_api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import it.matteomaiorano.weather_api.entity.WeatherMeasurement;

public interface WeatherMeasurementRepository
        extends JpaRepository<WeatherMeasurement, Long> {
}