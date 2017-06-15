package com.dwhpro.AppSettings;

/**
 * Created by satie on 29/06/16.
 */

public class HAConfiguration{

/*------------------------------- APP SETTINGS -------------------------*/

    //Go to your application details in Developer Console -> Services & APIs -> copy the "Base64-encoded RSA public key to include in your binary. Please remove any spaces." and paste here
    //this key is required to make secure API calls to google.
    //public static String Base64_encoded_License_Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAuTdn3i5FSIM+4ta26SDRu8ypXJXIvt9uvBri3W+RuY/zt7yd4j8T30wy8UGD+dyNQrKZukfrPIp/Ush9rNP0fgV9koI9QMY2KM+ru2R2jC/wVpY3FXdl3dFSPw9EXlcZYT87u+UIHdGxDBliZz5DTdj3OlreKRwB6K1J/IdpJcnTRz/eX7wutOwozIhzCkokP/WJx8NqcvWOT6iqi+pBMSCqsjpekAro1/9qFH/aCCH+i5F67hsdrCAP3r3bZ0nZsNJ5xLXJR8zLWD0qLUwEIBMPAcbuv2qo5N5bl5J/g+0N95XuELoeBlpCiHVH7PyI1Aq/8KCU2SswfwfITsGvBwIDAQAB\n";
    public static String Base64_encoded_License_Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEA4VZp4KDqKILVMDO656Dey3fmI8noqSDcMntewV5pawXCVHYcfnkzKOAS9msdQKwXabelLcISktXVssxAZ9HWmjrhsEXtIilxaInlKQbtuFM+TIY/MS+lMZMvcWhiPvykyZu8MhBauI8JSZmVCOJRhvYOfCQRI1+ZXbs8scw96iJ/j37peDSMkW44hp4ot/BDfhxkJC7b2uVxOVZSW7IPKV+sH0F7Yd5hpG+PVOqurmJE8VZZONvE/lg5qLtAcu8VshJIfc14YcZXMmjDOHWBlHxFiBn4Dxe+eHyzSoCSaRbvK5BTnsx5PjRVdTwME2N1cQKvzR0EAjSvpWnYJT+FKwIDAQAB\n";
    private int showSplashScreenForSeconds = 3;

    //Configuration of titles for all the screens
    private String homeScreenTitle = "Teradata Quiz";
    private String categoriesScreenTitle = "Choose Quiz";
    private String aboutScreenTitle = "About";
    private String settingsScreenTitle = "Settings";
    private String shareScreenTitle = "Share App";
    private String worldScoreScreenTitle = "Leaderboard";
    private String moreCategoriesScreenTitle = "More Quizzes";
    private String multiplayer1to1ScreenTitle = "Multiplayer Quiz";




    //inapp billing configrations
    private Boolean enableInAppPurchasesForCategories = false;
    //Ads inapp billing configurations
    private Boolean showAds = false;
    private Boolean showInterAds = false;
    private Boolean enableInAppPurchaseForRemoveAds = false; //if true specify removeAdsProductID below
    private String removeAdsProductID = "remove_ads7"; //add your managed product id for removing ads. Ignored If "enableInAppPurchaseForRemoveAds" is false


    private Boolean shuffelOptions = true;
    private Boolean enableTimeBasedScoring = false; //if this us true score will be calculated based on the time taken by the use to answer for timer based quizzes
    private Boolean highlightCorrectAnswerOnAnsweringWrong = true; //setting this to true will highlight correct answer if user answers wrong
    public int defaultAnsweringQuestionDuration = 60;


    //Ad settings
    //useLinkForAboutScreen = true to show webpage. if false show plain about text
    private Boolean useLinkForAboutScreen = true;
    private String aboutText = "The Teradata Quiz is brought to you by\nDWHPro - \"The Teradata Experts\".\nOur quiz contains more than 1000 question which will prepare you to become a Teradata Certified Master\n" +
            "\n" +
            "\n"; //set above parameter aboutUs = 0 to pick this text
    private String aboutWebLink = "http://www.facebook.com/dwhpro"; //set above parameter aboutUs = 1 to load this webpage


