package com.diatracker.ui.glucose;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.diatracker.DiaTrackerBroadcast;
import com.diatracker.DiaTrackerDB;
import com.diatracker.DiaTrackerMain;
import com.diatracker.R;

public class GlucoseFragment extends Fragment implements OnClickListener {
    private GlucoseViewModel glucoseViewModel;
    private Button submit;
    private EditText sugarLevel;
    private Button clear;
    private TextView date;
    private int enteredLevel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        glucoseViewModel =
                ViewModelProviders.of(this).get(GlucoseViewModel.class);
        View root = inflater.inflate(R.layout.fragment_glucose, container, false);
        final TextView textView = root.findViewById(R.id.text_glucose);
        submit = (Button) root.findViewById(R.id.buttonSubmit);
        clear = (Button) root.findViewById(R.id.buttonClear);
        sugarLevel = (EditText) root.findViewById(R.id.editSugar);
        date = (TextView) root.findViewById(R.id.textDate);
        date.setText(DiaTrackerMain.getDateStr());
        submit.setOnClickListener(this);
        clear.setOnClickListener(this);

        glucoseViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSubmit:
                addSugar();
                break;
            case R.id.buttonClear:
                sugarLevel.setText("");
                break;
            default:
                break;
        }
    }

    public void addSugar() {
        if(!sugarLevel.getText().toString().isEmpty()) {
            sugarLevel.setBackgroundResource(R.drawable.edit_normal);
            enteredLevel = Integer.parseInt(sugarLevel.getText().toString());

            if(enteredLevel > 180 || enteredLevel < 70) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                if(enteredLevel > 180) builder.setMessage(R.string.high_message).setTitle("High Blood Sugar");
                else builder.setMessage(R.string.low_message).setTitle("Low Blood Sugar");

                builder.setPositiveButton ("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
            scheduleNotification(getNotification("Reminder to check blood sugar levels"), 14400000); //4 hours

            DiaTrackerDB db = new DiaTrackerDB(getActivity());
            Boolean success = db.createGlucose(enteredLevel);
            if (success) {
                sugarLevel.setText("");
                Toast toast = Toast.makeText(getActivity(), "Sugar level successfully added", Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getActivity(), "There was an error", Toast.LENGTH_LONG);
                toast.show();
            }
        }
        else {
            sugarLevel.setError("Enter number");
            sugarLevel.setBackgroundResource(R.drawable.edit_error);
        }
    }

    private void scheduleNotification(Notification notification, int delay) {
        Intent notificationIntent = new Intent(getActivity(), DiaTrackerBroadcast.class);
        notificationIntent.putExtra(DiaTrackerBroadcast.notiId, 1);
        notificationIntent.putExtra(DiaTrackerBroadcast.noti, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        String CHANNEL_ID = "dia_channel";
        CharSequence name = "diachannel";
        int importance = NotificationManager.IMPORTANCE_HIGH;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription("DiaTracker channel");
        channel.enableVibration(true);
        channel.setShowBadge(false);
        notificationManager.createNotificationChannel(channel);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(), CHANNEL_ID);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.diabetes);
        return builder.build();
    }
}
