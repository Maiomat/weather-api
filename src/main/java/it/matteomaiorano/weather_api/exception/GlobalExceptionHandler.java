package it.matteomaiorano.weather_api.exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CityNotFoundException.class)
    public ProblemDetail handleCityNotFound(
            CityNotFoundException exception) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage());

        problem.setTitle("City not found");

        return problem;
    }

    @ExceptionHandler(NoWeatherMeasurementsException.class)
    public ProblemDetail handleNoWeatherMeasurements(
            NoWeatherMeasurementsException exception) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage());

        problem.setTitle("Weather measurements not found");

        return problem;
    }

    @ExceptionHandler(CityNameAlreadyExistsException.class)
    public ProblemDetail handleCityNameAlreadyExists(
            CityNameAlreadyExistsException exception) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage());

        problem.setTitle("City name already exists");

        return problem;
    }

    @ExceptionHandler(PostalCodeAlreadyExistsException.class)
    public ProblemDetail handlePostalCodeAlreadyExists(
            PostalCodeAlreadyExistsException exception) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage());

        problem.setTitle("Postal code already exists");

        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidationErrors(
            MethodArgumentNotValidException exception) {

        Map<String, String> errors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.putIfAbsent(
                        error.getField(),
                        error.getDefaultMessage()));

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Uno o più campi della richiesta non sono validi");

        problem.setTitle("Validation failed");
        problem.setProperty("errors", errors);

        return problem;
    }
}