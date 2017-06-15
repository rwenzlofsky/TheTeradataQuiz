package com.dwhpro.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.dwhpro.AppSettings.HAConfiguration;
import com.dwhpro.AppSettings.HASettings;
import com.dwhpro.AppSettings.HAUtilities;
import com.dwhpro.Singleton.HAGamesManager;
import com.dwhpro.Singleton.HAQuizDataManager;
import com.dwhpro.model.HACategory_new;
import com.dwhpro.model.HAGameTurnData;
import com.dwhpro.quizappnew.R;

import java.util.ArrayList;


public class HAMultiplayerMainActivity extends Activity{

    GoogleApiClient mGoogleApiClient;
    final String TAG = "Multiplayer";
    // For our intents
    private static final int RC_SIGN_IN = 9001;
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;


    LinearLayout playNowButton;
    LinearLayout matchesButton;
    LinearLayout homeButtonLayout;

    TextView usernameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hamultiplayer_main);
        getActionBar().hide();

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Multiplayer Activity")
                .putContentType("Multiplayer Activity Showing")
                .putContentId("multiplayer"));

        Typeface appFontFace = HASettings.getInstance().getAppFontFace();

        Log.v("YYYY","OnCreate called");
        HAQuizDataManager.getInstance().isMultiplayerGame = true;

        playNowButton = (LinearLayout) findViewById(R.id.play_now_layout);
        matchesButton = (LinearLayout) findViewById(R.id.matches_button_layout);
        usernameTextView = (TextView) findViewById(R.id.sign_user_name_textview);
        usernameTextView.setText("");

        //setting app font face to text
        ((TextView)playNowButton.getChildAt(1)).setTypeface(appFontFace);
        ((TextView)matchesButton.getChildAt(1)).setTypeface(appFontFace);
        ((TextView) findViewById(R.id.header_textview)).setTypeface(appFontFace);
        ((TextView) findViewById(R.id.sign_in_display_textview)).setTypeface(appFontFace);

        mGoogleApiClient = HAGamesManager.getInstance().mGoogleApiClient;

        usernameTextView.setTypeface(appFontFace);
        playNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                HAUtilities.getInstance().playTapSound();

                Intent playQuizIntent = new Intent(getApplicationContext(), HACategoriesActivity_new.class);
                startActivity(playQuizIntent);

            }
        });

        matchesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HAUtilities.getInstance().playTapSound();
                Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(mGoogleApiClient);
                startActivityForResult(intent, RC_LOOK_AT_MATCHES);
            }
        });

        homeButtonLayout = (LinearLayout) findViewById(R.id.home_button_layout);
        homeButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HAUtilities.getInstance().playTapSound();
                finish();
            }
        });

        HAGamesManager.getInstance().updateWins();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        Log.v("YYYY","onNewIntent called");
    }

    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        Log.v("YYYY","onActivityResult called");
        dismissSpinner();

        boolean userLoggedOut = (response == GamesActivityResultCodes.RESULT_RECONNECT_REQUIRED);
        if (userLoggedOut) {
            mGoogleApiClient.disconnect();
            HASettings.getInstance().setAutoSignIn(false);
            Toast.makeText(this, "You are signed out of Google Play Games!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }


        Log.d("request code :", String.valueOf(request));
        Log.d("response code :", String.valueOf(response));

        if (request == RC_LOOK_AT_MATCHES) {


            Log.d("GameHelper","Lookat matches called");
            // Returning from the 'Select Match' dialog
            if (response != Activity.RESULT_OK) {
                // user canceled
                return;
            }
            TurnBasedMatch match = data
                    .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (match != null) {
                //update match here
                updateMatch(match);
                Log.d("GameHelper","Update match called");
            } else {
               // Toast.makeText(getApplicationContext(), "finish", Toast.LENGTH_LONG).show();
            }

            //    Log.d(TAG, "Match = " + match);
        } else if (request == RC_SELECT_PLAYERS) {


            // Returned from 'Select players to Invite' dialog
            if (response != Activity.RESULT_OK) {
                // user canceled
            }
            else{

                TurnBasedMatch match = data
                        .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);
                if (match != null) {
                    //update match here
                    updateMatch(match);
                    Log.d("GameHelper","Update match called");
                } else {
                    // Toast.makeText(getApplicationContext(), "finish", Toast.LENGTH_LONG).show();
                    Log.d("GameHelper","startNewMatchWithIntent from onActivityResult");
                    startNewMatchWithIntent(data);
                }
            }
        }
    }



    public void showSpinner() {
        findViewById(R.id.progressLayout).setVisibility(View.VISIBLE);
    }

    public void dismissSpinner() {
        findViewById(R.id.progressLayout).setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        Log.v("YYYY","onStart called");
        checkIfMatchNeedsToBeStarted();

        if (HAQuizDataManager.getInstance().isCategorySelectedForMultiplayerGame)
        {
            Intent intent = Games.TurnBasedMultiplayer.getSelectOpponentsIntent(mGoogleApiClient,
                    1, 1, true);
            startActivityForResult(intent, RC_SELECT_PLAYERS);
        }

    }
    @Override
    protected void onRestart()
    {
        super.onRestart();
        Log.v("YYYY","onRestart called");
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    private void checkIfMatchNeedsToBeStarted()
    {
        Log.d("GameHelper","checkIfMatchNeedsToBeStarted");
        if (HAGamesManager.getInstance().startNewMatch)
        {
            HAGamesManager.getInstance().startNewMatch = false;
            showSpinner();
            playNowButton.performClick();
        }

        if (HAGamesManager.getInstance().startRematch)
        {
            HAGamesManager.getInstance().startRematch = false;
            showSpinner();

            //HAUtilities.getInstance().playTapSound();

            Games.TurnBasedMultiplayer.rematch(mGoogleApiClient, HAGamesManager.getInstance().turnBasedMatch.getMatchId()).setResultCallback(
                    new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                            dismissSpinner();
                            processResult(result);
                        }
                    });
        }
    }

    private void startNewMatchWithIntent(Intent intent)
    {
        //    Log.d("activityResult", "ResultOK");
        // get the invitee list
        final ArrayList<String> invitees = intent
                .getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
        // get automatch criteria
        Bundle autoMatchCriteria = null;

        int minAutoMatchPlayers = intent.getIntExtra(
                Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
        int maxAutoMatchPlayers = intent.getIntExtra(
                Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);


        if (minAutoMatchPlayers > 0) {
            Log.d("Automatch","Minimum players"+minAutoMatchPlayers);
            Log.d("Automatch","maximum players"+maxAutoMatchPlayers);

            autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                    minAutoMatchPlayers, maxAutoMatchPlayers, 0);
        } else {
            autoMatchCriteria = null;
        }

        Log.d("Automatch","invitees"+invitees);
        Log.d("Automatch","autoMatchCriteria"+autoMatchCriteria);

        HACategory_new category = HAQuizDataManager.getInstance().selectedCategory;

        TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                .addInvitedPlayers(invitees)
                .setAutoMatchCriteria(autoMatchCriteria).setVariant(Integer.parseInt(category.getCategoryID())).build();
        // Start the match
        Games.TurnBasedMultiplayer.createMatch(mGoogleApiClient, tbmc).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {

                    @Override
                    public void onResult(@NonNull TurnBasedMultiplayer.InitiateMatchResult result) {
                        dismissSpinner();
                        processResult(result);
                        //Log.d(TAG, "create match :" + String.valueOf(result.getMatch().getDescriptionParticipant()));
                    }
                });
        showSpinner();
    }

    private void startMatch(TurnBasedMatch match)
    {
        Log.d("GameHelper","startMatch");
        System.out.println("match status from multiplayer main activity startMatchmethod:" + match.getStatus() + " Turn status :" + match.getTurnStatus());
        dismissSpinner();

            HAGamesManager.getInstance().turnBasedMatch = match;
            HAGamesManager.getInstance().turnData = new HAGameTurnData();

        Intent playQuizIntent = new Intent(getApplicationContext(), HAGameActivity_new.class);
        startActivity(playQuizIntent);
    }

    private void takeSecondTurn(TurnBasedMatch match)
    {
        dismissSpinner();
            if (match.getData() == null)
            {
                startMatch(match);
            }
            else
            {
                HAGameTurnData turnData = HAGameTurnData.unpersist(match.getData());
                HAGamesManager.getInstance().turnData = turnData;
                HACategory_new category = HAQuizDataManager.getInstance().getCategoryForCategoryID(turnData.categoryID);

                if (HAConfiguration.getInstance().getEnableInAppPurchasesForCategories())
                {
                    System.out.println("product identifier :"+category.getCategoryProductIdetifier());

                    if (HASettings.getInstance().isProductPurchased(category.getCategoryProductIdetifier()))
                    {
                        System.out.println("match status from multiplayer main activity takeSecondTurnmethod:" + match.getStatus() + " Turn status :" + match.getTurnStatus());
                        Intent playQuizIntent = new Intent(getApplicationContext(), HAGameActivity_new.class);
                        startActivity(playQuizIntent);
                    }
                    else
                    {
                        Toast.makeText(this, "To play this match buy \""+category.getCategoryName()+"\" quiz from More categories!", Toast.LENGTH_SHORT).show();
                    }
                }else
                {
                    System.out.println("-match status from multiplayer main activity takeSecondTurnmethod:" + match.getStatus() + " Turn status :" + match.getTurnStatus());
                    Intent playQuizIntent = new Intent(getApplicationContext(), HAGameActivity_new.class);
                    startActivity(playQuizIntent);
                }
            }
    }

    private void updateMatch(TurnBasedMatch match) {
        dismissSpinner();
        int status = match.getStatus();
        String message = null;

        HAGamesManager gamesManager = HAGamesManager.getInstance();
        gamesManager.turnData = HAGameTurnData.unpersist(match.getData());
        gamesManager.turnBasedMatch = match;
        System.out.println("updateMatch - match status from multiplayer main activity:" + match.getStatus() + " Turn status :" + match.getTurnStatus());


        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                break;
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                message = getString(R.string.this_match_was_canceled);
                break;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                message = getString(R.string.this_match_expired);
                break;
            case TurnBasedMatch.MATCH_STATUS_ACTIVE:
                break;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (gamesManager.turnData.turnCount == 2)
                {
                    Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient, match.getMatchId());
                    Intent multiplayerResultActivity = new Intent(getApplicationContext(), HAMultiplayerResultActivity.class);
                    startActivity(multiplayerResultActivity);
                    return;
                }
                break;
        }

        int turnStatus = match.getTurnStatus();
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                message = getString(R.string.waiting_for_opponents_turn);
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE:
                if (status == TurnBasedMatch.MATCH_STATUS_COMPLETE)
                {
                        Intent multiplayerResultActivity = new Intent(getApplicationContext(), HAMultiplayerResultActivity.class);
                        startActivity(multiplayerResultActivity);
                        return;
                }
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                takeSecondTurn(match);
                break;
        }

        if (message != null)
        {
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }
    }


    private void processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        TurnBasedMatch match = result.getMatch();
        // Log.d("progressResult", "InitiateMatchResult");
        dismissSpinner();

        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }

        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            Log.d("UpdateMatch", "in update match");
            updateMatch(match);
            //update match as its aleady playing match
            return;
        }

        //start new match
        startMatch(match);
    }



    // Returns false if something went wrong, probably. This should handle
    // more cases, and probably report more accurate results.
    private boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                return true;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(match, statusCode,
                        R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_already_rematched);
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(match, statusCode,
                        R.string.network_error_operation_failed);
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                showErrorMessage(match, statusCode,
                        R.string.client_reconnect_required);
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                showErrorMessage(match, statusCode, R.string.internal_error);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(match, statusCode,
                        R.string.match_error_inactive_match);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_locally_modified);
                break;
            default:
                showErrorMessage(match, statusCode, R.string.unexpected_status);
                //     Log.d(TAG, "Did not have warning or string to deal with: "
                //          + statusCode);
        }

        return false;
    }

    public void showErrorMessage(TurnBasedMatch match, int statusCode,
                                 int stringId) {

        showWarning("Warning", getResources().getString(stringId));
    }

    // Generic warning/info dialog
    public void showWarning(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // set title
        alertDialogBuilder.setTitle(title).setMessage(message);
        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
//                        if (isSoundOn) {
//                            mp2.start();
//                        }
                    }
                });
        alertDialogBuilder.create().show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HAQuizDataManager.getInstance().isCategorySelectedForMultiplayerGame = false;
        HAUtilities.getInstance().playTapSound();
        finish();
    }
}
