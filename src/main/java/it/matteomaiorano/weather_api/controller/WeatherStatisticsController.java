package it.matteomaiorano.weather_api.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.matteomaiorano.weather_api.dto.WeatherAverageResponse;
import it.matteomaiorano.weather_api.service.WeatherStatisticsService;

@RestController
@RequestMapping("/api/v1/weather/averages")
@Tag(name = "Weather statistics", description = "Consultazione delle medie meteorologiche precalcolate")
public class WeatherStatisticsController {

    private final WeatherStatisticsService weatherStatisticsService;

    public WeatherStatisticsController(WeatherStatisticsService weatherStatisticsService) {
        this.weatherStatisticsService = weatherStatisticsService;
    }

    @GetMapping("/{postalCode}")
    @Operation(summary = "Restituisce le medie di una città tramite CAP", description = """
            Recupera le medie meteorologiche precalcolate
            associate alla città identificata dal CAP.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Medie recuperate correttamente"),
            @ApiResponse(responseCode = "404", description = "CAP inesistente o città senza rilevazioni")
    })
    public WeatherAverageResponse getAverageByPostalCode(
            @Parameter(description = "CAP della città", example = "50121") @PathVariable String postalCode) {

        return weatherStatisticsService
                .getAverageByPostalCode(postalCode);
    }

    @GetMapping
    @Operation(summary = "Restituisce le medie di tutte le città", description = """
            Restituisce le statistiche delle città che dispongono
            di almeno una rilevazione meteorologica.
            """)
    @ApiResponse(responseCode = "200", description = "Elenco delle medie recuperato correttamente")
    public List<WeatherAverageResponse> getAllAverages() {
        return weatherStatisticsService.getAllAverages();
    }

}
