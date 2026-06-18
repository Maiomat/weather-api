package it.matteomaiorano.weather_api.exception;

public class CityNotFoundException extends RuntimeException {

    public CityNotFoundException(String cityName) {
        super("Città non trovata: " + cityName);
    }
}