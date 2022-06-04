package com.example.atulweather.WeatherModal;

public class WeatherModal {

    private String time;
    private String icon;
    private String temp;
    private String windSpeed;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public WeatherModal(String time, String icon, String temp, String windSpeed) {
        this.time = time;
        this.icon = icon;
        this.temp = temp;
        this.windSpeed = windSpeed;


    }
}
