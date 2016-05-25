/*
 * Copyright 2013 WhiteByte (Nick Russler, Ahmet Yueksektepe).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.apollo.wifi.util;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Manager {
    private static final String TAG = "-----";
    private static final int SIGNAL_MAX = 4;
    private static Manager manager;
    private WifiManager wifiManager;

    private Manager(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }

    public static Manager getInstance(Context context) {
        if (manager == null) {
            manager = new Manager(context);
        }
        return manager;
    }

    /**
     * 判断Wifi是否可用
     *
     * @return true 可用; false 不可用
     */
    public boolean isEnabled() {
        //return wifiManager.isWifiEnabled();//这句代码太坑，wifi开关关闭时，还返回true;
        return wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED;
    }

    /**
     * 开启wifi
     */
    public void setEnable() {
        wifiManager.setWifiEnabled(true);
    }

    /**
     * 获得当前连接的wifi信息
     */
    public WifiStatus getCurrentWifiStatus() {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String ssid = wifiInfo.getSSID().replace("\"", "");
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), SIGNAL_MAX + 1);

        WifiStatus wifiStatus = new WifiStatus();
        wifiStatus.setSsid(ssid);
        wifiStatus.setLevel(level);

        return wifiStatus;
    }


    /**
     * 扫描附近的Wifi热点
     */
    public List<WifiStatus> getNearbyWfi() {
        List<WifiStatus> listResult = new ArrayList<>();
        if (wifiManager.startScan()) {
            List<ScanResult> list = wifiManager.getScanResults();
            if (list.size() > 0) {
                for (ScanResult item : list) {
                    int level = WifiManager.calculateSignalLevel(item.level, SIGNAL_MAX + 1);
                    WifiStatus wifi = new WifiStatus();
                    wifi.setSsid(item.SSID);
                    wifi.setLevel(level);
                    wifi.setCapabilities(item.capabilities);
                    listResult.add(wifi);
                }
            } else {
                Log.i(TAG, "no result");
            }
        } else {
            Log.i(TAG, "erro");
        }
        //按照信号强弱排序
        Collections.sort(listResult);
        Collections.reverse(listResult);
        return listResult;
    }

    /**
     * 获取加密方式
     *
     * @param capabilities ScanResult.capabilities
     * @return wpa, wep, open
     */
    public static String getEncryptionType(String capabilities) {
        if (!TextUtils.isEmpty(capabilities)) {
            if (capabilities.contains("WPA") || capabilities.contains("wpa")) {
                return "wpa";
            } else if (capabilities.contains("WEP") || capabilities.contains("wep")) {
                return "wep";
            }
        }

        return "open";
    }

    /**
     * 创建WifiConfiguration
     * @param SSID ssid
     * @param Password 密码
     * @param Type     1没有密码;2用wep加密;3用wpa加密
     * @return WifiConfiguration
     */
    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = exists(SSID);
        if (tempConfig != null) {
            wifiManager.removeNetwork(tempConfig.networkId);
        }

        //open
        if (Type == 1) {
//            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//            config.wepTxKeyIndex = 0;
        }
        //WIFICIPHER_WEP
        if (Type == 2) {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        //WIFICIPHER_WPA
        if (Type == 3) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration exists(String SSID) {
        List<WifiConfiguration> existingConfigs = wifiManager.getConfiguredNetworks();
        if(existingConfigs != null){
            for (WifiConfiguration existingConfig : existingConfigs) {
                if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    public boolean addWifi(WifiConfiguration newConfig) {
        int netID = wifiManager.addNetwork(newConfig);//添加
        return wifiManager.enableNetwork(netID, false);

    }
}
