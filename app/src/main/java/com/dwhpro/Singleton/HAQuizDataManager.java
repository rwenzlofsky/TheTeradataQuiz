package com.dwhpro.Singleton;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.Toast;

import com.dwhpro.AppSettings.HAConfiguration;
import com.dwhpro.AppSettings.HASettings;
import com.dwhpro.AppSettings.HAUtilities;
import com.dwhpro.model.HACategory_new;
import com.dwhpro.model.HAQuestion;
import com.dwhpro.utils.IabHelper;
import com.dwhpro.utils.IabResult;
import com.dwhpro.utils.Inventory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by satie on 29/06/16.
 */

interface HAQuizDataManagerConstants {
    final String kHighScoresPreference = "high_scores";
    final String kHighScoreForCategory = "high_score_for_category_";
    final String kPurchasesPreference = "purchases";
}

public class HAQuizDataManager implements HAQuizDataManagerConstants {

    public Context context;
    public HACategory_new selectedCategory;
    IabHelper mHelper;
    public Boolean isMultiplayerGame = false;
    public Boolean isCategorySelectedForMultiplayerGame;


    @SuppressLint("StaticFieldLeak")
    private static HAQuizDataManager singleton = new HAQuizDataManager();

    private HAQuizDataManager() {
    }

    /* Static 'instance' method */
    public static HAQuizDataManager getInstance() {
        return singleton;
    }


    public HACategory_new getCategoryForCategoryID(String categoryID) {
        List<HACategory_new> categories = getAllCategories();
        for (int i = 0; i < categories.size(); i++) {
            HACategory_new category = categories.get(i);
            if (category.getCategoryID().equals(categoryID)) {
                return category;
            }
        }
        return null;
    }

