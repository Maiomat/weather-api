package it.matteomaiorano.weather_api.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "weather_measurements")
public class WeatherMeasurement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @Column(nullable = false)
    private Double temperature;

    @Column(name = "wind_speed", nullable = false)
    private Double windSpeed;

    @Column(name = "wind_direction", nullable = false)
    private Integer windDirection;

    @Column(name = "weather_code", nullable = false)
    private Integer weatherCode;

    @Column(name = "is_day", nullable = false)
    private Boolean day;

    @Column(name = "temperature_unit", nullable = false, length = 10)
    private String temperatureUnit;

    @Column(name = "wind_speed_unit", nullable = false, length = 10)
    private String windSpeedUnit;

    @Column(name = "wind_direction_unit", nullable = false, length = 10)
    private String windDirectionUnit;

    protected WeatherMeasurement() {
        // Costruttore richiesto da JPA
    }

    public WeatherMeasurement(
            City city,
            LocalDateTime measuredAt,
            Double temperature,
            Double windSpeed,
            Integer windDirection,
            Integer weatherCode,
            Boolean day,
            String temperatureUnit,
            String windSpeedUnit,
            String windDirectionUnit) {

        this.city = city;
        this.measuredAt = measuredAt;
        this.temperature = temperature;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.weatherCode = weatherCode;
        this.day = day;
        this.temperatureUnit = temperatureUnit;
        this.windSpeedUnit = windSpeedUnit;
        this.windDirectionUnit = windDirectionUnit;
    }

    public Long getId() {
        return id;
    }

    public City getCity() {
        return city;
    }

    public LocalDateTime getMeasuredAt() {
        return measuredAt;
    }

    public Double getTemperature() {
        return temperature;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public Integer getWindDirection() {
        return windDirection;
    }

    public Integer getWeatherCode() {
        return weatherCode;
    }

    public Boolean getDay() {
        return day;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public String getWindSpeedUnit() {
        return windSpeedUnit;
    }

    public String getWindDirectionUnit() {
        return windDirectionUnit;
    }
}