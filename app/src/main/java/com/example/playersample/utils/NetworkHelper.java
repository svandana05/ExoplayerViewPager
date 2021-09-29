package com.example.playersample.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;

public class NetworkHelper {
    private Context context;

    public NetworkHelper(Context context) {
        this.context = context;
    }

    public boolean isNetworkConnected() {
        boolean isNetworkConnected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Network network = connectivityManager.getActiveNetwork();
            NetworkCapabilities networkCapabilities =
                    connectivityManager.getNetworkCapabilities(network);
            if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                isNetworkConnected = true;
            }else if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                isNetworkConnected = true;
            }else if(networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                isNetworkConnected = true;
            }else {
                isNetworkConnected = false;
            }
        } else {
            isNetworkConnected = connectivityManager.getActiveNetworkInfo().isConnected();

        }
        return isNetworkConnected;
    }
}
