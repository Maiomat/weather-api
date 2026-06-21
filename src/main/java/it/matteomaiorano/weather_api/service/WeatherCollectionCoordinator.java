package it.matteomaiorano.weather_api.service;

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class WeatherCollectionCoordinator {

    private static final Logger logger =
            LoggerFactory.getLogger(
                    WeatherCollectionCoordinator.class);

    private final WeatherCollectionService weatherCollectionService;
    private final TaskExecutor taskExecutor;

    private final AtomicBoolean collectionRunning =
            new AtomicBoolean(false);

    public WeatherCollectionCoordinator(
            WeatherCollectionService weatherCollectionService,
            @Qualifier("weatherCollectionTaskExecutor")
            TaskExecutor taskExecutor) {

        this.weatherCollectionService =
                weatherCollectionService;

        this.taskExecutor = taskExecutor;
    }

    public boolean startAsyncCollection() {
        if (!collectionRunning.compareAndSet(false, true)) {
            return false;
        }

        try {
            taskExecutor.execute(this::executeCollection);
            return true;
        } catch (RuntimeException exception) {
            collectionRunning.set(false);
            throw exception;
        }
    }

    public boolean runScheduledCollection() {
        if (!collectionRunning.compareAndSet(false, true)) {
            logger.info(
                    "Raccolta pianificata ignorata: "
                            + "un'altra raccolta è già in corso");

            return false;
        }

        executeCollection();
        return true;
    }

    private void executeCollection() {
        try {
            logger.info("Avvio raccolta dei dati meteo");

            weatherCollectionService.collectWeatherData();

            logger.info("Raccolta dei dati meteo completata");
        } catch (RuntimeException exception) {
            logger.error(
                    "Errore inatteso durante la raccolta meteo",
                    exception);
        } finally {
            collectionRunning.set(false);
        }
    }

    public boolean isCollectionRunning() {
        return collectionRunning.get();
    }
}