package it.matteomaiorano.weather_api.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import it.matteomaiorano.weather_api.entity.City;

public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByNameIgnoreCase(String name);

    Optional<City> findByPostalCode(String postalCode);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByPostalCode(String postalCode);
}