    public List<HACategory_new> getAllCategories() {
        String json = null;
        List<HACategory_new> categoriesList = new ArrayList<HACategory_new>();

        try {
            InputStream stream = context.getAssets().open("Quiz_Categories.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            json = new String(buffer, "UTF-8");
            System.out.println("categories json :" + json);
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.getJSONArray("Categories");

            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    HACategory_new category = new HACategory_new();
                    category.setCategoryName(jsonObj.getString(HACategory_new.kCategoryName));
                    category.setCategoryDescription(jsonObj.optString(HACategory_new.kCategoryDescription, null));
                    category.setCategoryIconFileName(jsonObj.optString(HACategory_new.kCategoryIconFileName, null));
                    category.setCategoryID(jsonObj.getString(HACategory_new.kCategoryID));
                    category.setCategoryLeaderboardID(jsonObj.optString(HACategory_new.kCategoryLeaderboardID, null));
                    category.setCategoryProductIdetifier(jsonObj.optString(HACategory_new.kCategoryProductIdentifier, null));
                    category.setCategoryQuestionsLimit(Integer.parseInt(jsonObj.getString(HACategory_new.kCategoryQuestionsLimit)));
                    category.setCategoryThemeColor(Color.parseColor(jsonObj.getString(HACategory_new.kCategoryColor)));
                    category.setTimerRequired(jsonObj.optBoolean(HACategory_new.kCategoryTimerRequired, false));
                    categoriesList.add(category);
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }

        return categoriesList;
    }


    public List<HACategory_new> getCategoriesForPlay() {
        String json = null;
        List<HACategory_new> categoriesList = new ArrayList<HACategory_new>();

        try {
            InputStream stream = context.getAssets().open("Quiz_Categories.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            json = new String(buffer, "UTF-8");
            System.out.println("categories json :" + json);
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.getJSONArray("Categories");

            Boolean purchaseEnabled = HAConfiguration.getInstance().getEnableInAppPurchasesForCategories();

            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    String productIdetifier = jsonObj.optString(HACategory_new.kCategoryProductIdentifier, null);

                    if (purchaseEnabled && productIdetifier != null) {
                        if (HASettings.getInstance().isProductPurchased(productIdetifier)) {
                            HACategory_new category = new HACategory_new();
                            category.setCategoryName(jsonObj.getString(HACategory_new.kCategoryName));
                            category.setCategoryDescription(jsonObj.optString(HACategory_new.kCategoryDescription, null));
                            category.setCategoryIconFileName(jsonObj.optString(HACategory_new.kCategoryIconFileName, null));
                            category.setCategoryID(jsonObj.getString(HACategory_new.kCategoryID));
                            category.setCategoryLeaderboardID(jsonObj.optString(HACategory_new.kCategoryLeaderboardID, null));
                            category.setCategoryProductIdetifier(jsonObj.optString(HACategory_new.kCategoryProductIdentifier, null));
                            category.setCategoryQuestionsLimit(Integer.parseInt(jsonObj.getString(HACategory_new.kCategoryQuestionsLimit)));
                            category.setCategoryThemeColor(Color.parseColor(jsonObj.getString(HACategory_new.kCategoryColor)));
                            category.setTimerRequired(jsonObj.optBoolean(HACategory_new.kCategoryTimerRequired, false));
                            categoriesList.add(category);

                        } else {
                            //do not add to list if not purchased
                        }
                    } else {
                        //directly to the list if in app purchase is enabled
                        HACategory_new category = new HACategory_new();
                        category.setCategoryName(jsonObj.getString(HACategory_new.kCategoryName));
                        category.setCategoryDescription(jsonObj.optString(HACategory_new.kCategoryDescription, null));
                        category.setCategoryIconFileName(jsonObj.optString(HACategory_new.kCategoryIconFileName, null));
                        category.setCategoryID(jsonObj.getString(HACategory_new.kCategoryID));
                        category.setCategoryLeaderboardID(jsonObj.optString(HACategory_new.kCategoryLeaderboardID, null));
                        category.setCategoryProductIdetifier(jsonObj.optString(HACategory_new.kCategoryProductIdentifier, null));
                        category.setCategoryQuestionsLimit(Integer.parseInt(jsonObj.getString(HACategory_new.kCategoryQuestionsLimit)));
                        category.setCategoryThemeColor(Color.parseColor(jsonObj.getString(HACategory_new.kCategoryColor)));
                        category.setTimerRequired(jsonObj.optBoolean(HACategory_new.kCategoryTimerRequired, false));
                        categoriesList.add(category);
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }

        return categoriesList;
    }


    public List<HACategory_new> getCategoriesRequirePurchase() {
        SharedPreferences purchasePreferences = context.getSharedPreferences(kPurchasesPreference, 0);
        String json = null;
        List<HACategory_new> categoriesList = new ArrayList<HACategory_new>();

        try {
            InputStream stream = context.getAssets().open("Quiz_Categories.json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            json = new String(buffer, "UTF-8");
            System.out.println("categories json :" + json);
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.getJSONArray("Categories");
            Boolean purchaseEnabled = HAConfiguration.getInstance().getEnableInAppPurchasesForCategories();

            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    HACategory_new category = new HACategory_new();
                    String productIdetifier = jsonObj.optString(HACategory_new.kCategoryProductIdentifier, null);

                    if (purchaseEnabled) {
                        if (productIdetifier != null) {
                            Boolean purchased = HASettings.getInstance().isProductPurchased(productIdetifier);
                            if (purchased) {

                            } else {
                                category.setCategoryName(jsonObj.getString(HACategory_new.kCategoryName));
                                category.setCategoryDescription(jsonObj.optString(HACategory_new.kCategoryDescription, null));
                                category.setCategoryIconFileName(jsonObj.optString(HACategory_new.kCategoryIconFileName, null));
                                category.setCategoryID(jsonObj.getString(HACategory_new.kCategoryID));
                                category.setCategoryLeaderboardID(jsonObj.optString(HACategory_new.kCategoryLeaderboardID, null));
                                category.setCategoryProductIdetifier(jsonObj.optString(HACategory_new.kCategoryProductIdentifier, null));
                                category.setCategoryQuestionsLimit(Integer.parseInt(jsonObj.getString(HACategory_new.kCategoryQuestionsLimit)));
                                category.setCategoryThemeColor(Color.parseColor(jsonObj.getString(HACategory_new.kCategoryColor)));
                                category.setTimerRequired(jsonObj.optBoolean(HACategory_new.kCategoryTimerRequired, false));
                                categoriesList.add(category);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            return null;
        }

        return categoriesList.size() == 0 ? null : categoriesList;
    }


    public List<HAQuestion> getAllQuestionsForCategory(HACategory_new category) {

        String json = null;
        List<HAQuestion> questionsList = new ArrayList<HAQuestion>();

        try {

            String categoryID = category.getCategoryID();
            InputStream stream = context.getAssets().open("Quiz_Category_" + categoryID + ".json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.reset();
            stream.reset();
            stream.close();
            json = new String(buffer, "UTF-8");
            System.out.println("questionsList json :" + json);
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.getJSONArray("Questions");

            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    HAQuestion question = new HAQuestion();
                    question.setQuestionText(jsonObj.getString(HAQuestion.kQuestionText));

                    question.setPoints(Integer.parseInt(jsonObj.getString(HAQuestion.kQuestionPoints)));
                    question.setNegativePoints(Integer.parseInt(jsonObj.getString(HAQuestion.kQuestionNegativePoints)));
                    question.setQuestionDuration(jsonObj.optInt(HAQuestion.kQuestionDuration, HAConfiguration.getInstance().defaultAnsweringQuestionDuration));
                    question.setCorrectAnswerExplanation(jsonObj.optString(HAQuestion.kQuestionCorrectAnswerExplanation, null));
                    question.setWrongAnswerExplanation(jsonObj.optString(HAQuestion.kQuestionWrongAnswerExplanation, null));
                    question.setQuestionMediaFileName(jsonObj.optString(HAQuestion.kQuestionMediaFileName, null));
                    HAQuestion.HAQuestionType questionType = question.getQuestionTypeForIntValue(jsonObj.getInt(HAQuestion.kQuestionType));
                    question.setQuestionType(questionType);

                    question.setAnswerIndex(jsonObj.getInt(HAQuestion.kQuestionAnswer));

                    JSONArray options = jsonObj.optJSONArray(HAQuestion.kQuestionOptions);
                    if (options != null) {
                        String[] optionsList = new String[options.length()];
                        String[] shuffleOptions = new String[options.length()];

                        for (int j = 0; j < options.length(); j++) {
                            String option = options.getString(j);
                            optionsList[j] = option;
                            shuffleOptions[j] = option;
                        }
                        question.setOptions(optionsList);

                        if (HAConfiguration.getInstance().getShuffelOptions())
                            Collections.shuffle(Arrays.asList(shuffleOptions));

                        question.setShuffledOptions(shuffleOptions);
                    }

                    questionsList.add(question);
                }
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return questionsList;
//        System.out.println("Questions xxx :" + peer1.questions);
        //return peer1.questions;
    }

    public List<HAQuestion> getQuestionsForCategory(HACategory_new category) {

        String json = null;
        List<HAQuestion> questionsList = new ArrayList<HAQuestion>();

        try {

            String categoryID = category.getCategoryID();
            InputStream stream = context.getAssets().open("Quiz_Category_" + categoryID + ".json");
            int size = stream.available();
            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();
            json = new String(buffer, "UTF-8");
            System.out.println("questionsList json :" + json);
            JSONObject object = new JSONObject(json);
            JSONArray jsonArray = object.getJSONArray("Questions");

            if (jsonArray != null) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObj = jsonArray.getJSONObject(i);
                    HAQuestion question = new HAQuestion();
                    question.setQuestionText(jsonObj.getString(HAQuestion.kQuestionText));
                    question.setAnswerIndex(Integer.parseInt(jsonObj.getString(HAQuestion.kQuestionAnswer)));
                    question.setPoints(Integer.parseInt(jsonObj.getString(HAQuestion.kQuestionPoints)));
                    question.setNegativePoints(Integer.parseInt(jsonObj.getString(HAQuestion.kQuestionNegativePoints)));
                    question.setQuestionDuration(jsonObj.optInt(HAQuestion.kQuestionDuration, HAConfiguration.getInstance().defaultAnsweringQuestionDuration));
                    question.setCorrectAnswerExplanation(jsonObj.optString(HAQuestion.kQuestionCorrectAnswerExplanation, null));
                    question.setWrongAnswerExplanation(jsonObj.optString(HAQuestion.kQuestionWrongAnswerExplanation, null));
                    question.setQuestionMediaFileName(jsonObj.optString(HAQuestion.kQuestionMediaFileName, null));
                    HAQuestion.HAQuestionType questionType = question.getQuestionTypeForIntValue(jsonObj.getInt(HAQuestion.kQuestionType));
                    question.setQuestionType(questionType);
                    JSONArray options = jsonObj.optJSONArray(HAQuestion.kQuestionOptions);


                    String[] optionsList = new String[4];
                    String[] shuffleOptions = new String[4];

                    if (options != null) {
                        for (int j = 0; j < options.length(); j++) {
                            String option = options.getString(j);
                            optionsList[j] = option;
                            shuffleOptions[j] = option;
                        }
                        question.setOptions(optionsList);

                        if (HAConfiguration.getInstance().getShuffelOptions())
                            Collections.shuffle(Arrays.asList(shuffleOptions));

                        question.setShuffledOptions(shuffleOptions);
                    } else {
                        optionsList[0] = "Yes";
                        optionsList[1] = "No";
                        question.setOptions(optionsList);
                        shuffleOptions[0] = "Yes";
                        shuffleOptions[1] = "No";
                        question.setShuffledOptions(shuffleOptions);
                    }

                    questionsList.add(question);
                }
            }

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        Collections.shuffle(questionsList);

//        HAGameTurnData peer = new HAGameTurnData();
//        peer.questions = questionsList;
//        peer.firstUserScore = "11111,1000";
//        peer.secondUserScore = "2222,2000";
//        byte[] bytes = peer.persist();
//
//        HAGameTurnData peer1 = HAGameTurnData.unpersist(bytes);
//
//        System.out.println("Questions xxx original:" + json);
        return questionsList.subList(0, category.getCategoryQuestionsLimit());
//        System.out.println("Questions xxx :" + peer1.questions);
        //return peer1.questions;
    }

    //Saving attempted question
    public void markQuestionAsRead(HAQuestion question, HACategory_new category) {
        String key = "mark_read_" + category.getCategoryID();
        SharedPreferences readQuestionsPreferences = context.getSharedPreferences(key, 0);
        SharedPreferences.Editor editor = readQuestionsPreferences.edit();
        StringBuilder optionsString = new StringBuilder();
        for (String option : question.getOptions()
                ) {
            optionsString.append(option);
        }
        String questionString = question.getQuestionText() + optionsString;
        String md5String = HAUtilities.MD5ForString(questionString);
        editor.putString(md5String, "");
        editor.apply();
    }

    public double progrePercentageForCategory(HACategory_new category) {
        String key = "mark_read_" + category.getCategoryID();
        SharedPreferences readQuestionsPreferences = context.getSharedPreferences(key, 0);
        int allValuesCount = readQuestionsPreferences.getAll().size();

        if (allValuesCount == 0)
            return 0.0;

        int questionsCount = getAllQuestionsForCategory(category).size();

        double percentage = ((double) allValuesCount / (double) questionsCount * 100.0) > 100.0 ? 100.0 : ((double) allValuesCount / (double) questionsCount * 100.0);
        return Math.ceil(percentage);
    }


    public void setHighScoreForCategory(long highScore, HACategory_new category) {
        SharedPreferences highscorePreferences = context.getSharedPreferences(kHighScoresPreference, 0);
        final SharedPreferences.Editor highScoreEditor = highscorePreferences.edit();
        long savedHighscore = highscorePreferences.getLong(kHighScoreForCategory + category.getCategoryID(), 0);
        if (savedHighscore < highScore) {
            String key = kHighScoreForCategory + category.getCategoryID();
            highScoreEditor.putLong(key, highScore);
            highScoreEditor.apply();
        }
    }

    public long getHighScoreForCategory(HACategory_new category) {
        SharedPreferences highscorePreferences = context.getSharedPreferences(kHighScoresPreference, 0);
        String key = kHighScoreForCategory + category.getCategoryID();
        return highscorePreferences.getLong(key, 0);
    }


    //Ads related methods
    public Boolean requireAdsDisplay() {
        if (HAConfiguration.getInstance().getShowAds()) {
            if (HAConfiguration.getInstance().isInAppPurchaseEnabledForRemovingAds()) {
                String removeAdsProductIdentifier = HAConfiguration.getInstance().getRemoveAdsProductID();
                SharedPreferences purchasePreferences = context.getSharedPreferences(kPurchasesPreference, 0);
                String presentIdentifier = purchasePreferences.getString(removeAdsProductIdentifier, null);
                return presentIdentifier == null;
            }
            return true;
        }
        return false;
    }

    //Inapp purchase related methods
    public void restorePurchases() {

        if (HAUtilities.isEmulator()) {
            Toast.makeText(context, "Restore purchases : InApp Purchase does not work in emulator", Toast.LENGTH_LONG).show();
            return;
        }

        if (!HASettings.getInstance().isInAppurchaseEnabled())
            return;

        // compute your public key and store it in base64EncodedPublicKey
        mHelper = new IabHelper(context, HAConfiguration.Base64_encoded_License_Key);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result) {
                                           if (result.isFailure()) {
                                               Log.d("Purchase", "HAQuizDataManager : In-app Billing setup failed: " +
                                                       result);
                                           } else {
                                               Log.d("Purchase", "In-app Billing is set up OK");
                                           }
                                           mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {

                                               @Override
                                               public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                                                   if (result.isFailure()) {
                                                       Log.d("Purchase", "HAQuizDataManager : Unable to get products");
                                                   } else {
                                                       if (HAConfiguration.getInstance().getEnableInAppPurchasesForCategories()) {
                                                           List<HACategory_new> categories = getCategoriesRequirePurchase();
                                                           for (int i = 0; i < (categories != null ? categories.size() : 0); i++) {
                                                               HACategory_new category = categories.get(i);
                                                               String productID = category.getCategoryProductIdetifier();
                                                               Boolean isPurchased = inv.hasPurchase(productID);

                                                               if (isPurchased) {
                                                                   //mark product purchased
                                                                   HAQuizDataManager.this.markProductPurchased(productID);
                                                                   Log.d("Purchased", productID + result);
                                                               } else {
                                                                   Log.d("Not Purchased", productID + result);
                                                               }
                                                           }
                                                       }

                                                       if (HAConfiguration.getInstance().isInAppPurchaseEnabledForRemovingAds()) {
                                                           String productID = HAConfiguration.getInstance().getRemoveAdsProductID();
                                                           Boolean isPurchased = inv.hasPurchase(HAConfiguration.getInstance().getRemoveAdsProductID());
                                                           if (isPurchased) {
                                                               //mark product purchased
                                                               HAQuizDataManager.this.markProductPurchased(productID);
                                                               Log.d("Purchased", productID + result);
                                                           } else {
                                                               Log.d("Not Purchased", productID + result);
                                                           }
                                                       }
                                                       Toast.makeText(context, "Restore complete!", Toast.LENGTH_SHORT).show();
                                                   }
                                               }
                                           });

                                       }
                                   });
    }

    public void markProductPurchased(String productID) {
        SharedPreferences.Editor purchasePreferencesEditor = context.getSharedPreferences(kPurchasesPreference, 0).edit();
        purchasePreferencesEditor.putString(productID, productID);
        purchasePreferencesEditor.apply();
    }

    public void buyCategory(HACategory_new category) {

    }
}
