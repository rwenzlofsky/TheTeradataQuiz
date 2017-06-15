package com.dwhpro.Activities;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.dwhpro.RateThisApp;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.dwhpro.AppSettings.HAConfiguration;
import com.dwhpro.AppSettings.HASettings;
import com.dwhpro.AppSettings.HAUtilities;
import com.dwhpro.HAWorldScoreActivity;
import com.dwhpro.Singleton.HAGamesManager;
import com.dwhpro.Singleton.HAQuizDataManager;
import com.dwhpro.model.HACategory_new;
import com.dwhpro.multiplayer.basegameutil.GameHelper;
import com.dwhpro.quizappnew.R;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.ads.MobileAds;


import java.util.List;

public class HAMainActivity extends AppCompatActivity implements AnimationListener, GameHelper.GameHelperListener {
    LinearLayout playQuiz, multiplayer, moreCategory, worldScore, shareApp;
    LinearLayout animateLayout;
    Animation animMove1;
    LinearLayout settingButton, infoButton;
    InterstitialAd mInterstitialAd;
    AdView mAdView;
    HAConfiguration configurations;
    Boolean showWorldScoreActivity;
    GameHelper gameHelper;
    private static final int REQUEST_INVITE = 0;
    Intent intent;


    private void onInviteClicked() {


        intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                //.setCustomImage(Uri.parse(getString(R.string.invitation_custom_image)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();

        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(intent,
                PackageManager.MATCH_DEFAULT_ONLY);
        boolean isIntentSafe = activities.size() > 0;

        if(isIntentSafe) {

            startActivityForResult(intent, REQUEST_INVITE);

        } else {

            Toast.makeText(getApplicationContext(),"Invitations not available on your device!",Toast.LENGTH_LONG).show();;

        }





    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobileAds.initialize(this, "ca-app-pub-3715837112683644~7305062124");
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        ActionBar bar = getSupportActionBar();

        if(bar != null) {

            bar.hide();
        }

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Main Activity")
                .putContentType("Main Activity Showing")
                .putContentId("main"));



        RateThisApp.init(new RateThisApp.Config(3, 3));

        RateThisApp.setCallback(new RateThisApp.Callback() {
            @Override
            public void onYesClicked() {
                //Toast.makeText(HAMainActivity.this, "Yes event", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNoClicked() {
                //Toast.makeText(HAMainActivity.this, "No event", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelClicked() {
                //Toast.makeText(HAMainActivity.this, "Cancel event", Toast.LENGTH_SHORT).show();
            }
        });




        // Monitor launch times and interval from installation
        RateThisApp.onCreate(this);
        // Show a dialog if criteria is satisfied
        RateThisApp.showRateDialogIfNeeded(this);

        HAGamesManager.getInstance().context = getApplicationContext();

        HASettings settings = HASettings.getInstance();
        settings.context = getApplicationContext();
        settings.loadSettings();
        HAQuizDataManager.getInstance().context = getApplicationContext();

        setContentView(R.layout.activity_main);
        configurations = HAConfiguration.getInstance();
        HAUtilities.getInstance().setContext(getApplicationContext());

