package it.matteomaiorano.weather_api.service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import it.matteomaiorano.weather_api.client.OpenMeteoClient;
import it.matteomaiorano.weather_api.client.dto.OpenMeteoResponse;
import it.matteomaiorano.weather_api.entity.City;
import it.matteomaiorano.weather_api.entity.WeatherMeasurement;
import it.matteomaiorano.weather_api.exception.WeatherProviderException;
import it.matteomaiorano.weather_api.repository.CityRepository;
import it.matteomaiorano.weather_api.repository.WeatherMeasurementRepository;

@Service
public class WeatherCollectionService {
    
    private static final Logger logger = 
        LoggerFactory.getLogger(WeatherCollectionService.class);
    
    private final CityRepository cityRepository;
    private final WeatherMeasurementRepository weatherMeasurementRepository;
    private final OpenMeteoClient openMeteoClient;

    public WeatherCollectionService(
            CityRepository cityRepository,
            WeatherMeasurementRepository weatherMeasurementRepository,
            OpenMeteoClient openMeteoClient) {

        this.cityRepository = cityRepository;
        this.weatherMeasurementRepository = weatherMeasurementRepository;
        this.openMeteoClient = openMeteoClient;
    }

    public void collectWeatherData() {
        List<City> cities = cityRepository.findAll();
        for (City city : cities) {
            try {
                OpenMeteoResponse response = openMeteoClient.getCurrentWeather(
                        city.getLatitude(),
                        city.getLongitude());

                OpenMeteoResponse.CurrentWeather currentWeather = response.currentWeather();
                OpenMeteoResponse.CurrentWeatherUnits units = response.currentWeatherUnits();

                WeatherMeasurement measurement = new WeatherMeasurement(
                        city,
                        LocalDateTime.parse(currentWeather.time()),
                        currentWeather.temperature(),
                        currentWeather.windSpeed(),
                        currentWeather.windDirection(),
                        currentWeather.weatherCode(),
                        currentWeather.isDay() == 1,
                        units.temperature(),
                        units.windSpeed(),
                        units.windDirection());

                weatherMeasurementRepository.save(measurement);
            } catch (WeatherProviderException exception) {
                logger.warn(
                        "Errore durante la raccolta dei dati meteo per la città {}: {}",
                        city.getName(),
                        exception.getMessage());
            }
        }
    }
}
