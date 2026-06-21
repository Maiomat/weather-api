package it.matteomaiorano.weather_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.matteomaiorano.weather_api.dto.WeatherAverageResponse;
import it.matteomaiorano.weather_api.entity.City;
import it.matteomaiorano.weather_api.exception.CityNotFoundException;
import it.matteomaiorano.weather_api.exception.NoWeatherMeasurementsException;
import it.matteomaiorano.weather_api.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class WeatherStatisticsServiceTest {

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private WeatherStatisticsService weatherStatisticsService;

    @Test
    void shouldReturnPrecomputedAverageByPostalCode() {
        City city = new City(
                "Firenze",
                "50121",
                43.7696,
                11.2558);

        city.updateWeatherAverages(
                20.0,
                10.0,
                "°C",
                "km/h");

        city.updateWeatherAverages(
                22.0,
                12.0,
                "°C",
                "km/h");

        city.updateWeatherAverages(
                24.0,
                14.0,
                "°C",
                "km/h");

        when(cityRepository.findByPostalCode("50121"))
                .thenReturn(Optional.of(city));

        WeatherAverageResponse response =
                weatherStatisticsService
                        .getAverageByPostalCode("50121");

        assertEquals("Firenze", response.city());
        assertEquals("50121", response.postalCode());
        assertEquals(3L, response.sampleCount());
        assertEquals(22.0, response.averageTemperature());
        assertEquals("°C", response.temperatureUnit());
        assertEquals(12.0, response.averageWindSpeed());
        assertEquals("km/h", response.windSpeedUnit());

        verify(cityRepository).findByPostalCode("50121");
    }

    @Test
    void shouldThrowExceptionWhenPostalCodeDoesNotExist() {
        when(cityRepository.findByPostalCode("40121"))
                .thenReturn(Optional.empty());

        assertThrows(
                CityNotFoundException.class,
                () -> weatherStatisticsService
                        .getAverageByPostalCode("40121"));

        verify(cityRepository).findByPostalCode("40121");
    }

    @Test
    void shouldThrowExceptionWhenCityHasNoMeasurements() {
        City city = new City(
                "Firenze",
                "50121",
                43.7696,
                11.2558);

        when(cityRepository.findByPostalCode("50121"))
                .thenReturn(Optional.of(city));

        assertThrows(
                NoWeatherMeasurementsException.class,
                () -> weatherStatisticsService
                        .getAverageByPostalCode("50121"));

        verify(cityRepository).findByPostalCode("50121");
    }
}
