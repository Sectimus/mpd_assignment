package org.gcu.me.mpd_assignment.ui.list;

import android.os.Build;

import org.gcu.me.mpd_assignment.models.Roadworks;
import org.gcu.me.mpd_assignment.models.Traffic;
import org.gcu.me.mpd_assignment.repositories.TrafficRepo;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ListViewModel extends ViewModel {
    private LiveData<List<Traffic>> traffic;

    public ListViewModel() {

    }
}