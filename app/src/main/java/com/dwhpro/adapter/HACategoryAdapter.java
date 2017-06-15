package com.dwhpro.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dwhpro.Activities.HACategoriesActivity_new;
import com.dwhpro.AppSettings.HASettings;
import com.dwhpro.Singleton.HAQuizDataManager;
import com.dwhpro.model.HACategory_new;
import com.dwhpro.quizappnew.R;

import java.io.File;
import java.util.List;

/**
 * Created by satie on 30/06/16.
 */

public class HACategoryAdapter extends BaseAdapter {
    private HACategoriesActivity_new activity;
    private LayoutInflater inflater;
    private List<HACategory_new> categoryItems;
    private Boolean isInAppRow;
    ProgressBar progressBar;

    public HACategoryAdapter(HACategoriesActivity_new activity,List<HACategory_new> categoryItems){
        this.activity  =  activity;
        this.categoryItems = categoryItems;
    }

    public void setCategoryItems(List<HACategory_new> categories)
    {
        this.categoryItems = categories;
    }

    @Override public int getCount() {
        // TODO Auto-generated method stub

        if (this.categoryItems == null)
            return 0;

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
        // TODO Auto-generated method stub

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.category_row, null);

        final HACategory_new category = categoryItems.get(position);

        Typeface text_face_medium = HASettings.getInstance().getAppMediumFontFace();

        Typeface text_face_light = HASettings.getInstance().getAppLightFontFace();

        TextView title = (TextView) convertView.findViewById(R.id.category_title);
        title.setTypeface(text_face_medium);
        TextView subTitle = (TextView) convertView.findViewById(R.id.category_subtitle);
        subTitle.setTypeface(text_face_light);
        ImageView iv = (ImageView) convertView.findViewById(R.id.category_image);
        TextView score = (TextView) convertView.findViewById(R.id.score_text);
        LinearLayout buyLayout = (LinearLayout) convertView.findViewById(R.id.buy_layout);
        LinearLayout highscoreLayout = (LinearLayout) convertView.findViewById(R.id.highscore_layout);

        if (isInAppRow)
        {
            TextView priceTextview = (TextView) convertView.findViewById(R.id.price_textview);
            priceTextview.setTypeface(text_face_light);
            priceTextview.setText(category.getProductPrice());
            //priceTextview.setText("$60.0");
            buyLayout.setVisibility(View.VISIBLE);
            highscoreLayout.setVisibility(View.GONE);
        }
        else
        {
            buyLayout.setVisibility(View.GONE);
            highscoreLayout.setVisibility(View.VISIBLE);

            TextView completePercentText = (TextView) convertView.findViewById(R.id.complete_percent_textview);
            completePercentText.setTypeface(text_face_light);
            double percentage = HAQuizDataManager.getInstance().progrePercentageForCategory(category);
            System.out.println(category.getCategoryName() + "----" + Double.toString(percentage));
            progressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar);
            completePercentText.setText(Integer.toString((int)percentage)+"%");
            progressBar.setProgress((int)percentage);
        }

        score.setTypeface(text_face_light);
        long highscore = HAQuizDataManager.getInstance().getHighScoreForCategory(category);
        score.setText(Long.toString(highscore));


        RelativeLayout cellLayout = (RelativeLayout) convertView.findViewById(R.id.cell_layout);
        cellLayout.setBackgroundColor(category.getCategoryThemeColor());
        title.setText(category.getCategoryName());
        subTitle.setText(category.getCategoryDescription());


        String pic = category.getCategoryIconFileName();
        Log.d("pic",pic);
        if (pic != null && !pic.isEmpty() && !pic.equals("null")){
            File f = new File(pic);

            // if it's a directory, don't remove the extention
            if (f.isDirectory()) {

            }
            String name = f.getName();

            final int lastPeriodPos = name.lastIndexOf('.');
            if (lastPeriodPos <= 0)
            {
                // No period after first character - return name as it was passed in

            }
            else {

                File renamed = new File(f.getParent(), name.substring(0, lastPeriodPos));
                Context contextImage = iv.getContext();
                String renamedImg = String.valueOf(renamed);
                int id = activity.getResources().getIdentifier(renamedImg, "drawable", contextImage.getPackageName());
                iv.setImageResource(id);
            }

        }

        return convertView;
    }

    public void setInAppRow(Boolean inAppRow) {
        isInAppRow = inAppRow;
    }
}
