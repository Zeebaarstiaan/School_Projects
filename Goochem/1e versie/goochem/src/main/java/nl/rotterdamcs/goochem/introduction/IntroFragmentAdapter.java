package nl.rotterdamcs.goochem.introduction;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

import nl.rotterdamcs.goochem.R;
import nl.rotterdamcs.goochem.fragments.BasicFragment;
import nl.rotterdamcs.goochem.fragments.DashboardFragment;

class IntroFragmentAdapter extends FragmentPagerAdapter {
    private Context context;

    public IntroFragmentAdapter(FragmentManager fm, Context nContext) {
        super(fm);
        context = nContext;
    }

    @Override
    public Fragment getItem(int pos) {
        switch(pos) {
            case 0: return new IntroFragment();
            case 1: return new LoginFragment();
            default: return new IntroFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}