package org.gcu.me.mpd_assignment;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.gcu.me.mpd_assignment.models.Incident;
import org.gcu.me.mpd_assignment.models.PlannedRoadworks;
import org.gcu.me.mpd_assignment.models.Roadworks;
import org.gcu.me.mpd_assignment.models.Traffic;
import org.gcu.me.mpd_assignment.ui.index.IndexFragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class MainActivity extends AppCompatActivity implements IndexFragment.ListFragmentListener {
    public interface ActionBarListener {
        void onFeedChanged();
    }
    private ActionBarListener actionBarListener;
    public void setActionBarListener(ActionBarListener actionBarListener){
        this.actionBarListener = actionBarListener;
    }

    private BottomNavigationView navView;
    //set initial value to show the roadworks
    private static Class<?> trafficType = Roadworks.class;

    public Class<?> getTrafficType(){
        return trafficType;
    }
    private void setTrafficType(Class<?> _trafficType){
        trafficType = _trafficType;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_options_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.plannedroadworks:{
                setTrafficType(PlannedRoadworks.class);
                break;
            }
            case R.id.currentroadworks:{
                setTrafficType(Roadworks.class);
                break;
            }
            case R.id.currentincidents:{
                setTrafficType(Incident.class);
                break;
            }
            default:{
                return super.onOptionsItemSelected(item);
            }
        }
        actionBarListener.onFeedChanged();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setup the places api
        Places.initialize(getApplicationContext(), "AIzaSyD9iqNWRFFy4SF-AeMCAxNWNCOGDjoBoT0");
        PlacesClient placesClient = Places.createClient(this);



        navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_map, R.id.navigation_index)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

    }

    public BottomNavigationView getNavView() {
        return navView;
    }

    public void setNavView(BottomNavigationView navView) {
        this.navView = navView;
    }

    @Override
    public void onListItemSelected(Traffic t) {

    }
}
