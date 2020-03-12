// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

import java.util.Enumeration;
import java.util.List;
import java.util.ArrayList;
import java.util.ListIterator;
import java.sql.*;

// Extend HttpServlet class
public class PhotoGalleryServlet extends HttpServlet {
	static int photo_index = 500;
	private String DB_URL;
	private List<Photo> photoList = new ArrayList<>();
	private ListIterator<Photo> photoListIterator;
	private String filePath;
	private Photo photo;
	private String title;

	private String page = "<!doctype html public \"-//w3c//dtd html 4.0 " + "transitional//en\">\n"+
			"<html>\n" +
			"<head><title> %s </title></head>\n" +
			"<body bgcolor = \"#f0f0f0\">\n" +
			"<h1 align = \"center\"> %s </h1>\n" +
			"<table align = \"center\">"+
			"<tr><td align=\"center\"><img src=\"data\\%s\" style=\"max-width:800px;max-height:600px\"></td></tr>" +
			"</table>"+
			"<table align = \"center\">"+
			"<tr>"+
			"</tr>"+
			"<tr>"+
			"<td align =\"center\"> %s </td>" +
			"</tr>"+
			"<tr>"+ "<td> &nbsp; </td>" + "</tr>"+
			"<tr>"+ "<td> &nbsp; </td>" + "</tr>"+
			"<tr>"+
			"<td align=\"center\"><form method=\"POST\"><button name = \"button_press\" type=\"submit\" value = \"Left\"> Left</button></form></td>"+
			"<td></td>"+
			"<td align=\"center\"><form method=\"POST\"><button name = \"button_press\" type=\"submit\" value = \"Right\"> Right</button></form></td>"+
			"</tr>"+
			"<tr>"+
			"<td></td>"+
			"<td align=\"center\"><form action=\"SearchPage\" method=\"GET\"><button name = \"button_press\" type=\"submit\" value = \"Filter\"> Filter</button></form></td>"+
			"<td></td>"+
			"</tr>"+
			"<tr>"+
			"<td></td>"+
			"<td align=\"center\"><form action=\"UploadPage\" method=\"GET\"><button name = \"button_press\" type=\"submit\" value = \"Upload\"> Upload</button></form></td>"+
			"<td></td>"+
			"</tr>"+
			"</table>"+
			"</body>" +
			"</html>";

	public void init() {
		filePath = System.getProperty("catalina.base") + "/webapps/photogallery/data/";
		DB_URL = "jdbc:sqlite:"+System.getProperty("catalina.base") + "\\webapps\\photogallery\\WEB-INF\\classes\\PhotoGallery.db";
	}

	// Method to handle GET method request.
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {

	  response.setContentType("text/html");

	  if (!photoList.isEmpty()) {
		  photoList.clear();
	  }

	  // Set response content type
      response.setContentType("text/html");
      HttpSession session = request.getSession(false);
      String user = "";

      if (session != null) {
		  user = (String) session.getAttribute("user");
	  }
      else {
		  response.sendRedirect("/photogallery/");
	  }

      Enumeration<String> parameterNames = request.getParameterNames();
      Connection conn = null;
      Statement stmt = null;

	  try {

		  conn = DriverManager.getConnection(DB_URL);
		  stmt = conn.createStatement();
		  String query =  "SELECT path, caption, date, latitude, longitude FROM photodata;";

		  if (parameterNames.hasMoreElements()) {
			query += "";
		  }

		  ResultSet rs = stmt.executeQuery(query);

		  while(rs.next()) {
		  		Photo p = new Photo(rs.getString("path"),
						            rs.getString("caption"),
						            String.valueOf(rs.getFloat("latitude")),
						            String.valueOf(rs.getFloat("longitude")),
						            rs.getString("date"));
				photoList.add(p);
		  }
	  }
	  catch (SQLException se) {
		  se.printStackTrace();
	  }
	  catch (Exception e) {
		  e.printStackTrace();
	  }
	  finally {
		  try {
			  conn.close();
			  photoListIterator = photoList.listIterator();
		  } catch (Exception e) {e.printStackTrace();}
	  }

	  if (photoListIterator.hasNext()) {
	  	photo = photoListIterator.next();
	  }
	  else {
	  	photo = new Photo("NoPhotos.png", "", "", "", "");
	  }

	  PrintWriter out = response.getWriter();
	  
	  title = "Photo Gallery - " + user;
	  out.println(String.format(page, title, title, photo.path, photo.caption));

	  //out.print(photo_index); // just to show that left/right buttons work. Remove when unneeded
   }

   // Method to handle POST method request.
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
	  String button = request.getParameter("button_press");
	  
	  if (button == null) {// no button was pressed when entering this Servlet
		
	  }
	  else if (button.equals("Left")) {
		  response.setContentType("text/html");
		  PrintWriter out = response.getWriter();

		  if (photoListIterator.hasPrevious()) {
			  photo = photoListIterator.previous();
		  }
		  out.println(String.format(page, title, title, photo.path, photo.caption));
	  }
	  else if (button.equals("Right")) {
		  response.setContentType("text/html");
		  PrintWriter out = response.getWriter();

	  	  if (photoListIterator.hasNext()) {
			  photo = photoListIterator.next();
		  }
		  out.println(String.format(page, title, title, photo.path, photo.caption));
	  }
	  else if (button.equals("Upload")) {
		  photo_index = 600;
		  response.sendRedirect("/photogallery/UploadPage");
		  //doGet(request, response);
	  } 
   }

   public class Photo {
		public String path;
		public String caption;
		public String latitude;
		public String longitude;
		public String date;

	    public Photo(String path, String caption, String latitude, String longitude, String date) {
	    	this.path = path;
	    	this.caption = caption;
	    	this.latitude = latitude;
	    	this.longitude = longitude;
	    	this.date = date;
	    }
   }
}