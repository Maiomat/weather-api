package it.matteomaiorano.weather_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.matteomaiorano.weather_api.client.OpenMeteoClient;
import it.matteomaiorano.weather_api.client.dto.OpenMeteoResponse;
import it.matteomaiorano.weather_api.client.dto.OpenMeteoResponse.CurrentWeather;
import it.matteomaiorano.weather_api.client.dto.OpenMeteoResponse.CurrentWeatherUnits;
import it.matteomaiorano.weather_api.entity.City;
import it.matteomaiorano.weather_api.entity.WeatherMeasurement;
import it.matteomaiorano.weather_api.exception.WeatherProviderException;
import it.matteomaiorano.weather_api.repository.CityRepository;
import it.matteomaiorano.weather_api.repository.WeatherMeasurementRepository;

@ExtendWith(MockitoExtension.class)
class WeatherCollectionServiceTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private WeatherMeasurementRepository weatherMeasurementRepository;

    @Mock
    private OpenMeteoClient openMeteoClient;

    @InjectMocks
    private WeatherCollectionService weatherCollectionService;

    @Test
    void shouldCollectAndSaveWeatherData() {
        City city = new City(
                "Firenze",
                43.7696,
                11.2558);

        OpenMeteoResponse response = mock(OpenMeteoResponse.class);

        CurrentWeather currentWeather = mock(CurrentWeather.class);

        CurrentWeatherUnits units = mock(CurrentWeatherUnits.class);

        when(cityRepository.findAll())
                .thenReturn(List.of(city));

        when(openMeteoClient.getCurrentWeather(
                city.getLatitude(),
                city.getLongitude()))
                .thenReturn(response);

        when(response.currentWeather())
                .thenReturn(currentWeather);

        when(response.currentWeatherUnits())
                .thenReturn(units);

        when(currentWeather.time())
                .thenReturn("2026-06-19T10:00");

        when(currentWeather.temperature())
                .thenReturn(24.5);

        when(currentWeather.windSpeed())
                .thenReturn(12.3);

        when(currentWeather.windDirection())
                .thenReturn(180);

        when(currentWeather.weatherCode())
                .thenReturn(1);

        when(units.temperature())
                .thenReturn("°C");

        when(units.windSpeed())
                .thenReturn("km/h");

        when(units.windDirection())
                .thenReturn("°");

        weatherCollectionService.collectWeatherData();

        ArgumentCaptor<WeatherMeasurement> captor = ArgumentCaptor.forClass(WeatherMeasurement.class);

        verify(weatherMeasurementRepository)
                .save(captor.capture());

        WeatherMeasurement savedMeasurement = captor.getValue();

        assertSame(city, savedMeasurement.getCity());

        assertEquals(
                LocalDateTime.of(2026, 6, 19, 10, 0),
                savedMeasurement.getMeasuredAt());

        assertEquals(
                24.5,
                savedMeasurement.getTemperature());

        assertEquals(
                12.3,
                savedMeasurement.getWindSpeed());

        assertEquals(
                180,
                savedMeasurement.getWindDirection());

        assertEquals(
                1,
                savedMeasurement.getWeatherCode());

        assertEquals(
                "°C",
                savedMeasurement.getTemperatureUnit());

        assertEquals(
                "km/h",
                savedMeasurement.getWindSpeedUnit());

        assertEquals(
                "°",
                savedMeasurement.getWindDirectionUnit());

        verify(openMeteoClient).getCurrentWeather(
                city.getLatitude(),
                city.getLongitude());
    }

    @Test
    void shouldContinueCollectionWhenOneCityFails() {
        City firenze = new City(
                "Firenze",
                43.7696,
                11.2558);

        City roma = new City(
                "Roma",
                41.9028,
                12.4964);

        OpenMeteoResponse response = mock(OpenMeteoResponse.class);
        CurrentWeather currentWeather = mock(CurrentWeather.class);
        CurrentWeatherUnits units = mock(CurrentWeatherUnits.class);

        when(cityRepository.findAll())
                .thenReturn(List.of(firenze, roma));

        when(openMeteoClient.getCurrentWeather(
                firenze.getLatitude(),
                firenze.getLongitude()))
                .thenThrow(new WeatherProviderException(
                        "Open-Meteo non disponibile"));

        when(openMeteoClient.getCurrentWeather(
                roma.getLatitude(),
                roma.getLongitude()))
                .thenReturn(response);

        when(response.currentWeather())
                .thenReturn(currentWeather);

        when(response.currentWeatherUnits())
                .thenReturn(units);

        when(currentWeather.time())
                .thenReturn("2026-06-19T10:00");

        when(currentWeather.temperature())
                .thenReturn(25.0);

        when(currentWeather.windSpeed())
                .thenReturn(10.0);

        when(currentWeather.windDirection())
                .thenReturn(90);

        when(currentWeather.weatherCode())
                .thenReturn(1);

        when(units.temperature())
                .thenReturn("°C");

        when(units.windSpeed())
                .thenReturn("km/h");

        when(units.windDirection())
                .thenReturn("°");

        weatherCollectionService.collectWeatherData();

        verify(openMeteoClient).getCurrentWeather(
                firenze.getLatitude(),
                firenze.getLongitude());

        verify(openMeteoClient).getCurrentWeather(
                roma.getLatitude(),
                roma.getLongitude());

        verify(weatherMeasurementRepository, times(1))
                .save(org.mockito.ArgumentMatchers.any(
                        WeatherMeasurement.class));
    }
}