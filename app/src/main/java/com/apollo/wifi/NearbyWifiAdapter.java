package com.apollo.wifi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apollo.wifi.util.Manager;
import com.apollo.wifi.util.WifiStatus;

import java.util.List;

/**
 * Created by Sun
 *
 * 2016-05-02
 *
 */
public class NearbyWifiAdapter extends BaseAdapter {
    private Context context;
    private List<WifiStatus> list;

    public NearbyWifiAdapter(Context context, List<WifiStatus> list) {
        this.context = context;
        this.list = list;

//        for(WifiStatus wifi : list){
//            Log.i("-----wifi", "====================================");
//            Log.i("-----wifi", wifi.getSsid());
//            Log.i("-----wifi", "" + wifi.getLevel());
//            Log.i("-----wifi", wifi.getCapabilities());
//        }
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
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, null);
            holder = new ViewHolder();
            holder.icon = (ImageView) convertView.findViewById(R.id.iv_list_item_icon);
            holder.name = (TextView) convertView.findViewById(R.id.tv_list_item_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        WifiStatus wifi = list.get(position);
        String encryptionType = Manager.getEncryptionType(wifi.getCapabilities());
        //根据加密方式和信号强度显示不同的图标
        if(encryptionType.equals("open")){
            switch (wifi.getLevel()){
                case 0:
                case 1:
                    holder.icon.setImageResource(R.drawable.wifi_open_1);
                    break;
                case 2:
                case 3:
                    holder.icon.setImageResource(R.drawable.wifi_open_2);
                    break;
                case 4:
                    holder.icon.setImageResource(R.drawable.wifi_open_3);
                    break;
            }
        }else{
            switch (wifi.getLevel()){
                case 0:
                case 1:
                    holder.icon.setImageResource(R.drawable.wifi_lock_1);
                    break;
                case 2:
                case 3:
                    holder.icon.setImageResource(R.drawable.wifi_lock_2);
                    break;
                case 4:
                    holder.icon.setImageResource(R.drawable.wifi_lock_3);
                    break;
            }
        }

        holder.name.setText(wifi.getSsid());
        return convertView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView name;
    }
}


