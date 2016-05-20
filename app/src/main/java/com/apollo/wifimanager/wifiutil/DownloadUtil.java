package com.apollo.wifimanager.wifiutil;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;

/**
 * Created by Sun
 * <p/>
 * 2016/5/20 16:22
 */
public class DownloadUtil {
    private static final String apkUrl = "http://king.myapp.com/myapp/kdown/img/NewKingrootV4.92_C143_B263_office_release_2016_05_09_105003_1.apk";

    public static void downloadRootApk(Context context) {
        DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir("WiFiManager", "root.apk");
// request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
// request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        long downloadId = downloadManager.enqueue(request);
    }
}
