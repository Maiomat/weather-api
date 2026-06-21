package it.matteomaiorano.weather_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateCityRequest(

        @Schema(
                description = "Nome della città",
                example = "Salerno")
        @NotBlank(message = "Il nome della città è obbligatorio")
        @Size(
                max = 100,
                message = "Il nome non può superare 100 caratteri")
        String name,

        @Schema(
                description = "CAP univoco associato alla città",
                example = "84121")
        @NotBlank(message = "Il CAP è obbligatorio")
        @Pattern(
                regexp = "\\d{5}",
                message = "Il CAP deve contenere esattamente 5 cifre")
        String postalCode,

        @Schema(
                description = "Latitudine geografica",
                example = "40.6824")
        @NotNull(message = "La latitudine è obbligatoria")
        @DecimalMin(value = "-90.0")
        @DecimalMax(value = "90.0")
        Double latitude,

        @Schema(
                description = "Longitudine geografica",
                example = "14.7681")
        @NotNull(message = "La longitudine è obbligatoria")
        @DecimalMin(value = "-180.0")
        @DecimalMax(value = "180.0")
        Double longitude
) {
}