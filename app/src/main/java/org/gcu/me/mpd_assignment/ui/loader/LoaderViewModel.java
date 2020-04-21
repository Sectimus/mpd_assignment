/*Amelia Magee | S1828146*/


package org.gcu.me.mpd_assignment.ui.loader;

import org.gcu.me.mpd_assignment.models.Incident;
import org.gcu.me.mpd_assignment.models.PlannedRoadworks;
import org.gcu.me.mpd_assignment.models.Roadworks;
import org.gcu.me.mpd_assignment.models.Traffic;
import org.gcu.me.mpd_assignment.repositories.TrafficRepo;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoaderViewModel extends ViewModel {

    private MutableLiveData<Double> mDownloadProg, mBuildProg;
    private MutableLiveData<Boolean> mCompletedProg;
    private Class<?> trafficType;
    public OnLoadingCompleteListener loadingCompleteListener;

    public interface OnLoadingCompleteListener {
        void onLoadingComplete(List<Traffic> result);
    }

    public LoaderViewModel() {
        mDownloadProg = new MutableLiveData<>();
        mBuildProg = new MutableLiveData<>();
        mCompletedProg = new MutableLiveData<>();

        //set the completed action to initiate at false;
        mCompletedProg.postValue(false);
    }

    public void attachListener(Class<?> trafficType, boolean force, OnLoadingCompleteListener _loadingCompleteListener){
        this.loadingCompleteListener = _loadingCompleteListener;

        //setup the task listener
        TrafficRepo.BuilderTask.TaskListener tl = new TrafficRepo.BuilderTask.TaskListener() {

            @Override
            public void onFinished(List<Traffic> result) {
                mCompletedProg.postValue(true);
                loadingCompleteListener.onLoadingComplete(result);
            }

            @Override
            public void onDownloadProgress(Double progress) {
                mDownloadProg.postValue(progress);
            }

            @Override
            public void onBuildProgress(Double progress) {
                mBuildProg.postValue(progress);
            }
        };


        if(trafficType == Roadworks.class){
            Roadworks.load(tl, force);
        } else if(trafficType == PlannedRoadworks.class){
            PlannedRoadworks.load(tl, force);
        } else if(trafficType == Incident.class){
            Incident.load(tl, force);
        }

    }

    public LiveData<Double> getDownloadProgress() {
        return mDownloadProg;
    }
    public MutableLiveData<Double> getBuildProgress() { return mBuildProg; }
    public MutableLiveData<Boolean> getCompletedProgress() { return mCompletedProg; }
}
