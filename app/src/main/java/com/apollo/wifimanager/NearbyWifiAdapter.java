package com.apollo.wifimanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.apollo.wifimanager.wifiutil.WifiStatus;

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
        ViewHolder holder = null;
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
        //TODO 需要根据情况显示不同的图标
        holder.icon.setImageResource(R.drawable.ic_wifi_lock_signal_4);
        holder.name.setText(wifi.getSsid());

        return convertView;
    }

    private static class ViewHolder {
        ImageView icon;
        TextView name;
    }
}


