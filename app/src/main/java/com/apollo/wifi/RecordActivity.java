package com.apollo.wifi;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.apollo.wifi.util.WifiBaseInfo;
import com.apollo.wifi.util.WifiPasswordUtil;

import java.util.List;

/**
 * 连接记录---显示已连接过的wifi热点，点击记录查看密码
 *
 * @deprecated 暂时没用
 */
public class RecordActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        TextView tvTopBarTitle = (TextView) findViewById(R.id.tv_top_bar_title);
        tvTopBarTitle.setText(R.string.title_activity_record);

        ImageView ivTopBarBack = (ImageView) findViewById(R.id.iv_top_bar_back);
        ivTopBarBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordActivity.this.finish();
            }
        });

        try {
            List<WifiBaseInfo> wifiList = WifiPasswordUtil.read();
            ListView listView=(ListView)findViewById(R.id.lv_record);
            MyAdapter adapter = new MyAdapter(wifiList, this);
            listView.setAdapter(adapter);
        } catch (Exception e) {
            //TODO 需要处理异常
            e.printStackTrace();
        }


    }

    private class MyAdapter extends BaseAdapter {

        private List<WifiBaseInfo> list =null;
        private Context context;

        public MyAdapter(List<WifiBaseInfo> list,Context context){
            this.list = list;
            this.context = context;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, null);
            TextView tv = (TextView)convertView.findViewById(android.R.id.text1);
            tv.setText("Wifi:"+ list.get(position).Ssid+"\n密码:"+ list.get(position).Password);
            return convertView;
        }

    }

}
