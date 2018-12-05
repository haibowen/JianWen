package com.example.administrator.myzhihuproject.internet;


import okhttp3.OkHttpClient;
import okhttp3.Request;

public class MyHttpUtil {

    public static void  SendRequestWithOkHttp(String url, okhttp3.Callback  callback){

        OkHttpClient okHttpClient=new OkHttpClient();
        Request request=new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);


    }


}
