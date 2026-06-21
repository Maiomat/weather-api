package it.matteomaiorano.weather_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.matteomaiorano.weather_api.client.dto.OpenMeteoResponse.CurrentWeather;
import it.matteomaiorano.weather_api.client.dto.OpenMeteoResponse.CurrentWeatherUnits;
import it.matteomaiorano.weather_api.entity.City;
import it.matteomaiorano.weather_api.entity.WeatherMeasurement;
import it.matteomaiorano.weather_api.repository.CityRepository;
import it.matteomaiorano.weather_api.repository.WeatherMeasurementRepository;

@ExtendWith(MockitoExtension.class)
class WeatherMeasurementServiceTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private WeatherMeasurementRepository weatherMeasurementRepository;

    @InjectMocks
    private WeatherMeasurementService weatherMeasurementService;

    @Test
    void shouldSaveNewMeasurementAndUpdateCityAverages() {
        City city = new City(
                "Firenze",
                "50121",
                43.7696,
                11.2558);

        CurrentWeather currentWeather = mock(CurrentWeather.class);
        CurrentWeatherUnits units = mock(CurrentWeatherUnits.class);

        LocalDateTime measuredAt =
                LocalDateTime.of(2026, 6, 21, 10, 0);

        when(currentWeather.time())
                .thenReturn("2026-06-21T10:00");

        when(currentWeather.temperature())
                .thenReturn(24.0);

        when(currentWeather.windSpeed())
                .thenReturn(12.0);

        when(currentWeather.windDirection())
                .thenReturn(180);

        when(currentWeather.weatherCode())
                .thenReturn(1);

        when(currentWeather.isDay())
                .thenReturn(1);

        when(units.temperature())
                .thenReturn("°C");

        when(units.windSpeed())
                .thenReturn("km/h");

        when(units.windDirection())
                .thenReturn("°");

        when(weatherMeasurementRepository
                .existsByCity_IdAndMeasuredAt(1L, measuredAt))
                .thenReturn(false);

        when(cityRepository.findById(1L))
                .thenReturn(Optional.of(city));

        boolean saved = weatherMeasurementService.saveIfNew(
                1L,
                currentWeather,
                units);

        assertTrue(saved);

        ArgumentCaptor<WeatherMeasurement> captor =
                ArgumentCaptor.forClass(WeatherMeasurement.class);

        verify(weatherMeasurementRepository)
                .saveAndFlush(captor.capture());

        WeatherMeasurement measurement = captor.getValue();

        assertSame(city, measurement.getCity());
        assertEquals(measuredAt, measurement.getMeasuredAt());
        assertEquals(24.0, measurement.getTemperature());
        assertEquals(12.0, measurement.getWindSpeed());
        assertEquals(180, measurement.getWindDirection());
        assertEquals(1, measurement.getWeatherCode());
        assertTrue(measurement.getDay());
        assertEquals("°C", measurement.getTemperatureUnit());
        assertEquals("km/h", measurement.getWindSpeedUnit());
        assertEquals("°", measurement.getWindDirectionUnit());

        assertEquals(24.0, city.getAverageTemperature());
        assertEquals(12.0, city.getAverageWindSpeed());
        assertEquals(1L, city.getMeasurementsCount());

        verify(cityRepository).save(city);
    }

    @Test
    void shouldIgnoreDuplicateMeasurement() {
        CurrentWeather currentWeather = mock(CurrentWeather.class);
        CurrentWeatherUnits units = mock(CurrentWeatherUnits.class);

        LocalDateTime measuredAt =
                LocalDateTime.of(2026, 6, 21, 10, 0);

        when(currentWeather.time())
                .thenReturn("2026-06-21T10:00");

        when(weatherMeasurementRepository
                .existsByCity_IdAndMeasuredAt(1L, measuredAt))
                .thenReturn(true);

        boolean saved = weatherMeasurementService.saveIfNew(
                1L,
                currentWeather,
                units);

        assertFalse(saved);

        verify(weatherMeasurementRepository)
                .existsByCity_IdAndMeasuredAt(1L, measuredAt);

        verify(weatherMeasurementRepository, never())
                .saveAndFlush(
                        org.mockito.ArgumentMatchers.any(
                                WeatherMeasurement.class));

        verifyNoInteractions(cityRepository);
    }
}
