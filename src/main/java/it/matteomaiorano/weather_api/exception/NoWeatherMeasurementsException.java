package it.matteomaiorano.weather_api.exception;

public class NoWeatherMeasurementsException extends RuntimeException {

    public NoWeatherMeasurementsException(String cityName) {
        super("Nessuna rilevazione meteo disponibile per la città: " + cityName);
    }
}