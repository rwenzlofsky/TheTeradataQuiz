package com.dwhpro.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.dwhpro.AppSettings.HAConfiguration;
import com.dwhpro.AppSettings.HASettings;
import com.dwhpro.AppSettings.HAUtilities;
import com.dwhpro.Singleton.HAQuizDataManager;
import com.dwhpro.adapter.HACategoryAdapter;
import com.dwhpro.model.HACategory_new;
import com.dwhpro.quizappnew.R;
import com.dwhpro.utils.IabHelper;
import com.dwhpro.utils.IabResult;
import com.dwhpro.utils.Inventory;
import com.dwhpro.utils.Purchase;

import java.util.ArrayList;
import java.util.List;

public class HACategoriesActivity_new extends Activity {

    ListView categoriesListview;
    private HACategoryAdapter adapter;
    LinearLayout backButton;
    List<HACategory_new> categoryList;
    public Boolean pushedFromMoreCategories;
    IabHelper mHelper;
    String TAG = "com";
    String payload = "Zp/xZ/gkX0WZ/IRbc0lQFzssgIbJewIDAQAB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hacategories_activity_new);
        getActionBar().hide();

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Categories Activity")
                .putContentType("Categories Activity Showing")
                .putContentId("categories"));

        HAQuizDataManager.getInstance().context = this;
        pushedFromMoreCategories = getIntent().getBooleanExtra("showMoreCategories",false);
        categoriesListview = (ListView) findViewById(R.id.category_listview);

        Typeface text_face = HASettings.getInstance().getAppFontFace();
        TextView titleTextView = (TextView) findViewById(R.id.header_text);
        titleTextView.setTypeface(text_face);


        if (pushedFromMoreCategories)
        {
            titleTextView.setText(HAConfiguration.getInstance().getMoreCategoriesScreenTitle());
            this.categoryList = HAQuizDataManager.getInstance().getCategoriesRequirePurchase();

            if (this.categoryList == null)
            {
                Toast.makeText(this, "No quizzes for purchase!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                this.setupInappBilling();
            }
        }
        else {
            titleTextView.setText(HAConfiguration.getInstance().getCategoriesScreenTitle());
            this.categoryList = HAQuizDataManager.getInstance().getCategoriesForPlay();
            adapter  = new HACategoryAdapter(this, categoryList);
            adapter.setInAppRow(pushedFromMoreCategories);
            LayoutAnimationController lac = new LayoutAnimationController(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down), 0.8f); //0.8f == time between appearance of listview items.
            categoriesListview.setLayoutAnimation(lac);
            categoriesListview.startLayoutAnimation();
            categoriesListview.setAdapter(adapter);
        }

        backButton = (LinearLayout) findViewById(R.id.back_buttonimage);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        categoriesListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                HAUtilities.getInstance().playTapSound();

                HAQuizDataManager.getInstance().selectedCategory = categoryList.get(position);

                if (pushedFromMoreCategories)
                {
                    buyProduct(HAQuizDataManager.getInstance().selectedCategory);
                }
                else
                {
                    if (HAQuizDataManager.getInstance().isMultiplayerGame)
                    {
                        HAQuizDataManager.getInstance().isCategorySelectedForMultiplayerGame = true;
                        finish();
                    }
                    else
                    {
                        Intent quizIntent = new Intent(HACategoriesActivity_new.this, HAGameActivity_new.class);
                        startActivity(quizIntent);
                        finish();
                    }
                }
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, " ");
        Log.d(TAG, "<<<<<< in IAP ONACTIVITYRESULT >>>>>>");
        Log.d(TAG, "onActivityResult " + requestCode + "," + resultCode + "," + data);
        if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void buyProduct(HACategory_new category)
    {
        if (mHelper == null)
        {
            Toast.makeText(this, "Unable to purchase, please try again later!", Toast.LENGTH_SHORT).show();
            return;
        }

        mHelper.flagEndAsync();

        //add listener on purchase
        //this method called purchase is made
        showSpinner();
        //invoke purchase flow
        mHelper.launchPurchaseFlow(this, category.getCategoryProductIdetifier(), 10001,
                new IabHelper.OnIabPurchaseFinishedListener() {
                    @Override
                    public void onIabPurchaseFinished(IabResult result, Purchase info) {
                        dismissSpinner();
                        if (result.isFailure())
                        {
                            int responseCode = result.getResponse();
                            if (responseCode == 7)// product is already owned
                            {
                                Toast.makeText(HACategoriesActivity_new.this, "This quiz already owned by you. Please restore from Settings screen!", Toast.LENGTH_LONG).show();
                            }
                            else {
                                Log.d(TAG + "Failed to purchase",result.getMessage());
                                Toast.makeText(HACategoriesActivity_new.this, "Unable to make purchase, please try again later!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {

                            //purchaseState The purchase state of the order. Possible values are 0 (purchased), 1 (canceled), 2 (refunded), or 3 (expired, for subscription purchases only).



                            int purchaseState = info.getPurchaseState();
                            if (purchaseState == 0) //purchased
                            {
                                HAQuizDataManager.getInstance().markProductPurchased(info.getSku());
                                categoryList = HAQuizDataManager.getInstance().getCategoriesRequirePurchase();

                                System.out.println("OnIabPurchaseFinishedListener categorieslist: "+categoryList);

                                if (categoryList == null)
                                {
                                    Toast.makeText(HACategoriesActivity_new.this, "Quiz purchased and added to your play quiz list!", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                                else
                                {

                                    ArrayList<String> skuList = new ArrayList<String>(categoryList.size());
                                    for (int i = 0; i < (categoryList != null ? categoryList.size() : 0); i++)
                                    {
                                        HACategory_new category = categoryList.get(i);
                                        skuList.add(category.getCategoryProductIdetifier());
                                    }
                                    Log.d("XXXX from Categories", "queryInventoryAsync call made with skulist " + skuList);

                                    mHelper.queryInventoryAsync(true, skuList, mGotInventoryListener);
                                }
                            }
                            else if (purchaseState == 1) //cancelled
                            {
                                Toast.makeText(HACategoriesActivity_new.this, "Purchase canceled!", Toast.LENGTH_LONG).show();
                            }
                            else if (purchaseState == 2)
                            {

                            }
                            else if (purchaseState == 3) //expired used in case of subscription
                            {

                            }
                        }
                    }
                }, payload);


    }

    private void setupInappBilling() {
        // TODO Auto-generated method stub

        showSpinner();
        mHelper = new IabHelper(this, HAConfiguration.Base64_encoded_License_Key);
        mHelper.enableDebugLogging(true);
        mHelper.startSetup(new
                                   IabHelper.OnIabSetupFinishedListener() {
                                       public void onIabSetupFinished(IabResult result) {
                                           if (!result.isSuccess()) {
                                               Log.d("XXXX from Categories:", "In-app Billing setup failed: " +
                                                       result);
                                               dismissSpinner();
                                           } else {
                                               Log.d("XXXX from Categories", "In-app Billing is set up OK");

                                               ArrayList<String> skuList = new ArrayList<String>(categoryList.size());
                                               for (int i = 0; i < (categoryList != null ? categoryList.size() : 0); i++)
                                               {
                                                   HACategory_new category = categoryList.get(i);
                                                   skuList.add(category.getCategoryProductIdetifier());
                                               }
                                               Log.d("XXXX from Categories", "queryInventoryAsync call made with skulist " + skuList);
                                               mHelper.queryInventoryAsync(true, skuList, mGotInventoryListener);
                                           }
                                       }
                                   });
    }

    // Get already purchased response
    private IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result,
                                             Inventory inventory) {
            dismissSpinner();

            if (result.isFailure()) {
                // handle error here
                Log.d("XXXX","Error checking inventory: " + result);
            }
            else {
                // does the user have the premium upgrade?
//                mIsPremium = inventory.hasPurchase("android.test.purchased");
                Log.d("XXXX","inventory object: " + inventory);

                for (int i = 0; i < (categoryList != null ? categoryList.size() : 0); i++)
                {
                    HACategory_new category = categoryList.get(i);
                    String productPrice = inventory.getSkuDetails(category.getCategoryProductIdetifier()).getPrice();
                    //Toast.makeText(HACategoriesActivity_new.this, "XXXX from categories : category :"+ category.getCategoryName() + "  product_id :"+category.getCategoryProductIdetifier() + "  price:" + category.getProductPrice(), Toast.LENGTH_SHORT).show();
                    category.setProductPrice(productPrice);
//                    findViewById(R.id.progress_Layout).setVisibility(View.INVISIBLE);
                }

                if (adapter == null)
                {
                    adapter  = new HACategoryAdapter(HACategoriesActivity_new.this, categoryList);
                    adapter.setInAppRow(pushedFromMoreCategories);
                    LayoutAnimationController lac = new LayoutAnimationController(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down), 0.8f); //0.8f == time between appearance of listview items.
                    categoriesListview.setLayoutAnimation(lac);
                    categoriesListview.startLayoutAnimation();
                    categoriesListview.setAdapter(adapter);
                }
                else
                {
                    adapter.setCategoryItems(categoryList);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    };


    public void onBackPressed() {

        HAUtilities.getInstance().playTapSound();

        if (HAQuizDataManager.getInstance().isMultiplayerGame)
        {
            HAQuizDataManager.getInstance().isCategorySelectedForMultiplayerGame = false;
        }

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


    public void showSpinner() {
        findViewById(R.id.progressLayout).setVisibility(View.VISIBLE);
    }

    public void dismissSpinner() {
        findViewById(R.id.progressLayout).setVisibility(View.GONE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // very important:
        if (mHelper != null) {
            mHelper.dispose();
            mHelper = null;
        }
    }

}


