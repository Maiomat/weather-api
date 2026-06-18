package it.matteomaiorano.weather_api.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.matteomaiorano.weather_api.service.WeatherCollectionService;

@RestController
@RequestMapping("/api/v1/weather/measurements")
public class WeatherCollectionController {

    private final WeatherCollectionService weatherCollectionService;

    public WeatherCollectionController(WeatherCollectionService weatherCollectionService) {
        this.weatherCollectionService = weatherCollectionService;
    }

    @PostMapping("/collect")
    public ResponseEntity<Void> collectWeatherData() {
        weatherCollectionService.collectWeatherData();
        return ResponseEntity.noContent().build();
    }
}
