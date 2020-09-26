package com.diatracker.ui.insulin;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class InsulinViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public InsulinViewModel() { mText = new MutableLiveData<>(); }

    public LiveData<String> getText() {
        return mText;
    }
}