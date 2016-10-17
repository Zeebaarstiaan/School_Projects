package nl.zwooosh.goochem_location;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.savagelook.android.UrlJsonAsyncTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


public class MainActivity extends Activity implements View.OnClickListener{
    // Settings
    private SharedPreferences mPreferences;
    public static final String ID_SETTINGS = "CurrentUser";
    public static final String ID_AUTH_TOKEN = "AuthToken";
    public static final String ID_USER_EMAIL = "UserEmail";
    public static final String ID_LONGITUDE = "Longitude";
    public static final String ID_LATITUDE = "Latitude";
    public static final String ID_MANUAL_LOCATION = "ManualLocation";

    private static final int TWO_MINUTES = 1000 * 60 * 2;

    public static final String BASE_URL = "http://test.zwooosh.eu/";
    private static final String NEW_LOCATION_URL = BASE_URL + "api/v1/users/update";
    private static final String PROFILE_URL = BASE_URL + "api/v1/users/profile.json";
    private static final String PROXIMITY_URL = BASE_URL + "api/v1/users/proximity_search";
    private final static String LOGOUT_URL = MainActivity.BASE_URL + "api/v1/sessions.json";

    // Debug
    public static final String TAG = "nl.zwooosh.goochem_location";

    // View Elements
    private Button btnGetLocation;
    private Button btnPushLocation;
    private TextView txtLat;
    private TextView txtLong;

    // Location
    private boolean gpsWarning;
    private LocationManager locationManager;
    private String locationProvider;
    private LocationListener locationListener;
    private ProgressDialog progressDialog;
    private Location lastKnownLocation;
    private Button btnProximitySearchClose;
    private Button btnProximitySearchFar;
    private int searchRadius = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get Settings
        mPreferences = getSharedPreferences(MainActivity.ID_SETTINGS, Context.MODE_PRIVATE);

        // Show GPS Warning
        gpsWarning = true;

        // Buttons
        btnGetLocation = (Button) findViewById(R.id.btnGetLocation);
        btnGetLocation.setOnClickListener(this);
        btnPushLocation = (Button) findViewById(R.id.btnPushLocation);
        btnPushLocation.setOnClickListener(this);
        btnProximitySearchClose = (Button) findViewById(R.id.btnProximitySearchClose);
        btnProximitySearchClose.setOnClickListener(this);
        btnProximitySearchFar = (Button) findViewById(R.id.btnProximitySearchFar);
        btnProximitySearchFar.setOnClickListener(this);

        // TextViews
        txtLat = (TextView) findViewById(R.id.txtLat);
        txtLong = (TextView) findViewById(R.id.txtLong);

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                // makeUseOfNewLocation(location);
                Log.d(TAG,"LOCATION FOUND!");
                progressDialog.dismiss();
                locationManager.removeUpdates(this);
                Log.d(TAG,"LISTENER DISABLED!");

