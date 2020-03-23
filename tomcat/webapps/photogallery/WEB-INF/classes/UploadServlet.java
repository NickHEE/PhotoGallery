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
	}

	private static String getSubmittedFileName(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1); // MSIE fix.
			}
		}
		return null;
	}


   // Method to handle GET method request.
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		  
		// Set response content type
		response.setContentType("text/html");

		HttpSession session = request.getSession(false);
		

		if (session == null) {
			response.sendRedirect("/photogallery/");
		}
		else {
			user = (String) session.getAttribute("user");
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
					"<tr><td> Choose file to upload </tr></td>"+
					"<tr><td> <input type=\"file\" id=\"file\" name=\"file\" /> </tr></td>"+
					"<tr>"+
					"<td><button>Upload</button></td>" +
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

		   fileName = getSubmittedFileName(filePart);
		   InputStream fileContent = filePart.getInputStream();

		   File f = new File(filePathTemp + fileName);
		   java.nio.file.Files.copy(fileContent, f.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

		   SimpleDateFormat sdf_caption = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		   fileDate = sdf_caption.format(f.lastModified());

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
				           "<tr>"+ "<td align=\"center\">" + fileName + " - " + fileDate + "</td>" + "</tr>" +
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
	   	   String latitude = request.getParameter("latitude");
	   	   String longitude = request.getParameter("longitude");

		   try {
			   //Class.forName("org.sqlite.JDBC");
			   conn = DriverManager.getConnection(DB_URL);
			   stmt = conn.createStatement();

			   String sql =  "INSERT INTO photodata (path, caption, latitude, longitude, date, UID) VALUES " +
					   String.format("(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", (SELECT UID FROM users WHERE Username = \"%s\"));",
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