package com.coolweatherpractice.app.model;

/**
 * Created by WHQ on 2016/8/17.
 * 县 类
 */
public class County {
    private int id;
    private String countyName;
    private String countyCode;
    private int cityId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String name) {
        this.countyName = name;
    }

    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String code) {
        this.countyCode = code;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cid) {
        this.cityId = cid;
    }
}
