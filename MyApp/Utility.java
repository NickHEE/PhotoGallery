package com.example.photoGallery.utility;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collection;

public class Utility {

    static public File createDataFile(File dir, String fName) {
        File f = new File(dir, fName);

        try {
            if (f.createNewFile()) {
                FileOutputStream stream = new FileOutputStream(f);
                stream.write("[]".getBytes());
                stream.close();
            }
        }
        catch (IOException ex) {
            Log.d("onCreate", "Data file creation FAIL" );
        }

        return f;

    }

    static public Collection<Photo> readDataFile(File f) {
        int length = (int) f.length();
        byte[] bytes = new byte[length];
        Gson gson = new Gson();

        try {
            FileInputStream dataIn = new FileInputStream(f);
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

    static public void writeDataFile(Collection<Photo> photoList, File f) {
        Gson gson = new Gson();
        String json = gson.toJson(photoList);

        try {
            FileOutputStream stream = new FileOutputStream(f,false);
            stream.write(json.getBytes());
            stream.close();
        }
        catch (IOException ex) {
            Log.d("writeDataFile", "write to dataFile FAILED");
            // TODO: handle exception
        }
    }
}


