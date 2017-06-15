package com.dwhpro.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.dwhpro.AppSettings.HAConfiguration;
import com.dwhpro.AppSettings.HASettings;
import com.dwhpro.AppSettings.HAUtilities;

public class HAInterstitialAdActivity extends Activity{
	InterstitialAd mInterstitialAd;
	ProgressDialog pDialog;
	MediaPlayer mp;
	Boolean isSoundOn;
	HAConfiguration configurations;
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	this.requestWindowFeature(Window.FEATURE_ACTION_BAR);


	configurations = HAConfiguration.getInstance();

	if (!HASettings.getInstance().showAds())
	{
		finish();
	}


	if (isNetworkConnected())
	{
		showPDialog();
		mInterstitialAd = new InterstitialAd(this);
		mInterstitialAd.setAdUnitId(configurations.getInterstitialAdId());
		mInterstitialAd.setAdListener(new AdListener() {
			@Override
			public void onAdClosed() {
				////startGame();
				//overridePendingTransition(0, 0);
				hidePDialog();
				finish();
				overridePendingTransition(0, 0);
			}
		});
		
	}
	else{
		this.finish();
	}
}

private void showInterstitial() {
	// TODO Auto-generated method stub
	if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
		mInterstitialAd.show();
	} else {
		
		loadAdAgain();
	}
}

private void loadAdAgain(){

	int interval = 2000; // 100 miliseconds
	Handler handler1 = new Handler();
	Runnable runnable1 = new Runnable(){
		public void run() {
			
			
			onResume();
		}
	};
	handler1.postAtTime(runnable1, System.currentTimeMillis()+interval);
	handler1.postDelayed(runnable1, interval);
}

@Override
protected void onResume() {
	// TODO Auto-generated method stub
	super.onResume();

	if (!mInterstitialAd.isLoaded()){
		AdRequest adRequest = new AdRequest.Builder().build();
		mInterstitialAd.loadAd(adRequest);
	}

	showInterstitial();
}
private void showPDialog(){
	pDialog = new ProgressDialog(HAInterstitialAdActivity.this);
    pDialog.show();

}

private void hidePDialog() {
	if (pDialog != null) {
		pDialog.dismiss();
		pDialog = null;
	}
	}

@Override
public void onBackPressed() {
	// TODO Auto-generated method stub
	super.onBackPressed();
	HAUtilities.getInstance().playTapSound();
	this.finish();
	overridePendingTransition(0, 0);
}
private boolean isNetworkConnected() {
	  ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

	  return cm.getActiveNetworkInfo() != null;
	 }
}
