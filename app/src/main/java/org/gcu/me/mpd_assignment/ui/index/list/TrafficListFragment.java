package org.gcu.me.mpd_assignment.ui.index.list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.gcu.me.mpd_assignment.R;
import org.gcu.me.mpd_assignment.models.Traffic;
import org.gcu.me.mpd_assignment.ui.index.IndexFragment;
import org.gcu.me.mpd_assignment.ui.index.IndexViewModel;
import org.gcu.me.mpd_assignment.ui.loader.LoaderFragment;

import java.util.LinkedList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class TrafficListFragment extends Fragment {

    private IndexViewModel indexViewModel;
    private LoaderFragment loaderFragment;
    private RecyclerView recyclerView;
    private ListAdapter listAdapter;
    private List<Traffic> traffic;
    private ListFragmentListener listener;

    public interface ListFragmentListener{
        //default implementation of callback as it is not required
        default void onListItemSelected(Traffic t){}
        List<Traffic> getTrafficList();
    }

    public TrafficListFragment(ListFragmentListener listener) {
        this.listener = listener;
    }

    public TrafficListFragment() {
        this.listener = () -> new LinkedList<>();
    }



    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        indexViewModel = ViewModelProviders.of(this).get(IndexViewModel.class);
        View root = inflater.inflate(R.layout.fragment_trafficlist, container, false);

        this.traffic = listener.getTrafficList();
        this.recyclerView = (RecyclerView) root;

        fillRecycler();
        return root;
    }

    private void fillRecycler(){
        //setup the recycler adapter
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        listAdapter = new ListAdapter(this, traffic, new ListAdapter.ListAdapterListener() {
            @Override
            public void onListItemClicked(Traffic t) {
                listener.onListItemSelected(t);
            }
        });
        recyclerView.setAdapter(listAdapter);

        //setup the spinner adapter
    }

    @Override
    public void onAttach(@NonNull Context context) {
        System.out.println(context);
        super.onAttach(context);
        if (context instanceof IndexFragment.ListFragmentListener){
        } else{
            throw new RuntimeException(context.toString() + " must implement ListFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private TrafficListFragment getOuter(){
        return this;
    }
}
