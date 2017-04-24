package com.ddddx.weather.gson;

/**
 * Created by Administrator on 2017/4/6.
 */

public class AQI {
    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
