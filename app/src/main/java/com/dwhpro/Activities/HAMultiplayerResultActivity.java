package com.dwhpro.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.dwhpro.AppSettings.HAConfiguration;
import com.dwhpro.AppSettings.HASettings;
import com.dwhpro.AppSettings.HAUtilities;
import com.dwhpro.Singleton.HAGamesManager;
import com.dwhpro.Singleton.HAQuizDataManager;
import com.dwhpro.TwitterActivity;
import com.dwhpro.model.HACategory_new;
import com.dwhpro.quizappnew.R;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class HAMultiplayerResultActivity extends Activity {

    TextView titleTextView;
    TextView resultTextView;
    TextView firstPointsTextView;
    TextView secondPointsView;

    ImageView prizeImageView;


    LinearLayout homeButtonLayout;
    Button rematchButton;
    Button newMatchButton;

    long score;

    ImageView facebookImageView;
    ImageView twitterImageView;
    ShareButton fbShareButton;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hamultiplayer_result);
        getActionBar().hide();
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("MultiplayerResult Activity")
                .putContentType("MultiplayerResult Activity Showing")
                .putContentId("multiplayerresult"));

        homeButtonLayout = (LinearLayout) findViewById(R.id.home_button_layout);
        homeButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Typeface appFontFace = HASettings.getInstance().getAppFontFace();
        score = this.getIntent().getLongExtra("score",-1);
        titleTextView = (TextView) findViewById(R.id.title_textview);
        resultTextView = (TextView) findViewById(R.id.result_textview);
        firstPointsTextView = (TextView) findViewById(R.id.first_points_textview);
        secondPointsView = (TextView) findViewById(R.id.second_points_textview);

        prizeImageView = (ImageView) findViewById(R.id.win_lose_imageview);

        rematchButton = (Button) findViewById(R.id.rematch_button);
        newMatchButton = (Button) findViewById(R.id.new_match_button);

        //set font face
        rematchButton.setTypeface(appFontFace);
        newMatchButton.setTypeface(appFontFace);
        firstPointsTextView.setTypeface(appFontFace);
        secondPointsView.setTypeface(appFontFace);
        titleTextView.setTypeface(appFontFace);

        HAGamesManager gamesManager = HAGamesManager.getInstance();
        TurnBasedMatch match = gamesManager.turnBasedMatch;
        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        Log.d("match status",Integer.toString(status));
        Log.d("match turn status",Integer.toString(turnStatus));
        System.out.println("Turn data : " + gamesManager.turnData);

        HACategory_new selectedCategory = HAQuizDataManager.getInstance().getCategoryForCategoryID(gamesManager.turnData.categoryID);
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
        mainLayout.setBackgroundColor(selectedCategory.getCategoryThemeColor());
        titleTextView.setText(selectedCategory.getCategoryName());

        if (score != -1)//update score on leaderboard
        {
            //submit points accumulating
            String leaderboardID = selectedCategory.getCategoryLeaderboardID();
            if (leaderboardID != null)
            {
                gamesManager.submitScore(leaderboardID,score);
                System.out.println("score submitted to leaderboard from multiplayer result activity : " + leaderboardID + "score :" + score);
            }
        }

        switch (status)
        {
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:

                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE || gamesManager.turnData.turnCount == 2)
                {
                    String playerScoreWithID1 = gamesManager.turnData.firstUserScore;
                    ArrayList<String> scoreID1 = new  ArrayList<String>(Arrays.asList(playerScoreWithID1.split(",")));

                    String playerScoreWithID2 = gamesManager.turnData.secondUserScore;
                    ArrayList<String> scoreID2 = new  ArrayList<String>(Arrays.asList(playerScoreWithID2.split(",")));

                    String playerId = Games.Players.getCurrentPlayerId(gamesManager.mGoogleApiClient);
                    long myscore = 0;
                    long opponentScore = 0;

                     String opponentParticipantID = gamesManager.getNextParticipantId();
                    Participant opponentParticipant = match.getParticipant(opponentParticipantID);

                    if (scoreID1.get(0).equals(playerId)) //my score is in scoreID1 list
                    {
                        myscore = Long.parseLong(scoreID1.get(1));
                        opponentScore = Long.parseLong(scoreID2.get(1));
                        firstPointsTextView.setText("You scored : " + myscore+ " points");
                        secondPointsView.setText(opponentParticipant.getDisplayName() + " scored : " + opponentScore + " points");
                    }
                    else //my score is in scoreID2 list
                    {
                        myscore = Long.parseLong(scoreID2.get(1));
                        opponentScore = Long.parseLong(scoreID1.get(1));
                        firstPointsTextView.setText("You scored : " + myscore + " points");
                        secondPointsView.setText(opponentParticipant.getDisplayName() + " scored : " + opponentScore + " points");
                    }

                    if (myscore > opponentScore)
                    {
                        resultTextView.setText("YOU WIN!");
                        prizeImageView.setImageResource(R.drawable.cup);
                        boolean updateLeaderboard = getIntent().getBooleanExtra("update_win_count",false);
                        if (updateLeaderboard)
                        {
                            HAGamesManager.getInstance().directUpdateTotalWins();
                        }
                    }
                    else if (myscore == opponentScore)
                    {
                        resultTextView.setText("TIE!");
                        prizeImageView.setImageResource(R.drawable.opponent);
                    }
                    else {
                        resultTextView.setText("YOU LOSE!");
                        prizeImageView.setImageResource(R.drawable.lose);
                    }
                }
                else
                {
                    finish();
                }
                break;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                //if (TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN == turnStatus)
                {
                    firstPointsTextView.setText("You scored : " + score + " points");
                    secondPointsView.setVisibility(View.GONE);
                    resultTextView.setText("ITS OPPONENT'S TRUN!");
                    Toast.makeText(this, "Finding a player for you!", Toast.LENGTH_SHORT).show();
                }
                break;
        }


        // OK, it's active. Check on turn status.
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                    firstPointsTextView.setText("You scored : " + score + " points");
                    secondPointsView.setText("");
                    resultTextView.setText("ITS OPPONENT'S TURN!");
                break;
        }


        if (match.canRematch())
        {
            rematchButton.setVisibility(View.VISIBLE);
            rematchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //HAUtilities.getInstance().playTapSound();
                    HAGamesManager.getInstance().startRematch = true;
                    onBackPressed();
                    finish();
                }
            });
        }
        else
        {
            rematchButton.setVisibility(View.GONE);
        }

        newMatchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //HAUtilities.getInstance().playTapSound();
                HAGamesManager.getInstance().startNewMatch = true;
                onBackPressed();
            }
        });


        facebookImageView = (ImageView) findViewById(R.id.facebook_image_button);
        fbShareButton = (ShareButton) findViewById(R.id.share_btn_fb);
        twitterImageView = (ImageView) findViewById(R.id.twitter_image_button);


        callbackManager = CallbackManager.Factory.create();


        if (HAConfiguration.getInstance().enableFacebookScoresSharing)
        {
            fbShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (HAUtilities.getInstance().isNetworkConnectionAvailable()) {

                        AppEventsLogger.activateApp(getApplication());

                        List<String> permissionNeeds = Collections.singletonList("publish_actions");

                        //this loginManager helps you eliminate adding a LoginButton to your UI
                        LoginManager manager = LoginManager.getInstance();

                        manager.logInWithPublishPermissions(HAMultiplayerResultActivity.this, permissionNeeds);

                        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                //sharePhotoToFacebook();
                                Bitmap screenShot = screenShot();
                                SharePhoto photo = new SharePhoto.Builder()
                                        .setBitmap(screenShot)
                                        .setCaption("Download now : " + HAConfiguration.getInstance().playStoreLinkOfThisApplication)
                                        .build();
                                SharePhotoContent content = new SharePhotoContent.Builder()
                                        .addPhoto(photo)
                                        .build();
                                ShareApi.share(content, null);
                            }
                            @Override
                            public void onCancel() {
                                System.out.println("onCancel");
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                System.out.println("onError");
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(HAMultiplayerResultActivity.this, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                    }

                }
            });
        }
        else {
            facebookImageView.setVisibility(View.GONE);
        }

        facebookImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HAUtilities.getInstance().playTapSound();
                fbShareButton.performClick();
            }
        });

        if (HAConfiguration.getInstance().enableTwitterScoresSharing)
        {
            twitterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (HAUtilities.getInstance().isNetworkConnectionAvailable()) {

                        Bitmap screenshotimage = screenShot();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        screenshotimage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        Intent twitterIntent = new Intent(getApplicationContext(), TwitterActivity.class);
                        twitterIntent.putExtra("score",HAConfiguration.getInstance().playStoreLinkOfThisApplication);
                        twitterIntent.putExtra("appURL","");
                        twitterIntent.putExtra("image", byteArray);
                        //	twitterIntent.putExtra("", String.valueOf(shareb))
                        startActivity(twitterIntent);
                    }
                    else
                    {
                        Toast.makeText(HAMultiplayerResultActivity.this, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        else
        {
            twitterImageView.setVisibility(View.GONE);
        }


    }

    public Bitmap screenShot() {
        View view = getWindow().getDecorView().getRootView();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (callbackManager!= null)
            callbackManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (HAQuizDataManager.getInstance().requireAdsDisplay())
        {
            Intent addIntent = new Intent(this, HAInterstitialAdActivity.class);
            startActivity(addIntent);
            overridePendingTransition(0, 0);
            finish();
        }
        else
        {
            finish();
        }
    }
}
