package nl.goochem.fragments;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.provider.OpenableColumns;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import nl.goochem.BaseActivity;
import nl.goochem.LocationService;
import nl.goochem.R;
import nl.goochem.helpers.UrlJsonAsyncTask;
import nl.goochem.introduction.IntroductionActivity;

public class FragmentChangeActivity extends BaseActivity {
	
	private Fragment mContent;

    // Settings
    private SharedPreferences mPreferences;
    private boolean showIntroduction;

    public static LocalBroadcastManager mBroadcaster;

    public FragmentChangeActivity() {
		super(R.string.app_name);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        // Settings
        mPreferences = getSharedPreferences(BaseActivity.ID_SETTINGS, Context.MODE_PRIVATE);
        PreferenceManager.setDefaultValues(this, BaseActivity.ID_SETTINGS, Context.MODE_PRIVATE, R.xml.preferences, false);
        showIntroduction = mPreferences.getBoolean(BaseActivity.ID_SHOW_INTRODUCTION, true);
        String userName = mPreferences.getString(BaseActivity.ID_USER_NAME, "");

        // First Start
        Intent i;
        if (showIntroduction){
            // Show Splash
            openWelcomeActivity();
        } else {
            startLocationService();
            mBroadcaster = LocalBroadcastManager.getInstance(this);

            // set the Above View
            if (savedInstanceState != null)
                mContent = getFragmentManager().getFragment(savedInstanceState, "mContent");
            if (mContent == null)
                  mContent = new DashboardFragment();

            // set the Above View
            setContentView(R.layout.content_frame);
            getFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_frame, mContent)
                    .commit();

            // set the Behind View
            View view = LayoutInflater.from(getApplication()).inflate(R.layout.menu_frame, null);
            TextView currentUser = (TextView) view.findViewById(R.id.currentUser);
            currentUser.setText(userName);

            setBehindContentView(view);
            getFragmentManager()
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
		getFragmentManager().putFragment(outState, "mContent", mContent);
	}
	
	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getFragmentManager()
		.beginTransaction()
		.replace(R.id.content_frame, fragment)
		.commit();
		getSlidingMenu().showContent();
	}

    public void destroySession() {
        stopLocationService();
        LogoutTask logoutTask = new LogoutTask(true, this);
        logoutTask.setMessageLoading("Signing Out...");
        logoutTask.setAuthToken(mPreferences.getString(BaseActivity.ID_AUTH_TOKEN, ""));
        logoutTask.execute(BaseActivity.LOGOUT_URL);
    }

    private class LogoutTask extends UrlJsonAsyncTask {
        public LogoutTask(Boolean showProgress, Context context) {
            super(showProgress, context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpDelete delete = new HttpDelete(urls[0]);
            String response = null;
            JSONObject json = new JSONObject();

            try {
                try {
                    // setup the returned values in case
                    // something goes wrong
                    json.put("success", false);
                    json.put("info", "Something went wrong. Retry!");

                    // setup the request headers
                    delete.setHeader("Accept", "application/json");
                    delete.setHeader("Content-Type", "application/json");
                    delete.setHeader("X-API-TOKEN", mPreferences.getString(BaseActivity.ID_AUTH_TOKEN,""));

                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    response = client.execute(delete, responseHandler);
                    json = new JSONObject(response);

                } catch (HttpResponseException e) {
                    e.printStackTrace();
                    Log.e("ClientProtocol", "" + e);
                    json.put("info", "Token Invalid. Retry!");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("IO", "" + e);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("JSON", "" + e);
            }

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                if (json.getBoolean("success")) {
                    SharedPreferences.Editor editor = mPreferences.edit();
                    editor.remove(BaseActivity.ID_AUTH_TOKEN);
                    editor.remove(BaseActivity.ID_USER_EMAIL);
                    editor.putBoolean(BaseActivity.ID_SHOW_INTRODUCTION, true);
                    editor.commit();

                    // launch the IntroActivity and close this one
                    openWelcomeActivity();
                }
                Toast.makeText(context, json.getString("info"), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                // something went wrong: show a Toast
                // with the exception message
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                super.onPostExecute(json);
            }
        }
    }

    public boolean isMyServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (LocationService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void startLocationService(){
        //Set up service
        if (!isMyServiceRunning()){
            Log.d("---","Activity START");

            startService(new Intent(this, LocationService.class));
        }
    }

    public void stopLocationService(){
        if (isMyServiceRunning()){
            Log.d("---","Activity STOP");

            stopService(new Intent(this, LocationService.class));
        }
    }

    public void restartLocationService(){
        if (isMyServiceRunning()){
            Log.d("---","Activity RESET");
            stopService(new Intent(this, LocationService.class));
            startService(new Intent(this, LocationService.class));
        }
    }

    @Override
    public void onBackPressed() {
        View fragment_dashboard = findViewById(R.id.fragment_dashboard);
        if (fragment_dashboard == null){
            switchContent(new DashboardFragment());
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (activeSession()) {
            // Check if we are authorized
            new RetrieveResponseCode(this.getBaseContext()).execute(PROFILE_URL);
        } else {
            openWelcomeActivity();
        }
    }

    private void openWelcomeActivity() {
        Intent i = new Intent(FragmentChangeActivity.this, IntroductionActivity.class);
        startActivity(i);
        finish();
    }

    /** Check if user credentials are present */
    private boolean activeSession(){
        if (mPreferences.contains(ID_AUTH_TOKEN) && mPreferences.contains(ID_USER_EMAIL)){
            return true;
        } else { return false;}
    };

    /** Retrieve the HTTP response code from the server
     * 401 means the token has become invalid, so the user will need to login again.
     */
    private class RetrieveResponseCode extends AsyncTask<String, Void, Integer> {
        protected Context context = null;
        public RetrieveResponseCode(Context context) {
            this.context = context;
        }
        @Override
        protected Integer doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection http = (HttpURLConnection) url.openConnection();
                http.setRequestProperty("X-API-TOKEN", mPreferences.getString(ID_AUTH_TOKEN, ""));
                return http.getResponseCode();
            } catch (Exception e) {
                Log.e(TAG,e.toString());
                return null;
            }
        }

        protected void onPostExecute(Integer httpCode) {
            Log.d(TAG, String.valueOf(httpCode));
            if (httpCode != null){
                if (httpCode == 401){
                    Toast.makeText(context, "Session expired!", Toast.LENGTH_LONG).show();
                    openWelcomeActivity();
                } else if (httpCode >= 401) {
                    Toast.makeText(context, "Server Error: "+httpCode, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(context, "Server Unreachable!", Toast.LENGTH_LONG).show();
            }
        }
    }

}
