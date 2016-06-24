package com.example.matiash.newyorktimessearchproject;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by matiash on 6/22/16.
 */
public class DatePickerFragment extends DialogFragment {
    DatePicker dp;

    public DatePickerFragment(){
    }

    public static DatePickerFragment newInstance() {
        DatePickerFragment datePickerFragment = new DatePickerFragment();
        return datePickerFragment;
    }

    public interface OnDatePickerFragmentSetDateListener {
        void OnFinishSetDate(DatePicker datePicker);
    }

        @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                //Do a bunch of stuff here
                dp = datePicker;
                sendBackResult();

            }
        };
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),listener,year,month,day);
        return datePickerDialog;
    }

    public void sendBackResult() {
        OnDatePickerFragmentSetDateListener listener = (OnDatePickerFragmentSetDateListener)getTargetFragment();
        listener.OnFinishSetDate(dp);
        dismiss();
    }
}
