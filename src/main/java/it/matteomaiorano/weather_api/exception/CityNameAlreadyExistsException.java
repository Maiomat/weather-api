package it.matteomaiorano.weather_api.exception;

public class CityNameAlreadyExistsException extends RuntimeException {

    public CityNameAlreadyExistsException(String cityName) {
        super("Esiste già una città con nome: " + cityName);
    }
}