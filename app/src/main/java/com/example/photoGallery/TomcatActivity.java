package com.example.photoGallery;

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

import androidx.appcompat.app.AppCompatActivity;

public class TomcatActivity extends AppCompatActivity {
    private EditText usernameField;
    private EditText passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tomcat);

        // comment/distance search box
        usernameField = (EditText) findViewById(R.id.username_field);
        passwordField = (EditText) findViewById(R.id.password_field);


        String username_ = getIntent().getStringExtra("CURRENT_USERNAME");
        String password_ = getIntent().getStringExtra("CURRENT_PASSWORD");

        usernameField.setText(username_);
        passwordField.setText(password_);
    }


    // method for cancel button
    public void cancel(final View v) {
        finish();
    }

    // method for applying filter to photo gallery (when "search" button is pressed)
    public void upload(final View v) {

        Intent i = new Intent(); // make intent and load everything onto it
        i.putExtra("USERNAME", usernameField.getText().toString());
        i.putExtra("PASSWORD", passwordField.getText().toString());
        setResult(RESULT_OK, i);
        finish();
    }


}




