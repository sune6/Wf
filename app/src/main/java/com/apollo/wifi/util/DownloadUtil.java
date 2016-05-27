package com.apollo.wifi.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

/**
 * Created by Sun
 * <p/>
 * 2016/5/20 16:22
 */
public class DownloadUtil {

    public static void downloadRootApk(Context context, String url) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        String fileName = url.hashCode() + ".apk";
        request.setDestinationInExternalPublicDir("WiFiManager", fileName);
// request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        downloadManager.enqueue(request);
    }
}
