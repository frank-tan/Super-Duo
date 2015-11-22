package it.jaschke.alexandria.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import it.jaschke.alexandria.R;


public class AboutFragment extends Fragment {

    public AboutFragment(){

    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.about);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(R.string.about);
        return inflater.inflate(R.layout.fragment_about, container, false);
    }

}
