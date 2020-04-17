package org.gcu.me.mpd_assignment.ui.loader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.gcu.me.mpd_assignment.MainActivity;
import org.gcu.me.mpd_assignment.R;
import org.gcu.me.mpd_assignment.models.Incident;
import org.gcu.me.mpd_assignment.models.PlannedRoadworks;
import org.gcu.me.mpd_assignment.models.Roadworks;
import org.gcu.me.mpd_assignment.models.Traffic;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

public class LoaderFragment extends Fragment {
    private LoaderViewModel loaderViewModel;
    private Fragment replacement;
    private LoaderViewModel.OnLoadingCompleteListener onLoadingCompleteListener;
    private Class<?> trafficType;
    private boolean force;

    public LoaderFragment(Class<?> trafficType, boolean force, Fragment replacement, LoaderViewModel.OnLoadingCompleteListener onLoadingCompleteListener){
        super();
        this.onLoadingCompleteListener = onLoadingCompleteListener;
        this.replacement = replacement;
        this.trafficType = trafficType;
        this.force = force;
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.trafficType = ((MainActivity) this.getActivity()).getTrafficType(); //TODO TEST

        loaderViewModel = ViewModelProviders.of(this).get(LoaderViewModel.class);
        loaderViewModel.attachListener(this.trafficType, this.force, onLoadingCompleteListener);
        View root = inflater.inflate(R.layout.fragment_loader, container, false);

        final ProgressBar downloadProgressBar = root.findViewById(R.id.prog_repoLoader);
        final ProgressBar buildProgressBar = root.findViewById(R.id.prog_build);
        final LinearLayout progressGroup = root.findViewById(R.id.lay_prog);

        //on download progress update, change the progressbar
        loaderViewModel.getDownloadProgress().observe(this, progress -> downloadProgressBar.setProgress((int)Math.round(progress)));

        //on build progress update, change the progressbar
        loaderViewModel.getBuildProgress().observe(this, progress -> buildProgressBar.setProgress((int)Math.round(progress)));

        //on completion of loading, hide the progress sections
        loaderViewModel.getCompletedProgress().observe(this, new Observer<Boolean>() {
            private void fadeSwap(Fragment f1, Fragment f2){
                //transition back to this page
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left);
                ft.replace(f1.getId(), f2);
                // Start the animated transition.
                ft.commit();
            }
            @Override
            public void onChanged(Boolean completed) {
                if (completed){
                    //onLoadingCompleteListener.onLoadingComplete();
                    //fadeSwap(getOuter(), replacement);
                } else{
                    //fadeSwap(replacement, getOuter());
                }
            }
        });
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private LoaderFragment getOuter(){
        return this;
    };

    public LoaderViewModel getLoaderViewModel() {
        return loaderViewModel;
    }
}
