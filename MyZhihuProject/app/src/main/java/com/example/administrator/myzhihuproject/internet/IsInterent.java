package com.example.administrator.myzhihuproject.internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class IsInterent {

    public static boolean isConnect(Context context) {

        //得到管理网络服务的实例
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //得到网络信息
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());


    }
}
