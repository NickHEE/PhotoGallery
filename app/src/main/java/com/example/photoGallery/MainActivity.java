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
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int photoIdx = 0;
    private ArrayList<String> photoList;
    private String photoPath = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Date minDate = new Date(Long.MIN_VALUE);
        Date maxDate = new Date(Long.MAX_VALUE);
        photoList = populateGallery(minDate, maxDate);
    }

    public void takePicture(View v) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = new Photo();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.example.photoGallery.fileprovider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private ArrayList<String> populateGallery(Date minDate, Date maxDate) {
        File file = new File(Environment.getExternalStorageDirectory()
                .getAbsolutePath(), "/Android/data/com.example.photoGallery/files/Pictures");
        photoList = new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for (File f : file.listFiles()) {
                photoList.add(f.getPath());
            }
        }
        return photoList;
    }

    public void changePicture(View v) {
        switch (v.getId()) {
            case R.id.btn_l:
                photoIdx--;
                break;
            case R.id.btn_r:
                photoIdx++;
                break;
            default: break;
        }

        photoIdx = photoIdx < 0 ? 0 : photoIdx >= photoList.size() ? photoList.size() - 1 : photoIdx;

        photoPath = photoList.get(photoIdx);
        displayPhoto(photoPath);
    }

    private void displayPhoto(String path) {
        ImageView imgV = (ImageView) findViewById(R.id.iv_gallery);
        imgV.setImageBitmap(BitmapFactory.decodeFile(path));
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg",storageDir);
        photoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            ImageView mImageView = (ImageView) findViewById(R.id.iv_gallery);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(photoPath));
        }
    }
}


public class Photo {

    private Date date;
    private String caption;
    private File file;

    public Photo(File dir, String cap) {

        date = new Date();

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(date);
        String fileName = "JPEG_" + timeStamp + "_";
        file = File.createTempFile(fileName, ".jpg", dir);
    }

    public Date getDate() {
        return date;
    }

    public String getCaption() {
        return caption;
    }

    public File getFile() {
        return file;
    }



}

