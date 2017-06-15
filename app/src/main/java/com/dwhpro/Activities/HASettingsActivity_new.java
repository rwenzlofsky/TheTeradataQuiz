package com.dwhpro.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.dwhpro.AppSettings.HAConfiguration;
import com.dwhpro.AppSettings.HASettings;
import com.dwhpro.AppSettings.HAUtilities;
import com.dwhpro.Singleton.HAQuizDataManager;
import com.dwhpro.quizappnew.R;
import com.dwhpro.utils.IabHelper;
import com.dwhpro.utils.IabResult;
import com.dwhpro.utils.Purchase;

public class HASettingsActivity_new extends Activity {

    Button removeAdsButton;
    Button restoreButton;
    Switch soundSwitch;
    Switch explanationSwitch;
    LinearLayout homeButtonLayout;

    IabHelper mHelper;
    String TAG = "Settings";
    String payload = "Zp/xZ/gkX0WZ/IRbc0lQFzssgIbJewIDAQAB";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hasettings_activity_new);
        getActionBar().hide();

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Setting Activity")
                .putContentType("Settings Activity Showing")
                .putContentId("settings"));

        Typeface textFace = HASettings.getInstance().getAppFontFace();

        TextView soundText = (TextView) findViewById(R.id.sound_text);
        soundText.setTypeface(textFace);
        TextView explanationText = (TextView) findViewById(R.id.explanation_text);
        explanationText.setTypeface(textFace);
        ((TextView) findViewById(R.id.title_textview)).setTypeface(textFace);


        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#2D2D2D"));
        }


        homeButtonLayout = (LinearLayout) findViewById(R.id.home_button_layout);
        homeButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        removeAdsButton = (Button) findViewById(R.id.remove_ads_button);
        restoreButton = (Button) findViewById(R.id.restore_button);
        soundSwitch = (Switch) findViewById(R.id.sound_switch);
        explanationSwitch = (Switch) findViewById(R.id.explanation_switch);
        removeAdsButton.setTypeface(textFace);
        restoreButton.setTypeface(textFace);


        if (HASettings.getInstance().isInAppurchaseEnabled())
        {
            restoreButton.setVisibility(View.VISIBLE);
            restoreButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HAUtilities.getInstance().playTapSound();

                    //Toast.makeText(HASettingsActivity_new.this, "Restore method called from Settings", Toast.LENGTH_SHORT).show();
                    HAQuizDataManager.getInstance().restorePurchases();
                }
            });
        }
        else {
            restoreButton.setVisibility(View.GONE);
        }

        soundSwitch.setChecked(HASettings.getInstance().getSoundEnabled());
        soundSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                HASettings.getInstance().setSoundEnabled(soundSwitch.isChecked());
            }
        });

        explanationSwitch.setChecked(HASettings.getInstance().getExplantionEnabled());
        explanationSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                HASettings.getInstance().setExplanationEnabled(explanationSwitch.isChecked());
            }
        });



        if (HAConfiguration.getInstance().isInAppPurchaseEnabledForRemovingAds())
        {
            Boolean purchased = HASettings.getInstance().isProductPurchased(HAConfiguration.getInstance().getRemoveAdsProductID());
            if (purchased)
            {
                removeAdsButton.setVisibility(View.GONE);
            }
            else
            {
                removeAdsButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        HAUtilities.getInstance().playTapSound();

                        mHelper = new IabHelper(HASettingsActivity_new.this, HAConfiguration.Base64_encoded_License_Key);
                        mHelper.enableDebugLogging(true);
                        mHelper.startSetup(new
                                                   IabHelper.OnIabSetupFinishedListener() {
                                                       public void onIabSetupFinished(IabResult result) {
                                                           if (!result.isSuccess()) {
                                                               Log.d("XXXX from Categories:", "In-app Billing setup failed: " +
                                                                       result);
                                                           } else {
                                                               Log.d("XXXX from Categories", "In-app Billing is set up OK");
                                                               String skuID = HAConfiguration.getInstance().getRemoveAdsProductID();
                                                               buyProduct(skuID);
                                                           }
                                                       }
                                                   });

                    }
                });

            }
        }
        else
        {
            removeAdsButton.setVisibility(View.GONE);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void buyProduct(String skuID)
    {
        //add listener on purchase
        //this method called purchase is made
        //invoke purchase flow
        mHelper.launchPurchaseFlow(this, skuID, 10001,
                new IabHelper.OnIabPurchaseFinishedListener() {
                    @Override
                    public void onIabPurchaseFinished(IabResult result, Purchase info) {

                        if (result.isFailure())
                        {
                            int responseCode = result.getResponse();
                            if (responseCode == 7)// product already owned
                            {
                                Toast.makeText(HASettingsActivity_new.this, "You have already purchased Remove ads!", Toast.LENGTH_LONG).show();
                                removeAdsButton.setVisibility(View.GONE);
                                HAQuizDataManager.getInstance().markProductPurchased(HAConfiguration.getInstance().getRemoveAdsProductID());
                            }
                            else {
                                Log.d(TAG + "Failed",result.getMessage());
                                Toast.makeText(HASettingsActivity_new.this, "Unable to make purchase, please try again later!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{

                            int purchaseState = info.getPurchaseState();

                            if (purchaseState == 0)
                            {
                                HAQuizDataManager.getInstance().markProductPurchased(HAConfiguration.getInstance().getRemoveAdsProductID());
                                removeAdsButton.setVisibility(View.GONE);
                            }
                            else if (purchaseState == 1) //canceled
                            {
                                Toast.makeText(HASettingsActivity_new.this, "Purchase cancelled!", Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }, payload);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // very important:
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HAUtilities.getInstance().playTapSound();
        finish();
    }
}
