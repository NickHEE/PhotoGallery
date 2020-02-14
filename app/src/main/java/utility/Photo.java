package utility;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class Photo {

    private String caption = "Enter a caption";
    private String filePath;
    private String timeStamp;
    private double longitude;
    private double latitude;

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
    public String getTimeStampPretty(){
        Date d = getDate();

        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(d);
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
    public void setLocation(Location loc) {
        latitude = loc.getLatitude();
        longitude = loc.getLongitude();
    }
    public Location getLocation() {
        Location location = new Location("");
        location.setLatitude(latitude);
        location.setLongitude(longitude);

        return location;
    }
    public String getLocationString(Geocoder geocoder) {
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            Address obj = addresses.get(0);
            return obj.getAddressLine(0).replace("\n", "");
        }
        catch (IndexOutOfBoundsException ex) {
            return "Unknown Location";
        }
        catch (IOException ex) {
            return "IO Exception you dummy";
        }
    }
}