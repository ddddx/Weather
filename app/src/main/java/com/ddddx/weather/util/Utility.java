package com.ddddx.weather.util;

import android.text.TextUtils;

import com.ddddx.weather.db.City;
import com.ddddx.weather.db.Country;
import com.ddddx.weather.db.Province;
import com.ddddx.weather.gson.Weather;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Created by Administrator on 2017/3/28.
 */

public class Utility {
    public static boolean handleProvinceResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allProvince = new JSONArray(response);
                for (int i = 0;i<allProvince.length();i++){
                    JSONObject jsonObject = allProvince.getJSONObject(i);
                    Province province = new Province();
                    province.setProvinceName(jsonObject.getString("name"));
                    province.setProvinceCode(jsonObject.getInt("id"));
                    province.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCityResponse(String response,int provinceId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCity = new JSONArray(response);
                for (int i = 0;i<allCity.length();i++){
                    JSONObject jsonObject = allCity.getJSONObject(i);
                    City city = new City();
                    city.setCityCode(jsonObject.getInt("id"));
                    city.setCityName(jsonObject.getString("name"));
                    city.setProvinceId(provinceId);
                    city.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static boolean handleCountryResponse(String response,int cityId){
        if (!TextUtils.isEmpty(response)){
            try {
                JSONArray allCountry = new JSONArray(response);
                for (int i = 0;i<allCountry.length();i++){
                    JSONObject jsonObject = allCountry.getJSONObject(i);
                    Country country = new Country();
                    country.setCountyrName(jsonObject.getString("name"));
                    country.setWeatherId(jsonObject.getString("weather_id"));
                    country.setCityId(cityId);
                    country.save();
                }
                return true;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public static Weather handleWeatherResponse(String response){
        try {
            JSONObject object = new JSONObject(response);
            JSONArray jsonArray = object.getJSONArray("HeWeather5");
            String weather = jsonArray.getJSONObject(0).toString();
            return new Gson().fromJson(weather,Weather.class);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
