package com.apollo.wifi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.autoupdate.IFlytekUpdate;
import com.iflytek.autoupdate.IFlytekUpdateListener;
import com.iflytek.autoupdate.UpdateConstants;
import com.iflytek.autoupdate.UpdateErrorCode;
import com.iflytek.autoupdate.UpdateInfo;
import com.iflytek.autoupdate.UpdateType;


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
                final Context context = SettingActivity.this;
                final IFlytekUpdate updManager = IFlytekUpdate.getInstance(context);
                //调试模式
                updManager.setDebugMode(Constants.DEBUG);
                //提示方式：通知栏模式
                updManager.setParameter(UpdateConstants.EXTRA_STYLE, UpdateConstants.UPDATE_UI_NITIFICATION);
                //启动自动更新，传入null更新过程交由SDK处理
                updManager.forceUpdate(context, new IFlytekUpdateListener() {
                    @Override
                    public void onResult(int errorCode, UpdateInfo updateInfo) {
                        if(errorCode == UpdateErrorCode.OK && updateInfo!= null) {
                            if(updateInfo.getUpdateType() == UpdateType.NoNeed) {
                                showTip(context, "已经是最新版本！");
                                return;
                            }
                            showTip(context, "发现新版本，点击通知栏更新");
                            updManager.showUpdateInfo(context, updateInfo);
                        } else {
                            String str = "请求更新失败！\n更新错误码：" + errorCode;
                            showTip(context, str);
                        }
                    }
                });
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

    private void showTip(final Context context, final String str) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
            }
        });
    }

}
