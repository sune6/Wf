package com.apollo.wifimanager;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.apollo.wifimanager.wifiutil.Manager;
import com.apollo.wifimanager.wifiutil.RootChecker;
import com.apollo.wifimanager.wifiutil.WifiStatus;

import java.util.List;

public class MainActivity extends Activity {
    private static final String TAG = "-----";
    private ImageView ivTopBarMenu;
    private TextView tvCurSSID;
    private ListView lvNearby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        setData();
    }

    private void initView() {
        tvCurSSID = (TextView) findViewById(R.id.tv_main_cur_ssid);
        ivTopBarMenu = (ImageView) findViewById(R.id.iv_top_bar_menu);
        ivTopBarMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //点击右上角菜单图标，显示选项菜单
                showMenuPopWindow(MainActivity.this);
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
        lvNearby.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(TAG, "onItemClick: "+position);
            }
        });
    }

    /**
     * 用PopupWindow模拟右上角菜单
     */
    private void showMenuPopWindow(final Context context) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(context).inflate(R.layout.content_menu_pop_window, null);


        final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true);
//        popupWindow.setTouchable(true);
//        popupWindow.setTouchInterceptor(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // 这里如果返回true的话，touch事件将被拦截
//                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
//                return false;
//            }
//        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_switch_track_mtrl_alpha));
        // 设置按钮的点击事件
        TextView tvDis = (TextView) contentView.findViewById(R.id.tv_main_menu_dis);
        tvDis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, DisclaimerActivity.class);
                context.startActivity(intent);
                popupWindow.dismiss();
            }
        });
        TextView tvAbout = (TextView) contentView.findViewById(R.id.tv_main_menu_about);
        tvAbout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AboutActivity.class);
                context.startActivity(intent);
                popupWindow.dismiss();
            }
        });
        // 设置好位置之后再show
        popupWindow.showAsDropDown(ivTopBarMenu);

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
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                final EditText ed = new EditText(context);
                builder.setTitle("请输入密码")
                        .setView(ed)
                        .setPositiveButton("连接", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "wifi=" + wifi.getSsid() + ", input psd=" + ed.getText());
                                connectWifi(wifi.getSsid(), ed.getText().toString());
                            }
                        });
                builder.show();
            }
        });
    }

    /**
     * 点击<查看密码>按钮
     */
    private void clickViewPasswordButton(){
        final Context context = MainActivity.this;
        if(RootChecker.isRoot()){
            //有root权限，跳转到<连接记录>界面
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("提示")
                    .setMessage("查询WiFi密码需要授予ROOT权限")
                    .setPositiveButton("授权", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(context, RecordActivity.class);
                            context.startActivity(intent);
                        }
                    });
            builder.show();

            /*
            View view = View.inflate(context, R.layout.dialog, null);
            Dialog dialog = new Dialog(context, R.style.Mydialog);
            dialog.setContentView(view);
            dialog.show();
            */
        }else {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("提示")
                    .setMessage("查询WiFi密码需要授予ROOT权限，您的设备尚未ROOT")
                    .setPositiveButton("一键ROOT", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Toast.makeText(context, "该下载了", Toast.LENGTH_SHORT).show();
                        }
                    });
            builder.show();
        }
    }

    /**
     * 连接wifi热点
     *
     * @param ssid
     * @param password
     */
    private void connectWifi(String ssid, String password){
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
        boolean b = manager.addWifi(conf);
        Log.i(TAG, "connectWifi: "+ b);
    }
}
