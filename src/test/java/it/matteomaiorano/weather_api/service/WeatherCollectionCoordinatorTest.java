package it.matteomaiorano.weather_api.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.TaskExecutor;

@ExtendWith(MockitoExtension.class)
class WeatherCollectionCoordinatorTest {

    @Mock
    private WeatherCollectionService weatherCollectionService;

    @Mock
    private TaskExecutor taskExecutor;

    @InjectMocks
    private WeatherCollectionCoordinator coordinator;

    @Test
    void shouldStartCollectionAsynchronously() {
        ArgumentCaptor<Runnable> taskCaptor =
                ArgumentCaptor.forClass(Runnable.class);

        boolean accepted = coordinator.startAsyncCollection();

        assertTrue(accepted);
        assertTrue(coordinator.isCollectionRunning());

        verify(taskExecutor).execute(taskCaptor.capture());

        taskCaptor.getValue().run();

        verify(weatherCollectionService).collectWeatherData();
        assertFalse(coordinator.isCollectionRunning());
    }

    @Test
    void shouldRejectAnotherCollectionWhileOneIsRunning() {
        boolean firstAccepted =
                coordinator.startAsyncCollection();

        boolean secondAccepted =
                coordinator.startAsyncCollection();

        assertTrue(firstAccepted);
        assertFalse(secondAccepted);

        verify(taskExecutor, times(1))
                .execute(org.mockito.ArgumentMatchers.any(Runnable.class));
    }
}