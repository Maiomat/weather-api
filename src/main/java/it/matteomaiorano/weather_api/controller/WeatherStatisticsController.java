package it.matteomaiorano.weather_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.matteomaiorano.weather_api.dto.WeatherAverageResponse;
import it.matteomaiorano.weather_api.service.WeatherStatisticsService;

@RestController
@RequestMapping("/api/v1/weather/averages")
public class WeatherStatisticsController {

    private final WeatherStatisticsService weatherStatisticsService;

    public WeatherStatisticsController(WeatherStatisticsService weatherStatisticsService) {
        this.weatherStatisticsService = weatherStatisticsService;
    }

    @GetMapping("/{postalCode}")
    public WeatherAverageResponse getAverageByPostalCode(
            @PathVariable String postalCode) {

        return weatherStatisticsService
                .getAverageByPostalCode(postalCode);
    }

    @GetMapping
    public List<WeatherAverageResponse> getAllAverages() {
        return weatherStatisticsService.getAllAverages();
    }

}
