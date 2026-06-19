package it.matteomaiorano.weather_api.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.matteomaiorano.weather_api.service.WeatherCollectionService;

@Component
public class WeatherCollectionScheduler {

    private final WeatherCollectionService weatherCollectionService;

    public WeatherCollectionScheduler(WeatherCollectionService weatherCollectionService) {
        this.weatherCollectionService = weatherCollectionService;
    }

    @Scheduled(fixedDelayString = "${weather.collection.interval-ms}")
    public void collectWeatherData() {
        weatherCollectionService.collectWeatherData();
    }

}
