package com.apollo.wifi.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * Created by Sun
 *
 * 2016/5/16 17:04
 */
public class NetworkUtil {
    /**
     * 网络是否可用
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * 根据网络类型处理
     */
    public static void testNetworkType(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        switch (activeNetwork.getType()) {
            case (ConnectivityManager.TYPE_WIFI):
                //wifi

                break;
            case (ConnectivityManager.TYPE_MOBILE): {
                switch (tm.getNetworkType()) {
                    case (TelephonyManager.NETWORK_TYPE_LTE | TelephonyManager.NETWORK_TYPE_HSPAP):
                        //3G、4G

                        break;
                    case (TelephonyManager.NETWORK_TYPE_EDGE | TelephonyManager.NETWORK_TYPE_GPRS):
                        //2G

                        break;
                    default:
                        break;
                }
                break;
            }
            default:
                break;
        }
    }
}
