package com.diatracker.ui.dietary;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.diatracker.Connection;
import com.diatracker.DiaTrackerDB;
import com.diatracker.DiaTrackerMain;
import com.diatracker.R;
import com.diatracker.DiaTrackerPrefs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DietaryFragment extends Fragment implements OnClickListener {

    private DietaryViewModel dietaryViewModel;
    private TextView date;
    private EditText calorie;
    private EditText carbs;
    private EditText sugar;
    private Button submit;
    private Button clear;
    private int enteredCalorie;
    private int enteredSugar;
    private int enteredCarbs;

    private SimpleDateFormat dateF = new SimpleDateFormat("MM/dd/yyyy");

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        dietaryViewModel =
                ViewModelProviders.of(this).get(DietaryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dietary, container, false);

        final TextView textView = root.findViewById(R.id.text_dietary);
        date = (TextView) root.findViewById(R.id.textDate);
        date.setText(DiaTrackerMain.getDateStr());
        submit = (Button) root.findViewById(R.id.buttonSubmit);
        clear = (Button) root.findViewById(R.id.buttonClear);
        calorie = (EditText) root.findViewById(R.id.editCalorie);
        carbs = (EditText) root.findViewById(R.id.editCarbs);
        sugar = (EditText) root.findViewById(R.id.editSugar);
        submit.setOnClickListener(this);
        clear.setOnClickListener(this);
        setEnabled();

        dietaryViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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
                addIntake();
                break;
            case R.id.buttonClear:
                calorie.setText("");
                carbs.setText("");
                sugar.setText("");
                break;
            default:
                break;
        }
    }

    public void addIntake() {
        EditText[] edits = {calorie, carbs, sugar};
        boolean error = false;
        String value = "";
        String highlow = "";

        for(int i=0;i<edits.length;i++) {
            if(edits[i].getText().toString().isEmpty()) {
                edits[i].setError("Enter number");
                edits[i].setBackgroundResource(R.drawable.edit_error);
                error = true;}
            else {
                edits[i].setBackgroundResource(R.drawable.edit_normal);
                error = false; }
        }
        if(!error) {
            enteredCalorie = Integer.parseInt(calorie.getText().toString());
            enteredCarbs = Integer.parseInt(carbs.getText().toString());
            enteredSugar = Integer.parseInt(sugar.getText().toString());
            if (enteredCalorie > 2500) {
                value = "calorie";
                highlow = "high";
            } else if (enteredCalorie < 1500) {
                value = "calorie";
                highlow = "low";
            } else if (enteredCarbs > 325) {
                value = "carbohydrate";
                highlow = "high";
            } else if (enteredCarbs < 225) {
                value = "carbohydrate";
                highlow = "low";
            } else if (enteredSugar > 45) {
                value = "sugar";
                highlow = "high";
            } else if (enteredSugar < 25) {
                value = "sugar";
                highlow = "low";
            }

            if (!value.isEmpty()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                String warning = getString(R.string.dietary_message, value, highlow);
                String email = getString(R.string.dietary_email, DiaTrackerPrefs.getName(getActivity()), value, highlow);
                builder.setMessage(warning).setTitle("Dietary Warning");

                builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                if(!DiaTrackerPrefs.isEmpty(getActivity()))
                    new Connection(getActivity()).execute(DiaTrackerPrefs.getEmail(getActivity()), email);
            }

            DiaTrackerDB db = new DiaTrackerDB(getActivity());
            Boolean success = db.createDiet(enteredCalorie, enteredCarbs, enteredSugar);
            if (success) {
                Date stopDate = new Date();
                Calendar c = Calendar.getInstance();
                c.setTime(stopDate);
                c.add(Calendar.DATE, 1);
                stopDate = c.getTime();
                String stopDateStr = dateF.format(stopDate);

                if (DiaTrackerPrefs.getDay(getActivity()).isEmpty()) {
                    DiaTrackerPrefs.setDay(getActivity(), stopDateStr);
                    setEnabled();
                }

                Toast toast = Toast.makeText(getActivity(), "Intake successfully added", Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getActivity(), "There was an error", Toast.LENGTH_LONG);
                toast.show();
            }
        }
        else {
            return;
        }
    }

    private void setEnabled() {
        EditText[] edits = {calorie, carbs, sugar};
        String prefDateStr = DiaTrackerPrefs.getDay(getActivity());
        try {
            Date prefDate = dateF.parse(prefDateStr);
            Date nowDate = new Date();
            Log.i("test", DiaTrackerPrefs.getDay(getActivity()));
            if (DiaTrackerPrefs.getDay(getActivity()).isEmpty() || nowDate.compareTo(prefDate) >= 0) {
                for (int i = 0; i < edits.length; i++) {
                    edits[i].setText("");
                    edits[i].setEnabled(true);
                    edits[i].setBackgroundResource(R.drawable.edit_normal);
                }
                submit.setClickable(true);
                if(prefDate.compareTo(nowDate) >= 0)
                    DiaTrackerPrefs.setDay(getActivity(), "");
            } else {
                for (int i = 0; i < edits.length; i++) {
                    edits[i].setText("");
                    edits[i].setEnabled(false);
                    edits[i].setBackgroundResource(R.color.colorGray);
                }
                submit.setClickable(false);
            }
        }
        catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
