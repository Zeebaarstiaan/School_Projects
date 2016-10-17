package nl.goochem.introduction;

import nl.goochem.R;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class IntroFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_intro, container, false);

        Button introLoginButton = (Button) rootView.findViewById(R.id.introLoginButton);
        introLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IntroductionActivity)getActivity()).switchToPage(2);
            }
        });
        Button introRegisterButton = (Button) rootView.findViewById(R.id.introRegisterButton);
        introRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((IntroductionActivity)getActivity()).switchToPage(0);
            }
        });

        return rootView;
    }
}
