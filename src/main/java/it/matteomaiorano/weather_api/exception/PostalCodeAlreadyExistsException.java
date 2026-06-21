package it.matteomaiorano.weather_api.exception;

public class PostalCodeAlreadyExistsException extends RuntimeException {

    public PostalCodeAlreadyExistsException(String postalCode) {
        super("Esiste già una città con CAP: " + postalCode);
    }
}