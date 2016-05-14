package com.apollo.wifimanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.apollo.wifimanager.view.DashboardView;
import com.apollo.wifimanager.view.DialogUtils;
import com.apollo.wifimanager.wifiutil.Manager;
import com.apollo.wifimanager.wifiutil.RootChecker;
import com.apollo.wifimanager.wifiutil.WifiPsdUtil;
import com.apollo.wifimanager.wifiutil.WifiStatus;

import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "-----";
    private TextView tvCurSSID;
    private ListView lvNearby;
    private DashboardView dvSignal;
    private DashboardView dvSpeed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setData();

    }

    private void initView() {
        tvCurSSID = (TextView) findViewById(R.id.tv_main_cur_ssid);
        ImageView ivTopBarMenu = (ImageView) findViewById(R.id.iv_top_bar_menu);
        ivTopBarMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击右上角菜单图标，进入设置界面
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        TextView tvViewPsd = (TextView) findViewById(R.id.tv_main_view_psd);
        tvViewPsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickViewPasswordButton();
            }
        });

        lvNearby = (ListView) findViewById(R.id.lv_main_nearby);
        dvSignal = (DashboardView) findViewById(R.id.dv1);
        dvSpeed = (DashboardView) findViewById(R.id.dv2);
    }

    private void setData() {
        Manager manager = Manager.getInstance(this);
        WifiStatus wifiStatus = manager.getCurrentWifiStatus();
        Log.i(TAG, wifiStatus.toString());
        tvCurSSID.setText(wifiStatus.getSsid());

        final List<WifiStatus> list = manager.getNearbyWfi();
        NearbyWifiAdapter adapter = new NearbyWifiAdapter(this, list);
        lvNearby.setAdapter(adapter);
        lvNearby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Context context = MainActivity.this;
                final WifiStatus wifi = list.get(position);
                //对话框-请输入密码
                View contentView = View.inflate(context, R.layout.dialog_connect, null);
                final Dialog dialog = DialogUtils.createDialog(context, contentView, 0.35f, 0.7f);
                TextView tv = (TextView) contentView.findViewById(R.id.tv_dialog_connect_ssid);
                tv.setText(wifi.getSsid());
                final EditText et = (EditText) contentView.findViewById(R.id.et_dialog_connect_password);
                Button btn = (Button) contentView.findViewById(R.id.btn_dialog_connect_ok);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                        connectWifi(wifi.getSsid(), et.getText().toString());
                    }
                });
                dialog.show();

            }
        });

        dvSignal.setRealTimeValue(100, true, 100);
    }

    /**
     * 点击<查看密码>按钮
     */
    private void clickViewPasswordButton() {
        final Context context = MainActivity.this;
        if (RootChecker.isRoot()) {
            //有root权限，跳转到<连接记录>界面
            View view = View.inflate(context, R.layout.dialog_request_root, null);
            final Dialog dialog = DialogUtils.createDialog(context, view);
            Button btn = (Button) view.findViewById(R.id.btn_dialog_grant);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //点击<授权>按钮
                    dialog.dismiss();
                    try {
                        String ssid = tvCurSSID.getText().toString();
                        String psd = WifiPsdUtil.getPassword(ssid);
                        showPassword(ssid, psd);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            dialog.show();

        } else {
            //没有root权限，弹出对话框，是否下载“一键ROOT”
            View view = View.inflate(context, R.layout.dialog_go_root, null);
            final Dialog dialog = DialogUtils.createDialog(context, view);
            Button btn = (Button) view.findViewById(R.id.btn_dialog_go_root);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //点击<确定>按钮
                    dialog.dismiss();
                    //TODO 下载<一键ROOT>
                    Toast.makeText(MainActivity.this, "go root", Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        }
    }

    /**
     * 弹出对话框，显示ssid和password
     *
     * @param ssid ssid
     * @param password password
     */
    private void showPassword(String ssid, String password){
        Context context = this;
        View view = View.inflate(context, R.layout.dialog_show_password, null);
        final Dialog dialog = DialogUtils.createDialog(context, view);
        TextView tvSsid = (TextView) view.findViewById(R.id.tv_dialog_show_pswd_ssid);
        tvSsid.setText(ssid);
        TextView tvPswd = (TextView) view.findViewById(R.id.tv_dialog_show_pswd_pswd);
        tvPswd.setText("密码：" + password);
        Button btn = (Button) view.findViewById(R.id.btn_dialog_show_password_ok);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //点击<确定>按钮
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 连接wifi热点
     */
    private void connectWifi(String ssid, String password) {
        /*
        WiFiConnector connector = new WiFiConnector(this);
        connector.connect(ssid, password, new WiFiConnector.ActionListener() {

            @Override
            public void onStarted(String ssid) {
                Log.i(TAG, "onStarted");
            }

            @Override
            public void onSuccess(WifiInfo info) {
                Log.i(TAG, "onSuccess");
            }

            @Override
            public void onFailure() {
                Log.i(TAG, "onFailure");
            }

            @Override
            public void onFinished(boolean isSuccessed) {
                Log.i(TAG, "onFinished" + isSuccessed);
            }

        });
        */

        Manager manager = Manager.getInstance(this);
        WifiConfiguration conf = manager.CreateWifiInfo(ssid, password, 3);
        boolean success = manager.addWifi(conf);
        Log.i(TAG, "connectWifi: " + success);
        if(success){

        }else{

        }
    }

    /*
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    */
}
