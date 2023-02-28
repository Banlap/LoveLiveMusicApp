package com.banlap.llmusic.utils;

import android.os.Bundle;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class OkhttpUtil {

    private static OkHttpClient mClient;

    public static OkhttpUtil newInstance() { return new OkhttpUtil(); }

    public OkhttpUtil() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .writeTimeout(5, TimeUnit.SECONDS);
        mClient = builder.build();
    }

    public void request(String url, OkHttpCallBack okHttpCallBack){
        try {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Call call = mClient.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    okHttpCallBack.onError(e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) {
                    if(null != response.body()) {
                        okHttpCallBack.onSuccess(response);
                        return;
                    }
                    okHttpCallBack.onError("response.body is null");
                }
            });

        } catch (Exception e) {
            okHttpCallBack.onError(e.getMessage());
        }
    }

    public interface OkHttpCallBack {
        void onSuccess(Response response);
        void onError(String e);
    }
}
