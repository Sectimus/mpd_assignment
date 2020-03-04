package org.gcu.me.mpd_assignment.ui.home;

import org.gcu.me.mpd_assignment.models.Roadworks;
import org.gcu.me.mpd_assignment.models.Traffic;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<Double> mProgress;

    public HomeViewModel() {
        mProgress = new MutableLiveData<>();

        Roadworks.load(new Traffic.LoaderTask.TaskListener() {
            @Override
            public void onFinished(String result) {
                System.out.println("YURIKA "+result);
            }

            @Override
            public void onStatusUpdate(Double progress) {
                mProgress.setValue(progress);
            }
        });

    }

    public LiveData<Double> getProgress() {
        return mProgress;
    }
}