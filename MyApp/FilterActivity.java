package com.example.photoGallery;
//
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import static java.lang.Double.valueOf;


public class FilterActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener {

    private TextView fromDate;
    private TextView toDate;
    private EditText commentSearch;
    private EditText distanceSearch;
    private EditText longitude;
    private EditText latitude;

    private DatePickerDialog.OnDateSetListener mDateSetListener_from;
    private DatePickerDialog.OnDateSetListener mDateSetListener_to;
    private static final String TAG = "FilterActivity";

    private Marker marker;   // marker user selects
    private LatLng position; // long/lat coordinate of marker
    private Toast toast;     // for popup window notifications
    private GoogleMap mMap;  // map object
    private static final LatLng DEFAULT_POSITION = new LatLng(-89, 150); // random initial value for position

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        // comment/distance search box
        commentSearch = (EditText) findViewById(R.id.Comment_search);
        distanceSearch = (EditText) findViewById(R.id.distance_search);

        // configure longitude/latitude boxes and make so only negative, decimal point numbers can be entered
        longitude = (EditText)findViewById(R.id.longitude_search);
        latitude = (EditText)findViewById(R.id.latitude_search);
        longitude.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
        latitude.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);

        // map stuff
        SupportMapFragment mapFragment =(SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // data spinner stuff
        fromDate = (TextView) findViewById(R.id.tvDate_from);
        toDate   = (TextView) findViewById(R.id.tvDate_to);
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

    /** Called when the map is ready. */
    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        // set default location for marker
        marker = mMap.addMarker(new MarkerOptions().position(DEFAULT_POSITION));
        marker.setTag(0);
        position = DEFAULT_POSITION;

        // Set a listener for map click.
        mMap.setOnMapClickListener(this);
    }

    @Override
    public void onMapClick(LatLng point) {
        marker.remove();
        position = point;
        marker = mMap.addMarker(new MarkerOptions().position(point));

        /* // displays temporary popup window showing current longitude/latitude
        toast = Toast.makeText(
                FilterActivity.this,
                "Lat " + position.latitude + " "
                        + "Long " + position.longitude,
                Toast.LENGTH_LONG);
        toast.show();
        */
        // some setting things to consider for positioning the popup window
        //toast.setGravity(Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, 0);
        // Gravity.TOP|Gravity.LEFT

        // update longitude/lattitude text fields
        longitude.setText(Double.toString(position.longitude));
        latitude.setText(Double.toString(position.latitude));
        return;
    }

    // method for cancel button
    public void cancel(final View v) {
        finish();
    }

    // update where marker on map is
    public void map(final View v) {

        // if nothing entered in one of the longitude/latitude boxes
        if (longitude.getText().toString().matches("") || latitude.getText().toString().matches(""))
        {
            toast = Toast.makeText(
                    FilterActivity.this,
                    "Longitude or Latitude field blank. Please ensure both fields are filled before updating marker on map.",
                    Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        // filter out of bound values. Putting as things like "89" due to edge case problems (-90 lat interpreted as 0 lat when placing marker for some reason)
        if (valueOf(latitude.getText().toString()) >= 90)
        {
            latitude.setText("89");
        } else if (valueOf(latitude.getText().toString()) <= -90)
        {
            latitude.setText("-89");
        }

        if (valueOf(longitude.getText().toString()) >= 180)
        {
            longitude.setText("179");
        } else if (valueOf(longitude.getText().toString()) <= -180)
        {
            longitude.setText("-179");
        }

        marker.remove(); // remove prior marker
        position = new LatLng(valueOf(latitude.getText().toString()), valueOf(longitude.getText().toString())); // update position based on inputted values
        marker = mMap.addMarker(new MarkerOptions().position(position));// put in new marker
        mMap.moveCamera(CameraUpdateFactory.newLatLng(position)); // move camera to this position


    }

    // method for applying filter to photo gallery (when "search" button is pressed)
    public void search(final View v) {

        Intent i = new Intent(); // make intent and load everything onto it
        i.putExtra("STARTDATE", fromDate.getText().toString());
        i.putExtra("ENDDATE", toDate.getText().toString() + "_23_59_59");
        i.putExtra("COMMENTSEARCH", commentSearch.getText().toString());
        i.putExtra("DISTANCE", distanceSearch.getText().toString());
        i.putExtra("LONGITUDE", longitude.getText().toString());
        i.putExtra("LATITUDE", latitude.getText().toString());

        setResult(RESULT_OK, i);
        finish();
    }
}




