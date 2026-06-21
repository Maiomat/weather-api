package it.matteomaiorano.weather_api.dto;

public record WeatherAverageResponse(
        String city,
        String postalCode,
        long sampleCount,
        Double averageTemperature,
        String temperatureUnit,
        Double averageWindSpeed,
        String windSpeedUnit
) {
}
