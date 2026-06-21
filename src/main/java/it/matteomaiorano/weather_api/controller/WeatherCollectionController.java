package it.matteomaiorano.weather_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.matteomaiorano.weather_api.service.WeatherCollectionCoordinator;

@RestController
@RequestMapping("/api/v1/weather/measurements")
@Tag(name = "Weather collection", description = "Avvio della raccolta dei dati meteorologici")
public class WeatherCollectionController {

    private final WeatherCollectionCoordinator coordinator;

    public WeatherCollectionController(
            WeatherCollectionCoordinator coordinator) {

        this.coordinator = coordinator;
    }

    @PostMapping("/collect")
    @Operation(summary = "Avvia la raccolta meteorologica", description = """
            Avvia in background la raccolta dei dati per tutte
            le città configurate e restituisce immediatamente
            una risposta al client.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "202", description = "Raccolta accettata e avviata in background"),
            @ApiResponse(responseCode = "409", description = "Un'altra raccolta è già in corso")
    })
    public ResponseEntity<Void> collectWeatherData() {
        boolean accepted = coordinator.startAsyncCollection();

        if (!accepted) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .build();
        }

        return ResponseEntity.accepted().build();
    }
}