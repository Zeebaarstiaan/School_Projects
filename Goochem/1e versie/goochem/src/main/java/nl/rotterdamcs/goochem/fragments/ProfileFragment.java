package nl.rotterdamcs.goochem.fragments;



import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import java.util.Arrays;

import nl.rotterdamcs.goochem.BaseActivity;
import nl.rotterdamcs.goochem.R;
import nl.rotterdamcs.goochem.introduction.IntroductionActivity;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 *
 */
public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private UiLifecycleHelper uiHelper;
    private TextView userInfoTextView;
    private SharedPreferences settings;

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    private String buildUserInfoDisplay(GraphUser user) {
        StringBuilder userInfo = new StringBuilder("");

        // Example: typed access (name)
        // - no special permissions required
        userInfo.append(String.format("Name: %s\n\n",
                user.getName()));

        // Example: typed access (birthday)
        // - requires user_birthday permission
        userInfo.append(String.format("Birthday: %s\n\n",
                user.getBirthday()));

        // Example: partially typed access, to location field,
        // name key (location)
        // - requires user_location permission
        userInfo.append(String.format("Location: %s\n\n",
                user.getLocation().getProperty("name")));

        // Example: access via property name (locale)
        // - no special permissions required
        userInfo.append(String.format("Locale: %s\n\n",
                user.getProperty("locale")));

        return userInfo.toString();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiHelper = new UiLifecycleHelper(getActivity(), callback);
        uiHelper.onCreate(savedInstanceState);
    }

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        settings = getActivity().getSharedPreferences(BaseActivity.ID_SETTINGS, Context.MODE_PRIVATE);

        // Get view
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        userInfoTextView = (TextView) rootView.findViewById(R.id.userInfoTextView);

        LoginButton authButton = (LoginButton) rootView.findViewById(R.id.authButton);
        authButton.setReadPermissions(Arrays.asList("user_location", "user_birthday", "user_likes"));
        authButton.setFragment(this);

        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            userInfoTextView.setVisibility(View.VISIBLE);
            // Request user data and show the results
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        // Display the parsed user info
                        userInfoTextView.setText(buildUserInfoDisplay(user));
                    }
                }
            });
        }

        return rootView;
    }

    /** Exit introduction */
    public void enableIntroduction() {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(BaseActivity.ID_SHOW_INTRODUCTION, true);
        editor.commit();

        Intent i = new Intent(getActivity(), IntroductionActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            Log.i(TAG, "Logged in... SECOND");
            userInfoTextView.setVisibility(View.VISIBLE);
            // Request user data and show the results
            Request.executeMeRequestAsync(session, new Request.GraphUserCallback() {

                @Override
                public void onCompleted(GraphUser user, Response response) {
                    if (user != null) {
                        // Display the parsed user info
                        userInfoTextView.setText(buildUserInfoDisplay(user));
                    }
                }
            });
        } else if (state.isClosed()) {
            Log.i(TAG, "Logged out... SECOND");
            userInfoTextView.setVisibility(View.INVISIBLE);

           enableIntroduction();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Session.getActiveSession().onActivityResult(getActivity(), requestCode, resultCode, data);
        if (Session.getActiveSession() == null || Session.getActiveSession().isClosed()){
            enableIntroduction();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // For scenarios where the main activity is launched and user
        // session is not null, the session state change notification
        // may not be triggered. Trigger it if it's open/closed.
        Session session = Session.getActiveSession();
        if (session != null &&
                (session.isOpened() || session.isClosed()) ) {
            onSessionStateChange(session, session.getState(), null);
        }

        uiHelper.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
    }


}
