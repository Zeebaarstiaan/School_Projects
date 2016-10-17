package nl.rotterdamcs.goochem.introduction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.Window;

import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;

import nl.rotterdamcs.goochem.R;

public class IntroductionActivity extends FragmentActivity {

    // Tag
    public static final String TAG = "IntroductionActivity";

    // Fragment variables
    private IntroFragmentAdapter mAdapter;
    private ViewPager mPager;
    private PageIndicator mIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Set circle layout
        setContentView(R.layout.simple_circles);

        // Create Adapter
        mAdapter = new IntroFragmentAdapter(getSupportFragmentManager(),getApplicationContext());

        // Set Adapter to pager
        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(mAdapter);

        // Bind indicator to pages
        mIndicator = (CirclePageIndicator)findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
    }
}