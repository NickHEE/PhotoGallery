// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

// Extend HttpServlet class
public class UploadServlet extends HttpServlet {
	static int photo_index = 500;
   // Method to handle GET method request.
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
		  
      // Set response content type
      response.setContentType("text/html");


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
							"<input type=\"file\" id=\"file\" name=\"file\" multiple>" +
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



   // Method to handle POST method request.
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
		//handle with uploaded image things here (put into database and update necessary values
		//consider using "if" statement for separating action for valid image file (.jpn, .png, etc.) vs invalid image files (.doc, .xlm, etc.)
	  
		response.sendRedirect("/midp/PhotoGallery");
	  
	  
	  }
}