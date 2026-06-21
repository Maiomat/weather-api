package it.matteomaiorano.weather_api.dto;

import it.matteomaiorano.weather_api.entity.City;

public record CityResponse(
        Long id,
        String name,
        String postalCode,
        Double latitude,
        Double longitude,
        Double averageTemperature,
        Double averageWindSpeed,
        Long measurementsCount
) {

    public static CityResponse fromEntity(City city) {
        return new CityResponse(
                city.getId(),
                city.getName(),
                city.getPostalCode(),
                city.getLatitude(),
                city.getLongitude(),
                city.getAverageTemperature(),
                city.getAverageWindSpeed(),
                city.getMeasurementsCount());
    }
}