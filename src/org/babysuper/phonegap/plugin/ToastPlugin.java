package org.babysuper.phonegap.plugin;

import android.os.Handler;
import android.widget.Toast;
import org.apache.cordova.api.Plugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by Bodil.
 * User: Bodil
 * Date: 12-5-22
 * Time: 下午12:49
 */
public class ToastPlugin extends Plugin {

    private Handler handler;
    private String ACTION_NAME = "makeText";

    @Override
    public PluginResult execute(String actionName, JSONArray params, String callBack) {
        PluginResult result = null;
        if (!ACTION_NAME.equals(actionName))
            return new PluginResult(PluginResult.Status.INVALID_ACTION, String.format("This Action:{0} is invalid.", actionName));
        try {
            final String showText = params.getString(0);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ctx.getApplicationContext(), showText, Toast.LENGTH_SHORT).show();
                }
            });
            result = new PluginResult(PluginResult.Status.OK, true);
        } catch (JSONException jsonEx) {
            result = new PluginResult(PluginResult.Status.JSON_EXCEPTION, jsonEx.getMessage());
        }
        return result;
    }
}
