package com.diatracker.ui.glucose;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GlucoseViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GlucoseViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}