package com.example.photoGallery.midterm;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Arrays;

public class asyncTaskExample extends AsyncTask<Integer, Void, String> {
    private final WeakReference<Activity> _activityReference;

    public asyncTaskExample(Activity activity) {
        super();
        this._activityReference = new WeakReference<>(activity);

    }

    protected String doInBackground(Integer... idx) {
        for (Integer i : idx) {
            i++;
        }
        return String.valueOf(Arrays.stream(idx).mapToInt(Integer::intValue).sum());
    }

    protected void onPostExecute(String result) {
        Activity activity = _activityReference.get();

        Toast toast = Toast.makeText(activity.getApplicationContext(), result, Toast.LENGTH_SHORT);
        toast.show();
    }
}

// To use in app: new asyncTaskExample(MainActivity.this).execute(photoIdx, photoIdx + 1, photoIdx + 2);
