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

import org.gcu.me.mpd_assignment.R;

public class ListFragment extends Fragment {

    private ListViewModel listViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        listViewModel = ViewModelProviders.of(this).get(ListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        final ProgressBar downloadProgressBar = root.findViewById(R.id.prog_repoLoader);
        final ProgressBar buildProgressBar = root.findViewById(R.id.prog_build);
        final LinearLayout progressGroup = root.findViewById(R.id.lay_prog);
        final TextView test = root.findViewById(R.id.textView3);

        //on download progress update, change the progressbar
        listViewModel.getDownloadProgress().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double progress) {
                downloadProgressBar.setProgress((int)Math.round(progress));
            }
        });
        //on build progress update, change the progressbar
        listViewModel.getBuildProgress().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double progress) {
                buildProgressBar.setProgress((int)Math.round(progress));
            }
        });
        //on completion of loading, hide the progress sections
        listViewModel.getCompletedProgress().observe(this, new Observer<Boolean>() {
            // Retrieve and cache the system's default "short" animation time.
            private final int shortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
            private void fadeSwap(View v1, View v2){
                v2.setAlpha(0f);
                v2.setVisibility(View.VISIBLE);
                v2.animate()
                        .alpha(1f)
                        .setDuration(shortAnimationDuration)
                        .setListener(null);

                v1.animate()
                        .alpha(0f)
                        .setDuration(shortAnimationDuration)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                v1.setVisibility(View.GONE);
                            }
                        });
            }
            @Override
            public void onChanged(Boolean completed) {
                if (completed){
                    fadeSwap(progressGroup, test);
                } else{
                    fadeSwap(test, progressGroup);
                }
            }
        });
        return root;
    }
}