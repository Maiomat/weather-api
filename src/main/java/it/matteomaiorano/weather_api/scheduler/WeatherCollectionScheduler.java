package it.matteomaiorano.weather_api.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.matteomaiorano.weather_api.service.WeatherCollectionCoordinator;

@Component
public class WeatherCollectionScheduler {

    private final WeatherCollectionCoordinator coordinator;

    public WeatherCollectionScheduler(
            WeatherCollectionCoordinator coordinator) {

        this.coordinator = coordinator;
    }

    @Scheduled(
            fixedDelayString =
                    "${weather.collection.interval-ms}")
    public void collectWeatherData() {
        coordinator.runScheduledCollection();
    }
}