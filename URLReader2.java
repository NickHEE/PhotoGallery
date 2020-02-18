import java.net.*;
import java.io.*;
class SimpleThread extends Thread {
	public SimpleThread(String str) {
		super(str);
	}
	public void run() {

           try {
           URL oracle = new URL("https://www.bcit.ca/");
            BufferedReader in = new BufferedReader(
            new InputStreamReader(oracle.openStream()));

            String inputLine;
            while ((inputLine = in.readLine()) != null)
              System.out.println(inputLine);
            in.close();
            } catch(Exception ex) { }
        }
}


public class URLReader2 {
    public static void main(String[] args)  {
      int numDownloads = 2;
      for (int i = 0; i < numDownloads; i++) {
         new SimpleThread("download#"+i).start();
      }      
    }   
}