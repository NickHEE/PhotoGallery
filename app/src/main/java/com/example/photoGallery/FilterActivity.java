package com.example.photoGallery;
<<<<<<< HEAD
//
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
=======

import android.app.DatePickerDialog;
import android.widget.DatePicker;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import java.util.Calendar;

import android.widget.TextView;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import android.widget.EditText;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Locale;
>>>>>>> 4111a2f2b6e2bdc698ff47148a7a2242a94928d7



public class FilterActivity extends AppCompatActivity {

    private TextView fromDate;
    private TextView toDate;
<<<<<<< HEAD
    private EditText commentSearch;

=======
    private EditText TRLocation;
    private EditText BLLocation;
    private EditText commentSearch;

    private Calendar fromCalendar;
    private Calendar toCalendar;
    private DatePickerDialog.OnDateSetListener fromListener;
    private DatePickerDialog.OnDateSetListener toListener;
>>>>>>> 4111a2f2b6e2bdc698ff47148a7a2242a94928d7

    private DatePickerDialog.OnDateSetListener mDateSetListener_from;
    private DatePickerDialog.OnDateSetListener mDateSetListener_to;
    private static final String TAG = "FilterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        fromDate = (TextView) findViewById(R.id.tvDate_from);
        toDate   = (TextView) findViewById(R.id.tvDate_to);


<<<<<<< HEAD

=======
        TRLocation = (EditText) findViewById(R.id.Location_TR);
        BLLocation   = (EditText) findViewById(R.id.Location_BL);
>>>>>>> 4111a2f2b6e2bdc698ff47148a7a2242a94928d7
        commentSearch = (EditText) findViewById(R.id.Comment_search);

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        FilterActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener_from,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        FilterActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener_to,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener_from = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
                String date = month + "/" + day + "/" + year;
                fromDate.setText(date);
            }
        };
        mDateSetListener_to = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                Log.d(TAG, "onDateSet: mm/dd/yyy: " + month + "/" + day + "/" + year);
                String date = month + "/" + day + "/" + year;
                toDate.setText(date);
            }
        };
    }

    public void cancel(final View v) {
        finish();
    }

    public void search(final View v) {
        Intent i = new Intent();
        i.putExtra("STARTDATE", fromDate.getText().toString());
<<<<<<< HEAD
        i.putExtra("ENDDATE", toDate.getText().toString() + "_23_59_59");
=======
        i.putExtra("ENDDATE", toDate.getText().toString());
        i.putExtra("STARTLOCATION", TRLocation.getText().toString());
        i.putExtra("ENDLOCATION", BLLocation.getText().toString());
>>>>>>> 4111a2f2b6e2bdc698ff47148a7a2242a94928d7
        i.putExtra("COMMENTSEARCH", commentSearch.getText().toString());

        setResult(RESULT_OK, i);
        finish();
    }
}
