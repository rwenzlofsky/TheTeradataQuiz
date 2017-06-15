package com.dwhpro.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by satie on 29/06/16.
 */

/*
{
      "Answer":"0",
      "points":"100",
      "duration_in_seconds":"30",
      "question":"What are John Abraham and Akshay Kumar's professions in Garam Masala?",
      "negative_points":"0",
      "options":[
        "Photographers",
        "Reporters",
        "Professors",
        "Lawyers"
      ],
      "question_type":"1"
    }
 */

interface HAQuestionsConstants
{
    public  final String kQuestionText = "question";
    public  final String kQuestionType = "question_type";
    public  final String kQuestionCorrectAnswerExplanation = "correct_ans_explanation";
    public  final String kQuestionWrongAnswerExplanation = "wrong_ans_explanation";
    public  final String kQuestionAnswer = "Answer";
    public  final String kQuestionPoints = "points";
    public  final String kQuestionNegativePoints = "negative_points";
    public  final String kQuestionOptions = "options";
    public  final String kQuestionShuffledOptions = "shuffled_options";
    public  final String kQuestionDuration = "duration_in_seconds";
    public final String kQuestionMediaFileName = "picture_or_video_name";
}


public class HAQuestion implements  HAQuestionsConstants
{

    public enum HAQuestionType {
        @SerializedName("0")
        eHANoQuestionType(0),

        @SerializedName("1")
        eHATextQuestionType(1),

        @SerializedName("2")
        eHAPictureQuestionType(3),

        @SerializedName("3")
        eHAVideoQuestionType(3),

        @SerializedName("4")
        eHATrueFalseQuestionType(4);

        private final int value;
        public int getValue() {
            return value;
        }

        private HAQuestionType(int value) {
            this.value = value;
        }
    }

    String questionText;
    int answerIndex;
    int questionDuration;
    int points;
    int negativePoints;
    String[] options;
    String[] shuffledOptions;
    String correctAnswerExplanation;
    String wrongAnswerExplanation;
    HAQuestionType questionType;


    public String getQuestionMediaFileName() {
        return questionMediaFileName;
    }

    public void setQuestionMediaFileName(String questionMediaFileName) {
        this.questionMediaFileName = questionMediaFileName;
    }

    String questionMediaFileName;

    public String getWrongAnswerExplanation() {
        return wrongAnswerExplanation;
    }

    public void setWrongAnswerExplanation(String wrongAnswerExplanation) {
        this.wrongAnswerExplanation = wrongAnswerExplanation;
    }

    public String getCorrectAnswerExplanation() {
        return correctAnswerExplanation;
    }

    public void setCorrectAnswerExplanation(String correctAnswerExplanation) {
        this.correctAnswerExplanation = correctAnswerExplanation;
    }



    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public int getAnswerIndex() {
        return answerIndex;
    }

    public void setAnswerIndex(int answerIndex) {
        this.answerIndex = answerIndex;
    }

    public int getQuestionDuration() {
        return questionDuration;
    }

    public void setQuestionDuration(int questionDuration) {
        this.questionDuration = questionDuration;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getNegativePoints() {
        return negativePoints;
    }

    public void setNegativePoints(int negativePoints) {
        this.negativePoints = negativePoints;
    }

    public String[] getOptions() {
        return options;
    }

    public void setOptions(String[] options) {
        this.options = options;
    }

    public String[] getShuffledOptions() {
        return shuffledOptions;
    }

    public void setShuffledOptions(String[] shuffledOptions) {
        this.shuffledOptions = shuffledOptions;
    }

    public HAQuestionType getQuestionType() {
        return questionType;
    }

    public void setQuestionType(HAQuestionType questionType) {
        this.questionType = questionType;
    }

    public HAQuestionType getQuestionTypeForIntValue(int value)
    {
        if (value == 1)
            return HAQuestionType.eHATextQuestionType;
        if (value == 2)
            return HAQuestionType.eHAPictureQuestionType;
        if (value == 3)
            return HAQuestionType.eHAVideoQuestionType;
        if (value == 4)
            return HAQuestionType.eHATrueFalseQuestionType;

        return HAQuestionType.eHATextQuestionType;
    }

    public Boolean isCorrectAnswer(String option)
    {
            String correctAnswer = options[answerIndex];
            if (option.contentEquals(correctAnswer))
                return true;

        return false;
    }

}
