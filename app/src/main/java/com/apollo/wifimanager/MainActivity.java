package com.apollo.wifimanager;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.apollo.wifimanager.wifiutil.DownloadUtil;
import com.apollo.wifimanager.wifiutil.Manager;
import com.apollo.wifimanager.wifiutil.NetworkUtil;
import com.apollo.wifimanager.wifiutil.RandomUtil;
import com.apollo.wifimanager.wifiutil.RootChecker;
import com.apollo.wifimanager.wifiutil.WifiPsdUtil;
import com.apollo.wifimanager.wifiutil.WifiStatus;
import com.umeng.analytics.MobclickAgent;
import com.umeng.onlineconfig.OnlineConfigAgent;

import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "-----";
    private static final String apkUrl = "http://king.myapp.com/myapp/kdown/img/NewKingrootV4.92_C143_B263_office_release_2016_05_09_105003_1.apk" ;
    private TextView tvCurSSID;
    private TextView tvViewPsd;
    private ListView lvNearby;
    private DashboardView dvSignal;
    private DashboardView dvSpeed;
    private WifiStatus wifiConnected = new WifiStatus(); //当前连接的wifi信息

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setData();
        registerWifiReceiver();
        initUmeng();
    }

    private void initUmeng(){
        //友盟统计
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //请求在线参数后，在线参数被缓存到本地
        OnlineConfigAgent.getInstance().setDebugMode(true);
        OnlineConfigAgent.getInstance().updateOnlineConfig(this);
    }

    private void registerWifiReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(wifiReceiver, filter);
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

        tvViewPsd = (TextView) findViewById(R.id.tv_main_view_psd);
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
        final Manager manager = Manager.getInstance(this);
        if (manager.isEnabled()) {
            //wifi已开
            if (NetworkUtil.isOnline(this)) {
                //已连接到热点
                setStatusConnected();
            } else {
                //未连接到热点
                setStatusNotConnected();
            }
        } else {
            //wifi关闭
            setStatusOpenWifi();
        }

        final List<WifiStatus> list = manager.getNearbyWfi();
        for (int i = 0; i < list.size(); i++) {
            WifiStatus wifi = list.get(i);
            if (wifi.getSsid().equals(wifiConnected.getSsid())) {
                //在附近的WiFi列表中删除当前已连接的WiFi
                list.remove(wifi);
            }
        }

        NearbyWifiAdapter adapter = new NearbyWifiAdapter(this, list);
        lvNearby.setAdapter(adapter);
        lvNearby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Context context = MainActivity.this;
                final WifiStatus wifi = list.get(position);
                String encryptionType = Manager.getEncryptionType(wifi.getCapabilities());
                if (encryptionType.equals("open")) {
                    //对话框-开放wifi
                    View contentView = View.inflate(context, R.layout.dialog_connect_open_wifi, null);
                    final Dialog dialog = DialogUtils.createDialog(context, contentView, 0.35f, 0.7f);
                    TextView tv = (TextView) contentView.findViewById(R.id.tv_dialog_connect_ssid);
                    tv.setText(wifi.getSsid());
                    Button btn = (Button) contentView.findViewById(R.id.btn_dialog_connect_ok);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            //TODO 无密码连接wifi
                        }
                    });
                    dialog.show();
                } else {
                    //对话框-请输入密码
                    View contentView = View.inflate(context, R.layout.dialog_connect_input_password, null);
                    final Dialog dialog = DialogUtils.createDialog(context, contentView, 0.35f, 0.7f);
                    TextView tv = (TextView) contentView.findViewById(R.id.tv_dialog_connect_ssid);
                    tv.setText(wifi.getSsid());
                    final EditText et = (EditText) contentView.findViewById(R.id.et_dialog_connect_password);
                    Button btn = (Button) contentView.findViewById(R.id.btn_dialog_connect_ok);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            boolean success = connectWifi(wifi.getSsid(), et.getText().toString());
                            Log.i(TAG, "connectWifi: " + success);
                            if (success) {
                                Toast.makeText(MainActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    dialog.show();
                }


            }
        });

    }

    /**
     * 未开启WiFi
     */
    private void setStatusOpenWifi() {
        tvCurSSID.setText("点击开启WiFi");
        //只有在进入应用后，wifi状态为关闭时，才可以点击
        tvCurSSID.setClickable(true);
        tvCurSSID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerWifiReceiver();
                Manager.getInstance(MainActivity.this).setEnable();
            }
        });

        tvViewPsd.setClickable(false);
    }

    /**
     * 未连接热点
     */
    private void setStatusNotConnected() {
        tvCurSSID.setText("未连接热点");
        tvCurSSID.setClickable(false);

        tvViewPsd.setClickable(false);
    }

    /**
     * 连接中...
     */
    private void setStatusIsConnecting() {
        tvCurSSID.setText("连接中...");
        tvCurSSID.setClickable(false);

        tvViewPsd.setClickable(false);
    }

    /**
     * 连接热点成功
     */
    private void setStatusConnected() {
        wifiConnected = Manager.getInstance(this).getCurrentWifiStatus();
        tvCurSSID.setText(wifiConnected.getSsid());
        //连接成功后，显示wifi热点名称，并设为不可点击
        tvCurSSID.setClickable(false);

        tvViewPsd.setClickable(true);

        int signal = wifiConnected.getLevel();
        dvSignal.setRealTimeValue(signal, true, 150);

        try {
            float ran1 = RandomUtil.nextFloat(0.4f, 1.5f);
            dvSpeed.setRealTimeValue(ran1, true, 100);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 点击<查看密码>按钮
     */
    private void clickViewPasswordButton() {
        final Context context = MainActivity.this;
        if (RootChecker.isRoot()) {
            //有root权限，提示查看密码需要授权
            View view = View.inflate(context, R.layout.dialog_request_root, null);
            final Dialog dialog = DialogUtils.createDialog(context, view);
            Button btn = (Button) view.findViewById(R.id.btn_dialog_grant);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //点击<授权>按钮
                    dialog.dismiss();

                    String ssid = wifiConnected.getSsid();
                    String psd = WifiPsdUtil.getPassword(ssid);
                    showPassword(ssid, psd);
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
                    //用友盟在线参数工具获取已缓存到本地的数据
                    String url = OnlineConfigAgent.getInstance().getConfigParams(context, "root_apk_url");
                    if(TextUtils.isEmpty(url)){
                       url = apkUrl;
                    }
                    DownloadUtil.downloadRootApk(context, url);
                }
            });
            dialog.show();
        }
    }

    /**
     * 弹出对话框，显示ssid和password
     *
     * @param ssid     ssid
     * @param password password
     */
    private void showPassword(String ssid, String password) {
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
    private boolean connectWifi(String ssid, String password) {
        Manager manager = Manager.getInstance(this);
        WifiConfiguration conf = manager.CreateWifiInfo(ssid, password, 3);
        return manager.addWifi(conf);
    }

    private BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG, "action: " + action);
            //是否连上无线路由器
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (wifiState) {
                    case WifiManager.WIFI_STATE_ENABLED:
                    case WifiManager.WIFI_STATE_ENABLING:
                        setStatusIsConnecting();
                        break;
                }
            }
            //是否连上无线路由器的某个热点
            if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info != null && info.isConnected()) {
                    //联网成功
                    setStatusConnected();
                } else {
                    //未连接到热点
                    setStatusNotConnected();
                }
            }

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(wifiReceiver);
    }

}
