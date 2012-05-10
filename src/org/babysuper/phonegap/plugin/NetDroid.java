package org.babysuper.phonegap.plugin;

import android.content.Context;
import android.net.DhcpInfo;
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
            JSONObject configJson = getDHCPJson();
            configJson.put("ip", getLocalIpAddress());

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
            Log.e("WifiPreference IpAddress", ex.toString());
        }
        return null;
    }

    private JSONObject getDHCPJson() {
        WifiManager wifiManger = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManger.getDhcpInfo();
        WifiInfo wifiInfo = wifiManger.getConnectionInfo();
        JSONObject dhcpJson = new JSONObject();
        try {
            dhcpJson.put("dns1", intToIp(dhcpInfo.dns1));
            dhcpJson.put("dns2", intToIp(dhcpInfo.dns2));
            dhcpJson.put("netmask", intToIp(dhcpInfo.netmask));
            dhcpJson.put("gateway", intToIp(dhcpInfo.gateway));
            dhcpJson.put("staticIp", intToIp(dhcpInfo.ipAddress));
            dhcpJson.put("serverAddress", intToIp(dhcpInfo.serverAddress));
            dhcpJson.put("mac",String.valueOf(wifiInfo.getMacAddress()));
            dhcpJson.put("wifiAddress",intToIp(wifiInfo.getIpAddress()));
        } catch (JSONException ex) {
            Log.e("getDHCPInfo Error", "JSON Error:" + ex.getMessage());
        } finally {
            return dhcpJson;
        }
    }

    private String intToIp(int ipInt){
        return ( ipInt & 0xFF) + "." + ((ipInt >> 8 ) & 0xFF) + "." + ((ipInt >> 16 ) & 0xFF) + "." + ((ipInt >> 24 ) & 0xFF );
    }
}
