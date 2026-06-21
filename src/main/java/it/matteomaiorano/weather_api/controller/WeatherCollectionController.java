package it.matteomaiorano.weather_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.matteomaiorano.weather_api.service.WeatherCollectionCoordinator;

@RestController
@RequestMapping("/api/v1/weather/measurements")
public class WeatherCollectionController {

    private final WeatherCollectionCoordinator coordinator;

    public WeatherCollectionController(
            WeatherCollectionCoordinator coordinator) {

        this.coordinator = coordinator;
    }

    @PostMapping("/collect")
    public ResponseEntity<Void> collectWeatherData() {
        boolean accepted =
                coordinator.startAsyncCollection();

        if (!accepted) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }

        return ResponseEntity.accepted().build();
    }
}