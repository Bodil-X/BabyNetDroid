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

    private IPlugin activityResultCallback;
    private Object activityResultKeepRunning;
    private Object keepRunning;

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
        this.activityResultCallback = iPlugin;
        this.activityResultKeepRunning = this.keepRunning;
        if(iPlugin != null)
            this.keepRunning = false;

        super.startActivityForResult(intent,i);
    }

    @Override
    public void setActivityResultCallback(IPlugin iPlugin) {
        this.activityResultCallback = iPlugin;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);
        IPlugin callback = this.activityResultCallback;
        if (callback != null) {
            callback.onActivityResult(requestCode, resultCode, intent);
        }
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
    public Object onMessage(String id, Object data) {
        if("exit".equals(id))
            super.finish();
        return null;
    }
}
