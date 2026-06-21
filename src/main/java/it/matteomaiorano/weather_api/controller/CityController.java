package it.matteomaiorano.weather_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import it.matteomaiorano.weather_api.dto.CityResponse;
import it.matteomaiorano.weather_api.dto.CreateCityRequest;
import it.matteomaiorano.weather_api.service.CityService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cities")
@Tag(name = "Cities", description = "Gestione delle città monitorate")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping
    @Operation(summary = "Inserisce una nuova città", description = """
            Registra una città utilizzando nome, CAP e coordinate.
            Il nome e il CAP devono essere univoci.
            """)
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Città creata correttamente"),
            @ApiResponse(responseCode = "400", description = "Dati della richiesta non validi"),
            @ApiResponse(responseCode = "409", description = "Nome o CAP già presente")
    })
    public ResponseEntity<CityResponse> createCity(
            @Valid @RequestBody CreateCityRequest request) {

        CityResponse response = cityService.createCity(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}