package org.babysuper.BabyNetDroid;

import android.os.Bundle;
import org.apache.cordova.DroidGap;

public class MainActivity extends DroidGap
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.loadUrl("file:///android_asset/www/pages/index.html");
    }
}
