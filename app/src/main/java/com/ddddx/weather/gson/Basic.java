package com.ddddx.weather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * "city":"北京",
 "cnty":"中国",
 "id":"CN101010100",
 "lat":"39.904989",
 "lon":"116.405285",
 "update":{
 "loc":"2017-04-06 11:51",
 "utc":"2017-04-06 03:51"
 }
 * Created by Administrator on 2017/4/6.
 */

public class Basic {

    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }
}
