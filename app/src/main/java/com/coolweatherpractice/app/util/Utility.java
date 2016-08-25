package com.coolweatherpractice.app.util;

import android.text.TextUtils;

import com.coolweatherpractice.app.database.CoolWeatherDB;
import com.coolweatherpractice.app.model.City;
import com.coolweatherpractice.app.model.County;
import com.coolweatherpractice.app.model.Province;

/**
 * Created by WHQ on 2016/8/24.
 * 数据解析类: 提 供 了 handleProvincesResponse() 、 handleCitiesResponse() 、 handleCountiesResponse()这三个方法，分别用于解析和处理服务器返回的省级、市级和县级数据。
 * 服务器返回的省市县数据都是“代号|城市,代号|城市”这种格式的,需要提供一个工具类来解析和处理这种数据
 * 解析的规则: 先按逗号分隔，再按单竖线分隔，接着将解析出来的数据设置到实体类中，最后调用 CoolWeatherDB 中的三个 save()方法将数据存储到相应的表中。
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
}
