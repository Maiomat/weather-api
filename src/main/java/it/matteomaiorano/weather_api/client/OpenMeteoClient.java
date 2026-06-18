package it.matteomaiorano.weather_api.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import it.matteomaiorano.weather_api.client.dto.OpenMeteoResponse;
import it.matteomaiorano.weather_api.exception.WeatherProviderException;

@Component
public class OpenMeteoClient {

    private final RestClient restClient;

    public OpenMeteoClient(
            RestClient.Builder restClientBuilder,
            @Value("${weather.api.base-url}") String baseUrl) {

        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    public OpenMeteoResponse getCurrentWeather(
            double latitude,
            double longitude) {

        try {
            OpenMeteoResponse response = restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/v1/forecast")
                            .queryParam("latitude", latitude)
                            .queryParam("longitude", longitude)
                            .queryParam("current_weather", true)
                            .build())
                    .retrieve()
                    .body(OpenMeteoResponse.class);

            if (response == null
                    || response.currentWeather() == null
                    || response.currentWeatherUnits() == null) {

                throw new WeatherProviderException(
                        "La risposta di Open-Meteo non contiene i dati meteo attesi");
            }

            return response;

        } catch (RestClientException exception) {
            throw new WeatherProviderException(
                    "Errore durante la comunicazione con Open-Meteo",
                    exception);
        }
    }
}