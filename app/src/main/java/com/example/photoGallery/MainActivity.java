package com.example.photoGallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.Location;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import utility.Photo;
import utility.Utility.*;


public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_FILTER_ACTIVITY = 2;

    private int photoIdx = 0;
    private ArrayList<Photo> photoList;
    private String photoPath = null;
    private File dataFile;
    private Photo currentPhoto; //TODO: Get rid of this?
    private Location currentLocation;

    private ImageView imgV;
    private TextView date;
    private TextView location;
    private EditText caption;
    private android.widget.Toast Toast;

    private final String namePackage = "com.discord";

    Geocoder geocoder;
    private LocationManager locationManager;
    private LocationListener locationListener = new LocationListener() {

        public void onLocationChanged(Location location) {
            // Called when a new location is found by the network location provider.
            currentLocation = location;
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {}

        public void onProviderEnabled(String provider) {}

        public void onProviderDisabled(String provider) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate", "Created!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgV = (ImageView) findViewById(R.id.iv_gallery);
        date = (TextView) findViewById(R.id.textView_date);
        caption = (EditText) findViewById(R.id.editText_caption);
        location = (TextView) findViewById(R.id.textView_location);
        dataFile = utility.Utility.createDataFile(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "data.json");

        EditText caption = (EditText) findViewById(R.id.editText_caption);
        caption.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (photoList.size() > 0) {
                    String newCaption = s.toString();

                    //Update datafile
                    ArrayList<Photo> photos = new ArrayList<Photo>(utility.Utility.readDataFile(dataFile));
                    Optional<Photo> photo = photos.stream().filter(p -> p.getPath().equals(currentPhoto.getPath())).findAny();
                    int i = photos.indexOf(photo.get());
                    Photo newPhoto = photo.get();
                    newPhoto.setCaption(newCaption);
                    photos.set(i,newPhoto);

                    utility.Utility.writeDataFile(photos, dataFile);

                    //Update current photolist
                    photoList.set(photoIdx, newPhoto);
                    currentPhoto = newPhoto;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Date minDate = new Date(Long.MIN_VALUE);
        Date maxDate = new Date(Long.MAX_VALUE);
        geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

        Location test = null;
        populateGallery(minDate, maxDate, test, 0.0, "");

        if (photoList.size() > 0) {
            currentPhoto = photoList.get(photoIdx);
            displayPhoto(currentPhoto);
        }

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d("onCreate", "requesting permission");
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } catch(SecurityException ex) {Log.d("onCreate", ex.toString());}

    }

    public void onFilter(View v) {
        Intent i = new Intent(MainActivity.this, FilterActivity.class);
        startActivityForResult(i, REQUEST_FILTER_ACTIVITY);
    }

    public void uploadPicture(View v) {

        // only allow photo to be uploaded if there is a picture selected
        if (photoList.size() <= 0) {
            Toast.makeText(getApplicationContext(), "No image selected to upload", Toast.LENGTH_LONG).show();
            return;
        }

        String filePath = currentPhoto.getPath();
        File file = new File(filePath);
        Uri imageUri = FileProvider.getUriForFile(getApplicationContext(),
                "com.example.photoGallery.fileprovider", file);
        Intent sendIntent = new Intent(); // make new intent to send
        sendIntent.setType("image/*");
        sendIntent.setAction(Intent.ACTION_SEND); // set what you want to do with intent
        sendIntent.putExtra(Intent.EXTRA_STREAM, imageUri); // attach image file to intent
        sendIntent.putExtra(Intent.EXTRA_TEXT, caption.getText().toString()); // attach caption to intent
        sendIntent.setPackage(namePackage); // attach discord as place to send to

        try {
            startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {

            Toast.makeText(getApplicationContext(), "Error: Unable to send intent.", Toast.LENGTH_SHORT).show();
        }
    }

    public void takePicture(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.d("takePicture", "createImageFile() IOException");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Log.d("takePicture", "createImageFile() Success");
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.photoGallery.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            }
        }
    }

    private void populateGallery(Date minDate, Date maxDate, Location location, Double distance, String caption) {
        //TODO: handle location

        Collection<Photo> photos = utility.Utility.readDataFile(dataFile);

        photos.removeIf(p -> (p.getDate().before(minDate) || p.getDate().after(maxDate)));
        if (!caption.isEmpty()) {
            photos.removeIf(p -> (!p.getCaption().contains(caption)));
        }
        if (location != null && distance > 0.0) {
            photos.removeIf(p -> (p.getLocation().distanceTo(location) > distance));
        }


        photoList = new ArrayList<Photo>(photos);

        if (photoList.size() > 0) {
            photoIdx = 0;
            currentPhoto = photoList.get(photoIdx);
            displayPhoto(currentPhoto);
        }
        else {
            noPhotoFound();
        }
    }

    public void changePicture(View v) {
        if (photoList.size() > 0) {

            switch (v.getId()) {
                case R.id.btn_l:
                    photoIdx--;
                    break;
                case R.id.btn_r:
                    photoIdx++;
                    break;
                default: break;
            }
            Log.d("changePicture", Integer.toString(photoIdx));

            photoIdx = photoIdx < 0 ? 0 : photoIdx >= photoList.size() ? photoList.size() - 1 : photoIdx;

            currentPhoto = photoList.get(photoIdx);
            displayPhoto(currentPhoto);
        }
    }

    private void displayPhoto(Photo photo) {
        date.setVisibility(View.VISIBLE);
        location.setVisibility(View.VISIBLE);
        imgV.setVisibility(View.VISIBLE);

        imgV.setImageBitmap(BitmapFactory.decodeFile(photo.getPath()));
        date.setText(photo.getTimeStampPretty());
        location.setText(photo.getLocationString(geocoder));
        caption.setText(photo.getCaption());
        caption.setEnabled(true);
    }

    private void noPhotoFound() {
        date.setVisibility(View.INVISIBLE);
        location.setVisibility(View.INVISIBLE);
        imgV.setVisibility(View.INVISIBLE);
        caption.setText("No Photos Found!");
        caption.setEnabled(false);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg",storageDir);
        photoPath = image.getAbsolutePath();

        currentPhoto = new Photo(photoPath, timeStamp, "");

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("onActivityResult", "Image Capture OK");

            currentPhoto.setLocation(currentLocation);
            ImageView mImageView = (ImageView) findViewById(R.id.iv_gallery);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(photoPath));

            // Update data file
            Collection<Photo> photos = utility.Utility.readDataFile(dataFile);
            photos.add(currentPhoto);
            utility.Utility.writeDataFile(photos, dataFile);

            // Update active photo list
            photoList.add(currentPhoto);
            photoIdx = photoList.indexOf(currentPhoto);
            displayPhoto(currentPhoto);
            //TODO: Don't add photo if not in current filter range?
        }
        else if (requestCode == REQUEST_FILTER_ACTIVITY && resultCode == RESULT_OK) {

            String s_d1 = data.getStringExtra("STARTDATE");
            String s_d2 = data.getStringExtra("ENDDATE");
            double lat = Double.parseDouble(data.getStringExtra("LATITUDE"));
            double lng = Double.parseDouble(data.getStringExtra("LONGITUDE"));
            double d = Double.parseDouble(data.getStringExtra("DISTANCE"));
            String caption = data.getStringExtra("COMMENTSEARCH");
            Log.d("onActivityResult", caption);

            try {
                Date d1 = new SimpleDateFormat("MM/dd/yyyy").parse(s_d1);
                Date d2 = new SimpleDateFormat("MM/dd/yyyy_HH_mm_ss").parse(s_d2);
                Location loc = new Location("");
                loc.setLatitude(lat);
                loc.setLongitude(lng);
                populateGallery(d1, d2, loc, d, caption);
            }
            catch (ParseException ex)
            {
                Log.d("onActivityResult", ex.toString());
            }
        }
    }


}