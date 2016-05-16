package com.apollo.wifimanager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.apollo.wifimanager.wifiutil.Info;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity----";
    private Info info = new Info();
    private boolean flag;
    private int last_degree = 0, cur_degree;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x123:
                    Log.i(TAG, "new speed:" + msg.arg1 + "KB/S");
                    Log.i(TAG, "ave speed:" + msg.arg2 + "KB/S");
                    break;
                case 0x100:
                    Log.i(TAG, "开始测试");
                    break;
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = hideBottomNavigationBar();
        setContentView(root);
//        splash();

        info.hadfinishByte = 0;
        info.speed = 0;
        info.totalByte = 1024;
        new DownloadThread().start();
        new GetInfoThread().start();
    }

    /**
     * 隐藏底部虚拟按键
     */
    private View hideBottomNavigationBar() {
        View root = getLayoutInflater().inflate(R.layout.activity_splash, null);
        root.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        return root;
    }

    private void splash() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
//                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                SplashActivity.this.finish();

            }
        }, 3000);
    }

    private int getDegree(double cur_speed) {
        int ret = 0;
        if (cur_speed >= 0 && cur_speed <= 512) {
            ret = (int) (15.0 * cur_speed / 128.0);
        } else if (cur_speed >= 512 && cur_speed <= 1024) {
            ret = (int) (60 + 15.0 * cur_speed / 256.0);
        } else if (cur_speed >= 1024 && cur_speed <= 10 * 1024) {
            ret = (int) (90 + 15.0 * cur_speed / 1024.0);
        } else {
            ret = 180;
        }
        return ret;
    }

    class DownloadThread extends Thread {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            String url_string = "http://m.shouji.360tpcdn.com/160512/fb3a683bd233c353d987306334d69d7e/com.qihoo360.mobilesafe_242.apk";
            long start_time, cur_time;
            URL url;
            URLConnection connection;
            InputStream iStream;

            try {
                url = new URL(url_string);
                connection = url.openConnection();

                info.totalByte = connection.getContentLength();

                iStream = connection.getInputStream();
                start_time = System.currentTimeMillis();
                while (iStream.read() != -1 && flag) {

                    info.hadfinishByte++;
                    cur_time = System.currentTimeMillis();
                    if (cur_time - start_time == 0) {
                        info.speed = 1000;
                    } else {
                        info.speed = info.hadfinishByte / (cur_time - start_time) * 1000;
                    }
                }
                iStream.close();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    class GetInfoThread extends Thread {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            double sum, counter;
            int cur_speed, ave_speed;
            try {
                sum = 0;
                counter = 0;
                while (info.hadfinishByte < info.totalByte && flag) {
                    Thread.sleep(1000);

                    sum += info.speed;
                    counter++;

                    cur_speed = (int) info.speed;
                    ave_speed = (int) (sum / counter);
                    Log.e("Test", "cur_speed:" + info.speed / 1024 + "KB/S ave_speed:" + ave_speed / 1024);
                    Message msg = new Message();
                    msg.arg1 = ((int) info.speed / 1024);
                    msg.arg2 = ((int) ave_speed / 1024);
                    msg.what = 0x123;
                    handler.sendMessage(msg);
                }
                if (info.hadfinishByte == info.totalByte && flag) {
                    handler.sendEmptyMessage(0x100);
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

}
