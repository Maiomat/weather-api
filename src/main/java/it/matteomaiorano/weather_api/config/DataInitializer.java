package it.matteomaiorano.weather_api.config;

import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.matteomaiorano.weather_api.entity.City;
import it.matteomaiorano.weather_api.repository.CityRepository;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initializeCities(CityRepository cityRepository) {
        return args -> {
            if (cityRepository.count() > 0) {
                return;
            }

            List<City> cities = List.of(
                    new City("Firenze", 43.7696, 11.2558),
                    new City("Roma", 41.9028, 12.4964),
                    new City("Milano", 45.4642, 9.1900),
                    new City("Napoli", 40.8518, 14.2681),
                    new City("Torino", 45.0703, 7.6869)
            );

            cityRepository.saveAll(cities);
        };
    }
}
