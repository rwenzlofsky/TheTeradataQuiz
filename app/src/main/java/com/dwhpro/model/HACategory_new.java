package com.dwhpro.model;

/**
 * Created by satie on 29/06/16.
 */


/*
 "category_color":"#FF5050",
      "timer_required":true,
      "category_id":"1",
      "category_name":"Bollywood",
      "leaderboard_id":"CgkIt9uijqQBEAIQCA",
      "category_description":"Test your knowledge about cars and logos",
      "category_image_path":"entertainment.png",
      "category_questions_max_limit":"10"
 */

interface HACategoryConstants
{
    public final String kCategoryName = "category_name";
    public final String kCategoryDescription = "category_description";
    public final String kCategoryColor = "category_color";
    public final String kCategoryID = "category_id";
    public final String kCategoryLeaderboardID = "leaderboard_id";
    public final String kCategoryProductIdentifier = "productIdentifier";
    public final String kCategoryIconFileName = "category_image_path";
    public  final String kCategoryQuestionsLimit = "category_questions_max_limit";
    public final String kCategoryTimerRequired = "timer_required";

    //others
    public final String kCategoryProductPrice = "product_price";
}




public class HACategory_new implements HACategoryConstants{

    String categoryName;
    String categoryDescription;
    String categoryID;
    String categoryLeaderboardID;
    String categoryProductIdetifier;
    String categoryIconFileName;
    int categoryThemeColor;
    int categoryQuestionsLimit;
    Boolean isTimerRequired;

    //price of product identifier
    String productPrice;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryDescription() {
        return categoryDescription;
    }

    public void setCategoryDescription(String categoryDescription) {
        this.categoryDescription = categoryDescription;
    }

    public String getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(String categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryLeaderboardID() {
        return categoryLeaderboardID;
    }

    public void setCategoryLeaderboardID(String categoryLeaderboardID) {
        this.categoryLeaderboardID = categoryLeaderboardID;
    }

    public String getCategoryProductIdetifier() {
        return categoryProductIdetifier;
    }

    public void setCategoryProductIdetifier(String categoryProductIdetifier) {
        this.categoryProductIdetifier = categoryProductIdetifier;
    }

    public String getCategoryIconFileName() {
        return categoryIconFileName;
    }

    public void setCategoryIconFileName(String categoryIconFileName) {
        this.categoryIconFileName = categoryIconFileName;
    }

    public int getCategoryThemeColor() {
        return categoryThemeColor;
    }

    public void setCategoryThemeColor(int categoryThemeColor) {
        this.categoryThemeColor = categoryThemeColor;
    }

    public int getCategoryQuestionsLimit() {
        return categoryQuestionsLimit;
    }

    public void setCategoryQuestionsLimit(int categoryQuestionsLimit) {
        this.categoryQuestionsLimit = categoryQuestionsLimit;
    }

    public Boolean getTimerRequired() {
        return isTimerRequired;
    }

    public void setTimerRequired(Boolean timerRequired) {
        isTimerRequired = timerRequired;
    }

    public String getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(String productPrice) {
        this.productPrice = productPrice;
    }

}
