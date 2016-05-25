package com.apollo.wifi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = hideBottomNavigationBar();
        setContentView(root);
        splash();

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

}
