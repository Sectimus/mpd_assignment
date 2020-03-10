package org.gcu.me.mpd_assignment.ui.list.item;

import android.media.Image;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ItemViewModel extends ViewModel {
    private MutableLiveData<String> mTitle, mDesc;
    private MutableLiveData<Integer> mImage;

    public ItemViewModel(){
        mTitle = new MutableLiveData<>();
        mDesc = new MutableLiveData<>();
        mImage = new MutableLiveData<>();
    }


    public MutableLiveData<String> getmTitle() { return mTitle; }
    public MutableLiveData<String> getmDesc() { return mDesc; }
    public MutableLiveData<Integer> getmImage() { return mImage; }
}
