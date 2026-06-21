package it.matteomaiorano.weather_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import it.matteomaiorano.weather_api.entity.City;

public record CityResponse(

        @Schema(example = "6")
        Long id,

        @Schema(example = "Salerno")
        String name,

        @Schema(example = "84121")
        String postalCode,

        @Schema(example = "40.6824")
        Double latitude,

        @Schema(example = "14.7681")
        Double longitude,

        @Schema(example = "0.0")
        Double averageTemperature,

        @Schema(example = "0.0")
        Double averageWindSpeed,

        @Schema(example = "0")
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