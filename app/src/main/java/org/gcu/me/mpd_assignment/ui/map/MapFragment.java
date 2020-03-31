package org.gcu.me.mpd_assignment.ui.map;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;

import org.gcu.me.mpd_assignment.MainActivity;
import org.gcu.me.mpd_assignment.R;
import org.gcu.me.mpd_assignment.models.Traffic;
import org.gcu.me.mpd_assignment.models.georss.Point;
import org.gcu.me.mpd_assignment.ui.index.IndexFragment;
import org.gcu.me.mpd_assignment.ui.index.list.TrafficListFragment;
import org.gcu.me.mpd_assignment.ui.loader.LoaderFragment;
import org.gcu.me.mpd_assignment.ui.loader.LoaderViewModel;


import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private LoaderFragment loaderFragment;
    private MapViewModel mapViewModel;
    private List<Traffic> traffic;
    private GoogleMap mMap;


    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        if (traffic == null){ //then get the traffic
            loaderFragment = new LoaderFragment(this, new LoaderViewModel.OnLoadingCompleteListener() {
                @Override
                public void onLoadingComplete(List<Traffic> result) {
                    //replace loaderfragment with this instance
                    traffic = result;
                    getActivity().runOnUiThread(() -> {
                        getFragmentManager().beginTransaction()
                                .replace(loaderFragment.getId(), getOuter())
                                .commit();
                    });
                    //build the view
                }
            });
            //show the loader
            getActivity().runOnUiThread(() -> {
                getFragmentManager().beginTransaction()
                        .replace(this.getId(), loaderFragment)
                        .commit();
            });
        } else{
            mapFragment.getMapAsync(this);
        }

        int orientation = this.getResources().getConfiguration().orientation;
        MainActivity activity = (MainActivity) getActivity();
        BottomNavigationView nav = activity.getNavView();
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // code for landscape mode
            //inner fragment creation

            Fragment trafficListFragment = new TrafficListFragment(new TrafficListFragment.ListFragmentListener() {
                @Override
                public List<Traffic> getTrafficList() {
                    return traffic;
                }
            });

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            transaction.replace(R.id.trafficlist_fragment_container, trafficListFragment).commit();
            nav.setVisibility(View.GONE);
        } else{
            nav.setVisibility(View.VISIBLE);
        }
        return root;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof IndexFragment.ListFragmentListener){

        } else{
            throw new RuntimeException(context.toString() + " must implement ListFragmentListener");
        }
    }

    private MapFragment getOuter(){
        return this;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney, Australia, and move the camera.
        LatLng uk = new LatLng(55.860916, -4.251433);
        for (Traffic t : traffic) {
            Point p = (Point) t.getLocation();
            LatLng loc = new LatLng(p.getLat(), p.getLon());
            mMap.addMarker(new MarkerOptions().position(loc).title(t.getTitle()));
        }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(uk));
    }
}