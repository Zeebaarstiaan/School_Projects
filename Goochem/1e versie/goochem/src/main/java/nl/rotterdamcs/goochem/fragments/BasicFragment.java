package nl.rotterdamcs.goochem.fragments;

import nl.rotterdamcs.goochem.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BasicFragment extends Fragment {
	
	private int mLayoutRes = -1;
	
	public BasicFragment() {
		this(R.layout.fragment_about);
	}
	
	public BasicFragment(int layoutRes) {
        mLayoutRes = layoutRes;
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (savedInstanceState != null)
            mLayoutRes = savedInstanceState.getInt("mColorRes");

        View rootView = inflater.inflate(mLayoutRes, container, false);
		return rootView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("mLayoutRes", mLayoutRes);
	}
	
}
