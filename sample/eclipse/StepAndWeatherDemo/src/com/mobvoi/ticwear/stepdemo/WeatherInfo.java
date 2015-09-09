package com.mobvoi.ticwear.stepdemo;

/**
 * 天气信息
 */
public class WeatherInfo{
    public String time;
    public String temp;
    public String address;
    public String location;
    public String maxtemp;
    public String mintemp;
    public String pm25;
    public String weather;
    public String sunset;
    public String sunrise;

    @Override
    public String toString() {
        return "WeatherInfo{" +
                "time='" + time + '\'' +
                ", temp='" + temp + '\'' +
                ", address='" + address + '\'' +
                ", location='" + location + '\'' +
                ", maxtemp='" + maxtemp + '\'' +
                ", mintemp='" + mintemp + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", weather='" + weather + '\'' +
                ", sunset='" + sunset + '\'' +
                ", sunrise='" + sunrise + '\'' +
                '}';
    }
}
