package com.coolweatherpractice.app.model;

/**
 * Created by WHQ on 2016/8/17.
 * 省 类
 */
public class Province {
    private int id;
    private String provinceName;
    private String provinceCode;

    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }

    public String getProvinceName(){
        return provinceName;
    }
    public void setProvinceName(String name){
        this.provinceName = name;
    }

    public String getProvinceCode(){
        return provinceCode;
    }
    public void setProvinceCode(String code){
        this.provinceCode = code;
    }
}
