// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.MultipartConfig;

import java.util.*;
import java.nio.*;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.sql.*;

//import org.apache.commons.fileupload.FileItem;
//import org.apache.commons.fileupload.FileUploadException;
//import org.apache.commons.fileupload.disk.DiskFileItemFactory;
//import org.apache.commons.fileupload.servlet.ServletFileUpload;
//import org.apache.commons.io.output.*;

// Extend HttpServlet class
@MultipartConfig
public class UploadServlet extends HttpServlet {
	private String filePathTemp;
	private String filePath;
	private String fileName;
	private String fileDate;

	private String user;
	private File file;
	private String DB_URL;

	public void init() {
		filePath = System.getProperty("catalina.base") + "/webapps/photogallery/data/";
		filePathTemp = filePath + "temp/";
		DB_URL = "jdbc:sqlite:"+System.getProperty("catalina.base") + "\\webapps\\photogallery\\WEB-INF\\classes\\PhotoGallery.db";
		//DB_URL = "jdbc:sqlite:F:\\COMP_Project\\PhotoGallery\\tomcat\\webapps\\photogallery\\WEB-INF\\classes\\PhotoGallery.db";
	}

   // Method to handle GET method request.
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		  
		// Set response content type
		response.setContentType("text/html");

		HttpSession session = request.getSession(false);
		user = (String) session.getAttribute("user");

		if (session == null) {
			response.sendRedirect("/photogallery/");
		}
		else {

			PrintWriter out = response.getWriter();

			String title = "Upload Files";
			String docType =
					"<!doctype html public \"-//w3c//dtd html 4.0 " +
							"transitional//en\">\n";

			out.println(docType + "<html>\n" +
					"<head><title>" + title + "</title></head>\n" +
					"<body bgcolor = \"#f0f0f0\">\n" +
					"<h1 align = \"center\">" + title + "</h1>\n" +
					"<br><br>" +
					"<form action= \"UploadPage\" method=\"POST\" enctype=\"multipart/form-data\">" +
					"<table align = \"center\">" +
					"<tr>"+
					"<td>"+
					"<label for=\"file\">Choose file to upload </label>" +
					"<input type=\"file\" id=\"file\" name=\"file\" />" +
					"</td>"+
					"</tr>"+
					"<tr>"+
					"<td><button>Submit</button></td>" +
					"</tr>"+
					"</table>" +
					"</form>"+
					"</body>" +
					"</html>");
		}
   }

   // Method to handle POST method request.
   public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	   response.setContentType("text/html");
	   java.io.PrintWriter out = response.getWriter();

	   Part filePart = request.getPart("file");

	   if (filePart != null) {

		   fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
		   InputStream fileContent = filePart.getInputStream();

		   File f = new File(filePathTemp + fileName);
		   java.nio.file.Files.copy(fileContent, f.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

		   SimpleDateFormat sdf_caption = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		   SimpleDateFormat sdf_db = new SimpleDateFormat("yyyyMMdd_HHmmss");
		   fileDate = sdf_db.format(f.lastModified());

		   String title = "Upload Files";
		   String docType =
				   "<!doctype html public \"-//w3c//dtd html 4.0 " +
						   "transitional//en\">\n";

		   out.println(docType + "<html>\n" +
						   "<head><title>" + title + "</title></head>\n" +
						   "<body bgcolor = \"#f0f0f0\">\n" +
						   "<h1 align = \"center\">" + title + "</h1>\n" +
						   "<br><br>" +
						   "<form id=\"data\"action= \"UploadPage\" method=\"POST\" enctype=\"multipart/form-data\">" +
						   "<table align = \"center\">" +
						   "<tr>" + String.format("<td> <img src=\"data\\temp\\%s \" style=\"max-width:800px;max-height:600px\"/> </td>", fileName) + "</tr>" +
				           "<tr>"+ "<td align=\"center\">" + fileName + " - " + sdf_caption.format(f.lastModified()) + "</td>" + "</tr>" +
				           "<tr>"+ "<td> &nbsp; </td>" + "</tr>"+
				           "<tr>"+ "<td> &nbsp; </td>" + "</tr>"+
						   "<tr>"+ "<td align=\"left\">" + "<label for=\"caption\">Caption:  </label>" + "<input type=\"text\" id=\"caption\" name=\"caption\" />" + "</td>"+ "</tr>"+
				           "<tr>"+ "<td> &nbsp; </td>" + "</tr>"+
				           "<tr>"+ "<td align=\"left\">" + "<label for=\"latitude\">Latitude:  </label>" + "<input type=\"text\" id=\"latitude\" name=\"latitude\" />" +
				                                           "<label for=\"longitude\">  Longitude:  </label>" + "<input type=\"text\" id=\"longitude\" name=\"longitude\" />" + "</td>"+ "</tr>"+
				           "<tr>"+ "<td> &nbsp; </td>" + "</tr>"+
						   "</form>" +
				           "<tr>"+ "<td align =\"center\"> <form action=\"UploadPage\" method=\"GET\"><button name = \"cancel\" type=\"submit\"> Cancel</button></form> " +
				           "<input type=\"submit\" form=\"data\" />" + "</tr>"+
						   "</table>" +
						   "</body>" +
						   "</html>");
	   }
	   else {

	   	   Connection conn = null;
		   Statement stmt = null;

		   String caption = request.getParameter("caption");
	   	   float latitude = Float.parseFloat(request.getParameter("latitude"));
	   	   float longitude = Float.parseFloat(request.getParameter("longitude"));

		   try {
			   //Class.forName("org.sqlite.JDBC");
			   conn = DriverManager.getConnection(DB_URL);
			   stmt = conn.createStatement();

			   String sql =  "INSERT INTO photodata (path, caption, latitude, longitude, date, UID) VALUES " +
					   String.format("(\"%s\", \"%s\", %f, %f, \"%s\", (SELECT UID FROM users WHERE Username = \"%s\"));",
							   fileName,
							   caption,
							   latitude,
							   longitude,
							   fileDate,
							   user);
			   out.println(sql);
			   stmt.executeUpdate(sql);
		   }
		   catch(SQLException se) {
			   se.printStackTrace();
			   Files.deleteIfExists(Paths.get(filePath + fileName));
		   }
		   catch(Exception e) {
			   e.printStackTrace();
			   Files.deleteIfExists(Paths.get(filePath + fileName));
		   }
		   finally {
		   	   try {
				   conn.close();
				   Files.move(Paths.get(filePathTemp + fileName), Paths.get(filePath + fileName));
			   } catch (Exception e) {}


			   response.sendRedirect("/photogallery/PhotoGallery");
		   }

	   }
	}
}