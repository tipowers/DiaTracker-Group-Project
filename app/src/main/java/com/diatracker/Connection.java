package com.diatracker;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

public class Connection extends AsyncTask {
    private Activity sendMailActivity;
    public Connection(Activity activity) {
        sendMailActivity = activity;
    }

    @Override
    protected Object doInBackground(Object... args) {
        try {
            DiaTrackerEmail androidEmail = new DiaTrackerEmail("DietaryTracker@gmail.com",
                    "diatracker321", args[0].toString(), "Dietary Alert",
                    args[1].toString());
            androidEmail.createEmailMessage();
            androidEmail.sendEmail();
        } catch (Exception e) {
            Log.e("MAIL", e.getMessage(), e);
        }
        return null;
    }
}