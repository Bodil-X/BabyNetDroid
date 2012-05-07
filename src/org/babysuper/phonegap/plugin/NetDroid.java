package org.babysuper.phonegap.plugin;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.phonegap.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by Bodil.
 * User: Bodil
 * Date: 12-5-7
 * Time: 下午4:45
 */
public class NetDroid extends Plugin {

    private static String ACTION_NAME = "getConfig";

    public PluginResult execute(String actionName, JSONArray jsonParams, String callBack) {
        if (!ACTION_NAME.equals(actionName))
            return new PluginResult(PluginResult.Status.INVALID_ACTION, "The action name must is:" + ACTION_NAME);
        PluginResult result;
        try {
            JSONObject configJson = new JSONObject();
            configJson.put("ip", getLocalIpAddress());
            configJson.put("mac", getLocalMacAddress());
            result = new PluginResult(PluginResult.Status.OK,configJson);
            return result;
        } catch (JSONException ex) {
            Log.e("NetDroid Error", String.format("Info:%s", ex.getMessage()));
        }
        return new PluginResult(PluginResult.Status.NO_RESULT,"What Error?I don't know.");
    }

    private String getLocalMacAddress() {
        WifiManager wifi = (WifiManager) ctx.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
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
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }
}
