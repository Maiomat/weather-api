package it.matteomaiorano.weather_api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.matteomaiorano.weather_api.dto.CityResponse;
import it.matteomaiorano.weather_api.dto.CreateCityRequest;
import it.matteomaiorano.weather_api.service.CityService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/cities")
public class CityController {

    private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    @PostMapping
    public ResponseEntity<CityResponse> createCity(
            @Valid @RequestBody CreateCityRequest request) {

        CityResponse response = cityService.createCity(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}