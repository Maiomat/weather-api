package it.matteomaiorano.weather_api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "cities")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "postal_code", nullable = false, unique = true, length = 5)
    private String postalCode;

    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    @Column(name = "average_temperature", nullable = false)
    private Double averageTemperature = 0.0;

    @Column(name = "temperature_unit", length = 10)
    private String temperatureUnit;

    @Column(name = "average_wind_speed", nullable = false)
    private Double averageWindSpeed = 0.0;

    @Column(name = "wind_speed_unit", length = 10)
    private String windSpeedUnit;

    @Column(name = "measurements_count", nullable = false)
    private Long measurementsCount = 0L;

    protected City() {
        // Costruttore richiesto da JPA
    }

    public City(
            String name,
            String postalCode,
            Double latitude,
            Double longitude) {

        this.name = name;
        this.postalCode = postalCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void updateWeatherAverages(
            double temperature,
            double windSpeed,
            String temperatureUnit,
            String windSpeedUnit) {

        long newMeasurementsCount = measurementsCount + 1;

        averageTemperature = averageTemperature
                + (temperature - averageTemperature)
                        / newMeasurementsCount;

        averageWindSpeed = averageWindSpeed
                + (windSpeed - averageWindSpeed)
                        / newMeasurementsCount;

        this.temperatureUnit = temperatureUnit;
        this.windSpeedUnit = windSpeedUnit;
        measurementsCount = newMeasurementsCount;
    }

    public String getTemperatureUnit() {
        return temperatureUnit;
    }

    public String getWindSpeedUnit() {
        return windSpeedUnit;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Double getAverageTemperature() {
        return averageTemperature;
    }

    public Double getAverageWindSpeed() {
        return averageWindSpeed;
    }

    public Long getMeasurementsCount() {
        return measurementsCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
}