        /*
        try {
        	PackageInfo info = getPackageManager().getPackageInfo("com.lak.lakquizapp", PackageManager.GET_SIGNATURES);
        	for (Signature signature : info.signatures) {
        	    MessageDigest md = MessageDigest.getInstance("SHA");
        	    md.update(signature.toByteArray());
        	    Log.e("MY KEY HASH:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                Log.e("hash",String.valueOf(Base64.encodeToString(md.digest(), Base64.DEFAULT)));
        	    Toast.makeText(getApplicationContext(), String.valueOf(Base64.encodeToString(md.digest(), Base64.DEFAULT)), Toast.LENGTH_LONG).show();
        	}
        	} catch (NameNotFoundException e) {

        	} catch (NoSuchAlgorithmException e) {

        	}*/

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(Color.parseColor("#2D2D2D"));
        }
        playQuiz = (LinearLayout) findViewById(R.id.first_button);
        multiplayer = (LinearLayout) findViewById(R.id.second_button);
        worldScore = (LinearLayout) findViewById(R.id.four_button);
        shareApp = (LinearLayout) findViewById(R.id.five_button);
        infoButton = (LinearLayout) findViewById(R.id.info_button);
        settingButton = (LinearLayout) findViewById(R.id.setting_button);

        //mAdView.setAdUnitId(haConstant.getBannerAdId());
        //mAdView.setAdSize(AdSize.SMART_BANNER);

        TextView headerText = (TextView) findViewById(R.id.header_text);
        TextView firstButtonText = (TextView) findViewById(R.id.play_quiz_text);
        TextView secondButtonText = (TextView) findViewById(R.id.multiplayer_text);
        secondButtonText.setText(configurations.getMultiplayer1to1ScreenTitle());
        TextView thirdButtonText = (TextView) findViewById(R.id.more_categories_text);
        thirdButtonText.setText(configurations.getMoreCategoriesScreenTitle());
        TextView fourButtonText = (TextView) findViewById(R.id.world_score_text);
        fourButtonText.setText(configurations.getWorldScoreScreenTitle());
        TextView fiveButtonText = (TextView) findViewById(R.id.share_text);
        fiveButtonText.setText(configurations.getShareScreenTitle());

        Typeface text_face_thin = settings.getAppFontFace();
        headerText.setTypeface(text_face_thin);
        headerText.setText(configurations.getHomeScreenTitle());


        firstButtonText.setTypeface(text_face_thin);
        secondButtonText.setTypeface(text_face_thin);
        thirdButtonText.setTypeface(text_face_thin);
        fourButtonText.setTypeface(text_face_thin);




        showWorldScoreActivity = null;
        if (gameHelper == null) {
            getGameHelper();
        }
        gameHelper.setup(this);


        infoButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                HAUtilities.getInstance().playTapSound();

                Intent aboutIntent = new Intent(getApplicationContext(), HAAboutActivity.class);
                startActivity(aboutIntent);
                overridePendingTransition(R.anim.slide_up, R.anim.no_change);
            }
        });
        settingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                HAUtilities.getInstance().playTapSound();

                Intent settingIntent = new Intent(getApplicationContext(), HASettingsActivity_new.class);
                startActivity(settingIntent);
                overridePendingTransition(R.anim.slide_up, R.anim.no_change);
            }
        });
        worldScore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                HAUtilities.getInstance().playTapSound();

                worldScore.setPressed(true);
                worldScore.setFocusableInTouchMode(true);

                if (gameHelper.isSignedIn()) {
                    showWorldScoreActivity = true;
                    showWorldscoresOrMultiplayer();
                } else {

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HAMainActivity.this);
                    alertDialogBuilder.setTitle("Google Play Games");
                    alertDialogBuilder.setMessage("Sign in to view Leaderboards and Achivements!");
                    alertDialogBuilder.setPositiveButton("Sign in",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    showWorldScoreActivity = true;
                                    gameHelper.beginUserInitiatedSignIn();
                                }
                            });
                    alertDialogBuilder.setNegativeButton("No, thanks",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {
                                    HASettings.getInstance().setAutoSignIn(false);
                                }
                            });
                    alertDialogBuilder.show();
                }
            }
        });

        playQuiz.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                HAUtilities.getInstance().playTapSound();

                playQuiz.setPressed(true);
                playQuiz.setFocusableInTouchMode(true);

                Intent playQuizIntent = new Intent(getApplicationContext(), HACategoriesActivity_new.class);
                startActivity(playQuizIntent);
            }
        });

        shareApp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                HAUtilities.getInstance().playTapSound();

                shareApp.setPressed(true);
                shareApp.setFocusableInTouchMode(true);

                onInviteClicked();
            //    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.dwhpro.com"));
            //    startActivity(browserIntent);
            }
        });


        multiplayer.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                HAQuizDataManager.getInstance().isCategorySelectedForMultiplayerGame = false;
                // TODO Auto-generated method stub
                if (HAUtilities.getInstance().isNetworkConnectionAvailable()) {
                    HAUtilities.getInstance().playTapSound();
                    multiplayer.setPressed(true);
                    multiplayer.setFocusableInTouchMode(true);

                    if (gameHelper.isSignedIn()) {
                        showWorldScoreActivity = false;
                        showWorldscoresOrMultiplayer();
                    } else {
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(HAMainActivity.this);
                        alertDialogBuilder.setTitle("Google Play Games");
                        alertDialogBuilder.setMessage("Sign in to play multiplayer games!");
                        alertDialogBuilder.setPositiveButton("Sign in",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        showWorldScoreActivity = false;
                                        gameHelper.beginUserInitiatedSignIn();
                                    }
                                });
                        alertDialogBuilder.setNegativeButton("No, thanks",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        HASettings.getInstance().setAutoSignIn(false);
                                    }
                                });
                        alertDialogBuilder.show();
                    }
                } else {
                    Toast.makeText(HAMainActivity.this, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                }
            }
        });


        moreCategory = (LinearLayout) findViewById(R.id.third_button);

        if (HASettings.getInstance().isInAppurchaseEnabled()) {
            if (HAUtilities.getInstance().isNetworkConnectionAvailable())
            {
                HAQuizDataManager.getInstance().restorePurchases(); //ignores restoring if app is run in emulator
            }
        }

        if (HAConfiguration.getInstance().getEnableInAppPurchasesForCategories()) {
            if (HAUtilities.getInstance().isNetworkConnectionAvailable()) {

                moreCategory.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        //	InputStream stream = getAssets()

                        if (HAUtilities.isEmulator()) {
                            Toast.makeText(getApplicationContext(), "Can not test in emulator, create signed APK and test on Android mobile device!", Toast.LENGTH_LONG).show();
                            return;
                        }

                        HAUtilities.getInstance().playTapSound();

                        moreCategory.setPressed(true);
                        List<HACategory_new> categories = HAQuizDataManager.getInstance().getCategoriesRequirePurchase();
                        if (categories == null) {
                            Toast.makeText(HAMainActivity.this, "No quizzes for purchase!", Toast.LENGTH_SHORT).show();
                        } else {
                            Intent moreCategoriesIntent = new Intent(getApplicationContext(), HACategoriesActivity_new.class);
                            moreCategoriesIntent.putExtra("showMoreCategories", true);
                            startActivity(moreCategoriesIntent);
                        }
                    }

                });
            }
            moreCategory.setVisibility(View.INVISIBLE); //later be visible after animation
        } else
        {
            moreCategory.setVisibility(View.GONE);
        }

        playQuiz.setVisibility(View.INVISIBLE);
        multiplayer.setVisibility(View.INVISIBLE);
        worldScore.setVisibility(View.INVISIBLE);
        shareApp.setVisibility(View.INVISIBLE);


        animateLayout = playQuiz;

        startAnimateLayoutWithIntervalAndDelay(100, 100);
    }

    private void getGameHelper() {
        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        gameHelper.enableDebugLog(true);
    }


    private void showWorldscoresOrMultiplayer() {

        if (showWorldScoreActivity == null)
            return;

        if (showWorldScoreActivity) {
            HAUtilities.getInstance().playTapSound();
            Intent worldScoreIntent = new Intent(HAMainActivity.this.getApplicationContext(), HAWorldScoreActivity.class);
            startActivity(worldScoreIntent);

        } else {
            HAUtilities.getInstance().playTapSound();
            Intent multiplayerQuizIntent = new Intent(HAMainActivity.this.getApplicationContext(), HAMultiplayerMainActivity.class);
            startActivity(multiplayerQuizIntent);
        }
    }



    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        gameHelper.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    //Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
                // Sending failed or it was canceled, show failure message to the user
                // ...
            }
        }
    }

    @Override
    public void onSignInFailed() {
        //Toast.makeText(HAMainActivity.this, "Not signed in", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSignInSucceeded() {
        HASettings.getInstance().setAutoSignIn(true);
        HAGamesManager.getInstance().mGoogleApiClient = gameHelper.getApiClient();
        //Toast.makeText(HAMainActivity.this, "Signed in", Toast.LENGTH_SHORT).show();
        showWorldscoresOrMultiplayer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        showWorldScoreActivity = null;
        gameHelper.setConnectOnStart(HASettings.getInstance().isAutoSignIn());
        gameHelper.onStart(this);
        int density = getResources().getDisplayMetrics().densityDpi;
        Log.d("main start", "Density start :" + density);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (gameHelper != null) {
            gameHelper.onStop();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        HAQuizDataManager.getInstance().isMultiplayerGame = false;

        Boolean showAds = HAQuizDataManager.getInstance().requireAdsDisplay();
        mAdView = (AdView) findViewById(R.id.adView);

        if (!showAds) {
            mAdView.setVisibility(View.GONE);
        } else {

            AdRequest adRequestBanner = new AdRequest.Builder().build();
            mAdView.loadAd(adRequestBanner);
        }

    }


    private void showInterstitial() {
        // TODO Auto-generated method stub

        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
            playQuiz.clearAnimation();
            multiplayer.clearAnimation();
            worldScore.clearAnimation();
            shareApp.clearAnimation();
        } else {
            Log.d("didnotload", "addNotLoaded");
        }
    }


    private void startAnimateLayoutWithIntervalAndDelay(int interval, int delay) {
        animMove1 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down_fast);
        // set animation listener
        animMove1.setAnimationListener(this);

        //int interval = 1000; // 100 miliseconds
        Handler handler1 = new Handler();
        Runnable runnable1 = new Runnable() {
            public void run() {
                animateLayout.setVisibility(View.VISIBLE);
                animateLayout.startAnimation(animMove1);
            }
        };
        handler1.postAtTime(runnable1, System.currentTimeMillis() + interval);
        handler1.postDelayed(runnable1, delay);
    }

    @Override
    public void onAnimationEnd(Animation animation) {
        // TODO Auto-generated method stub

        animMove1 = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_down_fast);

        if (animateLayout == playQuiz) {
            animateLayout = multiplayer;
            animMove1.setAnimationListener(this);

        } else if (animateLayout == multiplayer) {
            if (moreCategory.getVisibility() == View.GONE) {
                animateLayout = worldScore;
                animMove1.setAnimationListener(this);
            } else {
                animateLayout = moreCategory;
                animMove1.setAnimationListener(this);
            }

        } else if (animateLayout == moreCategory) {
            animateLayout = worldScore;
            // set animation listener
            //animMove1.setAnimationListener(this);

        } else if (animateLayout == worldScore) {
            animateLayout = shareApp;
            animMove1.setAnimationListener(this);
        }

        animateLayout.setVisibility(View.VISIBLE);
        animateLayout.startAnimation(animMove1);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub
    }


}