// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

// Extend HttpServlet class
public class LoginServlet extends HttpServlet {

   // Method to handle GET method request.
   public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
	  
		// Set response content type
		response.setContentType("text/html");
	
   }



   // Method to handle POST method request.
   public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
	  String button = request.getParameter("button_press");
	  
		String username1 = "Nicholas";
		String password1 = "root";
		if ((username1.equals(request.getParameter("Username"))) && (password1.equals(request.getParameter("Password"))))
		{
		  // if username-password correct, display photo gallery things
		  PrintWriter out = response.getWriter();
		  out.print(1);
		  //doGet(request, response);
		  response.sendRedirect("/midp/PhotoGallery");
		}
		else 
		{
			// if username-password pair not recognized, go back to login form
			response.setStatus(response.SC_MOVED_TEMPORARILY);
			response.setHeader("Location", "http://localhost:8081/midp/login_form.html");   
		}
	  }
}