    //Google ad units and also need to be configured in Strings.xml fo names "ad_unit_id" and "banner_ad_unit_id"
    private String interstitialAdId = "ca-app-pub-3940256099942544/1033173712";
    private String bannerAdId = "ca-app-pub-3940256099942544/6300978111";
    // ca-app-pub-3715837112683644~7305062124


    //Configure your total wins leaderboard id here
    private String totalWinsLeaderBoardId = "TeradataPro";
    boolean enableTimerBasedScoring = false;

    //Once sharing is enabled configure Facebook and Twitter required ids in strings.xml file
    public boolean enableTwitterScoresSharing = true;
    public boolean enableFacebookScoresSharing = false;
    public String playStoreLinkOfThisApplication = "https://www.dwhpro.com"; //add this app link here. This link will be used to share scores on FB and Twitter
/*-----------------------------------------------------------------------*/











//Below code lines are not meant for modifications

    private static HAConfiguration singleton = new HAConfiguration();

    private HAConfiguration() {
    }

    /* Static 'instance' method */
    public static HAConfiguration getInstance() {
        return singleton;
    }

    public Boolean getEnableTimeBasedScoring() {
        return enableTimeBasedScoring;
    }
    public Boolean getHighlightCorrectAnswerOnAnsweringWrong() {
        return highlightCorrectAnswerOnAnsweringWrong;
    }

    public String getMultiplayer1to1ScreenTitle() {
        return multiplayer1to1ScreenTitle;
    }

    public String getHomeScreenTitle() {
        return homeScreenTitle;
    }

    public String getCategoriesScreenTitle() {
        return categoriesScreenTitle;
    }

    public String getAboutScreenTitle() {
        return aboutScreenTitle;
    }

    public String getSettingsScreenTitle() {
        return settingsScreenTitle;
    }

    public String getWorldScoreScreenTitle() {
        return worldScoreScreenTitle;
    }

    public String getShareScreenTitle() {
        return shareScreenTitle;
    }


    public String getMoreCategoriesScreenTitle() {
        return moreCategoriesScreenTitle;
    }

    public String getInterstitialAdId() {
        return interstitialAdId;
    }
    public String getBannerAdId() {
        return bannerAdId;
    }
    public boolean isEnableTimerBasedScoring()
    {
        return enableTimerBasedScoring;
    }

    public Boolean isInAppPurchaseEnabledForRemovingAds()
    {
        return enableInAppPurchaseForRemoveAds;
    }

    public String getRemoveAdsProductID()
    {
        return removeAdsProductID;
    }
    public String getTotalWinsLeaderBoardIdinsLeaderBoardId() {
        return totalWinsLeaderBoardId;
    }

    public String getAboutText()
    {
        return aboutText;
    }

    public String getAboutWebLink()
    {
        return aboutWebLink;
    }


    public Boolean getShuffelOptions() {
        return shuffelOptions;
    }
    public void setShuffelOptions(Boolean shuffelOptions) {
        shuffelOptions = shuffelOptions;
    }
    public Boolean getuseLinkForAboutScreen() {
        return useLinkForAboutScreen;
    }
    public Boolean getShowAds() {
        return showAds;
    }
    public void setShowAds(Boolean showAds) {
        this.showAds = showAds;
    }
    public Boolean getEnableInAppPurchasesForCategories() {
        return enableInAppPurchasesForCategories;
    }
    public void setEnableInAppPurchasesForCategories(Boolean enableInAppPurchasesForCategories) {
        this.enableInAppPurchasesForCategories = enableInAppPurchasesForCategories;
    }

    public int getShowSplashScreenForSeconds() {
        return showSplashScreenForSeconds;
    }

    public void setShowSplashScreenForSeconds(int showSplashScreenForSeconds) {
        this.showSplashScreenForSeconds = showSplashScreenForSeconds;
    }
}

