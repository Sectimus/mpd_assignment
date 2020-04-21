/*Amelia Magee | S1828146*/


package org.gcu.me.mpd_assignment.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import org.gcu.me.mpd_assignment.MainActivity;
import org.gcu.me.mpd_assignment.R;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        //setup the default listener to ignore incoming feed changes until another tab is selected.
        ((MainActivity) getActivity()).setActionBarListener(new MainActivity.ActionBarListener() {
            @Override
            public void onFeedChanged() {
                //do nothing, but the method needs to be implemented
                //this will still propogate a change on a new fragment load.
            }
        });

        return root;
    }
}