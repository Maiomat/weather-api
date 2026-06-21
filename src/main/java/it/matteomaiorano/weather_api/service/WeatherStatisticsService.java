package it.matteomaiorano.weather_api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import it.matteomaiorano.weather_api.dto.WeatherAverageResponse;
import it.matteomaiorano.weather_api.entity.City;
import it.matteomaiorano.weather_api.exception.CityNotFoundException;
import it.matteomaiorano.weather_api.exception.NoWeatherMeasurementsException;
import it.matteomaiorano.weather_api.repository.CityRepository;

@Service
public class WeatherStatisticsService {

    private final CityRepository cityRepository;

    public WeatherStatisticsService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    public WeatherAverageResponse getAverageByPostalCode(
            String postalCode) {

        City city = cityRepository.findByPostalCode(postalCode)
                .orElseThrow(() ->
                        new CityNotFoundException(postalCode));

        if (city.getMeasurementsCount() == 0) {
            throw new NoWeatherMeasurementsException(
                    city.getName());
        }

        return toResponse(city);
    }

    public List<WeatherAverageResponse> getAllAverages() {
        List<City> cities = cityRepository.findAll();
        List<WeatherAverageResponse> averages =
                new ArrayList<>();

        for (City city : cities) {
            if (city.getMeasurementsCount() > 0) {
                averages.add(toResponse(city));
            }
        }

        return averages;
    }

    private WeatherAverageResponse toResponse(City city) {
        return new WeatherAverageResponse(
                city.getName(),
                city.getPostalCode(),
                city.getMeasurementsCount(),
                city.getAverageTemperature(),
                city.getTemperatureUnit(),
                city.getAverageWindSpeed(),
                city.getWindSpeedUnit());
    }
}
