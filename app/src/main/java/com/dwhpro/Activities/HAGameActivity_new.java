package com.dwhpro.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.LevelEndEvent;
import com.crashlytics.android.answers.LevelStartEvent;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.dwhpro.AppSettings.HAConfiguration;
import com.dwhpro.AppSettings.HASettings;
import com.dwhpro.AppSettings.HAUtilities;
import com.dwhpro.Singleton.HAGamesManager;
import com.dwhpro.Singleton.HAQuizDataManager;
import com.dwhpro.countdownanimation.CountDownProgress;
import com.dwhpro.model.HACategory_new;
import com.dwhpro.model.HAGameTurnData;
import com.dwhpro.model.HAQuestion;
import com.dwhpro.quizappnew.R;
import com.dwhpro.service.BackGroundMusicService;
import com.dwhpro.utils.CircleImageView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.List;

public class HAGameActivity_new extends Activity implements View.OnClickListener{

    private FirebaseAnalytics mFirebaseAnalytics;
    //UI controls
    TextView questionTextview;
    TextView pointsTextview;
    TextView questionsNumberTextview;
    TextView titleTextview;
    TextView explanationTextview;
    TextView questionAnsweredStatusTextview;
    LinearLayout explanationLayout;
    LinearLayout homeButtonLayout;
    Button nextButtonOnExplanationLayout;
    RelativeLayout optionsLayout;

    Button option1Button;
    Button option2Button;
    Button option3Button;
    Button option4Button;

    Button skipButton;
    CircleImageView questionImageview;

    RelativeLayout fullMediaScreenLayout;
    ImageView fullScreenImageView;
    VideoView videoView;
    MediaController mediaController;

    CountDownProgress countDownProgress;
    CountDownTimer countDownTimer;


