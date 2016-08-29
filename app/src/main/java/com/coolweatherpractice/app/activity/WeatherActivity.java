package com.coolweatherpractice.app.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolweatherpractice.app.R;
import com.coolweatherpractice.app.util.HttpCallbackListener;
import com.coolweatherpractice.app.util.HttpUtil;
import com.coolweatherpractice.app.util.Utility;

/**
 * Created by WHQ on 2016/8/25.
 * 在 onCreate()方法中仍然
 先是去获取一些控件的实例，然后会尝试从 Intent 中取出县级代号，如果可以取到就会调用
 queryWeatherCode()方法，如果不能取到则会调用 showWeather()方法，我们先来看下可以取
 到的情况。
 queryWeatherCode()方法中并没有几行代码，仅仅是拼装了一个地址，然后调用
 queryFromServer()方法来查询县级代号所对应的天气代号。服务器返回的数据仍然会回调到
 onFinish()方法中，这里对返回的数据进行解析，然后将解析出来的天气代号传入到
 queryWeatherInfo()方法中。
 queryWeatherInfo()方法也非常简单， 同样是拼装了一个地址， 然后调用 queryFromServer()
 方法来查询天气代号所对应的天气信息。由于天气信息是以 JSON 格式返回的，因此我们在
 handleWeatherResponse() 方 法 中 使 用 JSONObject 将 数 据 全 部 解 析 出 来 ， 然 后 调 用
 saveWeatherInfo()方法将所有的天气信息都存储到 SharedPreferences 文件中。注意除了天气
 信息之外，我们还存储了一个 city_selected 标志位，以此来辨别当前是否已经选中了一个城
 市。最后会去调用 showWeather()方法来将所有的天气信息显示到界面上，showWeather()方
 法中的逻辑很简单，就是从 SharedPreferences 文件中将数据读取出来，然后一一设置到界面
 上即可。
 刚才分析的是在 onCreate()方法中可以取到县级代号的情况，那么不能取到的时候呢？
 原来就是直接调用 showWeather()方法来显示本地存储的天气信息就可以了
 */
public class WeatherActivity extends AppCompatActivity implements View.OnClickListener {
    private LinearLayout weatherInfoLayout;
    private TextView cityNameText;///用于显示城市名，发布时间，天气情况，低温，高温，当前时刻
    private TextView publishTime;
    private TextView weatherDespText;
    private TextView temp1Text;
    private TextView temp2Text;
    private TextView currentDateText;
    private Button switchCity;//切换城市按钮
    private Button refreshWeather;//更新天气按钮

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_layout);

        //初始化各种控件
        weatherInfoLayout = (LinearLayout)findViewById(R.id.weather_info_layout);
        cityNameText = (TextView)findViewById(R.id.city_name);
        publishTime = (TextView)findViewById(R.id.publish_time);
        weatherDespText = (TextView)findViewById(R.id.weather_desp);
        temp1Text = (TextView)findViewById(R.id.temp1);
        temp2Text = (TextView)findViewById(R.id.temp2);
        currentDateText = (TextView)findViewById(R.id.current_date);
        switchCity = (Button)findViewById(R.id.switch_city);
        refreshWeather = (Button)findViewById(R.id.refresh_weather);

        String countyCode = getIntent().getStringExtra("county_code");///从上个活动中传入数据
        if(!TextUtils.isEmpty(countyCode)){
            publishTime.setText("同步中…");
            weatherInfoLayout.setVisibility(View.INVISIBLE);//将天气信息窗口隐藏起来
            cityNameText.setVisibility(View.INVISIBLE);//隐藏城市名
            queryWeatherCode(countyCode); //根据县级代号查询天气信息

        } else {
            showWeather();//没有县级代号时直接显示本地天气
        }
        switchCity.setOnClickListener(this);//切换城市按钮的监听器
        refreshWeather.setOnClickListener(this);//刷新按钮的监听器
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.switch_city:
                Intent intent = new Intent(this,ChooseAreaActivity.class);
                intent.putExtra("from weather_activity",true);
                startActivity(intent);//进入选择地点的界面，重新选择城市
                finish();
                break;
            case R.id.refresh_weather:
                publishTime.setText("同步中…");
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String weatherCode = prefs.getString("weather_code","");
                if(!TextUtils.isEmpty(weatherCode)){
                    queryWeatherInfo(weatherCode);//根据天气代号查询天气信息
                }
                break;
            default:
                break;
        }
    }

    /**
     * 查询县级代号所对应的天气信息
     * @param countyCode
     */
    private void queryWeatherCode(String countyCode){
        String address = "http://www.weather.com.cn/data/list3/city" + countyCode + ".xml";
        queryFromServer(address,"countyCode");
    }

    /**
     * 查询天气代号对应的天气
     * @param weatherCode
     */
    private void queryWeatherInfo(String weatherCode){
        String address = "http://www.weather.com.cn/data/cityinfo" + weatherCode + ".html";
        queryFromServer(address,"weatherCode");
    }

    /**
     * 根据传入地址和类型向服务器查询天气代号或天气信息
     * @param address
     * @param type 1. countyCode:程序先获取天气代号，再获取天气信息;  2. weatherCode:程序直接获取天气信息
     */
    private void queryFromServer(final String address,final String type){
        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {//发送服务器请求
            @Override
            public void onFinish(String response) {
                if("countyCode".equals(type)){  //如果查询的类型是县级代号，则从服务器返回的数据中解析天气代号
                    if(!TextUtils.isEmpty(response)){  //服务器返回的天气代号数据为： 190404|101190404。两部分，前面是县级代号，后面是天气代号
                        String[] array = response.split("\\|");
                        if(array != null && array.length == 2){
                            String weatherCode = array[1];//获得天气代号
                            queryWeatherInfo(weatherCode);//再去查询天气信息
                        }
                    }
                }else if("weatherCode".equals(type)){
                    Utility.handleWeatherResponse(WeatherActivity.this,response);//解析服务器返回的JSON数据，获取天气信息，并保存到本地
                    runOnUiThread(new Runnable() {//必须在 主线程中进行UI界面操作
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        publishTime.setText("同步失败");
                    }
                });
            }
        });
    }

    /**
     * 从SharedPreferences文件中读取存储的天气信息，并显示到界面上
     */
    private void showWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        cityNameText.setText(prefs.getString("city_name",""));
        temp1Text.setText(prefs.getString("temp1",""));
        temp2Text.setText(prefs.getString("temp2",""));
        weatherDespText.setText(prefs.getString("weather_desp",""));
        publishTime.setText("今天"+prefs.getString("publish_time","")+"发布");
        currentDateText.setText(prefs.getString("current_date",""));
        weatherInfoLayout.setVisibility(View.VISIBLE);
        cityNameText.setVisibility(View.VISIBLE);
    }
}
