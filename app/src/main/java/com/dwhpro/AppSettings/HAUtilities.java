package com.dwhpro.AppSettings;


import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.dwhpro.quizappnew.R;

/**
 * Created by satie on 02/07/16.
 */

public class HAUtilities {
    public void setContext(Context context) {
        this.context = context;
    }

    Context context;

    private static HAUtilities singleton = new HAUtilities();
    private HAUtilities() {
    }
    /* Static 'instance' method */
    public static HAUtilities getInstance() {
        return singleton;
    }


    public void playCorrectAnswerSound() {
        if (HASettings.getInstance().getSoundEnabled())
        {
            MediaPlayer mp;
            mp = MediaPlayer.create(context, R.raw.right);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.reset();
                    mp.release();
                }
            });
            mp.start();
        }
    }


    public void playWrongAnswerSound() {
        if (HASettings.getInstance().getSoundEnabled())
        {
            MediaPlayer mp;
            mp = MediaPlayer.create(context, R.raw.wrong);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.reset();
                    mp.release();
                }
            });
            mp.start();
        }
    }


    public void playTapSound()
    {
        if (HASettings.getInstance().getSoundEnabled())
        {
            MediaPlayer mp;
            mp = MediaPlayer.create(context, R.raw.tap);
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mp.reset();
                    mp.release();
                }
            });
            mp.start();
        }
    }

    public static String MD5ForString(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < array.length; ++i) {
                sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
        }
        return null;
    }

    public static boolean isEmulator() {
        return Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || "google_sdk".equals(Build.PRODUCT);
    }

    // added as an instance method to an Activity
    public boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info == null) return false;
        NetworkInfo.State network = info.getState();
        return (network == NetworkInfo.State.CONNECTED || network == NetworkInfo.State.CONNECTING);
    }


}