    //data elements
    int questionIndex = 0;
    long score = 0;
    public HACategory_new currentCategory;
    List<HAQuestion> questionsList;
    HAQuestion.HAQuestionType questionType;
    HAQuestion currentQuestion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);




        setContentView(R.layout.activity_hagame_activity_new);
        getActionBar().hide();

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Game Activity")
                .putContentType("Game Activity Showing")
                .putContentId("game"));

        Typeface typeface = HASettings.getInstance().getAppFontFace();

        questionIndex = 0;
        countDownProgress = (CountDownProgress) findViewById(R.id.progress_view);

        optionsLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        questionTextview = (TextView) findViewById(R.id.question_textview);
        questionTextview.setMovementMethod(new ScrollingMovementMethod());
        questionImageview = (CircleImageView) findViewById(R.id.question_imageview);
        pointsTextview  = (TextView) findViewById(R.id.points_textview);
        questionsNumberTextview = (TextView) findViewById(R.id.question_number_textview);
        titleTextview = (TextView) findViewById(R.id.title_textview);
        titleTextview.setTypeface(typeface);
        fullScreenImageView = (ImageView) findViewById(R.id.full_screen_imageview);
        videoView = (VideoView) findViewById(R.id.video_view);
        fullMediaScreenLayout = (RelativeLayout) findViewById(R.id.full_screen_image_layout);

        explanationTextview = (TextView) findViewById(R.id.explanation_textview);
        explanationTextview.setTypeface(typeface);
        explanationTextview.setMovementMethod(new ScrollingMovementMethod());
        questionAnsweredStatusTextview = (TextView) findViewById(R.id.answer_status_textview);
        explanationLayout = (LinearLayout) findViewById(R.id.explanation_layout);
        nextButtonOnExplanationLayout = (Button) findViewById(R.id.explanation_next_button);
        nextButtonOnExplanationLayout.setTypeface(typeface);

        //FirebaseCrash.log("Game played");


        //on taping explnation view it should hide itself and advance to next question
        nextButtonOnExplanationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                explanationLayout.setVisibility(View.GONE);
                HAGameActivity_new.this.nextQuestion();
            }
        });

        if (!HAQuizDataManager.getInstance().isMultiplayerGame)
        {
            homeButtonLayout = (LinearLayout) findViewById(R.id.home_button_layout);
            homeButtonLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }

        option1Button = (Button) findViewById(R.id.option1_button);
        option2Button = (Button) findViewById(R.id.option2_button);
        option3Button = (Button) findViewById(R.id.option3_button);
        option4Button = (Button) findViewById(R.id.option4_button);

        //set font type face
        option1Button.setTypeface(typeface);
        option2Button.setTypeface(typeface);
        option3Button.setTypeface(typeface);
        option4Button.setTypeface(typeface);
        questionTextview.setTypeface(typeface);


        option1Button.setOnClickListener(this);
        option2Button.setOnClickListener(this);
        option3Button.setOnClickListener(this);
        option4Button.setOnClickListener(this);

        skipButton = (Button) findViewById(R.id.skip_button);
        skipButton.setTypeface(typeface);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });


        Boolean loadQuestions = true;
        HAGamesManager gamesManager = HAGamesManager.getInstance();
        HAQuizDataManager quizDataManager = HAQuizDataManager.getInstance();

        if (HAQuizDataManager.getInstance().isMultiplayerGame)
        {
            if (HAGamesManager.getInstance().turnData.turnCount == 1)
            {
                loadQuestions = true;
            }
            else
            {
                loadQuestions = false;
                quizDataManager.selectedCategory = quizDataManager.getCategoryForCategoryID(gamesManager.turnData.categoryID);
                this.currentCategory = quizDataManager.selectedCategory;
                this.questionsList = gamesManager.turnData.questions;
            }
        }

        if (loadQuestions)
        {
            this.currentCategory = HAQuizDataManager.getInstance().selectedCategory;
            questionsNumberTextview.setText("0/0");
            this.questionsList = HAQuizDataManager.getInstance().getQuestionsForCategory(this.currentCategory);
        }

        titleTextview.setText(currentCategory.getCategoryName());

        this.startQuiz();

        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
        mainLayout.setBackgroundColor(this.currentCategory.getCategoryThemeColor());
        //explanationLayout.setBackgroundColor(this.currentCategory.getCategoryThemeColor());


        questionImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // TODO Auto-generated method stub

                HAQuestion question = questionsList.get(questionIndex);
                File file = new File(question.getQuestionMediaFileName());
                String name = file.getName();
                final int lastPeriodPos = name.lastIndexOf('.');
                File renamed = new File(file.getParent(), name.substring(0, lastPeriodPos));
                Context contextImage = questionImageview.getContext();
                String renamedImg = String.valueOf(renamed);
                int id = getApplicationContext().getResources().getIdentifier(renamedImg, "raw", contextImage.getPackageName());

                if (question.getQuestionType() == HAQuestion.HAQuestionType.eHAVideoQuestionType)
                {
                    mediaController = new MediaController(HAGameActivity_new.this);
                    fullMediaScreenLayout.setVisibility(View.VISIBLE);
                    fullScreenImageView.setVisibility(View.GONE);
                    videoView.setVisibility(View.VISIBLE);
                    String uri = "android.resource://"+ getPackageName() +"/" + id;
                    mediaController.setAnchorView(videoView);
                    videoView.setVideoURI(Uri.parse(uri));
                    videoView.setMediaController(mediaController);
                    videoView.requestFocus();
                    videoView.start();
                } else if (question.getQuestionType() == HAQuestion.HAQuestionType.eHAPictureQuestionType){

                    videoView.setVisibility(View.GONE);
                    fullMediaScreenLayout.setVisibility(View.VISIBLE);
                    fullScreenImageView.setVisibility(View.VISIBLE);
                    fullScreenImageView.setImageResource(id);
                }
            }
        });

        fullMediaScreenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fullMediaScreenLayout.setVisibility(View.GONE);
                if (videoView.isPlaying())
                    videoView.stopPlayback();
            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (HASettings.getInstance().getSoundEnabled())
        {
            Intent svc = new Intent(this, BackGroundMusicService.class);
            startService(svc);
        }
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        if (HASettings.getInstance().getSoundEnabled())
        {
            Intent svc = new Intent(this, BackGroundMusicService.class);
            stopService(svc);
        }
    }

    //Quiz Methods
    public void startQuiz()
    {
      /*  Bundle bundle = new Bundle();
        String id = "Game";
        String name = "Started Game";
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, id);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, name);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle); */
        Answers.getInstance().logLevelStart(new LevelStartEvent()
                .putLevelName("Level Started"));
        this.showQuestionAtIndex(questionIndex);
    }

    public void showCountDownAnimation()
    {
        final int ONE_SECOND_IN_MS = 1000;
        countDownProgress.setVisibility(View.VISIBLE);
        HAQuestion question = questionsList.get(questionIndex);
        int duration = question.getQuestionDuration();
        HAGameActivity_new.this.countDownProgress.setMax(duration);
        HAGameActivity_new.this.countDownProgress.setProgress(duration);
        duration = duration + 1;
        countDownTimer = new CountDownTimer(duration * ONE_SECOND_IN_MS, ONE_SECOND_IN_MS)
        {
            @Override
            public void onTick(long millisUntilFinished) {
                int secondsRemaining = (int) (millisUntilFinished / ONE_SECOND_IN_MS);
                HAGameActivity_new.this.countDownProgress.setProgress(secondsRemaining);
                if (secondsRemaining == 1) {
                    int interval = 1000; // 300 milliseconds
                    Handler handler4 = new Handler();
                    Runnable runnable4 = new Runnable() {
                        public void run() {
                            HAGameActivity_new.this.countDownProgress.setProgress(0);
                        }
                    };
                    handler4.postAtTime(runnable4, System.currentTimeMillis() + interval);
                    handler4.postDelayed(runnable4, interval);
                }
            }
            @Override
            public void onFinish() {
                HAGameActivity_new.this.countDownProgress.setProgress(0);
                countDownProgress.setVisibility(View.INVISIBLE);
                HAGameActivity_new.this.nextQuestion();
            }
        }.start();
    }

    public  void showQuestionAtIndex(int index)
    {

        //remove all animations
        option1Button.clearAnimation();
        option2Button.clearAnimation();
        option3Button.clearAnimation();
        option4Button.clearAnimation();
        optionsLayout.setClickable(true);

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        if (questionIndex >= questionsList.size())
        {
            if (HAQuizDataManager.getInstance().isMultiplayerGame)
            {

                //disable UI while animating options
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                HAGamesManager gamesManager = HAGamesManager.getInstance();
                String playerId = Games.Players.getCurrentPlayerId(gamesManager.mGoogleApiClient);

                if (gamesManager.turnData.turnCount == 1)
                {
                    gamesManager.turnData.turnCount++;
                    gamesManager.turnData.firstUserScore = playerId + "," + Long.toString(score);

                    gamesManager.turnData.categoryID = HAQuizDataManager.getInstance().selectedCategory.getCategoryID();
                    gamesManager.turnData.questions = this.questionsList;

                    String nextParticipantId = gamesManager.getNextParticipantId();
                    Games.TurnBasedMultiplayer.takeTurn(gamesManager.mGoogleApiClient, gamesManager.turnBasedMatch.getMatchId(),
                            gamesManager.turnData.persist(), nextParticipantId).setResultCallback(
                            new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() //called after match is played
                            {
                                @Override
                                public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    if (result.getStatus().isSuccess())
                                    {
                                        HAGamesManager gamesManager = HAGamesManager.getInstance();
                                        //update match and its data in games manager
                                        TurnBasedMatch match = result.getMatch();
                                        gamesManager.turnBasedMatch = match;
                                        gamesManager.turnData = HAGameTurnData.unpersist(match.getData());
                                        //Games.TurnBasedMultiplayer.finishMatch(gamesManager.mGoogleApiClient, gamesManager.turnBasedMatch.getMatchId(), gamesManager.turnData.persist());

                                        Intent multiplayerResultActivity = new Intent(getApplicationContext(), HAMultiplayerResultActivity.class);
                                        multiplayerResultActivity.putExtra("score",score);
                                        startActivity(multiplayerResultActivity);
                                        finish();
                                    }
                                    else{
                                        finish();
                                    }

                                }
                            });
                }
                else if (gamesManager.turnData.turnCount == 2)
                {
                    gamesManager.turnData.secondUserScore = playerId + "," + Long.toString(score);
                    Games.TurnBasedMultiplayer.finishMatch(gamesManager.mGoogleApiClient, gamesManager.turnBasedMatch.getMatchId(), gamesManager.turnData.persist())
                            .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                                @Override
                                public void onResult(@NonNull TurnBasedMultiplayer.UpdateMatchResult result) {

                                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    if (result.getStatus().isSuccess())
                                    {

                                        HAGamesManager gamesManager = HAGamesManager.getInstance();
                                        gamesManager.turnBasedMatch = result.getMatch();
                                        gamesManager.turnData = HAGameTurnData.unpersist(gamesManager.turnBasedMatch.getData());

                                        Intent multiplayerResultActivity = new Intent(getApplicationContext(), HAMultiplayerResultActivity.class);
                                        multiplayerResultActivity.putExtra("update_win_count",true);
                                        startActivity(multiplayerResultActivity);
                                        finish();
                                    }
                                    else {
                                        Toast.makeText(HAGameActivity_new.this, "Oops look like invalid match. Dismiss it from matches list!", Toast.LENGTH_LONG).show();
                                        finish();
                                    }
                                }
                            });
                }
            }
            else {
                Answers.getInstance().logLevelEnd(new LevelEndEvent()
                        .putLevelName("Level Completed"));
                HAQuizDataManager.getInstance().setHighScoreForCategory(score, currentCategory);
                Intent highScoreIntent = new Intent(getApplicationContext(), HAHighScoresActivity_new.class);
                highScoreIntent.putExtra("score",score);
                startActivity(highScoreIntent);
                finish();
            }
        }
        else {
            option1Button.setBackgroundResource(R.drawable.optionbg_white);
            option2Button.setBackgroundResource(R.drawable.optionbg_white);
            option3Button.setBackgroundResource(R.drawable.optionbg_white);
            option4Button.setBackgroundResource(R.drawable.optionbg_white);

            option1Button.setVisibility(View.INVISIBLE);
            option2Button.setVisibility(View.INVISIBLE);
            option3Button.setVisibility(View.INVISIBLE);
            option4Button.setVisibility(View.INVISIBLE);


            fullMediaScreenLayout.setVisibility(View.GONE);
            questionImageview.setVisibility(View.INVISIBLE);

            HAQuestion question = questionsList.get(index);
            currentQuestion = question;
            HAQuizDataManager.getInstance().markQuestionAsRead(currentQuestion, currentCategory);

            if (currentCategory.getTimerRequired())
            {
                this.showCountDownAnimation();
            }
            else
            {
                countDownProgress.setVisibility(View.INVISIBLE);
            }

            if (explanationLayout.getVisibility() == View.VISIBLE)
                explanationLayout.setVisibility(View.GONE);

            questionTextview.setText(question.getQuestionText());
            questionTextview.scrollTo(0,0);
            String[] options = question.getShuffledOptions();

            questionsNumberTextview.setText((index+1) + "/" + questionsList.size());

            if (question.getQuestionType() != HAQuestion.HAQuestionType.eHATrueFalseQuestionType)
            {
                option1Button.setText(options[0]);
                option2Button.setText(options[1]);
                option3Button.setText(options[2]);
                option4Button.setText(options[3]);
            }

            if (question.getQuestionType() == HAQuestion.HAQuestionType.eHATextQuestionType)
            {

            }
            else if (question.getQuestionType() == HAQuestion.HAQuestionType.eHAPictureQuestionType)
            {
                String picFileName = question.getQuestionMediaFileName();
                File file = new File(picFileName);
                String name = file.getName();
                final int lastPeriodPos = name.lastIndexOf('.');
                File renamed = new File(file.getParent(), name.substring(0, lastPeriodPos));

                Context contextImage = questionImageview.getContext();
                String renamedImg = String.valueOf(renamed);
                int id = getApplicationContext().getResources().getIdentifier(renamedImg, "raw", contextImage.getPackageName());
                questionImageview.setImageResource(id);
                questionImageview.setVisibility(View.VISIBLE);
            }
            else if (question.getQuestionType() == HAQuestion.HAQuestionType.eHAVideoQuestionType)
            {

                questionImageview.setVisibility(View.VISIBLE);
                File file = new File(question.getQuestionMediaFileName());
                String name = file.getName();
                final int lastPeriodPos = name.lastIndexOf('.');
                File renamed = new File(file.getParent(), name.substring(0, lastPeriodPos));

                Context contextImage = questionImageview.getContext();
                String renamedImg = String.valueOf(renamed);
                int id = getApplicationContext().getResources().getIdentifier(renamedImg, "raw", contextImage.getPackageName());
                Uri videoURI = Uri.parse("android.resource://"+getPackageName()+"/raw/"
                        + id);
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(this, videoURI);
                Bitmap bitmap = retriever
                        .getFrameAtTime(100000, MediaMetadataRetriever.OPTION_PREVIOUS_SYNC);
                Drawable drawable = new BitmapDrawable(getResources(), bitmap);
                questionImageview.setImageDrawable(drawable);
            }
            else if (question.getQuestionType() == HAQuestion.HAQuestionType.eHATrueFalseQuestionType)
            {
                option1Button.setText(options[0]);
                option2Button.setText(options[1]);
            }

            if (questionIndex == 0)
                animateOptionsWithDelay(100);
            else
                animateOptionsWithDelay(0);
        }

    }


    public void nextQuestion()
    {
        questionIndex++;
        showQuestionAtIndex(questionIndex);
    }

    public void previousQuestion()
    {
        questionIndex--;
        showQuestionAtIndex(questionIndex);
    }

    //Default methods

    @Override
    public void onClick(View v) {

        int optionIndex = 0;
        Boolean optionTapped = false;
        Button optionButton = null;
        switch (v.getId()) {
            case R.id.option1_button:
                optionButton = option1Button;
                optionIndex = 0;
                optionTapped = true;
                break;
            case R.id.option2_button:
                optionButton = option2Button;
                optionIndex = 1;
                optionTapped = true;
                break;
            case R.id.option3_button:
                optionButton = option3Button;
                optionIndex = 2;
                optionTapped = true;
                break;
            case R.id.option4_button:
                optionButton = option4Button;
                optionIndex = 3;
                optionTapped = true;
                break;
            default:
                break;
        }
        if (optionTapped)
        {
            //disable UI while animating options
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

            //cancel timer afetr user tapped answer
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }


            HAQuestion question = questionsList.get(questionIndex);
            String selectedOption = (String) question.getShuffledOptions()[optionIndex];
            Boolean isCorrect = question.isCorrectAnswer(selectedOption);

            if (question.getQuestionType() == HAQuestion.HAQuestionType.eHATrueFalseQuestionType)
            {
                int answerIndex = question.getAnswerIndex();
                if (answerIndex == 1 && optionIndex == 0)
                    isCorrect = true;
                else if (answerIndex == 0 && optionIndex == 1)
                    isCorrect = true;
                else
                    isCorrect = false;
            }

                if (isCorrect)
                {
                    HAUtilities.getInstance().playCorrectAnswerSound();

                    int points = question.getPoints();

                    if (currentCategory.getTimerRequired() && HAConfiguration.getInstance().getEnableTimeBasedScoring())
                    {
                        int duration = question.getQuestionDuration();
                        float lapsedTime = duration - countDownProgress.getProgress();
                        score = score +  (long) ((float)points * (1.0 - (lapsedTime/(float)duration)));
                    }
                    else
                    {
                        score += points;
                    }
                    optionButton.setBackgroundResource(R.drawable.optionbg_green);

                    String correctExplanation = question.getCorrectAnswerExplanation();
                    if (HASettings.getInstance().getExplantionEnabled() && correctExplanation != null)
                    {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        explanationLayout.setVisibility(View.VISIBLE);
                        questionAnsweredStatusTextview.setText(R.string.Correct);
                        questionAnsweredStatusTextview.setTextColor(Color.GREEN);
                        explanationTextview.setText(correctExplanation);
                        optionsLayout.setClickable(false);
                    }
                    else
                    {
                        new java.util.Timer().schedule(

                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        // your code here, and if you have to refresh UI put this code:
                                        runOnUiThread(new   Runnable() {
                                            public void run() {
                                                HAGameActivity_new.this.nextQuestion();

                                            }
                                        });
                                    }
                                },
                                2000
                        );
                    }
                }
                else
                {
                    HAUtilities.getInstance().playWrongAnswerSound();

                    optionButton.setBackgroundResource(R.drawable.optionbg_red);
                    int negativePoints = question.getNegativePoints();
                    score -= negativePoints;

                    if (question.getQuestionType() == HAQuestion.HAQuestionType.eHATrueFalseQuestionType)
                    {

                    }
                    else
                    {
                        if (HAConfiguration.getInstance().getHighlightCorrectAnswerOnAnsweringWrong())
                        {
                            for (int i=0; i<question.getShuffledOptions().length; i++)
                            {
                                String answerText = question.getShuffledOptions()[i];
                                if (question.isCorrectAnswer(answerText))
                                {
                                    Button correctOptionButton = null;
                                    if (i==0)
                                        correctOptionButton = option1Button;
                                    else if (i==1)
                                        correctOptionButton = option2Button;
                                    else if (i==2)
                                        correctOptionButton = option3Button;
                                    else if (i==3)
                                        correctOptionButton = option4Button;
                                    correctOptionButton.setBackgroundResource(R.drawable.optionbg_green);
                                    break;
                                }
                            }
                        }
                    }

                    String wrongExplanation = question.getWrongAnswerExplanation();
                    if (HASettings.getInstance().getExplantionEnabled() && wrongExplanation != null)
                    {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        explanationLayout.setVisibility(View.VISIBLE);
                        questionAnsweredStatusTextview.setText(R.string.Wrong);
                        questionAnsweredStatusTextview.setTextColor(Color.RED);
                        explanationTextview.setText(wrongExplanation);
                    }
                    else
                    {
                        new java.util.Timer().schedule(

                                new java.util.TimerTask() {
                                    @Override
                                    public void run() {
                                        // your code here, and if you have to refresh UI put this code:
                                        runOnUiThread(new   Runnable() {
                                            public void run() {
                                                HAGameActivity_new.this.nextQuestion();
                                            }
                                        });
                                    }
                                },
                                2000
                        );
                    }
            }
            pointsTextview.setText(Long.toString(score));
        }
        else //some other view tapped
        {

        }




    }


    private void animateOptionsWithDelay(int delay) {
        int interval = 100; // 100 milliseconds
        Handler handler = new Handler();
        Runnable runnable1 = new Runnable() {
            public void run() {

                //disable UI while animating options
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                final Animation  animMove1 = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.movelefttoright);
                animMove1.setDuration(400);
                option1Button.startAnimation(animMove1);
                option1Button.setVisibility(View.VISIBLE);
                final Animation  animMove2 = AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.movelefttoright);
                animMove2.setAnimationListener(new Animation.AnimationListener()
                {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (currentQuestion.getQuestionType() == HAQuestion.HAQuestionType.eHATrueFalseQuestionType)
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                animMove2.setDuration(600);
                option2Button.startAnimation(animMove2);
                option2Button.setVisibility(View.VISIBLE);


                if (currentQuestion.getQuestionType() != HAQuestion.HAQuestionType.eHATrueFalseQuestionType)
                {
                    final Animation  animMove3 = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.movelefttoright);
                    animMove3.setDuration(750);
                    option3Button.startAnimation(animMove3);
                    option3Button.setVisibility(View.VISIBLE);


                    final Animation  animMove4 = AnimationUtils.loadAnimation(getApplicationContext(),
                            R.anim.movelefttoright);
                    animMove4.setAnimationListener(new Animation.AnimationListener()
                    {
                        @Override
                        public void onAnimationStart(Animation animation) {

                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {

                        }
                    });

                    animMove4.setDuration(900);
                    option4Button.startAnimation(animMove4);
                    option4Button.setVisibility(View.VISIBLE);

                }
            }
        };

        handler.postAtTime(runnable1, System.currentTimeMillis() + delay);
        handler.postDelayed(runnable1, delay);
    }

    @Override
    protected void onStart() {
        super.onStart();
        HAQuizDataManager.getInstance().isCategorySelectedForMultiplayerGame = false;
    }

    public void onBackPressed() {

        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }

        HAUtilities.getInstance().playTapSound();

        if (HAQuizDataManager.getInstance().isMultiplayerGame)
        {
            //cancel match here
            HAGamesManager.getInstance().endMatch();
            this.finish();
            return;
        }

        if (HAQuizDataManager.getInstance().requireAdsDisplay())
        {
            Intent addIntent = new Intent(this, HAInterstitialAdActivity.class);
            startActivity(addIntent);
            this.overridePendingTransition(0, 0);
            this.finish();
        }
        else
        {
            this.finish();
        }
    }
}
