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

    private MutableLiveData<Double> mProgress;

    public ListViewModel() {
        mProgress = new MutableLiveData<>();

        Roadworks.load(new TrafficRepo.BuilderTask.TaskListener() {

            @Override
            public void onFinished(List<Traffic> result) {
                System.out.println(result);
            }

            @Override
            public void onDownloadProgress(Double progress) {
                //mProgress.setValue(progress);
            }

            @Override
            public void onBuildProgress(Double progress) {
                mProgress.setValue(progress);
            }
        });
    }

    public LiveData<Double> getProgress() {
        return mProgress;
    }
}