package nl.rotterdamcs.goochem.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import nl.rotterdamcs.goochem.R;

public class MenuFragment extends ListFragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

        // Menu Titles Array
        String[] item_titles = getResources().getStringArray(R.array.menu_titles);
        // Menu Icons Array
        TypedArray item_icons = getResources().obtainTypedArray(R.array.menu_icons);

        // Menu Adapter
        MenuAdapter adapter = new MenuAdapter(getActivity());
        for (int i = 0; i < item_titles.length; i++) {
            adapter.add(new Item(item_titles[i], item_icons.getResourceId(i, -1)));
        }

		setListAdapter(adapter);
	}

    /** Menu Item **/
    private class Item {
        public String tag;
        public int iconRes;
        public Item(String tag, int iconRes) {
            this.tag = tag;
            this.iconRes = iconRes;
        }
    }

    /** Menu Adapter **/
    public class MenuAdapter extends ArrayAdapter<Item> {
        public MenuAdapter(Context context) {
            super(context, 0);
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
            }
            ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
            icon.setImageResource(getItem(position).iconRes);
            TextView title = (TextView) convertView.findViewById(R.id.row_title);
            title.setText(getItem(position).tag);
            return convertView;
        }
    }

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		Fragment newContent = null;
		switch (position) {
		case 0:
			newContent = new DashboardFragment();
			break;
		case 1:
			newContent = new BasicFragment(R.layout.fragment_about);
			break;
        case 2:
            newContent = new ProfileFragment();
            break;
		}
		if (newContent != null)
			switchFragment(newContent);
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null){
            return;
        }

		if (getActivity() instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) getActivity();
			fca.switchContent(fragment);
		}
	}


}
