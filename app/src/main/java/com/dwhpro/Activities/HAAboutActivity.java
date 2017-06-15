package com.dwhpro.Activities;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.dwhpro.AppSettings.HAConfiguration;
import com.dwhpro.AppSettings.HASettings;
import com.dwhpro.AppSettings.HAUtilities;
import com.dwhpro.Singleton.HAQuizDataManager;
import com.dwhpro.quizappnew.R;

import java.net.URL;

import static com.facebook.login.widget.ProfilePictureView.TAG;

public class HAAboutActivity extends Activity{
	 private WebView mWebview ;
	 Boolean purchased;
	Boolean isSoundOn;
	HAConfiguration configurations;
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.activity_haabout);

			Answers.getInstance().logContentView(new ContentViewEvent()
					.putContentName("About Activity")
					.putContentType("About Activity Showing")
					.putContentId("about"));

			configurations = HAConfiguration.getInstance();
			String EXTRA_URL = HAConfiguration.getInstance().getAboutWebLink();
			LinearLayout webViewlayout = (LinearLayout) findViewById(R.id.web_view_layout);
            webViewlayout.setVisibility(View.GONE);
            LinearLayout textViewlayout = (LinearLayout) findViewById(R.id.text_view_layout);


			textViewlayout.setVisibility(View.GONE);

			Typeface text_face_thin = HASettings.getInstance().getAppFontFace();
            TextView titleText = (TextView) findViewById(R.id.header_textview);
            titleText.setTypeface(text_face_thin);

            if (configurations.getuseLinkForAboutScreen()) {
            	webViewlayout.setVisibility(View.VISIBLE);
            	mWebview = (WebView) findViewById(R.id.fb_page_web_view);
    	        mWebview.setWebViewClient(new MyWebViewClient());
    	        mWebview.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
    	        mWebview.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
    	        mWebview.getSettings().setJavaScriptEnabled(true);
    	        mWebview.loadUrl(EXTRA_URL);
			} else
			{
                textViewlayout.setVisibility(View.VISIBLE);
                TextView aboutText =(TextView) findViewById(R.id.about_textview);
				aboutText.setMovementMethod(new ScrollingMovementMethod());
				aboutText.setTypeface(text_face_thin);
                aboutText.setText(configurations.getAboutText());
			}

	        if (Build.VERSION.SDK_INT >= 21) {
	            Window window = getWindow();
	            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
	            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
	            window.setStatusBarColor(Color.parseColor("#2D2D2D"));
	        }
            LinearLayout homeImageView = (LinearLayout) findViewById(R.id.home_button_layout);
            homeImageView.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					onBackPressed();
				}
			});
	    }
	    @Override
		public void onBackPressed() {
			HAUtilities.getInstance().playTapSound();

			Boolean showAds = HAQuizDataManager.getInstance().requireAdsDisplay();

	    		if (!showAds) {
					this.finish();
				} else {
					 Intent addIntent = new Intent(this,HAInterstitialAdActivity.class);
					 startActivity(addIntent);
					 this.overridePendingTransition(0, 0);
					 this.finish();
				}

		}
	    
	    @Override
	    protected void onResume() {
	    	// TODO Auto-generated method stub
	    	super.onResume();
	    	laodAdsSharedPreference();
			loadSoundPreference();
	    }
	    
	    private void laodAdsSharedPreference() {
			// TODO Auto-generated method stub
			SharedPreferences purchaseAdd = getSharedPreferences("purchseadd", 0);
			purchased = purchaseAdd.getBoolean("purchase_add", false);
		}
	private void loadSoundPreference() {
		// TODO Auto-generated method stub
		SharedPreferences soundreferences = getSharedPreferences("sound", 0);

		isSoundOn = soundreferences.getBoolean("turnOnOff", true);
	}
	    
	    class MyWebViewClient extends WebViewClient {
	    	 @Override
	         public void onPageStarted(WebView view, String url, Bitmap favicon) {
	             // TODO Auto-generated method stub
	             super.onPageStarted(view, url, favicon);
	         }
/*	         @Override
	         public boolean shouldOverrideUrlLoading(WebView view, String url) {
	             // TODO Auto-generated method stub
	             view.loadUrl(url);
	             return true;
	         }*/

			@SuppressWarnings("deprecation")
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {

				return handleUrl(url,view);
			}

			@TargetApi(Build.VERSION_CODES.N)
			@Override
			public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
				final Uri uri = request.getUrl();

				return handleUrl(uri.toString(),view);
			}


			private boolean handleUrl(final String url, WebView view) {
                view.loadUrl(url);
                return true;
			}
	}

}

	
