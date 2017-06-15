package com.dwhpro;


import android.app.Activity;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import com.android.vending.billing.IInAppBillingService;
import com.crashlytics.android.Crashlytics;
import com.dwhpro.Activities.HAMainActivity;
import com.dwhpro.AppSettings.HAConfiguration;
import com.dwhpro.quizappnew.R;


import io.fabric.sdk.android.Fabric;

public class HASplashScreen extends Activity {

    static final String ITEM_SKU = "my_price";
    ServiceConnection connection;
    IInAppBillingService mService;
    HAConfiguration configurations;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.splash_screen);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#2D2D2D"));
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Intent in = new Intent(getApplicationContext(),
                        HAMainActivity.class);
                startActivity(in);
            }
        }, (HAConfiguration.getInstance().getShowSplashScreenForSeconds() * 1000));
    }
}
