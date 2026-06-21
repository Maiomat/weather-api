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
            List<City> cities = List.of(
                    new City(
                            "Firenze",
                            "50121",
                            43.7696,
                            11.2558),
                    new City(
                            "Roma",
                            "00184",
                            41.9028,
                            12.4964),
                    new City(
                            "Milano",
                            "20121",
                            45.4642,
                            9.1900),
                    new City(
                            "Napoli",
                            "80121",
                            40.8518,
                            14.2681),
                    new City(
                            "Torino",
                            "10121",
                            45.0703,
                            7.6869));

            for (City city : cities) {
                boolean nameAlreadyExists = cityRepository.existsByNameIgnoreCase(
                        city.getName());

                boolean postalCodeAlreadyExists = cityRepository.existsByPostalCode(
                        city.getPostalCode());

                if (!nameAlreadyExists && !postalCodeAlreadyExists) {
                    cityRepository.save(city);
                }
            }
        };
    }
}
