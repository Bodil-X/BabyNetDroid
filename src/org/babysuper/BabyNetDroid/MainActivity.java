package org.babysuper.BabyNetDroid;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.api.CordovaInterface;
import org.apache.cordova.api.IPlugin;

public class MainActivity extends Activity implements CordovaInterface
{
    CordovaWebView cwv;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        cwv = (CordovaWebView)findViewById(R.id.cordovaWebView);
        cwv.loadUrl("file:///android_asset/www/page/index.html");
        //super.loadUrl("file:///android_asset/www/page/index.html");
    }

    @Override
    public void startActivityForResult(IPlugin iPlugin, Intent intent, int i) {
        super.startActivityForResult(intent,i);
    }

    @Override
    public void setActivityResultCallback(IPlugin iPlugin) {
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void cancelLoadUrl() {
    }

    @Override
    public Object onMessage(String s, Object o) {
        return null;
    }
}
