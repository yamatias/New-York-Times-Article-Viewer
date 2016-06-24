package com.example.matiash.newyorktimessearchproject.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.matiash.newyorktimessearchproject.DatePickerFragment;
import com.example.matiash.newyorktimessearchproject.R;

public class FiltersFragment extends DialogFragment implements DatePickerFragment.OnDatePickerFragmentSetDateListener{

    TextView tvDate;
    Spinner spSort;
    CheckBox cbArts, cbEducation, cbSports;
    Button bnFilterSearch;

    public FiltersFragment() {

    }

    public interface OnFinishFiltersClickListener {
        void OnFinishFiltersClick(Bundle bundle);
    }

    public static FiltersFragment newInstance(String title) {
        FiltersFragment filtersFragment = new FiltersFragment();

        Bundle args = new Bundle();
        args.putString("title",title);

        filtersFragment.setArguments(args);
        return filtersFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_filter, container);
        tvDate = (TextView)view.findViewById(R.id.tvDate);
        spSort = (Spinner)view.findViewById(R.id.spSort);
        cbArts = (CheckBox)view.findViewById(R.id.cbArts);
        cbEducation = (CheckBox)view.findViewById(R.id.cbEducation);
        cbSports = (CheckBox)view.findViewById(R.id.cbSports);
        bnFilterSearch = (Button)view.findViewById(R.id.bnFilterSearch);
        return view;

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        getDialog().setTitle(title);

        //Setting up the spinner adapter
        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(getContext(),R.array.sort_array,android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSort.setAdapter(spinnerAdapter);

        // Show soft keyboard automatically and request focus to field
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE); //We'll see what this does

        //Create a listener for the button
        bnFilterSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendBackResult();
            }
        });
    }

    public void runDatePicker(View view) {
        view.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.image_click));
        FragmentManager fm = getActivity().getSupportFragmentManager();
        DatePickerFragment datePickerFragment = DatePickerFragment.newInstance();
        datePickerFragment.setTargetFragment(FiltersFragment.this,300);
        datePickerFragment.show(fm,"fragment_date_picker");
    }

    @Override
    public void OnFinishSetDate(DatePicker datePicker) {
//        Toast.makeText(getActivity(),"You set the date!",Toast.LENGTH_SHORT).show();
        tvDate.setText((datePicker.getMonth()+1) + "/" + datePicker.getDayOfMonth()+"/"+datePicker.getYear());
    }

    public void sendBackResult() {
        OnFinishFiltersClickListener listener = (OnFinishFiltersClickListener) getActivity();

        String sort = spSort.getSelectedItem().toString();
        String date = tvDate.getText().toString();
        String[] checkBoxes = {cbArts.isChecked()?"Arts":"",cbEducation.isChecked()?"Education":"",cbSports.isChecked()?"Sports":""};

        Bundle bundle = new Bundle();
        bundle.putString("sort",sort);
        bundle.putString("date",date);
        bundle.putStringArray("checkBoxes",checkBoxes);
        listener.OnFinishFiltersClick(bundle);
        dismiss();
    }
}
