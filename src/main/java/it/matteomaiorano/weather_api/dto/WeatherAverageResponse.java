package it.matteomaiorano.weather_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record WeatherAverageResponse(

        @Schema(example = "Firenze")
        String city,

        @Schema(example = "50121")
        String postalCode,

        @Schema(
                description = "Numero di rilevazioni utilizzate",
                example = "4")
        long sampleCount,

        @Schema(example = "23.7")
        Double averageTemperature,

        @Schema(example = "°C")
        String temperatureUnit,

        @Schema(example = "11.4")
        Double averageWindSpeed,

        @Schema(example = "km/h")
        String windSpeedUnit
) {
}