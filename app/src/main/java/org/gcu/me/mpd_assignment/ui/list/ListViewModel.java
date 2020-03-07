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

    private MutableLiveData<Double> mDownloadProg, mBuildProg;
    private MutableLiveData<Boolean> mCompletedProg;

    public ListViewModel() {
        mDownloadProg = new MutableLiveData<>();
        mBuildProg = new MutableLiveData<>();
        mCompletedProg = new MutableLiveData<>();

        //set the completed action to initiate at false;
        mCompletedProg.postValue(false);

        Roadworks.load(new TrafficRepo.BuilderTask.TaskListener() {

            @Override
            public void onFinished(List<Traffic> result) {
                mCompletedProg.postValue(true);
            }

            @Override
            public void onDownloadProgress(Double progress) {
                mDownloadProg.postValue(progress);
            }

            @Override
            public void onBuildProgress(Double progress) {
                mBuildProg.postValue(progress);
            }
        }, false);
    }

    public LiveData<Double> getDownloadProgress() {
        return mDownloadProg;
    }
    public MutableLiveData<Double> getBuildProgress() { return mBuildProg; }
    public MutableLiveData<Boolean> getCompletedProgress() { return mCompletedProg; }
}