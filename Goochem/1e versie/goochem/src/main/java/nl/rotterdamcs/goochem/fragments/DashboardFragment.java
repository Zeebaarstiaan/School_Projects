package nl.rotterdamcs.goochem.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import java.util.ArrayList;

import nl.rotterdamcs.goochem.BaseActivity;
import nl.rotterdamcs.goochem.R;


public class DashboardFragment extends Fragment {
    // Tag
    public static final String TAG = "nl.rotterdamcs.goochem.fragments.DashboardFragment";
    private SlidingMenu menu;
    private ViewPager vp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menu = ((BaseActivity)getActivity()).getSlidingMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Get view
        View rootView = inflater.inflate(R.layout.fragment_pager, container, false);

        vp = (ViewPager) rootView.findViewById(R.id.viewPager);
        vp.setAdapter(new BasicPagerAdapter(getChildFragmentManager()));

        ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int arg0) { }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) { }

            @Override
            public void onPageSelected(int position) {
                Log.v("positions:", String.valueOf(position));
                switch (position) {
                    case 0:
                        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
                        break;
                    case 2:
                        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                        Button share_button = (Button) vp.findViewById(R.id.button_share);
                        share_button.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                jumpToPage(v);
                            }
                        });
                        break;
                    default:
                        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
                        break;
                }
            }
        };
        vp.setOnPageChangeListener(pageChangeListener);
        vp.setCurrentItem(0);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);

        return rootView;
    }

    public void jumpToPage(View view) {
        try
        {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String sAux = getString(R.string.share_message);
            sAux = sAux + getString(R.string.share_link);
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, getString(R.string.pick_one)));
        }
        catch(Exception e)
        {
            e.toString();
        }
    }

    public class BasicPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> mFragments;

        private final int[] LAYOUTS = new int[] {
                R.layout.fragment_dashboard_1,
                R.layout.fragment_dashboard_2,
                R.layout.fragment_dashboard_3
        };

        public BasicPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragments = new ArrayList<Fragment>();
            for (int layout : LAYOUTS)
                mFragments.add(new BasicFragment(layout));
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

    }

}
