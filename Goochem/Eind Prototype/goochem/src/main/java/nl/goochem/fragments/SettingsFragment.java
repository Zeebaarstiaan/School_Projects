package nl.goochem.fragments;



import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.goochem.BaseActivity;
import nl.goochem.R;

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    private Boolean changesMade;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle(getString(R.string.action_settings));

        PreferenceManager prefMgr = getPreferenceManager();
        prefMgr.setSharedPreferencesName(BaseActivity.ID_SETTINGS);
        prefMgr.setSharedPreferencesMode(Context.MODE_PRIVATE);

        prefMgr.getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        changesMade = false;

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.d("SETTINGS","Setting Changed!");
        changesMade = true;
    }

    @Override
    public void onStop() {
        if (changesMade){
            ((FragmentChangeActivity)getActivity()).restartLocationService();
        }
        super.onStop();
    }
}
