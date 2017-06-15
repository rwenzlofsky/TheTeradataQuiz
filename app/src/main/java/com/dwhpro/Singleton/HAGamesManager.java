package com.dwhpro.Singleton;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.leaderboard.LeaderboardVariant;
import com.google.android.gms.games.leaderboard.Leaderboards;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchBuffer;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.dwhpro.AppSettings.HAConfiguration;
import com.dwhpro.model.HAGameTurnData;
import com.dwhpro.model.HAAchievement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by satie on 02/07/16.
 */

interface HAGamesManagerConstants {
    final String kTotalWinsPreference = "total_wins_preference";
    final String kMyWinsCount = "my_wins_count";
}



public class HAGamesManager extends Application implements HAGamesManagerConstants{

    // Client used to interact with Google APIs.
    public GoogleApiClient mGoogleApiClient;
    public Context context;
    public TurnBasedMatch turnBasedMatch;
    public HAGameTurnData turnData;
    public Boolean startNewMatch = false;
    public Boolean startRematch = false;
    private long totalWinsCount;


    private static HAGamesManager singleton = new HAGamesManager();

    private HAGamesManager() {

    }
    /* Static 'instance' method */
    public static HAGamesManager getInstance() {return singleton;}


    public void endMatch()
    {

        if (turnData.turnCount == 2) //loose the match
        {
            String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);

            turnData.secondUserScore = playerId + ",-1"; //loose second user
            Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient, turnBasedMatch.getMatchId(), turnData.persist())
                    .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                            turnBasedMatch = result.getMatch();
                        }
                    });
        }
        else
        {
            Games.TurnBasedMultiplayer.cancelMatch(mGoogleApiClient, turnBasedMatch.getMatchId())
                    .setResultCallback(new ResultCallback<TurnBasedMultiplayer.CancelMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.CancelMatchResult result) {
                            Toast.makeText(context, "This match has been canceled!", Toast.LENGTH_SHORT).show();
                        }
                    });
        }

    }


    public String getNextParticipantId() {

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = turnBasedMatch.getParticipantId(playerId);

        ArrayList<String> participantIds = turnBasedMatch.getParticipantIds();

        int desiredIndex = -1;

        for (int i = 0; i < participantIds.size(); i++) {
            if (participantIds.get(i).equals(myParticipantId)) {
                desiredIndex = i + 1;
            }
        }

        if (desiredIndex < participantIds.size()) {
            return participantIds.get(desiredIndex);
        }

        if (turnBasedMatch.getAvailableAutoMatchSlots() <= 0) {
            // You've run out of automatch slots, so we start over.
            return participantIds.get(0);
        } else {
            // You have not yet fully automatched, so null will find a new
            // person to play against.
            Log.d("AutoMatch","Null passed as participant id for automatch");
            return null;
        }
    }

    public void updateWins()
    {
        if (mGoogleApiClient == null)
            return;

        if (mGoogleApiClient.isConnected())
        {
            int[] statuses = new int[] {TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE};
            Games.TurnBasedMultiplayer.loadMatchesByStatus(mGoogleApiClient,statuses).setResultCallback(new ResultCallback<TurnBasedMultiplayer.LoadMatchesResult>() {
                @SuppressLint("CommitPrefEdits")

                @Override
                public void onResult(@NonNull TurnBasedMultiplayer.LoadMatchesResult loadMatchesResult) {

                    if( loadMatchesResult.getStatus().getStatusCode() != GamesStatusCodes.STATUS_OK )
                    {
                        System.out.println("updateWins :" + loadMatchesResult.getStatus().getStatusMessage());
                        return;
                    }

                    SharedPreferences myWinsPreferences = context.getSharedPreferences(kTotalWinsPreference,0);
                    SharedPreferences.Editor myWinsPreferencesEditor = myWinsPreferences.edit();

                    long myWinsCount = myWinsPreferences.getLong(kMyWinsCount,0);
                    totalWinsCount = myWinsCount;

                    TurnBasedMatchBuffer matches = loadMatchesResult.getMatches().getCompletedMatches();
                    for (int i = 0; i < matches.getCount(); i++)
                    {
                        TurnBasedMatch match = matches.get(i);

                        if (match == null)
                            continue;
                        else
                            System.out.println("updateWins match status:" + match.getStatus() + " Turn status :" + match.getTurnStatus());


                        int matchStatus = match.getStatus();
                        int turnStatus = match.getTurnStatus();

                        switch (matchStatus)
                        {
                            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE)
                                {
                                    HAGameTurnData matchTurnData = HAGameTurnData.unpersist(match.getData());

                                    assert matchTurnData != null;
                                    if (matchTurnData.turnCount == 2)
                                    {
                                        System.out.println("updateWins firstuserscore:" + matchTurnData.firstUserScore);
                                        System.out.println("updateWins seconduserscore:" + matchTurnData.secondUserScore);
                                        System.out.println("updateWins turnCount:" + matchTurnData.turnCount);

                                        String matchID = myWinsPreferences.getString(match.getMatchId(),null);
                                        if (matchID == null) //increase win count if I have won
                                        {
                                            String playerScoreWithID1 = matchTurnData.firstUserScore;
                                            ArrayList<String> scoreID1 = new  ArrayList<String>(Arrays.asList(playerScoreWithID1.split(",")));
                                            System.out.println("scoreID1 : "+playerScoreWithID1);

                                            String playerScoreWithID2 = matchTurnData.secondUserScore;
                                            System.out.println("scoreID2 : "+playerScoreWithID2);

                                            ArrayList<String> scoreID2 = new  ArrayList<String>(Arrays.asList(playerScoreWithID2.split(",")));

                                            String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
                                            int myscore = 0;
                                            int opponentScore = 0;

                                            if (scoreID1.get(0).equals(playerId)) //my score is in scoreID1 list
                                            {
                                                myscore = Integer.parseInt(scoreID1.get(1));
                                                opponentScore = Integer.parseInt(scoreID2.get(1));
                                            }
                                            else //my score is in scoreID2 list
                                            {
                                                myscore = Integer.parseInt(scoreID2.get(1));
                                                opponentScore = Integer.parseInt(scoreID1.get(1));
                                            }
                                            if (myscore > opponentScore)
                                            {
                                                totalWinsCount++;
                                                myWinsPreferencesEditor.putString(match.getMatchId(),match.getMatchId());
                                                myWinsPreferencesEditor.commit();
                                            }
                                        }
                                    }
                                }
                                break;
                            }
                        }

                    if (myWinsCount != totalWinsCount)
                    {
                        String totalWinsLeaderboardID = HAConfiguration.getInstance().getTotalWinsLeaderBoardIdinsLeaderBoardId();
                        Games.Leaderboards.submitScoreImmediate(mGoogleApiClient,totalWinsLeaderboardID,totalWinsCount).setResultCallback(new ResultCallback<Leaderboards.SubmitScoreResult>() {
                            @Override
                            public void onResult(@NonNull Leaderboards.SubmitScoreResult submitScoreResult) {

                                if (submitScoreResult.getStatus().getStatusCode() == 0)
                                {
                                    //Now update latest wins in preferences
                                    SharedPreferences myWinsPreferences = context.getSharedPreferences(kTotalWinsPreference,0);
                                    SharedPreferences.Editor myWinsPreferencesEditor = myWinsPreferences.edit();
                                    myWinsPreferencesEditor.putLong(kMyWinsCount,totalWinsCount);
                                    myWinsPreferencesEditor.commit();
                                    Toast.makeText(context, "Your match wins updated : "+ totalWinsCount, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
            });
        }
    }


    public void directUpdateTotalWins()
    {
        if (mGoogleApiClient == null)
            return;

        if (mGoogleApiClient.isConnected())
        {
            SharedPreferences myWinsPreferences = context.getSharedPreferences(kTotalWinsPreference,0);

            long myWinsCount = myWinsPreferences.getLong(kMyWinsCount,0);
            totalWinsCount = myWinsCount + 1;

            String totalWinsLeaderboardID = HAConfiguration.getInstance().getTotalWinsLeaderBoardIdinsLeaderBoardId();
            Games.Leaderboards.submitScoreImmediate(mGoogleApiClient,totalWinsLeaderboardID,totalWinsCount).setResultCallback(new ResultCallback<Leaderboards.SubmitScoreResult>() {
                @SuppressLint("CommitPrefEdits")
                @Override
                public void onResult(@NonNull Leaderboards.SubmitScoreResult submitScoreResult) {

                    if (submitScoreResult.getStatus().isSuccess())
                    {
                        //Now update latest wins in preferences
                        SharedPreferences myWinsPreferences = context.getSharedPreferences(kTotalWinsPreference,0);
                        SharedPreferences.Editor myWinsPreferencesEditor = myWinsPreferences.edit();
                        myWinsPreferencesEditor.putLong(kMyWinsCount,totalWinsCount);
                        myWinsPreferencesEditor.commit();
                        //Toast.makeText(context, "Your match wins updated: "+ totalWinsCount, Toast.LENGTH_LONG).show();
                        updateAchievementsForWins(totalWinsCount);
                    }
                }
            });
        }
    }

    public void updateAchievementsForWins(long wins)
    {
        List<HAAchievement> achievementsList = getAllAchievements();

        if (achievementsList == null)
            return;

        for (int i = 0; i <achievementsList.size() ; i++) {
            HAAchievement achievement = achievementsList.get(i);
            if (wins >= achievement.winsCount)
            {
                Games.Achievements.unlock(mGoogleApiClient, achievement.achivementID);
                Toast.makeText(context, "Achievement unlocked" + achievement.achivementID, Toast.LENGTH_LONG).show();
            }
        }
    }


    public void submitScore(final String leaderboardID, final long score)
    {
        if (mGoogleApiClient == null)
            return;

        if (mGoogleApiClient.isConnected())
        {
            Games.Leaderboards.loadCurrentPlayerLeaderboardScore(mGoogleApiClient, leaderboardID, LeaderboardVariant.TIME_SPAN_ALL_TIME, LeaderboardVariant.COLLECTION_PUBLIC).setResultCallback(new ResultCallback<Leaderboards.LoadPlayerScoreResult>() {
                @Override
                public void onResult(@NonNull  Leaderboards.LoadPlayerScoreResult scoreResult) {
                    long points;
                    if (GamesStatusCodes.STATUS_OK == scoreResult.getStatus().getStatusCode() && scoreResult.getScore() != null){
                        points = scoreResult.getScore().getRawScore();
                    }else{
                        points = 0L;
                    }

                    long scoreToPost =  points + score;
                    Games.Leaderboards.submitScore(mGoogleApiClient, leaderboardID, scoreToPost);
                }
            });
        }
    }



    public List<HAAchievement> getAllAchievements() {
        String json = null;
        List<HAAchievement> achievementsList = new ArrayList<HAAchievement>();

        try {
            InputStream stream = context.getAssets().open("achievements.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            json = new String(buffer, "UTF-8");
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.getJSONArray("Achievements");

            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    HAAchievement achievement = new HAAchievement();
                    achievement.achivementID = jsonObj.getString("Achievement_ID");
                    achievement.winsCount = Long.parseLong(jsonObj.getString("Wins"));
                    achievementsList.add(achievement);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }
        return achievementsList;
    }

    public Boolean isConnected()
    {
        if (mGoogleApiClient == null)
        {
            return false;
        }
        return mGoogleApiClient.isConnected();
    }
}
