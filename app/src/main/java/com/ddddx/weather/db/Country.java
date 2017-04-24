package com.ddddx.weather.db;

import org.litepal.crud.DataSupport;

/**
 *
 * Created by Administrator on 2017/3/28.
 */

public class Country extends DataSupport {
    private int id;
    private String countyrName;
    private int cityId;
    private String weatherId;

    public String getWeatherId() {
        return weatherId;
    }

    public void setWeatherId(String weatherId) {
        this.weatherId = weatherId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public String getCountyrName() {
        return countyrName;
    }

    public void setCountyrName(String countyrName) {
        this.countyrName = countyrName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
