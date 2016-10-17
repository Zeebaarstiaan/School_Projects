package nl.goochem;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import nl.goochem.fragments.FragmentChangeActivity;
import nl.goochem.helpers.UrlJsonAsyncTask;

public class LocationService extends Service {

    private static final String TAG = "nl.goochem.LocationService";
    // Notification related stuff
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    public static final int ID_NOTIFICATION_ID = 1;
    public static final String ID_COMMUNICATION_INTENT_LOCATION = "communicationIntent";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private static final int ONE_MINUTE = 1000 * 60;


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
    private Location mLocation;

    private SharedPreferences mPreferences;


    public LocationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("---", "SERVICE START");

        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Settings
        mPreferences = getSharedPreferences(BaseActivity.ID_SETTINGS, Context.MODE_PRIVATE);
        Boolean showNotification = mPreferences.getBoolean("show_notification_preference",true);

        if (showNotification){
            // Create Notification
            Intent dashboardIntent = new Intent(this, FragmentChangeActivity.class);
            PendingIntent pendingDashboardIntent = PendingIntent.getActivity(this, 0, dashboardIntent, 0);
            mNotificationBuilder = new NotificationCompat.Builder(this)
                    .setWhen(0)
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText("Goochem houdt je op de hoogte!")
                    .setOngoing(true)
                    .setAutoCancel(false)
                    .setContentIntent(pendingDashboardIntent);
            startForeground(ID_NOTIFICATION_ID,mNotificationBuilder.build());
        }

        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Location Listener
        // Define a listener that responds to location updates
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                // makeUseOfNewLocation(location);
                Log.d(TAG,"LOCATION FOUND!");
//                locationManager.removeUpdates(this);
//                Log.d(TAG,"LISTENER DISABLED!");

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
        getLocation();

    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "STOP LOCATIONLISTENER");
        locationManager.removeUpdates(locationListener);
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    /** Set needed location providers and acquire new location */
    private void getLocation() {
        // Check if Location Services are enabled
        boolean locationEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        // Check if GPS is enabled
        boolean gpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        String locationPreferences = mPreferences.getString("location_preference","");
        Integer location_update_interval = Integer.parseInt(mPreferences.getString("location_interval_preference", "5000"));

        if (locationPreferences.equals("auto") || locationPreferences.equals("")){
            if(gpsEnabled){
                highAccuracyProvider();
            } else{
                batterySavingProvider();
            }
        } else if (locationPreferences.equals("network")){
            batterySavingProvider();
        } else if (locationPreferences.equals("gps")){
            highAccuracyProvider();
        }



        // Get location if provider is set and warnings are disabled
        if (locationProvider != null) {
            Log.d(TAG, "Using provider: " + locationProvider);

            // Get last know location
            lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

            // Request new location update
            locationManager.requestLocationUpdates(locationProvider,location_update_interval,0,locationListener);
        }
    }

    private void highAccuracyProvider(){
        // Use High Accuracy criteria if GPS is enabled
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);

        locationProvider = locationManager.getBestProvider(criteria,true);
    }

    private void batterySavingProvider(){
        // Use Battery Saving criteria if GPS is disabled
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);

        locationProvider = locationManager.getBestProvider(criteria,false);
    }

    /** Sets current latitude and longitude in the appropriate textviews
     * @param location  The new Location that you want the latitude and longitude from
     */
    private void setLongLat(Location location) {
        saveLocation(BaseActivity.ID_AUTO_LOCATION, location);
        mLocation = location;
        pushLocationFromAPI(BaseActivity.NEW_LOCATION_URL);

        double longitude = location.getLongitude();
        Log.v(TAG, "Longitude: " +longitude);

        double latitude = location.getLatitude();
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

    private void pushLocationFromAPI(String newLocationUrl) {
        if (mLocation != null){
            PushLocationTask pushLocation = new PushLocationTask(false, getApplication().getApplicationContext());
            pushLocation.setAuthToken(mPreferences.getString(BaseActivity.ID_AUTH_TOKEN, ""));
            pushLocation.execute(newLocationUrl);
        }
    }

    private class PushLocationTask extends UrlJsonAsyncTask {
        public PushLocationTask(Boolean showProgress, Context context) {
            super(showProgress,context);
        }

        @Override
        protected JSONObject doInBackground(String... urls) {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpPut put = new HttpPut(urls[0]);
            JSONObject holder = new JSONObject();
            JSONObject userObject = new JSONObject();
            String response = null;
            JSONObject json = new JSONObject();

            if (mLocation != null) {
                double longitude = mLocation.getLongitude();
                double latitude = mLocation.getLatitude();
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
                        put.setHeader("X-API-TOKEN", mPreferences.getString(BaseActivity.ID_AUTH_TOKEN,""));

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
                if (json.getBoolean("success")){
                    Log.d(TAG, "LOCATION PUSH SUCCESSFUL");
                } else {
                    pushLocationFromAPI(BaseActivity.NEW_LOCATION_URL);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                super.onPostExecute(json);
            }
        }
    }
}
