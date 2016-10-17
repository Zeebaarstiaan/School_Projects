package nl.goochem.introduction;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import nl.goochem.R;
import nl.goochem.fragments.BasicFragment;
import nl.goochem.fragments.DashboardFragment;

class IntroFragmentAdapter extends FragmentPagerAdapter {
    private Context mContext;
    private List<Fragment> mFragments = new ArrayList<Fragment>();

    public IntroFragmentAdapter(FragmentManager fm, Context nContext) {
        super(fm);
        mContext = nContext;
    }

    @Override
    public Fragment getItem(int pos) {
        switch(pos) {
            case 0: return new RegisterFragment(mContext);
            case 1: return new IntroFragment();
            case 2: return new LoginFragment(mContext);
            default: return new IntroFragment();
        }
    }



    @Override
    public int getCount() {
        return 3;
    }

}