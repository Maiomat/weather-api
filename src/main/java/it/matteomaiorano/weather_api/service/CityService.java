package it.matteomaiorano.weather_api.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.matteomaiorano.weather_api.dto.CityResponse;
import it.matteomaiorano.weather_api.dto.CreateCityRequest;
import it.matteomaiorano.weather_api.entity.City;
import it.matteomaiorano.weather_api.exception.PostalCodeAlreadyExistsException;
import it.matteomaiorano.weather_api.repository.CityRepository;

@Service
public class CityService {

    private final CityRepository cityRepository;

    public CityService(CityRepository cityRepository) {
        this.cityRepository = cityRepository;
    }

    @Transactional
    public CityResponse createCity(CreateCityRequest request) {
        String normalizedName = request.name().trim();
        String normalizedPostalCode = request.postalCode().trim();

        if (cityRepository.existsByPostalCode(normalizedPostalCode)) {
            throw new PostalCodeAlreadyExistsException(
                    normalizedPostalCode);
        }

        City city = new City(
                normalizedName,
                normalizedPostalCode,
                request.latitude(),
                request.longitude());

        City savedCity = cityRepository.save(city);

        return CityResponse.fromEntity(savedCity);
    }
}