package it.matteomaiorano.weather_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.matteomaiorano.weather_api.dto.WeatherAverageResponse;
import it.matteomaiorano.weather_api.entity.City;
import it.matteomaiorano.weather_api.entity.WeatherMeasurement;
import it.matteomaiorano.weather_api.exception.CityNotFoundException;
import it.matteomaiorano.weather_api.exception.NoWeatherMeasurementsException;
import it.matteomaiorano.weather_api.repository.CityRepository;
import it.matteomaiorano.weather_api.repository.WeatherMeasurementRepository;

@ExtendWith(MockitoExtension.class)
class WeatherStatisticsServiceTest {

        @Mock
        private CityRepository cityRepository;

        @Mock
        private WeatherMeasurementRepository weatherMeasurementRepository;

        @InjectMocks
        private WeatherStatisticsService weatherStatisticsService;

        @Test
        void shouldCalculateAverageForCity() {
                City city = mock(City.class);

                when(city.getId()).thenReturn(1L);
                when(city.getName()).thenReturn("Firenze");

                WeatherMeasurement firstMeasurement = mock(WeatherMeasurement.class);

                when(firstMeasurement.getTemperature()).thenReturn(20.0);
                when(firstMeasurement.getWindSpeed()).thenReturn(10.0);
                when(firstMeasurement.getTemperatureUnit()).thenReturn("°C");
                when(firstMeasurement.getWindSpeedUnit()).thenReturn("km/h");

                WeatherMeasurement secondMeasurement = mock(WeatherMeasurement.class);

                when(secondMeasurement.getTemperature()).thenReturn(22.0);
                when(secondMeasurement.getWindSpeed()).thenReturn(12.0);

                WeatherMeasurement thirdMeasurement = mock(WeatherMeasurement.class);

                when(thirdMeasurement.getTemperature()).thenReturn(24.0);
                when(thirdMeasurement.getWindSpeed()).thenReturn(14.0);

                when(cityRepository.findByNameIgnoreCase("Firenze"))
                                .thenReturn(Optional.of(city));

                when(weatherMeasurementRepository.findByCityId(1L))
                                .thenReturn(List.of(
                                                firstMeasurement,
                                                secondMeasurement,
                                                thirdMeasurement));

                WeatherAverageResponse response = weatherStatisticsService.getAverageByCity("Firenze");

                assertEquals("Firenze", response.city());
                assertEquals(3, response.sampleCount());
                assertEquals(22.0, response.averageTemperature());
                assertEquals("°C", response.temperatureUnit());
                assertEquals(12.0, response.averageWindSpeed());
                assertEquals("km/h", response.windSpeedUnit());

                verify(cityRepository).findByNameIgnoreCase("Firenze");
                verify(weatherMeasurementRepository).findByCityId(1L);
        }

        @Test
        void shouldThrowExceptionWhenCityDoesNotExist() {
                when(cityRepository.findByNameIgnoreCase("Bologna"))
                                .thenReturn(Optional.empty());

                assertThrows(
                                CityNotFoundException.class,
                                () -> weatherStatisticsService.getAverageByCity("Bologna"));

                verify(cityRepository).findByNameIgnoreCase("Bologna");
                verifyNoInteractions(weatherMeasurementRepository);
        }

        @Test
        void shouldThrowExceptionWhenCityHasNoMeasurements() {
                City city = mock(City.class);

                when(city.getId()).thenReturn(1L);

                when(cityRepository.findByNameIgnoreCase("Firenze"))
                                .thenReturn(Optional.of(city));

                when(weatherMeasurementRepository.findByCityId(1L))
                                .thenReturn(List.of());

                assertThrows(
                                NoWeatherMeasurementsException.class,
                                () -> weatherStatisticsService.getAverageByCity("Firenze"));

                verify(cityRepository).findByNameIgnoreCase("Firenze");
                verify(weatherMeasurementRepository).findByCityId(1L);
        }
}