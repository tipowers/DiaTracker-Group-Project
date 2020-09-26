package com.diatracker.ui.insulin;

import android.os.Bundle;
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
import com.diatracker.DiaTrackerDB;
import com.diatracker.DiaTrackerMain;
import com.diatracker.R;

public class InsulinFragment extends Fragment implements OnClickListener {

    private InsulinViewModel insulinViewModel;
    private TextView date;
    private Button submit;
    private Button clear;
    private EditText insulinLevel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        insulinViewModel =
                ViewModelProviders.of(this).get(InsulinViewModel.class);
        View root = inflater.inflate(R.layout.fragment_insulin, container, false);

        final TextView textView = root.findViewById(R.id.text_insulin);
        date = (TextView) root.findViewById(R.id.textDate);
        date.setText(DiaTrackerMain.getDateStr());
        submit = (Button) root.findViewById(R.id.buttonSubmit);
        clear = (Button) root.findViewById(R.id.buttonClear);
        insulinLevel = (EditText) root.findViewById(R.id.editInsulin);
        submit.setOnClickListener(this);
        clear.setOnClickListener(this);

        insulinViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
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
                addInjection();
                break;
            case R.id.buttonClear:
                insulinLevel.setText("");
                break;
            default:
                break;
        }
    }

    private void addInjection() {
        if(!insulinLevel.getText().toString().isEmpty()) {
            DiaTrackerDB db = new DiaTrackerDB(getActivity());
            int enteredLevel = Integer.parseInt(insulinLevel.getText().toString());
            Boolean success = db.createInsulin(enteredLevel);
            if (success) {
                insulinLevel.setText("");
                Toast toast = Toast.makeText(getActivity(), "Injection successfully added", Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(getActivity(), "There was an error", Toast.LENGTH_LONG);
                toast.show();
            }
            insulinLevel.setBackgroundResource(R.drawable.edit_normal);
        }
        else {
            insulinLevel.setError("Enter number");
            insulinLevel.setBackgroundResource(R.drawable.edit_error);
        }
    }
}
