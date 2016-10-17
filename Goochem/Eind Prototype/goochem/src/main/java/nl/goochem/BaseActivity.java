package nl.goochem;

import android.app.ActivityManager;
import android.app.FragmentManager;
import android.app.ListFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ShareActionProvider;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;


public class BaseActivity extends SlidingFragmentActivity {
    // Settings
    private SharedPreferences mPreferences;
    public static final String ID_SETTINGS = "CurrentUser";
    public static final String ID_AUTH_TOKEN = "AuthToken";
    public static final String ID_USER_EMAIL = "UserEmail";
    public static final String ID_USER_NAME = "UserName";
    public static final String ID_AUTO_LOCATION = "AutoLocation";
    public static final String ID_MANUAL_LOCATION = "ManualLocation";

    public static final String ID_SHOW_INTRODUCTION = "showIntroduction";


    public static String getBaseUrl() {
        return BASE_URL;
    }
    public static String getProximityUrl() {
        return PROXIMITY_URL;
    }


    // URLS
    protected static final String BASE_URL = "http://test.zwooosh.eu/";
    protected static final String NEW_LOCATION_URL = BASE_URL + "api/v1/users/update";
    protected static final String PROFILE_URL = BASE_URL + "api/v1/users/profile.json";
    protected static final String PROXIMITY_URL = BASE_URL + "api/v1/users/proximity_search";
    protected final static String LOGOUT_URL = BaseActivity.BASE_URL + "api/v1/sessions.json";

    // Debug
    public static final String TAG = "nl.goochem";

    // ETC
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int ONE_MINUTE = 1000 * 60;

    private int mTitleRes;
    protected ListFragment mFrag;
    private ShareActionProvider mShareActionProvider;


    public BaseActivity(int titleRes) {
        mTitleRes = titleRes;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display logo
        // getActionBar().setDisplayUseLogoEnabled(true);
        // getActionBar().setDisplayShowTitleEnabled(false);
        setSpannableString("goochem");
        getActionBar().setDisplayHomeAsUpEnabled(true);

        // set the Behind View
        setBehindContentView(R.layout.menu_frame);
        if (savedInstanceState == null) {
            mFrag = new MenuListFragment();
            getFragmentManager().beginTransaction()
            .replace(R.id.menu_frame, mFrag)
            .commit();
        } else {
            mFrag = (ListFragment)this.getFragmentManager().findFragmentById(R.id.menu_frame);
        }

        // customize the SlidingMenu
        SlidingMenu menu = getSlidingMenu();
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                toggle();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Create string with custom font */
    private void setSpannableString(CharSequence title){
//        SpannableString s = new SpannableString(title);
//        s.setSpan(new TypefaceSpan(getBaseContext(), "LilyScriptOne.ttf"), 0, s.length(),
//                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        getActionBar().setTitle(s);
        int titleId = getResources().getIdentifier("action_bar_title", "id",
                "android");
        TextView actionbarTitle = (TextView) findViewById(titleId);
        Typeface font = Typeface.createFromAsset(getAssets(), "LilyScriptOne.ttf");
        actionbarTitle.setTypeface(font);
        actionbarTitle.setText(title);
        actionbarTitle.setTextSize(25);
    }

    /** Set Actionbar title */
    @Override
    public void setTitle(CharSequence title) {
        setSpannableString(title);
    }

    @Override
    public SlidingMenu getSlidingMenu() {
        return super.getSlidingMenu();
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    public boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > ONE_MINUTE;
        boolean isSignificantlyOlder = timeDelta < -ONE_MINUTE;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
}
