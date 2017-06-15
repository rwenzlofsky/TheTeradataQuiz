package com.dwhpro;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.dwhpro.AppSettings.HASettings;
import com.dwhpro.AppSettings.HAUtilities;
import com.dwhpro.Singleton.HAGamesManager;
import com.dwhpro.quizappnew.R;

public class HAWorldScoreActivity extends Activity {
    LinearLayout achievementButton, leaderButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_worldscore);

        Typeface appFontFace = HASettings.getInstance().getAppFontFace();

        ((TextView) findViewById(R.id.title_textview)).setTypeface(appFontFace);

        ((TextView) findViewById(R.id.achievement_textview)).setTypeface(appFontFace);
        ((TextView) findViewById(R.id.leaderboards_textview)).setTypeface(appFontFace);

        LinearLayout homeImage = (LinearLayout) findViewById(R.id.home_image_world_score);
        homeImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                onBackPressed();
            }
        });
        achievementButton = (LinearLayout) findViewById(R.id.achievement_button);

        leaderButton = (LinearLayout) findViewById(R.id.leaderboards_button);

        achievementButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HAUtilities.getInstance().playTapSound();
                try {
                    startActivityForResult(Games.Achievements.getAchievementsIntent(HAGamesManager.getInstance().mGoogleApiClient), 5001);
                }
                catch(IllegalStateException e) {

                    Toast.makeText(getApplicationContext(),"Could not connect to play services!",Toast.LENGTH_LONG).show();

                }

            }
        });
        leaderButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HAUtilities.getInstance().playTapSound();
                try {
                    startActivityForResult(Games.Leaderboards.getAllLeaderboardsIntent(HAGamesManager.getInstance().mGoogleApiClient), 5002);
                } catch(IllegalStateException e) {
                    Toast.makeText(getApplicationContext(),"Could not connect to play services!",Toast.LENGTH_LONG).show();

                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean userLoggedOut = (resultCode == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED);
        if (userLoggedOut) {
            HAGamesManager.getInstance().mGoogleApiClient.disconnect();
            HASettings.getInstance().setAutoSignIn(false);
            Toast.makeText(this, "You are signed out of Google Play Games!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        HAUtilities.getInstance().playTapSound();
        finish();
    }

    protected void onStart() {
        super.onStart();
    }

        protected void onStop() {
            super.onStop();
        }

}
