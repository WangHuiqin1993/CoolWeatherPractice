package com.coolweatherpractice.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.SimpleDateFormat;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.coolweatherpractice.app.database.CoolWeatherDB;
import com.coolweatherpractice.app.model.City;
import com.coolweatherpractice.app.model.County;
import com.coolweatherpractice.app.model.Province;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.util.Date;
import java.util.Locale;

/**
 * Created by WHQ on 2016/8/24.
 * 数据解析类: 提 供 了 handleProvincesResponse() 、 handleCitiesResponse() 、 handleCountiesResponse()这三个方法，分别用于解析和处理服务器返回的省级、市级和县级数据。
 * 服务器返回的省市县数据都是“代号|城市,代号|城市”这种格式的,需要提供一个工具类来解析和处理这种数据
 * 解析的规则: 先按逗号分隔，再按单竖线分隔，接着将解析出来的数据设置到实体类中，最后调用 CoolWeatherDB 中的三个 save()方法将数据存储到相应的表中。
 * 解析和处理服务返回的 JSON 数据：其中 handleWeatherResponse()方法用于将 JSON 格式的天气信息全部解析出来，saveWeatherInfo()方法用于将这些数据都存储到 SharedPreferences 文件中。
 */
public class Utility {
    /**
     * 解析和处理服务器返回的省级数据
     */
    public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB, String response){
        if(!TextUtils.isEmpty(response)){
            String[] allProvinces = response.split(",");
            if(allProvinces != null && allProvinces.length > 0){
                for(String p : allProvinces){   //遍历allProvinces
                    String[] array = p.split("\\|");
                    Province province = new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    coolWeatherDB.saveProvince(province);//将解析的数据存储到Province表中
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的市级数据
     */
    public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB, String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities = response.split(",");
            if(allCities != null && allCities.length > 0){
                for(String c : allCities){   //遍历allCities
                    String[] array = c.split("\\|");
                    City city = new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);//将解析的数据存储到City表中
                }
                return true;
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的县级数据
     */
    public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB, String response, int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties = response.split(",");
            if(allCounties != null && allCounties.length > 0){
                for(String c : allCounties){   //遍历allCounties
                    String[] array = c.split("\\|");
                    County county = new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);//将解析的数据存储到County表中
                }
                return true;
            }
        }
        return false;
    }

    ////////////////////////////////////

    /**
     * 解析服务器返回的JSON数据，并将解析出的数据存储到本地
     * @param context
     * @param response
     */
    public static void handleWeatherResponse(Context context,String response){
        try {
            JSONObject jsonObject = new JSONObject(response);//接收服务器返回的JSON数据
            JSONObject weatherInfo = jsonObject.getJSONObject("weatherinfo");//在所有JSON数据中挑出weatherinfo的JSON数据
            String cityName = weatherInfo.getString("city");///解析JSON数据
            String weatherCode = weatherInfo.getString("cityid");
            String temp1 = weatherInfo.getString("temp1");
            String temp2 = weatherInfo.getString("temp2");
            String weatherDesp = weatherInfo.getString("weather");
            String publishTime = weatherInfo.getString("ptime");

            saveWeatherInfo(context,cityName,weatherCode,temp1,temp2,weatherDesp,publishTime);//保存解析的数据
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将服务器返回的所有天气信息存储到SharedPreferences中
     * @param context
     * @param cityName 城市名
     * @param weatherCode 天气的代码，那串数字
     * @param temp1 最低温度
     * @param temp2 最高温度
     * @param weatherDesp 天气信息
     * @param publishTime 发布时间
     */
    public static void saveWeatherInfo(Context context,String cityName,String weatherCode,
                                       String temp1,String temp2,String weatherDesp,String publishTime){
        //SimpleDateFormat sdf = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);//要求API>=24
        long time = System.currentTimeMillis();//获取时间
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected",true);
        editor.putString("city_name",cityName);
        editor.putString("weather_code",weatherCode);
        editor.putString("temp1",temp1);
        editor.putString("temp2",temp2);
        editor.putString("weather_desp",weatherDesp);
        editor.putString("publish_time",publishTime);
        //editor.putString("current_date",sdf.format(new Date()));
        editor.putString("current_date", String.valueOf(time));//
        editor.commit();
    }
}
