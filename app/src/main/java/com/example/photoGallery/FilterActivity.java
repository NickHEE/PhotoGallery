package com.example.photoGallery;

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import java.util.Calendar;

import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.app.DatePickerDialog;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.widget.EditText;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;



public class FilterActivity extends AppCompatActivity {

    private EditText fromDate;
    private EditText toDate;
    private Calendar fromCalendar;
    private Calendar toCalendar;
    private DatePickerDialog.OnDateSetListener fromListener;
    private DatePickerDialog.OnDateSetListener toListener;

    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private static final String TAG = "FilterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        fromDate = (EditText) findViewById(R.id.search_fromDate);
        toDate   = (EditText) findViewById(R.id.search_toDate);

        mDisplayDate = (TextView) findViewById(R.id.tvDate);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        FilterActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
                String date = month + "/" + day + "/" + year;
                mDisplayDate.setText(date);
            }
        };
    }

    public void cancel(final View v) {
        finish();
    }

    public void search(final View v) {
        Intent i = new Intent();
        i.putExtra("STARTDATE", fromDate.getText().toString());
        i.putExtra("ENDDATE", toDate.getText().toString());
        setResult(RESULT_OK, i);
        finish();
    }
}
