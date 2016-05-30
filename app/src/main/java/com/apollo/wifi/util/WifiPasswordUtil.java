package com.apollo.wifi.util;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WifiPasswordUtil {
    private static final String TAG = "WifiPasswordUtil";

    public static List<WifiBaseInfo> read() throws Exception {
        List<WifiBaseInfo> wifiList = new ArrayList<WifiBaseInfo>();

        Process process = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        StringBuffer wifiConf = new StringBuffer();
        try {
            process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataInputStream = new DataInputStream(process.getInputStream());
            dataOutputStream
                    .writeBytes("cat /data/misc/wifi/*.conf\n");
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            InputStreamReader inputStreamReader = new InputStreamReader(
                    dataInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(
                    inputStreamReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                wifiConf.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            process.waitFor();
        } catch (Exception e) {
            throw e;
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                process.destroy();
            } catch (Exception e) {
                throw e;
            }
        }


        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
        Matcher networkMatcher = network.matcher(wifiConf.toString());
        while (networkMatcher.find()) {
            String networkBlock = networkMatcher.group();
            Pattern ssid = Pattern.compile("ssid=\"([^\"]+)\"");
            Matcher ssidMatcher = ssid.matcher(networkBlock);

            if (ssidMatcher.find()) {
                WifiBaseInfo wifiInfo = new WifiBaseInfo();
                wifiInfo.Ssid = ssidMatcher.group(1);
                Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");
                Matcher pskMatcher = psk.matcher(networkBlock);
                if (pskMatcher.find()) {
                    wifiInfo.Password = pskMatcher.group(1);
                } else {
                    wifiInfo.Password = "无密码";
                }
                wifiList.add(wifiInfo);
            }

        }

        return wifiList;
    }

    public static String getPassword(String ssid) {
        String failed = "获取失败";

        if (TextUtils.isEmpty(ssid)) {
            return failed;
        }

        Process process = null;
        DataOutputStream dataOutputStream = null;
        DataInputStream dataInputStream = null;
        StringBuffer wifiConf = new StringBuffer();
        try {
            process = Runtime.getRuntime().exec("su");
            dataOutputStream = new DataOutputStream(process.getOutputStream());
            dataInputStream = new DataInputStream(process.getInputStream());
            dataOutputStream.writeBytes("cat /data/misc/wifi/*.conf\n");
            dataOutputStream.writeBytes("exit\n");
            dataOutputStream.flush();
            InputStreamReader inputStreamReader = new InputStreamReader(dataInputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                wifiConf.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (dataOutputStream != null) {
                    dataOutputStream.close();
                }
                if (dataInputStream != null) {
                    dataInputStream.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
        Matcher networkMatcher = network.matcher(wifiConf.toString());
        String password;
        while (networkMatcher.find()) {
            String networkBlock = networkMatcher.group();
            Pattern ssidPattern = Pattern.compile("ssid=\"([^\"]+)\"");
            Matcher ssidMatcher = ssidPattern.matcher(networkBlock);

            if (ssidMatcher.find()) {
                String id = ssidMatcher.group(1);
                Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");
                Matcher pskMatcher = psk.matcher(networkBlock);
                if (ssid.equals(id)) {
                    if (pskMatcher.find()) {
                        password = pskMatcher.group(1);
                    } else {
                        password = "无密码";
                    }
                    return password;
                }
            }

        }

        return failed;
    }


    public static String getPassword2(String ssid) {
        //TODO
        String failed = "获取失败";

        if (TextUtils.isEmpty(ssid)) {
            return failed;
        }

        Process process = null;
        InputStreamReader inputStreamReader = null;
        BufferedReader bufferedReader = null;
        StringBuffer wifiConf = new StringBuffer();

        try {
            process = Runtime.getRuntime().exec("su -c /system/bin/cat  /data/misc/wifi/wpa_supplicant.conf");
            inputStreamReader = new InputStreamReader(process.getInputStream());
            bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                wifiConf.append(line);
            }
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStreamReader != null) {
                    inputStreamReader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);
        Matcher networkMatcher = network.matcher(wifiConf.toString());
        String password;
        while (networkMatcher.find()) {
            String networkBlock = networkMatcher.group();
            Pattern ssidPattern = Pattern.compile("ssid=\"([^\"]+)\"");
            Matcher ssidMatcher = ssidPattern.matcher(networkBlock);

            if (ssidMatcher.find()) {
                String id = ssidMatcher.group(1);
                Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");
                Matcher pskMatcher = psk.matcher(networkBlock);
                if (ssid.equals(id)) {
                    if (pskMatcher.find()) {
                        password = pskMatcher.group(1);
                    } else {
                        password = "无密码";
                    }
                    return password;
                }
            }

        }

        return failed;
    }

    public static ArrayList<WifiBaseInfo> readFile() {

        String line;
        String title = "";
        String password = "";
        String[] mLocationList = {"/data/misc/wifi/wpa_supplicant.conf", "/data/wifi/bcm_supp.conf", "/data/misc/wifi/wpa.conf"};
        final String SSID = "ssid=\"";
        final String WPA_PSK = "psk";
        final String WEP_PSK = "wep_key0";
        final String ENTRY_START = "network={";
        final String ENTRY_END = "}";

        ArrayList<WifiBaseInfo> listWifi = new ArrayList<>();
        BufferedReader bufferedReader = null;

        try {

            //Check for file in all known locations
            for (int i = 0; i < mLocationList.length; i++) {

                Process suProcess = Runtime.getRuntime().exec("su -c /system/bin/cat " + mLocationList[i]);
                try {
                    suProcess.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                bufferedReader = new BufferedReader(new InputStreamReader(suProcess.getInputStream()));
                String testString = bufferedReader.readLine();

                if (testString != null) {
                    break;

                } else if (i == mLocationList.length - 1) {
                    //Show Error Dialog
                    return new ArrayList<>();
                }
            }


            if (bufferedReader == null) {
                return new ArrayList<>();
            }

            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains(ENTRY_START)) {

                    while (!line.contains(ENTRY_END)) {
                        line = bufferedReader.readLine();

                        if (line.contains(SSID)) {
                            title = line.replace(SSID, "").replace("=", "").replace("\"", "").replace(" ", "");
                        }

                        if (line.contains(WPA_PSK)) {

                            password = line.replace(WPA_PSK, "").replace("=", "").replace("\"", "").replace(" ", "");

                        } else if (line.contains(WEP_PSK)) {

                            password = line.replace(WEP_PSK, "").replace("=", "").replace("\"", "").replace(" ", "");
                        }

                    }


                    if (password.equals("")) {
                        password = "无密码";
                    }

                    WifiBaseInfo current = new WifiBaseInfo();
                    current.Ssid = title;
                    current.Password = password;
                    listWifi.add(current);

                    title = "";
                    password = "";
                }
            }


        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return listWifi;
    }

}
