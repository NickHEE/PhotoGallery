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
			"<style>\n" +
			".img-container {\n" +
			"   text-align: center;\n" +
			"   display: block;\n" +
			" }\n" +
			" </style>" +
			"<head><title> %s </title></head>\n" +
			"<body bgcolor = \"#f0f0f0\">\n" +
			"<h1 align = \"center\"> %s </h1>\n" +
			"<table align = \"center\">"+
			"<tr><td align=\"left\"> %s </td><td align=\"right\"> %s, %s </td></tr>" +
			"<tr><td align=\"center\"><div class=\"img-container\"><img src=\"data\\%s\" style=\"max-width:800px;max-height:600px\"><div></td></tr>" +
			"</table>"+
			"<p align = \"center\"> <b>%s</b> </p>" +
			"<table align = \"center\">"+
			"<tr>"+
			"</tr>"+
			"<tr>"+
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
		  String query =  String.format("SELECT path, caption, date, latitude, longitude FROM photodata WHERE UID = (SELECT UID FROM users WHERE Username = \"%s\")", user);

		  String longitude = new String();
		  String latitude = new String();
		  String distance = new String();

		  if (parameterNames.hasMoreElements()) {
			  while (parameterNames.hasMoreElements()) {

				  String paramName = parameterNames.nextElement();
				  String paramValue = request.getParameter(paramName);

				  switch (paramName) {
					  case "caption":
						  if (paramValue.length() > 0) {
							  query += String.format("AND caption LIKE \"%%%s%%\"", paramValue);
						  }
						  break;
					  case "from_date":
						  String toDate = request.getParameter("to_date");
						  if (toDate.length() > 0 && paramValue.length() > 0) {
							  query += String.format("AND date BETWEEN \"%s\" AND \"%s\"", paramValue, toDate);
						  }
						  break;
					  default:
						  break;
				  }
			  }

			  longitude = request.getParameter("longitude");
			  latitude = request.getParameter("latitude");
			  distance = request.getParameter("distance");
		  }
		  query += ";";

		  ResultSet rs = stmt.executeQuery(query);

		  while(rs.next()) {

			  double _latitude = Double.valueOf(rs.getString("latitude"));
			  double _longitude = Double.valueOf(rs.getString("longitude"));

			  if (longitude.length() > 0 && latitude.length() > 0 && distance.length() > 0) {
			  		if (getDistance(Double.valueOf(latitude), Double.valueOf(longitude), _latitude, _longitude, 0.0, 0.0) > Double.valueOf(distance)) {
			  			continue;
			  		}
			  }

			  Photo p = new Photo(rs.getString("path"),
									rs.getString("caption"),
									String.valueOf(rs.getString("latitude")),
									String.valueOf(rs.getString("longitude")),
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

	  if (photoListIterator.hasNext()) { //hmmm
	  	photo = photoListIterator.next();
	  }
	  else {
	  	photo = new Photo("NoPhotos.png", "", "", "", "");
	  }

	  PrintWriter out = response.getWriter();
	  
	  title = "Photo Gallery - " + user;
	  out.println(String.format(page, title, title, photo.date, photo.latitude, photo.longitude, photo.path, photo.caption));

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
		  out.println(String.format(page, title, title, photo.date, photo.latitude, photo.longitude, photo.path, photo.caption));
	  }
	  else if (button.equals("Right")) {
		  response.setContentType("text/html");
		  PrintWriter out = response.getWriter();

	  	  if (photoListIterator.hasNext()) {
			  photo = photoListIterator.next();
		  }
		  out.println(String.format(page, title, title, photo.date, photo.latitude, photo.longitude, photo.path, photo.caption));
	  }
	  else if (button.equals("Upload")) {
		  photo_index = 600;
		  response.sendRedirect("/photogallery/UploadPage");
		  //doGet(request, response);
	  } 
   }

	public static double getDistance(double lat1, double lat2, double lon1,
								  double lon2, double el1, double el2) {

		final int R = 6371; // Radius of the earth

		double latDistance = Math.toRadians(lat2 - lat1);
		double lonDistance = Math.toRadians(lon2 - lon1);
		double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
				+ Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
				* Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		double distance = R * c * 1000; // convert to meters

		double height = el1 - el2;

		distance = Math.pow(distance, 2) + Math.pow(height, 2);

		return Math.sqrt(distance);
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