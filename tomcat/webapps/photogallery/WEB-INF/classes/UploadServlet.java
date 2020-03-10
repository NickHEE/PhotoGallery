// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.MultipartConfig;

import java.util.*;
import java.nio.*;
import java.nio.file.Paths;
import java.nio.file.Files;

//import org.apache.commons.fileupload.FileItem;
//import org.apache.commons.fileupload.FileUploadException;
//import org.apache.commons.fileupload.disk.DiskFileItemFactory;
//import org.apache.commons.fileupload.servlet.ServletFileUpload;
//import org.apache.commons.io.output.*;

// Extend HttpServlet class
@MultipartConfig
public class UploadServlet extends HttpServlet {
	static int photo_index = 500;
	private String filePath;
	private File file;

	public void init() {
		filePath = System.getProperty("catalina.base") + "/webapps/data/";
	}


   // Method to handle GET method request.
   public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		  
		// Set response content type
		response.setContentType("text/html");

		HttpSession session = request.getSession(false);
		String user = "";

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
	   String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString(); // MSIE fix.String fileName = Paths
	   InputStream fileContent = filePart.getInputStream();

	   File f = new File(filePath + fileName);
	   java.nio.file.Files.copy(fileContent, f.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

	   out.println(filePath);
	  
	}
}