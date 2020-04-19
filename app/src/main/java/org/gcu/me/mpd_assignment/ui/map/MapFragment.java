package org.gcu.me.mpd_assignment.ui.map;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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

    //time period selection
    private Class<?> trafficType;
    private ImageView dateicon;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    //location selection
    Point chosenLocation;
    //radius selection
    EditText txt_radius;

    //map marker hashmap
    private HashMap<Traffic, Marker> markers;
    Button viewinmaps;



    private Calendar c;
    private Context ctx;

    //info for the reloader
    private Bundle savedInstanceState;
    private SupportMapFragment mapFragment;
    private View root;

    //checks the times of the markers if they overlap with the specified times
    private void checkMarkers(){

        Integer radius;
        try{
            radius = Integer.parseInt(txt_radius.getText().toString());
        } catch(NumberFormatException e){
            radius = null;
        }

        for(Map.Entry<Traffic, Marker> entry : markers.entrySet()) {
            //TODO not only roadworks
            Roadworks key = (Roadworks) entry.getKey();
            Marker marker = entry.getValue();

            boolean requirement1 = true;
            if(startDate != null && endDate != null && (startDate.isBefore(endDate) || startDate.isEqual(endDate))) {
                if ((startDate.isBefore(key.getEnd()) || startDate.isEqual(key.getEnd())) && (endDate.isAfter(key.getStart()) || endDate.isEqual(key.getStart()))) {
                    requirement1 = true;
                } else {
                    requirement1 = false;
                }
            }

            boolean requirement2 = true;
            if(chosenLocation != null && radius != null){
                float[] result = new float[2];
                Location.distanceBetween(chosenLocation.getLat(), chosenLocation.getLon(), marker.getPosition().latitude, marker.getPosition().longitude, result);

                if(result[0] <=radius){
                    requirement2 = true;
                } else{
                    requirement2 = false;
                }
            }

            marker.setVisible(requirement1 && requirement2);
        }
    }

    private void calendarClickHandler(){
        c = Calendar.getInstance();
        (new DatePickerDialog(ctx,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int startyear, int startmonth, int startday) {
                        //now get the start date
                        (new TimePickerDialog(ctx,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int starthour, int startminute) {
                                    //set the start date
                                    startDate = LocalDateTime.parse(startyear+"-"+(startmonth+1)+"-"+startday+" "+starthour+":"+startminute, DateTimeFormatter.ofPattern("yyyy-M-d H:m"));
                                    //now ask for the end date
                                    (new DatePickerDialog(ctx,
                                            new DatePickerDialog.OnDateSetListener() {
                                                @Override
                                                public void onDateSet(DatePicker view, int endyear, int endmonth, int endday) {
                                                    //now get the end date
                                                    (new TimePickerDialog(ctx,
                                                            new TimePickerDialog.OnTimeSetListener() {
                                                                @Override
                                                                public void onTimeSet(TimePicker view, int endhour, int endminute) {
                                                                    //set the end date
                                                                    endDate = LocalDateTime.parse(endyear+"-"+(endmonth+1)+"-"+endday+" "+endhour+":"+endminute, DateTimeFormatter.ofPattern("yyyy-M-d H:m"));
                                                                }
                                                            }, LocalDateTime.now().getHour(), LocalDateTime.now().getMinute(), true)).show();
                                                }
                                            }, LocalDateTime.now().plusDays(3).getYear(), LocalDateTime.now().plusDays(3).getMonthValue()-1, LocalDateTime.now().plusDays(3).getDayOfMonth())).show();
                                }
                            }, LocalDateTime.now().getHour(), LocalDateTime.now().getMinute(), true)).show();
                    }
                }, LocalDateTime.now().getYear(), LocalDateTime.now().getMonthValue()-1, LocalDateTime.now().getDayOfMonth())).show();
    }

    //orientation used to change select operation
    private int orientation;

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        if(this.trafficType != null){
            outState.putString("lastTrafficType", this.trafficType.getName());
        }
    }

    private void loadifneeded( boolean isCallback){
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

        this.dateicon = root.findViewById(R.id.calendarIcon);

        MapFragment finalMf = mf;
        this.dateicon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarClickHandler();
            }
        });


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
        ctx = this.getContext();

        this.trafficType = ((MainActivity) getActivity()).getTrafficType();

        mapViewModel = ViewModelProviders.of(this).get(MapViewModel.class);
        View root = inflater.inflate(R.layout.fragment_map, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        //setup for reloader
        this.savedInstanceState = savedInstanceState;
        this.mapFragment = mapFragment;
        this.root = root;
        ((MainActivity) getActivity()).setActionBarListener(new MainActivity.ActionBarListener() {
            @Override
            public void onFeedChanged() {
                loadifneeded(true);
            }
        });

        loadifneeded(false);

        // Initialize the AutocompleteSupportFragment.

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment) getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG));

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng test = place.getLatLng();
                chosenLocation = new Point(test.latitude, test.longitude);
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("MyTag", "An error occurred: " + status);
            }
        });

        //setup the radius handler
        txt_radius = root.findViewById(R.id.radius);

        //setup the form submit button click.
        ImageView btn_submit = root.findViewById(R.id.submitIcon);
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkMarkers();
            }
        });

        //setup the "view in google maps" button
        viewinmaps = root.findViewById(R.id.viewinmaps);

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
        //setup the marker hashmap
        markers = new HashMap<>();

        for (Traffic t : traffic) {
            //load only traffic that are in the time selected.
            Point p = (Point) t.getLocation();
            LatLng loc = new LatLng(p.getLat(), p.getLon());

            MarkerOptions markeropt = new MarkerOptions().position(loc).title(t.getTitle());
            Marker m = mMap.addMarker(markeropt);
            //add the marker to the hashmap
            markers.put(t, m);
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

        viewinmaps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", ((Point) t.getLocation()).getLat(), ((Point) t.getLocation()).getLon());
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                ctx.startActivity(intent);
            }
        });

    }
}