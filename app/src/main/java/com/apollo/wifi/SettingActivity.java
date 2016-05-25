package com.apollo.wifi;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class SettingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        TextView tvTopBarTitle = (TextView) findViewById(R.id.tv_top_bar_title);
        tvTopBarTitle.setText(R.string.title_activity_setting);

        ImageView ivTopBarBack = (ImageView) findViewById(R.id.iv_top_bar_back);
        ivTopBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });

        TextView tvComment = (TextView) findViewById(R.id.tv_setting_comment);
        TextView tvUpdate = (TextView) findViewById(R.id.tv_setting_check_update);
        TextView tvAbout = (TextView) findViewById(R.id.tv_setting_about);
        TextView tvDis = (TextView) findViewById(R.id.tv_setting_disclaimer);

        tvComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //好评点赞
                String mAddress = "market://details?id=" + SettingActivity.this.getPackageName();
                Intent marketIntent = new Intent("android.intent.action.VIEW");
                marketIntent.setData(Uri.parse(mAddress));
                SettingActivity.this.startActivity(marketIntent);
            }
        });
        tvUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //检查更新
                Toast.makeText(SettingActivity.this, "update", Toast.LENGTH_SHORT).show();
            }
        });
        tvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关于我们
                Intent intent = new Intent(SettingActivity.this, AboutActivity.class);
                SettingActivity.this.startActivity(intent);
            }
        });
        tvDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //免责声明
                Intent intent = new Intent(SettingActivity.this, DisclaimerActivity.class);
                SettingActivity.this.startActivity(intent);
            }
        });

    }

}
