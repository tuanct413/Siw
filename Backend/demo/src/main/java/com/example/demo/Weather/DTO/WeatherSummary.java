package com.example.demo.Weather.DTO;


public class WeatherSummary {
    private String city;
    private String country ;
    private double temperatureC;   // Nhiệt độ °C
    private String condition;      // Mưa / Nắng / Mây
    private int humidity;          // Độ ẩm %
    private double windKph;        // Gió km/h
    private double visibilityKm;   // Tầm nhìn km
    private int uvIndex;           // Chỉ số UV



    // getters & setters
    public double getTemperatureC() {
        return temperatureC;
    }

    public void setTemperatureC(double temperatureC) {
        this.temperatureC = temperatureC;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public int getHumidity() {
        return humidity;
    }

    public void setHumidity(int humidity) {
        this.humidity = humidity;
    }

    public double getWindKph() {
        return windKph;
    }

    public void setWindKph(double windKph) {
        this.windKph = windKph;
    }

    public double getVisibilityKm() {
        return visibilityKm;
    }

    public void setVisibilityKm(double visibilityKm) {
        this.visibilityKm = visibilityKm;
    }

    public int getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(int uvIndex) {
        this.uvIndex = uvIndex;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }


}
