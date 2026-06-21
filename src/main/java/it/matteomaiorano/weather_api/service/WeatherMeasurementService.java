package it.matteomaiorano.weather_api.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.matteomaiorano.weather_api.client.dto.OpenMeteoResponse;
import it.matteomaiorano.weather_api.entity.City;
import it.matteomaiorano.weather_api.entity.WeatherMeasurement;
import it.matteomaiorano.weather_api.repository.CityRepository;
import it.matteomaiorano.weather_api.repository.WeatherMeasurementRepository;

@Service
public class WeatherMeasurementService {

    private final CityRepository cityRepository;
    private final WeatherMeasurementRepository weatherMeasurementRepository;

    public WeatherMeasurementService(
            CityRepository cityRepository,
            WeatherMeasurementRepository weatherMeasurementRepository) {

        this.cityRepository = cityRepository;
        this.weatherMeasurementRepository = weatherMeasurementRepository;
    }

    @Transactional
    public boolean saveIfNew(
            Long cityId,
            OpenMeteoResponse.CurrentWeather currentWeather,
            OpenMeteoResponse.CurrentWeatherUnits units) {

        LocalDateTime measuredAt =
                LocalDateTime.parse(currentWeather.time());

        boolean alreadyExists =
                weatherMeasurementRepository
                        .existsByCity_IdAndMeasuredAt(
                                cityId,
                                measuredAt);

        if (alreadyExists) {
            return false;
        }

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new IllegalStateException(
                        "Città non trovata con id: " + cityId));

        WeatherMeasurement measurement = new WeatherMeasurement(
                city,
                measuredAt,
                currentWeather.temperature(),
                currentWeather.windSpeed(),
                currentWeather.windDirection(),
                currentWeather.weatherCode(),
                currentWeather.isDay() == 1,
                units.temperature(),
                units.windSpeed(),
                units.windDirection());

        /*
         * Il flush forza subito l'esecuzione dell'INSERT,
         * facendo emergere un eventuale conflitto sul vincolo univoco
         * prima dell'aggiornamento delle medie.
         */
        weatherMeasurementRepository.saveAndFlush(measurement);

        city.updateWeatherAverages(
                currentWeather.temperature(),
                currentWeather.windSpeed());

        cityRepository.save(city);

        return true;
    }
}