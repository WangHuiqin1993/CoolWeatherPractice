package com.coolweatherpractice.app.util;

/**
 * Created by WHQ on 2016/8/24.
 * HttpCallbackListener：接口
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}
