// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

// Extend HttpServlet class
public class HitServlet extends HttpServlet {
	static int photo_index = 500;
   // Method to handle GET method request.
   public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
		  
      // Set response content type
      response.setContentType("text/html");


	  PrintWriter out = response.getWriter();
	  /*
	  String title = "Photo Gallery";
	  String docType =
		 "<!doctype html public \"-//w3c//dtd html 4.0 " +
		 "transitional//en\">\n";
		 
	  out.println(docType +
		 "<html>\n" +
			"<head><title>" + title + "</title></head>\n" +
			"<body bgcolor = \"#f0f0f0\">\n" +
			   "<h1 align = \"center\">" + title + "</h1>\n" +
			   "<ul>\n" +
				  "  <li><b>First Name</b>: "
				  + request.getParameter("Username") + "\n" +
				  "  <li><b>Last Name</b>: "
				  + request.getParameter("Password") + "\n" +
			   "</ul>\n" +
			"</body>" +
		 "</html>"
	  );
	  */
	  
	out.print(photo_index);
	out.print("<form method=\"POST\"><button name = \"button_press\" type=\"submit\" value = \"Left\"> Left</button></form>");
	out.print("<form method=\"POST\"><button name = \"button_press\" type=\"submit\" value = \"Right\"> Right</button></form>");

   }



   // Method to handle POST method request.
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
	  String button = request.getParameter("button_press");
	  
	  if (button == null)
	  {// no button was pressed when entering this Servlet
			
	  } else if (button.equals("Login"))
	  {
		String username1 = "Nicholas";
		String password1 = "root";
		if ((username1.equals(request.getParameter("Username"))) && (password1.equals(request.getParameter("Password"))))
		{
		  // if username-password correct, display photo gallery things
		  doGet(request, response);
		}
		else 
		{
			// if username-password pair not recognized, go back to login form
			response.setStatus(response.SC_MOVED_TEMPORARILY);
			response.setHeader("Location", "http://localhost:8081/midp/login_form.html");   
		}
	  } else if (button.equals("Left"))
	  {
		  photo_index--; 
		  doGet(request, response);
	  } else if (button.equals("Right"))
	  {
		  photo_index++; 
		  doGet(request, response);
	  } 
   }
}