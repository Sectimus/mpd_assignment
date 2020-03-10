package org.gcu.me.mpd_assignment.ui.map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.Loader;

import org.gcu.me.mpd_assignment.R;
import org.gcu.me.mpd_assignment.models.Roadworks;
import org.gcu.me.mpd_assignment.models.Traffic;
import org.gcu.me.mpd_assignment.repositories.TrafficRepo;
import org.gcu.me.mpd_assignment.ui.loader.LoaderFragment;
import org.gcu.me.mpd_assignment.ui.loader.LoaderViewModel;

import java.util.List;

public class MapFragment extends Fragment {
    private LoaderFragment loaderFragment;
    private MapViewModel mapViewModel;



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        mapViewModel.getText().observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        if (!TrafficRepo.isLoaded()){
            loaderFragment = new LoaderFragment(this, new LoaderViewModel.OnLoadingCompleteListener() {
                @Override
                public void onLoadingComplete(List<Traffic> result) {
                    build(result);
                }
            });
            getFragmentManager().beginTransaction()
                    .replace(this.getId(), loaderFragment)
                    .commit();
        }

        return root;
    }

    private void build(List<Traffic> data){

        //builds the stuff using the data
        //replace loaderfragment with this instance
        getFragmentManager().beginTransaction()
                .replace(loaderFragment.getId(), this)
                .commit();
    }
}