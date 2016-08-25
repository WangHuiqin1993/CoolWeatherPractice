package com.coolweatherpractice.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by WHQ on 2016/8/17.
 * 与服务器的交互（为获得省市县信息）
 */
public class HttpUtil {
    public static void sendHttpRequest(final String address, final HttpCallbackListener listener){
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(address);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");//GET型请求
                    connection.setConnectTimeout(8000);
                    connection.setReadTimeout(8000);
                    InputStream in = connection.getInputStream();//推流
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));//读取缓冲区
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null){
                        response.append(line);
                    }
                    if(listener != null){
                        listener.onFinish(response.toString());//回调接口HttpCallbackListener的onFinish()方法
                    }
                } catch (Exception e){
                    if(listener != null){
                        listener.onError(e);//回调onError方法
                    }
                }finally {
                    if(connection != null){
                        connection.disconnect();
                    }
                }
            }
        }).start();
    }
}
