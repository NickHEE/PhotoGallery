// Import required java libraries
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

// Extend HttpServlet class
public class LoginServlet extends HttpServlet {
	
	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:sqlite:F:\\COMP_Project\\PhotoGallery\\tomcat\\webapps\\photogallery\\WEB-INF\\classes\\PhotoGallery.db";
  
	static final String USERNAME = "root";
	static final String PASS = "ilovebobgill69";

   // Method to handle GET method request.
   public void doGet(HttpServletRequest request, HttpServletResponse response)
	throws ServletException, IOException {
	  
		// Set response content type
		response.setContentType("text/html");

		HttpSession session = request.getSession(false);

		if (session != null) {

			String user = (String) session.getAttribute("user");
			String password = (String) session.getAttribute("password");

//			java.util.Date creationTime = new java.util.Date(session.getCreationTime());
//			java.util.Date now = new java.util.Date();
//
//			long diffMS = Math.abs(now.getTime()) - creationTime.getTime();
//			long sessionHrs = TimeUnit.HOURS.convert(diffMS, TimeUnit.MILLISECONDS);

			if (validateLogin(user, password)) {
				response.sendRedirect("/photogallery/PhotoGallery");
			}
			else {
				session.invalidate();
			}

		}
   }

   // Method to handle POST method request.
   public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
      
		String button = request.getParameter("button_press");
		String username = request.getParameter("Username");
		String password = request.getParameter("Password");

	   	PrintWriter out = response.getWriter();

		if (validateLogin(username, password)) {

			HttpSession session = request.getSession(true);
			session.setAttribute("user", username);
			session.setAttribute("password", password);

			response.sendRedirect("/photogallery/PhotoGallery");
		}
		else {
			response.setStatus(response.SC_MOVED_TEMPORARILY);
			response.setHeader("Location", "http://localhost:8081/photogallery/");
		}

   }

	public boolean validateLogin (String username, String password) {
		try {
			Connection conn = null;
			Statement stmt = null;

			conn = DriverManager.getConnection(DB_URL);
			stmt = conn.createStatement();
			String query =  "SELECT * FROM users WHERE "+ "Username = \"%s\" AND " + "Password = \"%s\";";

			ResultSet rs = stmt.executeQuery(String.format(query, username, password));

			return rs.isBeforeFirst();
		}
		catch (SQLException se) {
			se.printStackTrace();
			return false;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}