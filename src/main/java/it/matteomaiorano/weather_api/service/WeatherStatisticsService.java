package it.matteomaiorano.weather_api.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import it.matteomaiorano.weather_api.dto.WeatherAverageResponse;
import it.matteomaiorano.weather_api.entity.City;
import it.matteomaiorano.weather_api.entity.WeatherMeasurement;
import it.matteomaiorano.weather_api.exception.CityNotFoundException;
import it.matteomaiorano.weather_api.exception.NoWeatherMeasurementsException;
import it.matteomaiorano.weather_api.repository.CityRepository;
import it.matteomaiorano.weather_api.repository.WeatherMeasurementRepository;

@Service
public class WeatherStatisticsService {

    private final CityRepository cityRepository;
    private final WeatherMeasurementRepository weatherMeasurementRepository;

    public WeatherStatisticsService(
            CityRepository cityRepository,
            WeatherMeasurementRepository weatherMeasurementRepository) {

        this.cityRepository = cityRepository;
        this.weatherMeasurementRepository = weatherMeasurementRepository;
    }

    public WeatherAverageResponse getAverageByCity(String cityName) {
        City city = cityRepository.findByNameIgnoreCase(cityName)
                .orElseThrow(() -> new CityNotFoundException(cityName));
        List<WeatherMeasurement> measurements = weatherMeasurementRepository.findByCityId(city.getId());

        if (measurements.isEmpty()) {
            throw new NoWeatherMeasurementsException(cityName);
        }

        return calculateAverage(city, measurements);
    }

    public List<WeatherAverageResponse> getAllAverages() {
        List<City> cities = cityRepository.findAll();
        List<WeatherAverageResponse> averages = new ArrayList<>();

        for (City city : cities) {
            List<WeatherMeasurement> measurements = weatherMeasurementRepository.findByCityId(city.getId());

            if (!measurements.isEmpty()) {
                averages.add(calculateAverage(city, measurements));
            }
        }

        return averages;
    }

    private WeatherAverageResponse calculateAverage(
            City city,
            List<WeatherMeasurement> measurements) {

        double temperatureSum = 0.0;
        double windSpeedSum = 0.0;

        for (WeatherMeasurement measurement : measurements) {
            temperatureSum += measurement.getTemperature();
            windSpeedSum += measurement.getWindSpeed();
        }

        long sampleCount = measurements.size();

        WeatherMeasurement firstMeasurement = measurements.get(0);

        return new WeatherAverageResponse(
                city.getName(),
                sampleCount,
                temperatureSum / sampleCount,
                firstMeasurement.getTemperatureUnit(),
                windSpeedSum / sampleCount,
                firstMeasurement.getWindSpeedUnit());
    }
}