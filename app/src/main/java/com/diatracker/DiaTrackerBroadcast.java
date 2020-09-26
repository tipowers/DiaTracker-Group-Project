package com.diatracker;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DiaTrackerBroadcast extends BroadcastReceiver {

    public static String notiId = "notification-id";
    public static String noti = "notification";

    public void onReceive(Context context, Intent intent) {
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = intent.getParcelableExtra(noti);
        int id = intent.getIntExtra(notiId, 0);
        notificationManager.notify(id, notification);
    }
}
