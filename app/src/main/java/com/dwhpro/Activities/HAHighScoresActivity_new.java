package com.dwhpro.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
import com.dwhpro.AppSettings.HAConfiguration;
import com.dwhpro.AppSettings.HASettings;
import com.dwhpro.AppSettings.HAUtilities;
import com.dwhpro.Singleton.HAGamesManager;
import com.dwhpro.Singleton.HAQuizDataManager;
import com.dwhpro.TwitterActivity;
import com.dwhpro.model.HACategory_new;
import com.dwhpro.multiplayer.basegameutil.GameHelper;
import com.dwhpro.quizappnew.R;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;

public class HAHighScoresActivity_new extends Activity implements GameHelper.GameHelperListener {

    TextView titleTextview;
    TextView scoreTextView;
    HighScoreListAdapter adapter;
    ListView scoreListView;
    RelativeLayout mainLayout;
    ImageView homeImageView;

    ImageView facebookImageView;
    ImageView twitterImageView;
    private CallbackManager callbackManager;
    TextView highScoresDsiplayTextView;
    ShareButton fbShareButton;

    GameHelper gameHelper;

    Long score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hahigh_scores_activity_new);
        getActionBar().hide();
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Highscore Activity")
                .putContentType("Highscore Activity Showing")
                .putContentId("highscore"));


        Typeface appFontFace = HASettings.getInstance().getAppFontFace();


        homeImageView = (ImageView) findViewById(R.id.home_imageview);
        homeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        titleTextview = (TextView) findViewById(R.id.title_textview);
        scoreTextView = (TextView) findViewById(R.id.score_textview);

        highScoresDsiplayTextView = (TextView) findViewById(R.id.high_scores_display_textview);

        scoreListView = (ListView) findViewById(R.id.highscore_list);
        List<HACategory_new> categories = HAQuizDataManager.getInstance().getAllCategories();
        adapter = new HighScoreListAdapter(this, categories);
        scoreListView.setAdapter(adapter);
        Typeface text_face_thin = HASettings.getInstance().getAppFontFace();
        HACategory_new caetgory = HAQuizDataManager.getInstance().selectedCategory;
        titleTextview.setText(caetgory.getCategoryName());
        titleTextview.setTypeface(text_face_thin);

        Intent scoreIntent = getIntent();
        score = scoreIntent.getLongExtra("score", 0);
        scoreTextView.setText("You scored " + Long.toString(score) + " points");

        mainLayout = (RelativeLayout) findViewById(R.id.highscore_background_layout);
        mainLayout.setBackgroundColor(caetgory.getCategoryThemeColor());

        callbackManager = CallbackManager.Factory.create();


        facebookImageView = (ImageView) findViewById(R.id.facebook_image_button);
        fbShareButton = (ShareButton) findViewById(R.id.share_btn_fb);
        twitterImageView = (ImageView) findViewById(R.id.twitter_image_button);


        if (HAConfiguration.getInstance().enableFacebookScoresSharing) {

            fbShareButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (HAUtilities.getInstance().isNetworkConnectionAvailable()) {

                        AppEventsLogger.activateApp(getApplication());

                        List<String> permissionNeeds = Collections.singletonList("publish_actions");

                        //this loginManager helps you eliminate adding a LoginButton to your UI
                        LoginManager manager = LoginManager.getInstance();

                        manager.logInWithPublishPermissions(HAHighScoresActivity_new.this, permissionNeeds);

                        manager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                            @Override
                            public void onSuccess(LoginResult loginResult) {
                                //sharePhotoToFacebook();
                                Bitmap screenShot = screenShot();
                                if (screenShot == null)
                                    Log.d("GameHelper","Screenshot is null");

                                SharePhoto photo = new SharePhoto.Builder()
                                        .setBitmap(screenShot)
                                        .build();
                                SharePhotoContent content = new SharePhotoContent.Builder()
                                        .addPhoto(photo)
                                        .build();
                                ShareDialog shareDialog = new ShareDialog(HAHighScoresActivity_new.this);
                                shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
                                    @Override
                                    public void onSuccess(Sharer.Result result) {
                                        Toast.makeText(HAHighScoresActivity_new.this, "Share success", Toast.LENGTH_SHORT).show();
                                        Log.d("GameHelper",result.toString());
                                    }

                                    @Override
                                    public void onCancel() {
                                        Log.d("GameHelper","Cancelled");
                                    }

                                    @Override
                                    public void onError(FacebookException error) {
                                        System.out.println("GameHelper" + error.toString() + " dialog error");

                                    }
                                });
                                shareDialog.show(content, ShareDialog.Mode.AUTOMATIC);
                                Log.d("GameHelper","facebook login success");
                            }
                            @Override
                            public void onCancel() {
                                Toast.makeText(HAHighScoresActivity_new.this, "Sharing cancelled!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onError(FacebookException exception) {
                                System.out.println("GameHelper" + exception.toString());
                            }
                        });
                    } else {
                        Toast.makeText(HAHighScoresActivity_new.this, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                    }
                }
            });

            facebookImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HAUtilities.getInstance().playTapSound();
                    fbShareButton.performClick();
                }
            });

        } else {
            facebookImageView.setVisibility(View.GONE);
        }


        if (HAConfiguration.getInstance().enableTwitterScoresSharing) {
            twitterImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (HAUtilities.getInstance().isNetworkConnectionAvailable()) {
                        Bitmap screenshotimage = screenShot();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        screenshotimage.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        Intent twitterIntent = new Intent(getApplicationContext(), TwitterActivity.class);
                        twitterIntent.setAction(Intent.ACTION_SEND);
                        twitterIntent.putExtra("score", "I scored " + score + " points in Quiz!");
                        twitterIntent.putExtra("image", byteArray);
                        twitterIntent.putExtra("appURL", HAConfiguration.getInstance().playStoreLinkOfThisApplication);
                        startActivity(twitterIntent);
                    } else {
                        Toast.makeText(HAHighScoresActivity_new.this, R.string.no_internet_message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            twitterImageView.setVisibility(View.GONE);
        }

        if (gameHelper == null)
        {
            getGameHelper();
        }
        gameHelper.setup(this);

        if (HAGamesManager.getInstance().isConnected())
        {
            submitScore();
        }
        else
        {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle("Google Play Games");
            alertDialogBuilder.setMessage("Sign in to submit your scores to Leaderboards!");
            alertDialogBuilder.setPositiveButton("Sign in",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
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

            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    public void submitScore() {
        String leaderboardID = HAQuizDataManager.getInstance().selectedCategory.getCategoryLeaderboardID();
        HAGamesManager.getInstance().submitScore(leaderboardID, score);
    }

    private void getGameHelper() {
        gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
        gameHelper.enableDebugLog(true);
    }



    @Override
    public void onSignInFailed() {
    }

    @Override
    public void onSignInSucceeded() {
        HASettings.getInstance().setAutoSignIn(true);
        HAGamesManager.getInstance().mGoogleApiClient = gameHelper.getApiClient();
        submitScore();
        Toast.makeText(this, "Score submitted", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        gameHelper.setConnectOnStart(HASettings.getInstance().isAutoSignIn());
        gameHelper.onStart(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
        gameHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (HAQuizDataManager.getInstance().requireAdsDisplay()) {
            Intent addIntent = new Intent(this, HAInterstitialAdActivity.class);
            startActivity(addIntent);
            this.overridePendingTransition(0, 0);
            finish();
        } else {
            finish();
        }
    }


    public class HighScoreListAdapter extends BaseAdapter {
        private LayoutInflater inflater;
        private List<HACategory_new> categoryItems;
        Activity activity;

        public HighScoreListAdapter(Activity activity, List<HACategory_new> categoryItems) {
            this.categoryItems = categoryItems;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return this.categoryItems.size();
        }

        @Override
        public Object getItem(int location) {
            // TODO Auto-generated method stub
            return location;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            Typeface text_face_thin = HASettings.getInstance().getAppFontFace();
            if (inflater == null)
                inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (convertView == null)
                convertView = inflater.inflate(R.layout.score_row, null);
            final HACategory_new category = categoryItems.get(position);

            LinearLayout linearLayout = (LinearLayout) convertView.findViewById(R.id.main_row_highscore);
            linearLayout.setBackgroundColor(category.getCategoryThemeColor());
            TextView categoryName = (TextView) convertView.findViewById(R.id.category_name);
            categoryName.setTypeface(text_face_thin);
            categoryName.setText(category.getCategoryName());
            long highScore = HAQuizDataManager.getInstance().getHighScoreForCategory(category);
            TextView categoryScore = (TextView) convertView.findViewById(R.id.category_score);
            int categoryNumber = position + 1;
            categoryScore.setText(Long.toString(highScore));
            categoryScore.setTypeface(text_face_thin);
            return convertView;
        }
    }
}
