package it.matteomaiorano.weather_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class TaskExecutionConfig {

    @Bean(name = "weatherCollectionTaskExecutor")
    ThreadPoolTaskExecutor weatherCollectionTaskExecutor() {
        ThreadPoolTaskExecutor executor =
                new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(0);
        executor.setThreadNamePrefix("weather-collection-");
        executor.initialize();

        return executor;
    }
}