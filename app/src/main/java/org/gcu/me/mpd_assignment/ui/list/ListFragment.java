package org.gcu.me.mpd_assignment.ui.list;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.gcu.me.mpd_assignment.R;
import org.gcu.me.mpd_assignment.models.Roadworks;
import org.gcu.me.mpd_assignment.models.Traffic;
import org.gcu.me.mpd_assignment.repositories.TrafficRepo;
import org.gcu.me.mpd_assignment.ui.list.item.ItemFragment;
import org.gcu.me.mpd_assignment.ui.loader.LoaderFragment;
import org.gcu.me.mpd_assignment.ui.loader.LoaderViewModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListFragment extends Fragment {

    private ListViewModel listViewModel;
    private LoaderFragment loaderFragment;
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private List<Traffic> traffic;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listViewModel = ViewModelProviders.of(this).get(ListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_list, container, false);

        recyclerView = root.findViewById(R.id.rec_list);

        if (traffic == null){ //then get the traffic
            loaderFragment = new LoaderFragment(this, new LoaderViewModel.OnLoadingCompleteListener() {
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
            //SET EMPTY ADAPTER
            recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
            List<Traffic> test = new LinkedList<>();
            test.add(new Roadworks());
            listAdapter = new ListAdapter(new ItemFragment(), traffic);
            recyclerView.setAdapter(listAdapter);
        }


        return root;
    }

    private ListFragment getOuter(){
        return this;
    }
}