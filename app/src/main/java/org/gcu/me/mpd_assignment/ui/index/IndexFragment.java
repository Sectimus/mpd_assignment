package org.gcu.me.mpd_assignment.ui.index;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.gcu.me.mpd_assignment.MainActivity;
import org.gcu.me.mpd_assignment.R;
import org.gcu.me.mpd_assignment.models.PlannedRoadworks;
import org.gcu.me.mpd_assignment.models.Traffic;
import org.gcu.me.mpd_assignment.ui.index.list.ListAdapter;
import org.gcu.me.mpd_assignment.ui.index.list.TrafficListFragment;
import org.gcu.me.mpd_assignment.ui.loader.LoaderFragment;
import org.gcu.me.mpd_assignment.ui.loader.LoaderViewModel;
import org.gcu.me.mpd_assignment.ui.map.MapFragment;

import java.util.LinkedList;
import java.util.List;

public class IndexFragment extends Fragment {

    private IndexViewModel indexViewModel;
    private LoaderFragment loaderFragment;
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private List<Traffic> traffic;
    private ListFragmentListener listener;

    public interface ListFragmentListener{
        void onListItemSelected(Traffic t);
    }

    private void loadifneeded(boolean isCallback){
        boolean force = false;

        IndexFragment mf = this;
        //if its a view callback
        if(isCallback){
            mf = new IndexFragment();
            mf.setArguments(this.getArguments());
            //set traffic to null for the reset
            traffic = null;
            force = true;
        }

        if (traffic == null){ //then get the traffic
            loaderFragment = new LoaderFragment(PlannedRoadworks.class, force, this, new LoaderViewModel.OnLoadingCompleteListener() {
                @Override
                public void onLoadingComplete(List<Traffic> result) {
                    //replace loaderfragment with this instance
                    traffic = result;

                    getFragmentManager().beginTransaction()
                            .replace(loaderFragment.getId(), getOuter())
                            .commit();


                    //build the view
                }
            });
            //show the loader

            getFragmentManager().beginTransaction()
                    .replace(this.getId(), loaderFragment)
                    .commit();

        } else{
            build();
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        boolean force = false;

        super.onCreate(savedInstanceState);
        indexViewModel = ViewModelProviders.of(this).get(IndexViewModel.class);
        View root = inflater.inflate(R.layout.fragment_index, container, false);

        ((MainActivity) getActivity()).setActionBarListener(new MainActivity.ActionBarListener() {
            @Override
            public void onFeedChanged() {
                loadifneeded(true);
            }
        });

        loadifneeded(false);

        return root;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //inner fragment creation
        Fragment trafficListFragment = new TrafficListFragment(new TrafficListFragment.ListFragmentListener() {
            @Override
            public List<Traffic> getTrafficList() {
                return traffic;
            }
        });
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.trafficlist_fragment_container, trafficListFragment).commit();
    }

    private void build(){
        //setup the spinner adapter

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof ListFragmentListener){
        } else{
            throw new RuntimeException(context.toString() + " must implement ListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private IndexFragment getOuter(){
        return this;
    }
}