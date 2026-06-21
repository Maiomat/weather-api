package it.matteomaiorano.weather_api.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCityRequest(

        @NotBlank(message = "Il nome della città è obbligatorio")
        @Size(
                max = 100,
                message = "Il nome non può superare 100 caratteri")
        String name,

        @NotBlank(message = "Il CAP è obbligatorio")
        @Pattern(
                regexp = "\\d{5}",
                message = "Il CAP deve contenere esattamente 5 cifre")
        String postalCode,

        @NotNull(message = "La latitudine è obbligatoria")
        @DecimalMin(
                value = "-90.0",
                message = "La latitudine deve essere maggiore o uguale a -90")
        @DecimalMax(
                value = "90.0",
                message = "La latitudine deve essere minore o uguale a 90")
        Double latitude,

        @NotNull(message = "La longitudine è obbligatoria")
        @DecimalMin(
                value = "-180.0",
                message = "La longitudine deve essere maggiore o uguale a -180")
        @DecimalMax(
                value = "180.0",
                message = "La longitudine deve essere minore o uguale a 180")
        Double longitude
) {
}