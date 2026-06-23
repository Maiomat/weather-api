package it.matteomaiorano.weather_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.matteomaiorano.weather_api.entity.City;

public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByPostalCode(String postalCode);

    boolean existsByPostalCode(String postalCode);
}