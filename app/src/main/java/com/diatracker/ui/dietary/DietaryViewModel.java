package com.diatracker.ui.dietary;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DietaryViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public DietaryViewModel() {
        mText = new MutableLiveData<>();
    }

    public LiveData<String> getText() {
        return mText;
    }
}