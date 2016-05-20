package com.apollo.wifimanager.wifiutil;

/**
 * Created by Sun
 * <p/>
 * 2016-04-30
 */
public class WifiStatus implements Comparable {
    private String ssid = "";
    private String capabilities = "";
    private int level;

    public String getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(String capabilities) {
        this.capabilities = capabilities;
    }

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public String toString() {
        return "WifiStatus{" +
                "ssid=" + ssid +
                ", level=" + level +
                ", capabilities=" + capabilities +
                '}';
    }

    @Override
    public int compareTo(Object obj) {
        WifiStatus another = (WifiStatus) obj;
        if (this.level > another.level) {
            return 1;
        } else if (this.level < another.level) {
            return -1;
        } else {
            //this.level == another.level
            return 0;
        }
    }
}
