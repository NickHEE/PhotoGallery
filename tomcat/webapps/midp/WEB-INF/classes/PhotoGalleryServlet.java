// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

// Extend HttpServlet class
public class PhotoGalleryServlet extends HttpServlet {
	static int photo_index = 500;
   // Method to handle GET method request.
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
  
	  // request.getParameter("Username");
      
	  
	  // Set response content type
      response.setContentType("text/html");


	  PrintWriter out = response.getWriter();
	  
	  String title = "Photo Gallery";
	  String docType =
		 "<!doctype html public \"-//w3c//dtd html 4.0 " +
		 "transitional//en\">\n";
		 
	  out.println(docType +
		 "<html>\n" +
			"<head><title>" + title + "</title></head>\n" +
			"<body bgcolor = \"#f0f0f0\">\n" +
			   "<h1 align = \"center\">" + title + "</h1>\n" +
			   "<table align = \"center\">"+
					"<tr>"+
						"<td span = \"3\"><img src=\"\" alt=\"Image Here\"></td>"+
					"</tr>"+
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
		 "</html>"
	  );
	  
	  
	out.print(photo_index); // just to show that left/right buttons work. Remove when unneeded
   }



   // Method to handle POST method request.
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
	  String button = request.getParameter("button_press");
	  
	  if (button == null)
	  {// no button was pressed when entering this Servlet
		
	  } else if (button.equals("Left"))
	  {
		  photo_index--; 
		  doGet(request, response);
	  } else if (button.equals("Right"))
	  {
		  photo_index++; 
		  doGet(request, response);
	  } else if (button.equals("Upload"))
	  {
		  photo_index = 600;
		  response.sendRedirect("/midp/UploadPage");
		  //doGet(request, response);
	  } 
   }
}