                // Initialize the location fields
                if (lastKnownLocation != null) {
                    if (isBetterLocation(lastKnownLocation,location)){
                        Log.d(TAG,"Current location is better than last: UPDATING");
                        setLongLat(location);
                    } else {
                        Log.d(TAG,"Current location is worse than last: USE LAST KNOWN");
                        setLongLat(lastKnownLocation);
                    }
                } else {
                    setLongLat(location);
                }
            }
            public void onStatusChanged(String provider, int status, Bundle extras) {Log.d(TAG,"Provider STATUS CHANGED!");}
            public void onProviderEnabled(String provider) {Log.d(TAG,"Provider ENABLED!");}
            public void onProviderDisabled(String provider) {Log.d(TAG,"Provider DISABLED!");}
        };

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        } else if ( id == R.id.action_logout ){
            destroySession();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnGetLocation:
                getLocation();
                break;
            case R.id.btnPushLocation:
                if (activeSession()){
                    Log.d(TAG,"TOKEN: "+mPreferences.getString(MainActivity.ID_AUTH_TOKEN,""));
                    Log.d(TAG,"EMAIL: "+mPreferences.getString(MainActivity.ID_USER_EMAIL,""));
                    pushLocationFromAPI(NEW_LOCATION_URL);
                }
                break;
            case R.id.btnProximitySearchClose:
                getUsersFromAPI(1000,PROXIMITY_URL);
                break;
            case R.id.btnProximitySearchFar:
                getUsersFromAPI(100000000,PROXIMITY_URL);
                break;
        }
    }

    private void getUsersFromAPI(int distance, String newLocationUrl) {
        searchRadius = distance;
        ProximitySearchTask proximitySearch = new ProximitySearchTask(MainActivity.this);
        proximitySearch.setMessageLoading("Retrieving users...");
        proximitySearch.setAuthToken(mPreferences.getString(MainActivity.ID_AUTH_TOKEN, ""));
        proximitySearch.execute(newLocationUrl);
    }

    private class ProximitySearchTask extends UrlJsonAsyncTask {
        public ProximitySearchTask(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost(urls[0]);
            JSONObject holder = new JSONObject();
            JSONObject dataObject = new JSONObject();
            String response = null;
            JSONObject json = new JSONObject();

            try {
                try {
                    json.put("success", false);
                    json.put("info", "Something went wrong. Retry!");
                    dataObject.put("distance", searchRadius);
                    holder.put("data", dataObject);
                    StringEntity se = new StringEntity(holder.toString());
                    post.setEntity(se);

                    post.setHeader("Accept", "application/json");
                    post.setHeader("Content-Type", "application/json");
                    post.setHeader("X-API-TOKEN", mPreferences.getString(MainActivity.ID_AUTH_TOKEN,""));

                    ResponseHandler<String> responseHandler = new BasicResponseHandler();
                    response = client.execute(post, responseHandler);
                    json = new JSONObject(response);

                } catch (HttpResponseException e) {
                    e.printStackTrace();
                    Log.e("ClientProtocol", "" + e);
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
//                Toast.makeText(context, json.getString("info"), Toast.LENGTH_LONG).show();

                JSONArray jsonUsers = json.getJSONArray("users");
                JSONObject jsonUser = new JSONObject();
                int length = jsonUsers.length();
                final ArrayList<User> userArray = new ArrayList<User>(length);

                for (int i = 0; i < length; i++) {
                    jsonUser = jsonUsers.getJSONObject(i);
                    userArray.add(new User(jsonUser.getLong("id"), jsonUser.getString("name")));
                }

                customDialog(userArray);

            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                super.onPostExecute(json);
            }
        }
    }

    private void pushLocationFromAPI(String newLocationUrl) {
        if (mPreferences.contains(MainActivity.ID_MANUAL_LOCATION)){
            PushLocationTask pushLocation = new PushLocationTask(MainActivity.this);
            pushLocation.setMessageLoading("Pushing location...");
            pushLocation.setAuthToken(mPreferences.getString(MainActivity.ID_AUTH_TOKEN, ""));
            pushLocation.execute(newLocationUrl);
        } else {
            Toast.makeText(getBaseContext(), "Get location first!", Toast.LENGTH_LONG).show();
        }

    }

    private class PushLocationTask extends UrlJsonAsyncTask {
        public PushLocationTask(Context context) {
            super(context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPut put = new HttpPut(urls[0]);
            JSONObject holder = new JSONObject();
            JSONObject userObject = new JSONObject();
            String response = null;
            JSONObject json = new JSONObject();

            Location location = getLocation(MainActivity.ID_MANUAL_LOCATION);
            if (location != null) {
                double longitude = location.getLongitude();
                double latitude = location.getLatitude();
                String userLocation = "POINT(" + longitude + ' ' + latitude + ')';

                try {
                    try {
                        json.put("success", false);
                        json.put("info", "Something went wrong. Retry!");
                        userObject.put("lonlat", userLocation);
                        holder.put("user", userObject);
                        StringEntity se = new StringEntity(holder.toString());
                        put.setEntity(se);

                        put.setHeader("Accept", "application/json");
                        put.setHeader("Content-Type", "application/json");
                        put.setHeader("X-API-TOKEN", mPreferences.getString(MainActivity.ID_AUTH_TOKEN,""));

                        ResponseHandler<String> responseHandler = new BasicResponseHandler();
                        response = client.execute(put, responseHandler);
                        json = new JSONObject(response);

                    } catch (HttpResponseException e) {
                        e.printStackTrace();
                        Log.e("ClientProtocol", "" + e);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("IO", "" + e);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON", "" + e);
                }
            } else {
                try {
                    json.put("success", false);
                    json.put("info", "No location found. Retry!");
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("JSON", "" + e);
                }
            }

            return json;
        }

        @Override
        protected void onPostExecute(JSONObject json) {
            try {
                Toast.makeText(context, json.getString("info"), Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            } finally {
                super.onPostExecute(json);
            }
        }
    }


    /** Set needed location providers and acquire new location */
    private void getLocation() {
        // Check if Location Services are enabled
        boolean locationEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        // Check if GPS is enabled
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (!locationEnabled){
            // Show Location Warning
            alertLocation("Location Services Disabled", "Your Location Services are currently disabled.\nYou must enable them for this application to work");
        } else {
            // Determine optimal locationProvider
            if(!gpsEnabled && gpsWarning){
                // Show GPS Warning if GPS is disabled
                alertGps("GPS Disabled", "Your GPS is currently disabled.\nGPS provides the most accurate results.\nDo you want to enable GPS?");
            } else if(gpsEnabled){
                // Use High Accuracy criteria if GPS is enabled
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_FINE);
                criteria.setPowerRequirement(Criteria.POWER_HIGH);
                criteria.setCostAllowed(true);
                criteria.setBearingRequired(false);

                locationProvider = locationManager.getBestProvider(criteria,true);
            } else{
                // Use Battery Saving criteria if GPS is disabled
                Criteria criteria = new Criteria();
                criteria.setAccuracy(Criteria.ACCURACY_COARSE);
                criteria.setPowerRequirement(Criteria.POWER_LOW);
                criteria.setCostAllowed(true);
                criteria.setBearingRequired(false);

                locationProvider = locationManager.getBestProvider(criteria,false);
            }

            // Get location if provider is set and warnings are disabled
            if (locationProvider != null && !gpsWarning) {
                Log.d(TAG, "Using provider: " + locationProvider);

                // Get last know location
                lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

                // Request new location update
                locationManager.requestLocationUpdates(locationProvider,0,0,locationListener);

                // Show progress dialog while determining position
                progressDialog = ProgressDialog.show(this, "", "Acquiring position...", true,true,new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface arg0) {
                        // Stop location search if progress dialog is cancelled
                        locationManager.removeUpdates(locationListener);
                    }
                });
            }
        }
    }

    /** Sets current latitude and longitude in the appropriate textviews
     * @param location  The new Location that you want the latitude and longitude from
     */
    private void setLongLat(Location location) {
        saveLocation(MainActivity.ID_MANUAL_LOCATION, location);

        double longitude = location.getLongitude();
        txtLong.setText(String.valueOf(longitude));
        Log.v(TAG, "Longitude: " +longitude);

        double latitude = location.getLatitude();
        txtLat.setText(String.valueOf(latitude));
        Log.v(TAG, "Latitude: " +latitude);
    }

    /** Determines whether one Location reading is better than the current Location fix
     * @param location  The new Location that you want to evaluate
     * @param currentBestLocation  The current Location fix, to which you want to compare the new one
     */
    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
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

    /** Creates an alert dialog for general Location Settings
     * @param title  The title of the dialog
     * @param message The message inside the dialog
     */
    public void alertLocation(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setTitle(title);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Open Location Settings
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /** Creates an alert dialog for GPS Location Settings
     * Warns a user to enable GPS for optimal results or configures network location provider
     * @param title  The title of the dialog
     * @param message The message inside the dialog
     */
    public void alertGps(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setTitle(title);

        // Add the buttons
        builder.setPositiveButton(R.string.enable_gps, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Open Location Settings
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.cancel();
            }
        });
        builder.setNegativeButton(R.string.use_network, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // Disable the warning, use non-gps location provider
                gpsWarning = false;
                getLocation();
                dialog.cancel();
            }
        });

        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void customDialog(ArrayList<User> userArray) {
        ListAdapter adapter = new UserAdapter(MainActivity.this,
                android.R.layout.simple_list_item_1, userArray);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Close Users")
                .setCancelable(false)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private class UserAdapter extends ArrayAdapter<User> implements View.OnClickListener {

        private ArrayList<User> items;
        private int layoutResourceId;

        public UserAdapter(Context context, int layoutResourceId, ArrayList<User> items) {
            super(context, layoutResourceId, items);
            this.layoutResourceId = layoutResourceId;
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = (TextView) layoutInflater.inflate(layoutResourceId, null);
            }
            User user = items.get(position);
            if (user != null) {
                TextView userTextView = (TextView) view.findViewById(android.R.id.text1);
                if (userTextView != null) {
                    userTextView.setText(user.getName());
                    userTextView.setOnClickListener(this);
                }
                view.setTag(user.getId());
            }
            return view;
        }

        @Override
        public void onClick(View view) {
            TextView userTextView = (TextView) view.findViewById(android.R.id.text1);
            Toast.makeText(getBaseContext(), "User ID:" + String.valueOf(view.getTag()) , Toast.LENGTH_SHORT).show();
        }
    }

    /** Check if user credentials are present */
    private boolean activeSession(){
        if (mPreferences.contains(MainActivity.ID_AUTH_TOKEN) && mPreferences.contains(MainActivity.ID_USER_EMAIL)){
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
                http.setRequestProperty("X-API-TOKEN", mPreferences.getString(MainActivity.ID_AUTH_TOKEN, ""));
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



    private void destroySession() {
        Log.d(TAG, "CLICK");
        LogoutTask logoutTask = new LogoutTask(MainActivity.this);
        logoutTask.setMessageLoading("Signing Out...");
        logoutTask.setAuthToken(mPreferences.getString(MainActivity.ID_AUTH_TOKEN, ""));
        logoutTask.execute(MainActivity.LOGOUT_URL);
    }

    private class LogoutTask extends UrlJsonAsyncTask {
        public LogoutTask(Context context) {
            super(context);
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
                    delete.setHeader("X-API-TOKEN", mPreferences.getString(MainActivity.ID_AUTH_TOKEN,""));

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
                    editor.remove(MainActivity.ID_AUTH_TOKEN);
                    editor.remove(MainActivity.ID_USER_EMAIL);
                    editor.commit();
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
        // Login + Register
        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
        startActivityForResult(intent, 0);
    }

    /**
     * Save a location/key pair.
     *
     * @param key the key associated with the location
     * @param location the location for the key
     * @return true if saved successfully false otherwise
     */
    public boolean saveLocation(String key, Location location) {
        try{
            SharedPreferences.Editor prefsEditor = mPreferences.edit();
            Gson gson = new Gson();
            String json = gson.toJson(location);
            prefsEditor.putString(key, json);
            prefsEditor.commit();
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets location data for a key.
     *
     * @param key the key for the saved location
     * @return a {@link Location} object or null if there is no entry for the key
     */
    public Location getLocation(String key) {
        Gson gson = new Gson();
        String json = mPreferences.getString(key, "");
        Location location = gson.fromJson(json, Location.class);
        return location;
    }

    @Override
    public void onBackPressed() {
        finish();
    }





}

