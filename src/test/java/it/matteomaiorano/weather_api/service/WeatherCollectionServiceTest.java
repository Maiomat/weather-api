package it.matteomaiorano.weather_api.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.matteomaiorano.weather_api.client.OpenMeteoClient;
import it.matteomaiorano.weather_api.client.dto.OpenMeteoResponse;
import it.matteomaiorano.weather_api.client.dto.OpenMeteoResponse.CurrentWeather;
import it.matteomaiorano.weather_api.client.dto.OpenMeteoResponse.CurrentWeatherUnits;
import it.matteomaiorano.weather_api.entity.City;
import it.matteomaiorano.weather_api.exception.WeatherProviderException;
import it.matteomaiorano.weather_api.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class WeatherCollectionServiceTest {

    @Mock
    private CityRepository cityRepository;

    @Mock
    private OpenMeteoClient openMeteoClient;

    @Mock
    private WeatherMeasurementService weatherMeasurementService;

    @InjectMocks
    private WeatherCollectionService weatherCollectionService;

    @Test
    void shouldCollectAndDelegateWeatherDataSaving() {
        City city = mock(City.class);

        OpenMeteoResponse response =
                mock(OpenMeteoResponse.class);

        CurrentWeather currentWeather =
                mock(CurrentWeather.class);

        CurrentWeatherUnits units =
                mock(CurrentWeatherUnits.class);

        when(city.getId()).thenReturn(1L);
        when(city.getLatitude()).thenReturn(43.7696);
        when(city.getLongitude()).thenReturn(11.2558);

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

        when(weatherMeasurementService.saveIfNew(
                1L,
                currentWeather,
                units))
                .thenReturn(true);

        weatherCollectionService.collectWeatherData();

        verify(cityRepository).findAll();

        verify(openMeteoClient).getCurrentWeather(
                city.getLatitude(),
                city.getLongitude());

        verify(weatherMeasurementService).saveIfNew(
                1L,
                currentWeather,
                units);
    }

    @Test
    void shouldContinueCollectionWhenOneCityFails() {
        City firenze = mock(City.class);
        City roma = mock(City.class);

        OpenMeteoResponse response =
                mock(OpenMeteoResponse.class);

        CurrentWeather currentWeather =
                mock(CurrentWeather.class);

        CurrentWeatherUnits units =
                mock(CurrentWeatherUnits.class);

        when(firenze.getName()).thenReturn("Firenze");
        when(firenze.getLatitude()).thenReturn(43.7696);
        when(firenze.getLongitude()).thenReturn(11.2558);

        when(roma.getId()).thenReturn(2L);
        when(roma.getLatitude()).thenReturn(41.9028);
        when(roma.getLongitude()).thenReturn(12.4964);

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

        when(weatherMeasurementService.saveIfNew(
                2L,
                currentWeather,
                units))
                .thenReturn(true);

        weatherCollectionService.collectWeatherData();

        verify(openMeteoClient).getCurrentWeather(
                firenze.getLatitude(),
                firenze.getLongitude());

        verify(openMeteoClient).getCurrentWeather(
                roma.getLatitude(),
                roma.getLongitude());

        verify(weatherMeasurementService).saveIfNew(
                2L,
                currentWeather,
                units);

        verifyNoMoreInteractions(weatherMeasurementService);
    }
}