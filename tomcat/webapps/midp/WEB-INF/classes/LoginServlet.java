// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;

// Extend HttpServlet class
public class LoginServlet extends HttpServlet {
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/photogallery";
  
	static final String USERNAME = "root";
	static final String PASS = "ilovebobgill69";

   // Method to handle GET method request.
   public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
	  
		// Set response content type
		response.setContentType("text/html");
	
   }

   // Method to handle POST method request.
   public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      
		String button = request.getParameter("button_press");

		String username1 = "Nicholas";
		String password1 = "root";
		if ((username1.equals(request.getParameter("Username"))) && (password1.equals(request.getParameter("Password"))))
		{
		  // if username-password correct, display photo gallery things
		  PrintWriter out = response.getWriter();
		  out.print(1);
		  response.sendRedirect("/midp/PhotoGallery");
		}
		try {
			Connection conn = null;
			Statement stmt = null;
			
			Class.forName(JDBC_DRIVER);
			conn = DriverManager.getConnection(DB_URL, USERNAME, PASS);
			stmt = conn.createStatement();
			String query =  "SELECT EXISTS(SELECT * FROM users WHERE "+ "Username = \"%s\" AND " + "`Password` = \"%s\");";
			
			ResultSet rs = stmt.executeQuery(String.format(query, request.getParameter("Username"), request.getParameter("Password")));
			
			if (rs.isBeforeFirst()) {
				response.sendRedirect("/midp/PhotoGallery");
			}
			else 
			{
				// if username-password pair not recognized, go back to login form
				response.setStatus(response.SC_MOVED_TEMPORARILY);
				response.setHeader("Location", "http://localhost:8081/midp/login_form.html");   
			}
			
			stmt.close();
		}
		catch (SQLException se) {
			se.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}