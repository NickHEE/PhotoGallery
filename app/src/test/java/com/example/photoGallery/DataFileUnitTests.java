package com.example.photoGallery;

import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;

import com.example.photoGallery.utility.Utility;
import com.example.photoGallery.utility.Photo;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class DataFileUnitTests {

    private File dataFile;
    private String filePath = "fake/file/path.jpeg";
    private String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    private String caption = "Hello World!";

    @Test
    public void DataFileReadWrite() {
        dataFile = Utility.createDataFile(new File("./"), "data.json");
        assertTrue(dataFile.exists());

        Photo photo = new Photo(filePath, timeStamp, caption);
        ArrayList<Photo> photoList = new ArrayList<Photo>();
        photoList.add(photo);

        Utility.writeDataFile(photoList, dataFile);
        ArrayList<Photo> _photoList = new ArrayList<>(Utility.readDataFile(dataFile));
        Photo _photo = _photoList.get(0);

        assertEquals(filePath, _photo.getPath());
        assertEquals(timeStamp, _photo.getTimeStamp());
        assertEquals(caption, _photo.getCaption());
    }

    @After
    public void CleanUp() {
        dataFile.delete();
    }

}