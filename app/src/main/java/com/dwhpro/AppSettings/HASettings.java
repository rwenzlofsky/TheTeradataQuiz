package com.dwhpro.AppSettings;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;

import com.dwhpro.Singleton.HAQuizDataManager;
import com.dwhpro.quizappnew.R;

import java.io.IOException;

import static com.facebook.login.widget.ProfilePictureView.TAG;

/**
 * Created by satie on 30/06/16.
 */
interface HASettingsConstants {
    public final String kSettings = "settings";
    public final String kSound = "sound_enabled";
    public final String kExplanation = "explanation_enabled";
    public final String kAutoSignIn = "auto_sign_in";
}

public class HASettings implements HASettingsConstants {

    private Boolean isExplantionEnabled = true;
    private Boolean isSoundEnabled = true;
    public Context context = null;
    private boolean autoSignIn;


    public boolean isAutoSignIn() {
        if (autoSignIn)
            Log.d("GameHelper", "Auto sigin in current value :" + "true");
        else
            Log.d("GameHelper", "Auto sigin in current value :" + "false");
        return autoSignIn;
    }


    private static HASettings singleton = new HASettings();

    private HASettings() {
    }

    public void loadSettings() {
        SharedPreferences settings = context.getSharedPreferences(HASettingsConstants.kSettings, 0);
        isSoundEnabled = settings.getBoolean(HASettingsConstants.kSound, true);
        isExplantionEnabled = settings.getBoolean(HASettingsConstants.kExplanation, true);
        autoSignIn = settings.getBoolean(HASettingsConstants.kAutoSignIn, false);
    }

    /* Static 'instance' method */
    public static HASettings getInstance() {
        if (singleton == null)
        {
            singleton = new HASettings();
            Log.d("GameHelper","Settings allocated");
        }
        return singleton;
    }

    public void setAutoSignIn(boolean signIn) {

        SharedPreferences.Editor settingsEditor = context.getSharedPreferences(HASettingsConstants.kSettings, 0).edit();
        settingsEditor.putBoolean(kAutoSignIn, signIn);
        settingsEditor.commit();
        autoSignIn = signIn;

        if (autoSignIn)
            Log.d("GameHelper", "Auto sign in true");
        else
            Log.d("GameHelper", "Auto sign in false");
    }

    public Boolean getExplantionEnabled() {
        return isExplantionEnabled;
    }


    public Boolean getSoundEnabled() {
        return isSoundEnabled;
    }

    public void setSoundEnabled(Boolean soundEnabled) {
        SharedPreferences.Editor settingsEditor = context.getSharedPreferences(HASettingsConstants.kSettings, 0).edit();
        settingsEditor.putBoolean(HASettingsConstants.kSound, soundEnabled);
        settingsEditor.commit();
        isSoundEnabled = soundEnabled;
    }

    public void setExplanationEnabled(Boolean explanationEnabled) {
        SharedPreferences.Editor settingsEditor = context.getSharedPreferences(HASettingsConstants.kSettings, 0).edit();
        settingsEditor.putBoolean(HASettingsConstants.kExplanation, explanationEnabled);
        settingsEditor.commit();
        isExplantionEnabled = explanationEnabled;
    }

    public Boolean isInAppurchaseEnabled() {
        return (HAConfiguration.getInstance().getEnableInAppPurchasesForCategories() || HAConfiguration.getInstance().isInAppPurchaseEnabledForRemovingAds());
    }

    public Typeface getAppFontFace() {

        Typeface myTypeFace = null;

        try {
            //return Typeface.createFromAsset(context.getAssets(),  context.getString(R.string.app_font_face));
            //return Typeface.createFromAsset(context.getAssets(), "fonts/HelveticaNeue Thin.ttf");
            myTypeFace = Typeface.createFromAsset(context.getAssets(), context.getString(R.string.app_font_face));


        }
        catch (NullPointerException e)  {
            Log.i(TAG,"Font could not be loaded");
            myTypeFace = null;

        } finally {
            return myTypeFace;
        }
    }

    public Typeface getAppMediumFontFace() {
        return Typeface.createFromAsset(context.getAssets(),
                context.getString(R.string.app_medium_font_face));
    }

    public Typeface getAppLightFontFace() {
        return Typeface.createFromAsset(context.getAssets(),
                context.getString(R.string.app_light_font_face)); //used for categories list
    }


    public Boolean isProductPurchased(String skuID) {
        if (skuID == null)
            return true;

        SharedPreferences purchasePreferences = context.getSharedPreferences(HAQuizDataManager.kPurchasesPreference, 0);
        String purchased = purchasePreferences.getString(skuID, null);
        return purchased != null;
    }


    public Boolean showAds() {
        if (HAConfiguration.getInstance().getShowAds()) {
            if (HAConfiguration.getInstance().isInAppPurchaseEnabledForRemovingAds()) {
                String removeAdsIdentifier = HAConfiguration.getInstance().getRemoveAdsProductID();
                if (removeAdsIdentifier != null) {
                    Boolean purchased = isProductPurchased(removeAdsIdentifier);
                    return !purchased;
                } else {
                    return true;
                }
            } else {
                return true;
            }
        }
        return false;
    }
}
