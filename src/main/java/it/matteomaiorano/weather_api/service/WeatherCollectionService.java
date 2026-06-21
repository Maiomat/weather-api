package it.matteomaiorano.weather_api.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import it.matteomaiorano.weather_api.client.OpenMeteoClient;
import it.matteomaiorano.weather_api.client.dto.OpenMeteoResponse;
import it.matteomaiorano.weather_api.entity.City;
import it.matteomaiorano.weather_api.exception.WeatherProviderException;
import it.matteomaiorano.weather_api.repository.CityRepository;

@Service
public class WeatherCollectionService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherCollectionService.class);

    private final CityRepository cityRepository;
    private final OpenMeteoClient openMeteoClient;
    private final WeatherMeasurementService weatherMeasurementService;

    public WeatherCollectionService(
            CityRepository cityRepository,
            OpenMeteoClient openMeteoClient,
            WeatherMeasurementService weatherMeasurementService) {

        this.cityRepository = cityRepository;
        this.openMeteoClient = openMeteoClient;
        this.weatherMeasurementService = weatherMeasurementService;
    }

    public void collectWeatherData() {
        List<City> cities = cityRepository.findAll();

        for (City city : cities) {
            try {
                OpenMeteoResponse response = openMeteoClient.getCurrentWeather(
                        city.getLatitude(),
                        city.getLongitude());

                boolean saved = weatherMeasurementService.saveIfNew(
                        city.getId(),
                        response.currentWeather(),
                        response.currentWeatherUnits());

                if (!saved) {
                    logger.info(
                            "Rilevazione duplicata ignorata per la città {}",
                            city.getName());
                }

            } catch (WeatherProviderException exception) {
                logger.warn(
                        "Errore durante la raccolta dei dati meteo "
                                + "per la città {}: {}",
                        city.getName(),
                        exception.getMessage());

            } catch (DataIntegrityViolationException exception) {
                logger.warn(
                        "Rilevazione non salvata per la città {} "
                                + "a causa di un conflitto sui dati",
                        city.getName());
            }
        }
    }
}