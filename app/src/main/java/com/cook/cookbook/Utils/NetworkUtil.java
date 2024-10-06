package com.cook.cookbook.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {

    public static boolean isConnectedToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager !=null){
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
           return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
            return  false;
    }
}
