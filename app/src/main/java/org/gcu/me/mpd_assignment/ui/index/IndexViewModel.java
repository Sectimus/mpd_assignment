/*Amelia Magee | S1828146*/


package org.gcu.me.mpd_assignment.ui.index;

import org.gcu.me.mpd_assignment.models.Traffic;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

public class IndexViewModel extends ViewModel {
    private LiveData<List<Traffic>> traffic;

    public IndexViewModel() {

    }
}