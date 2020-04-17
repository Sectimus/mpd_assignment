package org.gcu.me.mpd_assignment.ui.map;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

import org.gcu.me.mpd_assignment.MainActivity;
import org.gcu.me.mpd_assignment.R;
import org.gcu.me.mpd_assignment.models.PlannedRoadworks;
import org.gcu.me.mpd_assignment.models.Roadworks;
import org.gcu.me.mpd_assignment.models.Traffic;
import org.gcu.me.mpd_assignment.models.georss.Point;
import org.gcu.me.mpd_assignment.ui.index.IndexFragment;
import org.gcu.me.mpd_assignment.ui.index.list.TrafficListFragment;
import org.gcu.me.mpd_assignment.ui.loader.LoaderFragment;
import org.gcu.me.mpd_assignment.ui.loader.LoaderViewModel;


import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private LoaderFragment loaderFragment;
    private MapViewModel mapViewModel;
    private List<Traffic> traffic;
    private GoogleMap mMap;
    //hud
    private ConstraintLayout hud;
    private TextView location, distance, length;
    //side view
    private Fragment trafficlist;

    private Class<?> trafficType;

    //orientation used to change select operation
    private int orientation;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if(this.trafficType != null){
            outState.putString("lastTrafficType", this.trafficType.getName());
        }
    }

    private void loadifneeded(Bundle savedInstanceState, SupportMapFragment mapFragment, View root, boolean isCallback){
        boolean force = false;

        //set the traffic to null if the lastTrafficType is different to the current one.
        if(savedInstanceState != null){
            String lastTrafficType = savedInstanceState.getString("lastTrafficType");
            if(lastTrafficType != this.trafficType.getName()){
                this.traffic = null;
                force = true;
            }
        }

        MapFragment mf = this;
        //if its a view callback
        if(isCallback){
            mf = new MapFragment();
            mf.setArguments(this.getArguments());
            //set traffic to null for the reset
            traffic = null;
            force = true;
        }
        System.out.println("a");

        if (traffic == null){ //then get the traffic
            MapFragment finalMf = mf;
            loaderFragment = new LoaderFragment(trafficType, force, finalMf, new LoaderViewModel.OnLoadingCompleteListener() {
                @Override
                public void onLoadingComplete(List<Traffic> result) {
                    //replace loaderfragment with this instance
                    finalMf.traffic = result;
                    loaderFragment.getActivity().runOnUiThread(() -> {
                        loaderFragment.getFragmentManager().beginTransaction()
                                .replace(loaderFragment.getId(), finalMf)
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

        this.orientation = this.getResources().getConfiguration().orientation;
        MainActivity activity = (MainActivity) getActivity();
        BottomNavigationView nav = activity.getNavView();

        if (this.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            // code for landscape mode
            //inner fragment creation

            Fragment trafficListFragment = new TrafficListFragment(new TrafficListFragment.ListFragmentListener() {
                @Override
                public List<Traffic> getTrafficList() {
                    return traffic;
                }
            });

            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            this.trafficlist = trafficListFragment;
            transaction.replace(R.id.trafficlist_fragment_container, trafficListFragment).commit();
            nav.setVisibility(View.GONE);
        } else{
            //hud info
            this.hud = root.findViewById(R.id.map_hud);
            this.location = (TextView) hud.findViewById(R.id.location);
            this.distance = (TextView) hud.findViewById(R.id.txt_dist);
            this.length = (TextView) hud.findViewById(R.id.txt_time);

            //show the nav
            nav.setVisibility(View.VISIBLE);
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.trafficType = ((MainActivity) getActivity()).getTrafficType();

        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        ((MainActivity) getActivity()).setActionBarListener(new MainActivity.ActionBarListener() {
            @Override
            public void onFeedChanged() {
                loadifneeded(savedInstanceState, mapFragment, root, true);
            }
        });

        loadifneeded(savedInstanceState, mapFragment, root, false);

        //set the loaded
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

        for (Traffic t : traffic) {
            Point p = (Point) t.getLocation();
            LatLng loc = new LatLng(p.getLat(), p.getLon());

            MarkerOptions markeropt = new MarkerOptions().position(loc).title(t.getTitle());
            Marker m = mMap.addMarker(markeropt);
            //set the tag as this traffic object to be used on the click
            m.setTag(t);
        }
        //set the onclick handler
        mMap.setOnMarkerClickListener(this);

        //set the traffic view to enabled so the user can judge current traffic
        mMap.setTrafficEnabled(true);

        // Move camera to UK
        LatLng uk = new LatLng(56.4907, -4.2026);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(uk, 7.0f));
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        //get the traffic object associated with this marker
        Traffic t = (Traffic) marker.getTag();

        //set the hud data
        this.setSelectedData(t);

        //return false to enable the default behaviour
        return false;
    }

    private void setSelectedData(Traffic t){
        if (this.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RecyclerView recyclerView = (RecyclerView) this.trafficlist.getView().findViewById(R.id.rec_list);
            int index = traffic.indexOf(t);

            //create the smoothscroller to be used with the sidebar recyclerview, this allows the index of the recylcerobject to snap to the top as opposed to anywhere in view.
            RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(getContext()) {
                @Override protected int getVerticalSnapPreference() {
                    return LinearSmoothScroller.SNAP_TO_START;
                }
            };

            smoothScroller.setTargetPosition(index);
            recyclerView.getLayoutManager().startSmoothScroll(smoothScroller);
        } else{
            this.hud.setVisibility(View.VISIBLE);
            if(t instanceof Roadworks){
                Roadworks r = (Roadworks) t;

                //place the title
                this.location.setText(r.getTitle());

                //place the formatted length
                this.length.setText(r.getDurationAsString());
                //check the length and color appropriately
                long seconds = r.getDuration();
                if (seconds <= 86400){ //less than or equal to a day
                    this.length.setTextColor(getResources().getColor(R.color.length1,null));
                }
                if(seconds > 86400 && seconds <= 604800){ //more than a day but less than or equal to a week
                    this.length.setTextColor(getResources().getColor(R.color.length2,null));
                }
                if(seconds > 604800 && seconds <= 1209600){ //more than a week but less than or equal to a fortnight
                    this.length.setTextColor(getResources().getColor(R.color.length3,null));
                }
                if(seconds > 1209600 && seconds <= 2628000){ //more than a fortnight but less than or equal to a month
                    this.length.setTextColor(getResources().getColor(R.color.length4,null));
                }
                if(seconds > 2628000){ //more than a month
                    this.length.setTextColor(getResources().getColor(R.color.length5,null));
                }
            } else if(false){

            }
        }

    }
}