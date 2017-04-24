package com.ddddx.weather.gson;

import android.provider.ContactsContract;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/4/6.
 */

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More {
        @SerializedName("txt")
        public String info;
    }
}
