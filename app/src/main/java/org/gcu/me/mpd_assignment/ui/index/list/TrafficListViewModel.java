package org.gcu.me.mpd_assignment.ui.index.list;

import org.gcu.me.mpd_assignment.models.Traffic;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class TrafficListViewModel extends ViewModel {
    private LiveData<List<Traffic>> traffic;

    public TrafficListViewModel() {

    }
}