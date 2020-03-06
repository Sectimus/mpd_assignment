package org.gcu.me.mpd_assignment.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listViewModel =
                ViewModelProviders.of(this).get(ListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_list, container, false);
        final ProgressBar progressBar = root.findViewById(R.id.prog_repoLoader);

       listViewModel.getProgress().observe(this, new Observer<Double>() {
            @Override
            public void onChanged(Double progress) {
                progressBar.setProgress((int)Math.round(progress));
            }
        });
        return root;
    }
}