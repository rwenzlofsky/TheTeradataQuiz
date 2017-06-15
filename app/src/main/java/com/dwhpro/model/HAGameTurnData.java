/*
 * Copyright (C) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dwhpro.model;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic turn data. It's just a blank data string and a turn number counter.
 *
 * @author wolff
 */
public class HAGameTurnData {

    public static final String TAG = "EBTurn";

    public String categoryID;
    public int turnCount;
    public String firstUserScore;
    public String secondUserScore;
    public List<HAQuestion> questions;


//    public String data = "";
//    public String id = "";
//    public int turnCounter;
//    public String playedQuestion = "";
//    public String secondUserScoreddata = "";
//    public String userName = "";
//    public String firstUserName = "";

    public HAGameTurnData() {
        categoryID = "";
        turnCount = 1;
        firstUserScore = "";
        secondUserScore = "";
        questions = new ArrayList<HAQuestion>();
    }


    // This is the byte array we will write out to the TBMP API.
    public byte[] persist() {
        JSONObject retVal = new JSONObject();

        try {
            retVal.put("questions", this.questionsStringForQuestions(questions));
            retVal.put("turnCount", turnCount);
            retVal.put("categoryID", categoryID);
            retVal.put("firstUserScore", firstUserScore);
            retVal.put("secondUserScore", secondUserScore);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        String st = retVal.toString();

        Log.d(TAG, "==== PERSISTING\n" + st);

        return st.getBytes(Charset.forName("UTF-8"));
    }

    // Creates a new instance of SkeletonTurn.
    public static HAGameTurnData unpersist(byte[] byteArray) {

        if (byteArray == null) {
            Log.d(TAG, "Empty array---possible bug.");
            return new HAGameTurnData();
        }

        String st = null;
        try {
            st = new String(byteArray, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
            return null;
        }


        HAGameTurnData retVal = new HAGameTurnData();
        try {
            JSONObject obj = new JSONObject(st);
            if (obj.has("categoryID")) {
                retVal.categoryID = obj.getString("categoryID");
            }
            if (obj.has("turnCount")) {
                retVal.turnCount = obj.getInt("turnCount");
            }
            if (obj.has("questions")) {
                Gson gson = new Gson();
                Type type = new TypeToken<List<HAQuestion>>() {}.getType();
                String questionsString = obj.getString("questions");
                retVal.questions = gson.fromJson(questionsString, type);
            }
            if (obj.has("secondUserScore")) {
                retVal.secondUserScore = obj.getString("secondUserScore");
            }

            if (obj.has("firstUserScore")) {
                retVal.firstUserScore = obj.getString("firstUserScore");
            }

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Log.d(TAG, "====UNPERSIST \n" + st);
        return retVal;
    }

    private String questionsStringForQuestions(List<HAQuestion> questions)
    {
        Gson gson = new Gson();
        Type type = new TypeToken<List<HAQuestion>>() {}.getType();
        return gson.toJson(questions, type);

/*        String[] questionsArray = new String[questions.size()];
        for (int i=0; i<questions.size(); i++)
        {
            HAQuestion question = questions.get(i);
            //Bundle questionBundle = new Bundle();
            Map<String, String> questionMap =  new HashMap<String,String>();
            questionMap.put(HAQuestion.kQuestionText,question.getQuestionText());
            questionMap.put(HAQuestion.kQuestionType,question.getQuestionType().toString());

            if (question.getQuestionType() == HAQuestion.HAQuestionType.eHATrueFalseQuestionType)
            {

            }
            else
            {
                questionMap.put(HAQuestion.kQuestionOptions,question.getOptions().toString());
                questionMap.put(HAQuestion.kQuestionShuffledOptions,question.getShuffledOptions().toString());
            }

            if (question.getCorrectAnswerExplanation() != null)
                questionMap.put(HAQuestion.kQuestionCorrectAnswerExplanation,question.getCorrectAnswerExplanation());


            if (question.getWrongAnswerExplanation() != null)
                questionMap.put(HAQuestion.kQuestionWrongAnswerExplanation,question.getWrongAnswerExplanation());

            questionMap.put(HAQuestion.kQuestionDuration,Integer.toString(question.getQuestionDuration()));
            questionMap.put(HAQuestion.kQuestionPoints,Integer.toString(question.getPoints()));
            questionMap.put(HAQuestion.kQuestionNegativePoints,Integer.toString(question.getNegativePoints()));
            questionMap.put(HAQuestion.kQuestionNegativePoints,Integer.toString(question.getNegativePoints()));

            if (question.getQuestionMediaFileName() != null)
            {
                questionMap.put(HAQuestion.kQuestionMediaFileName,question.getQuestionMediaFileName());
            }
            questionsArray[i] = questionMap.toString();
        }
        System.out.println("Questions xxx array :" + questionsArray);

        System.out.println("Questions xxx string :" + questionsArray.toString());
        return questionsArray.toString();*/
    }

}
