// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

// Extend HttpServlet class
public class SearchServlet extends HttpServlet {
   // Method to handle GET method request.
   public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
	  
	// Set response content type
	response.setContentType("text/html");

	HttpSession session = request.getSession(false);
	if (session == null) {
		response.sendRedirect("/photogallery/");
	}
	  
	  
	  
	  PrintWriter out = response.getWriter();
	  
	  String title = "Photo-Gallery Search Filter";
	  
	  String docType =
		 "<!doctype html public \"-//w3c//dth html 4.0 " +
		 "transitional//en\">\n";
		 
		 
	  out.println(docType +
		 "<html>\n" +
			"<head><title>" + title + "</title></head>\n" +
			"<body bgcolor = \"#f0f0f0\">\n" +
			   "<h1 align = \"center\">" + title + "</h1>\n" +
			   "<p align=\"center\"> Choose which filters to use, then input the desired values before clicking \"submit\"</p>" +
			   "<p align=\"center\">" +
				   "<form action=\"/photogallery/PhotoGallery\" method = \"GET\">" +
					   "<table align=\"center\">"+
						  "<tr>"+
							"<td align = \"right\"><label for=\"from_date\">From Date:</label></td>" +
							"<td><input type=\"text\" id=\"from_date\" name=\"from_date\"></td>"+
						  "</tr>" +
						  "<tr>"+
							"<td align = \"right\"><label for=\"to_date\">To Date:</label></td>" +
							"<td><input type=\"text\" id=\"to_date\" name=\"to_date\"><td>"+
						  "</tr>" +
						  "<tr>"+
							"<td><br></td>"+
						  "</tr>" +
						  "<tr>"+
							"<td align = \"right\"><label for=\"latitude\">Latitude:</label></td>" +
							"<td><input type=\"text\" id=\"latitude\" name=\"latitude\"></td>"+
						  "</tr>" +
						  "<tr>"+
							"<td align = \"right\"><label for=\"longitude\">Longitude:</label></td>" +
							"<td><input type=\"text\" id=\"longitude\" name=\"longitude\"></td>"+
						  "</tr>" +
						  "<tr>"+
							"<td align = \"right\"><label for=\"distance\">Distance from point:</label></td>" +
							"<td><input type=\"text\" id=\"distance\" name=\"distance\"></td>"+
						  "</tr>" +
						  "<tr>"+
							"<td><br></td>"+
							"</tr>" +
						  "<tr>"+
						  "<td align = \"right\"><label for=\"caption\">Caption:</label>"+
						  "</td>" +
						  "<td>"+
							"<input type=\"text\" id=\"caption\" name=\"caption\"></td>"+
						  "</tr>" +
						  "<tr>"+
							"<td><br></td>"+
						  "</tr>" +
						  "<tr>"+
							"<td></td><td align = \"right\"><input type=\"submit\" value=\"Submit\"></td>"+
						  "</tr>" +
						"</table>"+
					"</form>" +
				"</p>" +
			"</body>" +
		 "</html>"
	  );
	  
   }


   // Method to handle POST method request.
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
	  doGet(request, response);
   }
}