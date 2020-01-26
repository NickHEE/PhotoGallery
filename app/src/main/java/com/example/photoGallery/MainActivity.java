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
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.ArrayList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class MainActivity extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private int photoIdx = 0;
    private ArrayList<String> photoList;
    private String photoPath = null;
    private File dataFile;
    private Photo currentPhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        setContentView(R.layout.activity_main);

        Date minDate = new Date(Long.MIN_VALUE);
        Date maxDate = new Date(Long.MAX_VALUE);
        photoList = populateGallery(minDate, maxDate);
        displayPhoto(photoList.get(photoIdx));

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
        currentPhoto = new Photo(photoPath, timeStamp);

        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Log.d("onActivityResult", "Image Capture OK");
            ImageView mImageView = (ImageView) findViewById(R.id.iv_gallery);
            mImageView.setImageBitmap(BitmapFactory.decodeFile(photoPath));

            int length = (int) dataFile.length();
            byte[] bytes = new byte[length];

            try {
                FileInputStream dataIn = new FileInputStream(dataFile);
                dataIn.read(bytes);
                dataIn.close();
            }
            catch (IOException ex) {
                Log.d("onActivityResult", "Read from dataFile FAILED");
            }

            String json = new String(bytes);
            Gson gson = new Gson();
            Type collectionType = new TypeToken<Collection<Photo>>(){}.getType();
            Collection<Photo> photoList = gson.fromJson(json, collectionType);

            photoList.add(currentPhoto);
            json = gson.toJson(photoList);

            try {
                FileOutputStream stream = new FileOutputStream(dataFile,false);
                stream.write(json.getBytes());
                stream.close();
            }
            catch (IOException ex) {
                Log.d("onActivityResult", "write to dataFile FAILED");
            }
        }
        else {
            Log.d("onActivityResult", "Image Capture FAIL");
        }
    }

    private class Photo {

        private String caption = "Enter a caption";
        private String filePath;
        private String timeStamp;

        public Photo(String filePath, String timeStamp) {
            this.filePath = filePath;
            this.timeStamp = timeStamp;

        }
    }
}


