package nl.rotterdamcs.goochem.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Window;

import nl.rotterdamcs.goochem.BaseActivity;
import nl.rotterdamcs.goochem.R;
import nl.rotterdamcs.goochem.introduction.SplashScreen;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.widget.UserSettingsFragment;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class FragmentChangeActivity extends BaseActivity {
	
	private Fragment mContent;
    private UserSettingsFragment userSettingsFragment;

    // Settings
    private SharedPreferences settings;
    private boolean showIntroduction;

	public FragmentChangeActivity() {
		super(R.string.app_name);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // Settings
        settings = getSharedPreferences(BaseActivity.ID_SETTINGS, Context.MODE_PRIVATE);
        showIntroduction = settings.getBoolean(BaseActivity.ID_SHOW_INTRODUCTION, true);
        // First Start
        Intent i;
        if (showIntroduction){
            // Show Splash
            i = new Intent(FragmentChangeActivity.this, SplashScreen.class);
            startActivity(i);
            finish();
        } else {
            // set the Above View
            if (savedInstanceState != null)
                mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
            if (mContent == null)
//                userSettingsFragment = new com.facebook.widget.UserSettingsFragment();
//                userSettingsFragment.setSessionStatusCallback(new Session.StatusCallback() {
//                    @Override
//                    public void call(Session session, SessionState state, Exception exception) {
//                        Log.d("LoginUsingLoginFragmentActivity", String.format("New session state: %s", state.toString()));
//                    }
//                });
//                mContent = userSettingsFragment;
                  mContent = new ProfileFragment();

            // set the Above View
            setContentView(R.layout.content_frame);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, mContent)
                    .commit();

            // set the Behind View
            setBehindContentView(R.layout.menu_frame);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.menu_frame, new MenuFragment())
                    .commit();

            // customize the SlidingMenu
            getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        }
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}
	
	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.commit();
		getSlidingMenu().showContent();
	}

}
