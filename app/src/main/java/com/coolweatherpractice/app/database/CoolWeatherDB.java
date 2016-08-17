package com.coolweatherpractice.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweatherpractice.app.model.City;
import com.coolweatherpractice.app.model.County;
import com.coolweatherpractice.app.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by WHQ on 2016/8/17.
 * CoolWeather类，将一些常用的数据库操作封装起来
 * CoolWeatherDB 是一个单例类，我们将它的构造方法私有化，并提供了一个 getInstance()方法来获取 CoolWeatherDB 的实例，
 * 这样就可以保证全局范围内只会有一个 CoolWeatherDB 的实例。接下来我们在 CoolWeatherDB 中提供了六组方法，
 * saveProvince()、 loadProvinces()、saveCity()、loadCities()、saveCounty()、loadCounties()，
 * 分别用于存储省份数据、读取所有省份数据、存储城市数据、读取某省内所有城市数据、存储县数据、读取某 市内所有县的数据
 */
public class CoolWeatherDB {
    /**
     * 数据库名
     */
    public static final String DB_NAME = "cool_weather";

    /**
     * 数据库版本
     */
    public static final int VERSION = 1;

    private static CoolWeatherDB coolweatherDB;
    private SQLiteDatabase database;

    /**
     * 构造方法私有化，构造了包含省、市、县的数据库database
     * 因为是用getInstance()函数来实例化类的，该构造方法也是在getInstance()函数中调用，所有被设置为private型
     * @param context
     */
    private CoolWeatherDB(Context context){
        CoolWeatherPracticeOpenHelper dbHelper = new CoolWeatherPracticeOpenHelper(context,DB_NAME,null,VERSION);
        database = dbHelper.getWritableDatabase();
    }

    /**
     * getInstance()用于实例化CoolWeatherDB:如果已经实例化，则不变；如果还未实例化，则new CoolWeatherDB(context)
     * synchronized：Java语言的关键字，可用来给对象和方法或者代码块加锁，当它锁定一个方法或者一个代码块的时候，同一时刻最多只有一个线程执行这段代码。
     * @param context
     * @return 一个实例化的CoolWeatherDB类
     */
    public synchronized static CoolWeatherDB getInstance(Context context){
        if (coolweatherDB == null){
            coolweatherDB = new CoolWeatherDB(context);
        }
        return coolweatherDB;
    }

    /**
     * 将Province实例化存储到数据库中
     * @param province
     */
    public void saveProvince(Province province){
        if (province != null){
            ContentValues values = new ContentValues();
            values.put("province_name",province.getProvinceName());
            values.put("province_code",province.getProvinceCode());
            database.insert("Province",null,values);
        }
    }

    /**
     *从数据库中的读取全国的省份信息
     * @return list<Province>
     */
    public List<Province> loadProvinces(){
        List<Province> list = new ArrayList<Province>();
        Cursor cursor = database.query("Province",null,null,null,null,null,null,null);//查找数据库中的Province
                                                        //一次可以查找8项，没有的话则为null
        if (cursor.moveToFirst()){  //将curor的游标定位到第一行
            do{
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvinceName(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvinceCode(cursor.getString(cursor.getColumnIndex("province_code")));
                                                    //getColumnIndex(String columnName)——返回指定列的名称，如果不存在返回-1
                                                    //cursor.getInt/String——获得这一行的int/string
                list.add(province);
            }while(cursor.moveToNext());    ////将curor的游标移动到下一行
        }
        return list;
    }

    /**
     * 将City实例化存储到数据库中
     * @param city
     */
    public void saveCity(City city){
        if (city != null){
            ContentValues values = new ContentValues();
            values.put("city_name",city.getCityName());
            values.put("city_code",city.getCityCode());
            values.put("province_id",city.getProvinceId());
            database.insert("City",null,values);
        }
    }

    /**
     *从数据库中的读取各市信息
     * @return list<City>
     */
    public List<City> loadCites(int provinceId){
        List<City> list = new ArrayList<City>();
        Cursor cursor = database.query("City",null,"province_id = ?",new String[]{String.valueOf(provinceId)},null,null,null,null);//查找数据库中的City
        //Cursor cursor = database.query("City",null,"province_id",new String[]{String.valueOf(provinceId)},null,null,null);
        //一次可以查找8项，没有的话则为null
        if (cursor.moveToFirst()){  //将curor的游标定位到第一行
            do{
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCityName(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCityCode(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvinceId(provinceId);
                                        //getColumnIndex(String columnName)——返回指定列的名称，如果不存在返回-1
                                        //cursor.getInt/String——获得这一行的int/string
                list.add(city);
            }while(cursor.moveToNext());    ////将curor的游标移动到下一行
        }
        return list;
    }

    /**
     * 将County实例化存储到数据库中
     * @param county
     */
    public void saveCounty(County county){
        if (county != null){
            ContentValues values = new ContentValues();
            values.put("county_name",county.getCountyName());
            values.put("county_code",county.getCountyCode());
            values.put("city_id",county.getCityId());
            database.insert("County",null,values);
        }
    }

    /**
     *从数据库中的读取各县信息
     * @return list<County>
     */
    public List<County> loadCounties(int cityId){
        List<County> list = new ArrayList<County>();
        Cursor cursor = database.query("County",null,"city_id = ?",new String[]{String.valueOf(cityId)},null,null,null,null);//查找数据库中的City
        //一次可以查找8项，没有的话则为null
        if (cursor.moveToFirst()){  //将curor的游标定位到第一行
            do{
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCountyName(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCountyCode(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCityId(cityId);
                                            //getColumnIndex(String columnName)——返回指定列的名称，如果不存在返回-1
                                            //cursor.getInt/String——获得这一行的int/string
                list.add(county);
            }while(cursor.moveToNext());    ////将curor的游标移动到下一行
        }
        return list;
    }
}
