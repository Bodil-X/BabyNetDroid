package org.babysuper.phonegap.plugin;

import android.content.*;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;
import com.phonegap.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * Created by Bodil.
 * User: Bodil
 * Date: 12-5-7
 * Time: 下午4:45
 */
public class NetDroid extends Plugin {

    private static String ACTION_NAME_GETCONFIG = "getConfig";
    private static String ACTION_NAME_GETSSIDS = "getSSIDs";
    private static String ACTION_NAME_SETCONFIG = "setConfig";
    private static String ACTION_NAME_SETWIFIUSE = "setWifiUse";
    private List<ScanResult> wifiResultList;
    private int recordSize = 0;
    private HashMap<String, String> ssidHashMap = new HashMap<String, String>();
    private WifiManager wifiManger;
    private static Boolean isRegSSIDScan = false;

    public PluginResult execute(String actionName, JSONArray jsonParams, String callBack) {
        if (!ACTION_NAME_GETCONFIG.equals(actionName) && !ACTION_NAME_GETSSIDS.equals(actionName) &&
                !ACTION_NAME_SETCONFIG.equals(actionName) && !ACTION_NAME_SETWIFIUSE.equals(actionName))
            return new PluginResult(PluginResult.Status.INVALID_ACTION, "The action name must is:" + ACTION_NAME_GETCONFIG
                    + " or " + ACTION_NAME_GETSSIDS + " or " + ACTION_NAME_SETCONFIG + " or " + ACTION_NAME_SETWIFIUSE);
        PluginResult result;
        try {
            JSONObject configJson;
            String message = "Can't get the Net Info.";
            if (ACTION_NAME_SETWIFIUSE.equals(actionName)) {
                setWifiUse(0);
                configJson = new JSONObject();
                configJson.put("message", "Set Auto Success");
            } else if (ACTION_NAME_SETCONFIG.equals(actionName)) {
                configJson = new JSONObject();
                String ipStr = jsonParams.getString(0);
                String netmask = jsonParams.getString(1);
                String gateway = jsonParams.getString(2);
                String dns1 = jsonParams.getString(3);
                String dns2 = jsonParams.getString(4);
                boolean isSetSucc = setDhcpConfig(ipStr, netmask, gateway, dns1, dns2);
                if (isSetSucc)
                    configJson.put("message", "Set DHCP Success");
                else
                    message = "Set DHCP Failure.";

            } else if (ACTION_NAME_GETCONFIG.equals(actionName)) {
                configJson = getDHCPJson();
                configJson.put("ip", getLocalIpAddress());
            } else
                configJson = getSSIDJson();
            if (configJson.length() == 0)
                result = new PluginResult(PluginResult.Status.NO_RESULT, message);
            else
                result = new PluginResult(PluginResult.Status.OK, configJson);
            return result;
        } catch (JSONException ex) {
            Log.e("NetDroid Error", String.format("Info:%s", ex.getMessage()));
        }
        return new PluginResult(PluginResult.Status.NO_RESULT, "What Error?I don't know.");
    }

    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface netInterface = en.nextElement();
                for (Enumeration<InetAddress> inetAddresses = netInterface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("getLocalIpAddress Error", "WifiPreference IpAddress: " + ex.toString());
        }
        return null;
    }

    private JSONObject getDHCPJson() {
        wifiManger = (WifiManager) ctx.getActivity().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManger.getDhcpInfo();
        WifiInfo wifiInfo = wifiManger.getConnectionInfo();
        JSONObject dhcpJson = new JSONObject();
        try {
            dhcpJson.put("staticIp", intToIp(dhcpInfo.ipAddress));
            dhcpJson.put("wifiAddress", intToIp(wifiInfo.getIpAddress()));
            dhcpJson.put("netmask", intToIp(dhcpInfo.netmask));
            dhcpJson.put("gateway", intToIp(dhcpInfo.gateway));
            dhcpJson.put("dns1", intToIp(dhcpInfo.dns1));
            dhcpJson.put("dns2", intToIp(dhcpInfo.dns2));
            dhcpJson.put("serverAddress", intToIp(dhcpInfo.serverAddress));
            dhcpJson.put("mac", String.valueOf(wifiInfo.getMacAddress()));
            dhcpJson.put("ssid", wifiInfo.getSSID());
            int useStatic = Settings.System.getInt(ctx.getActivity().getContentResolver(), Settings.System.WIFI_USE_STATIC_IP);
            dhcpJson.put("use_static_ip", useStatic == 1 ? "Static" : "Auto");
        } catch (JSONException ex) {
            Log.e("getDHCPInfo Error", "JSON Error:" + ex.getMessage());
        } finally {
            return dhcpJson;
        }
    }

    private String intToIp(int ipInt) {
        return (ipInt & 0xFF) + "." + ((ipInt >> 8) & 0xFF) + "." + ((ipInt >> 16) & 0xFF) + "." + ((ipInt >> 24) & 0xFF);
    }

    private JSONObject getSSIDJson() {
        regSSIDListener();
        ssidHashMap.clear();
        wifiManger.startScan();
        long scanTimeMillis = System.currentTimeMillis();
        boolean isFinished = false;
        try {
            while (new Date(System.currentTimeMillis() - scanTimeMillis).getSeconds() < 30 && !isFinished) {
                while (recordSize > 0) {
                    ScanResult recordItem = wifiResultList.get(recordSize - 1);
                    ssidHashMap.put(recordItem.SSID, recordItem.capabilities + "|" + recordItem.frequency + "|" + recordItem.level);
                    recordSize--;
                    isFinished = (recordSize <= 0);
                }
            }
        } catch (Exception ex) {
            Log.e("getSSIDJson error", "get wifi ssid error:" + ex.getMessage());
        }
        JSONObject ssidJsonObj = new JSONObject();
        try {
            Iterator iterator = ssidHashMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> ssidEntry = (Map.Entry<String, String>) iterator.next();
                ssidJsonObj.put(ssidEntry.getKey(), ssidEntry.getValue());
            }
        } catch (JSONException ex) {
            Log.e("getSSIDJson Error", "ssid parse error:" + ex.getMessage());
        }
        return ssidJsonObj;
    }

    private void regSSIDListener() {
        if (wifiManger == null)
            wifiManger = (WifiManager) ctx.getActivity().getSystemService(Context.WIFI_SERVICE);
        if (!isRegSSIDScan) {
            ctx.getActivity().registerReceiver(new BroadcastReceiver() {
                public void onReceive(Context context, Intent intent) {
                    wifiResultList = wifiManger.getScanResults();
                    recordSize = wifiResultList.size();
                }
            }, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
            isRegSSIDScan = true;
        }
    }

    private boolean setDhcpConfig(String ip, String netmask, String gateway, String dns1, String dns2) {
        final ContentResolver contentResolver = ctx.getActivity().getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.WIFI_USE_STATIC_IP, 1);
        Settings.System.putString(contentResolver, Settings.System.WIFI_STATIC_IP, ip);
        Settings.System.putString(contentResolver, Settings.System.WIFI_STATIC_NETMASK, netmask);
        Settings.System.putString(contentResolver, Settings.System.WIFI_STATIC_GATEWAY, gateway);
        Settings.System.putString(contentResolver, Settings.System.WIFI_STATIC_DNS1, dns1);
        Settings.System.putString(contentResolver, Settings.System.WIFI_STATIC_DNS2, dns2);

        return true;
    }

    private boolean setWifiUse(int value) {
        final ContentResolver contentResolver = ctx.getActivity().getContentResolver();
        Settings.System.putInt(contentResolver, Settings.System.WIFI_USE_STATIC_IP, value);

        return true;
    }
}
