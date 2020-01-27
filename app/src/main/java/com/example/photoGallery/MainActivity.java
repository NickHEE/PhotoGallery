package com.example.photoGallery;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.content.Context;
import android.graphics.BitmapFactory;
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
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_FILTER_ACTIVITY = 2;

    private int photoIdx = 0;
    private ArrayList<Photo> photoList;
    private String photoPath = null;
    private File dataFile;
    private Photo currentPhoto; //TODO: Get rid of this?

    private ImageView imgV;
    private TextView date;
    private TextView location;
    private EditText caption;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("onCreate", "Created!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgV = (ImageView) findViewById(R.id.iv_gallery);
        date = (TextView) findViewById(R.id.textView_date);
        caption = (EditText) findViewById(R.id.editText_caption);
        location = (TextView) findViewById(R.id.textView_location);

        dataFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "data.json");
        try {
            if (dataFile.createNewFile()) {
                FileOutputStream stream = new FileOutputStream(dataFile);
                stream.write("[]".getBytes());
                stream.close();
            }
        }
        catch (IOException ex) {
            Log.d("onCreate", "Data file creation FAIL" );
        }

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
                    ArrayList<Photo> photos = new ArrayList<Photo>(readDataFile());
                    Optional<Photo> photo = photos.stream().filter(p -> p.getPath().equals(currentPhoto.getPath())).findAny();
                    int i = photos.indexOf(photo.get());
                    Photo newPhoto = photo.get();
                    newPhoto.setCaption(newCaption);
                    photos.set(i,newPhoto);

                    writeDataFile(photos);

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

        populateGallery(minDate, maxDate, "", "", "");

        if (photoList.size() > 0) {
            currentPhoto = photoList.get(photoIdx);
            displayPhoto(currentPhoto);
        }
    }

    public void onFilter(View v) {
        Intent i = new Intent(MainActivity.this, FilterActivity.class);
        startActivityForResult(i, REQUEST_FILTER_ACTIVITY);
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

    private void populateGallery(Date minDate, Date maxDate, String startLocation, String endLocation, String caption) {
        //TODO: handle location

        Collection<Photo> photos = readDataFile();
        photos.removeIf(p -> (p.getDate().before(minDate) || p.getDate().after(maxDate)));
        if (!caption.isEmpty()) {
            photos.removeIf(p -> (!p.caption.contains(caption)));
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

        imgV.setImageBitmap(BitmapFactory.decodeFile(photo.getPath()));
        date.setText(photo.getTimeStamp());
        caption.setText(photo.getCaption());
    }

    private void noPhotoFound() {
        date.setVisibility(View.INVISIBLE);
        location.setVisibility(View.INVISIBLE);
        caption.setText("No Photos Found!");
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

    private Collection<Photo> readDataFile() {
        int length = (int) dataFile.length();
        byte[] bytes = new byte[length];
        Gson gson = new Gson();

        try {
            FileInputStream dataIn = new FileInputStream(dataFile);
            dataIn.read(bytes);
            dataIn.close();
        }
        catch (IOException ex) {
            Log.d("readDataFile", "Read from dataFile FAILED");
            // TODO: handle exception
        }

        String json = new String(bytes);
        Type collectionType = new TypeToken<Collection<Photo>>(){}.getType();

        return gson.fromJson(json, collectionType);
    }

    private void writeDataFile(Collection<Photo> photoList) {
        Gson gson = new Gson();
        String json = gson.toJson(photoList);

        try {
            FileOutputStream stream = new FileOutputStream(dataFile,false);
            stream.write(json.getBytes());
            stream.close();
        }
        catch (IOException ex) {
            Log.d("writeDataFile", "write to dataFile FAILED");
            // TODO: handle exception
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            Log.d("onActivityResult", "Image Capture OK");
            ImageView mImageView = (ImageView) findViewById(R.id.iv_gallery);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(photoPath));

            Collection<Photo> photos = readDataFile();
            photos.add(currentPhoto);
            writeDataFile(photos);
            photoList.add(currentPhoto);
            //TODO: change index to current photo
        }
        else if (requestCode == REQUEST_FILTER_ACTIVITY && resultCode == RESULT_OK) {
            String s_d1 = data.getStringExtra("STARTDATE");
            String s_d2 = data.getStringExtra("ENDDATE");
            String loc1 = data.getStringExtra("STARTLOCATION");
            String loc2 = data.getStringExtra("ENDLOCATION");
            String caption = data.getStringExtra("COMMENTSEARCH");

            Log.d("onActivityResult", s_d1);
            Log.d("onActivityResult", s_d2);
            Log.d("onActivityResult", loc1);
            Log.d("onActivityResult", loc2);
            Log.d("onActivityResult", caption);

            try {
                Date d1 = new SimpleDateFormat("MM/dd/yyyy").parse(s_d1);
                Date d2 = new SimpleDateFormat("MM/dd/yyyy_HH_mm_ss").parse(s_d2);
                populateGallery(d1, d2, loc1, loc2, caption);
            }
            catch (ParseException ex)
            {
                Log.d("onActivityResult", ex.toString());
            }
        }
    }

    private class Photo {

        private String caption = "Enter a caption";
        private String filePath;
        private String timeStamp;

        public Photo(String filePath, String timeStamp, String caption) {
            this.filePath = filePath;
            this.timeStamp = timeStamp;
        }

        public Date getDate() {
            Date date;
            try {
                date = new SimpleDateFormat("yyyyMMdd_HHmmss").parse(timeStamp);
            }
            catch (ParseException ex) {
                return new Date();
            }

            return date;
        }
        public String getTimeStamp() {

            return timeStamp;
        }
        public String getPath() {

            return filePath;
        }
        public String getCaption() {

            return caption;
        }
        public void setCaption(String cap) {

            caption = cap;
        }
    }
}


