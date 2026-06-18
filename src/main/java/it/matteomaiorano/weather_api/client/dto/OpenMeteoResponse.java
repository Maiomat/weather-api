package it.matteomaiorano.weather_api.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OpenMeteoResponse(

        @JsonProperty("current_weather_units")
        CurrentWeatherUnits currentWeatherUnits,

        @JsonProperty("current_weather")
        CurrentWeather currentWeather
) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CurrentWeatherUnits(
            String time,
            String interval,
            String temperature,

            @JsonProperty("windspeed")
            String windSpeed,

            @JsonProperty("winddirection")
            String windDirection,

            @JsonProperty("is_day")
            String isDay,

            @JsonProperty("weathercode")
            String weatherCode
    ) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CurrentWeather(
            String time,
            Integer interval,
            Double temperature,

            @JsonProperty("windspeed")
            Double windSpeed,

            @JsonProperty("winddirection")
            Integer windDirection,

            @JsonProperty("is_day")
            Integer isDay,

            @JsonProperty("weathercode")
            Integer weatherCode
    ) {
    }
}