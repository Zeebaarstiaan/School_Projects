package nl.goochem.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import nl.goochem.BaseActivity;
import nl.goochem.R;
import nl.goochem.User;
import nl.goochem.helpers.UrlJsonAsyncTask;


public class DashboardFragment extends Fragment {
    private Context mContext;
    private SlidingMenu menu;
    private SharedPreferences mPreferences;
    private Integer mSearchRadius;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = ((BaseActivity)getActivity()).getSlidingMenu();
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get view
        View rootView = inflater.inflate(R.layout.fragment_dashboard, container, false);
        getActivity().setTitle(getString(R.string.fragment_dashboard));

        // Settings
        mPreferences = getActivity().getSharedPreferences(BaseActivity.ID_SETTINGS, Context.MODE_PRIVATE);
        String radius = mPreferences.getString("proximity_radius","0");

        TextView currentRadius = (TextView) rootView.findViewById(R.id.currentRadius);
        currentRadius.setText("Huidige Radius: "+radius+"m");

        Button sendButton = (Button) rootView.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getUsersFromAPI(BaseActivity.getProximityUrl());
            }
        });
        Button sendButton2 = (Button) rootView.findViewById(R.id.sendButton2);
        sendButton2.setVisibility(View.INVISIBLE);
//        sendButton2.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                stopService();
//            }
//        });

        return rootView;
    }

    private void stopService() {
        Log.d("---","Fragment STOP");
        ((FragmentChangeActivity)getActivity()).stopLocationService();
    }

    private void startService() {
        Log.d("---","Fragment START");
        ((FragmentChangeActivity)getActivity()).startLocationService();

    }

    private void getUsersFromAPI(String newLocationUrl) {
        if(mPreferences.contains(BaseActivity.ID_AUTO_LOCATION)) {
            mSearchRadius = Integer.parseInt(mPreferences.getString("proximity_radius",getResources().getString(R.string.meters_800)));
            ProximitySearchTask proximitySearch = new ProximitySearchTask(true, mContext);
            proximitySearch.setMessageLoading("Retrieving users...");
            proximitySearch.setAuthToken(mPreferences.getString(BaseActivity.ID_AUTH_TOKEN, ""));
            proximitySearch.execute(newLocationUrl);
        } else {
            Toast.makeText(mContext, "Nog geen locatie gevonden, probeer het nog eens", Toast.LENGTH_LONG).show();
        }
    }

    private class ProximitySearchTask extends UrlJsonAsyncTask {
        public ProximitySearchTask(Boolean showProgress, Context context) {
            super(showProgress, context);
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
                    dataObject.put("distance", mSearchRadius);
                    holder.put("data", dataObject);
                    StringEntity se = new StringEntity(holder.toString());
                    post.setEntity(se);

                    post.setHeader("Accept", "application/json");
                    post.setHeader("Content-Type", "application/json");
                    post.setHeader("X-API-TOKEN", mPreferences.getString(BaseActivity.ID_AUTH_TOKEN,""));

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

    private void customDialog(ArrayList<User> userArray) {
        ListAdapter adapter = new UserAdapter(mContext,
                android.R.layout.simple_list_item_1, userArray);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Close Users")
                .setCancelable(false)
                .setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        // Add the buttons
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
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
                LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            Toast.makeText(mContext, "User ID:" + String.valueOf(view.getTag()) , Toast.LENGTH_SHORT).show();
        }
    }
}
