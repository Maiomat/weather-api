package it.matteomaiorano.weather_api.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import it.matteomaiorano.weather_api.dto.CityResponse;
import it.matteomaiorano.weather_api.dto.CreateCityRequest;
import it.matteomaiorano.weather_api.entity.City;
import it.matteomaiorano.weather_api.exception.PostalCodeAlreadyExistsException;
import it.matteomaiorano.weather_api.repository.CityRepository;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

        @Mock
        private CityRepository cityRepository;

        @InjectMocks
        private CityService cityService;

        @Test
        void shouldCreateCityWhenNameAndPostalCodeAreUnique() {
                CreateCityRequest request = new CreateCityRequest(
                                " Salerno ",
                                " 84121 ",
                                40.6824,
                                14.7681);

                when(cityRepository.existsByPostalCode("84121"))
                                .thenReturn(false);

                when(cityRepository.save(any(City.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                CityResponse response = cityService.createCity(request);

                assertEquals("Salerno", response.name());
                assertEquals("84121", response.postalCode());
                assertEquals(40.6824, response.latitude());
                assertEquals(14.7681, response.longitude());
                assertEquals(0.0, response.averageTemperature());
                assertEquals(0.0, response.averageWindSpeed());
                assertEquals(0L, response.measurementsCount());

                ArgumentCaptor<City> captor = ArgumentCaptor.forClass(City.class);

                verify(cityRepository).save(captor.capture());

                City savedCity = captor.getValue();

                assertEquals("Salerno", savedCity.getName());
                assertEquals("84121", savedCity.getPostalCode());
        }

        @Test
        void shouldThrowExceptionWhenPostalCodeAlreadyExists() {
                CreateCityRequest request = new CreateCityRequest(
                                "Salerno",
                                "84121",
                                40.6824,
                                14.7681);

                when(cityRepository.existsByPostalCode("84121"))
                                .thenReturn(true);

                assertThrows(
                                PostalCodeAlreadyExistsException.class,
                                () -> cityService.createCity(request));

                verify(cityRepository)
                                .existsByPostalCode("84121");

                verify(cityRepository, never())
                                .save(any(City.class));
        }

        @Test
        void shouldAllowSameCityNameWithDifferentPostalCode() {
                CreateCityRequest request = new CreateCityRequest(
                                "Firenze",
                                "50122",
                                43.7696,
                                11.2558);

                when(cityRepository.existsByPostalCode("50122"))
                                .thenReturn(false);

                when(cityRepository.save(any(City.class)))
                                .thenAnswer(invocation -> invocation.getArgument(0));

                CityResponse response = cityService.createCity(request);

                assertEquals("Firenze", response.name());
                assertEquals("50122", response.postalCode());

                verify(cityRepository).existsByPostalCode("50122");
                verify(cityRepository).save(any(City.class));
        }
}