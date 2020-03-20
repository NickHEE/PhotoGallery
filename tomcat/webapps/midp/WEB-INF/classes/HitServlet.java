import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.sql.*;
import java.io.*;

public class HitServlet extends HttpServlet {
	private int mCount;
  
	static final String DB_URL = "jdbc:sqlite:"+System.getProperty("catalina.base") + "\\webapps\\photogallery\\WEB-INF\\classes\\PhotoGallery.db";

  
  
  
  public void doGet(HttpServletRequest request,
      HttpServletResponse response)
      throws ServletException, IOException {
    // Set response content type


      response.setContentType("text/html");

      PrintWriter out = response.getWriter();
	  

      out.println("<html>\n" +
                "<body>\n" + 
                "<form action=\"/midp/hits\" method=\"POST\">\n" +
                "User: <input type=\"text\" name=\"Username\">\n"   +
                "<br />\n" +
                "Password: <input type=\"text\" name=\"Password\" />\n"   +
                "<input type=\"submit\" value=\"Submit\" />\n"
                + 
                "</form>\n</body>\n</html\n");

  }
// Method to handle POST method request.
  public void doPost(HttpServletRequest request,
                     HttpServletResponse response)
      throws ServletException, IOException {
response.setContentType("text/html");

		String username = request.getParameter("Username");
		String password = request.getParameter("Password");
		PrintWriter out = response.getWriter();
      
	  
	  
		if (validateLogin(username, password)) {

			HttpSession session = request.getSession(true);
			session.setAttribute("user", username);
			session.setAttribute("password", password);
			out.println("good");
			//response.sendRedirect("/photogallery/PhotoGallery");
		} else
		{
			// bad login
			out.println("<html>\n" +
                "<body>\n" + 
                "<form action=\"/midp/hits\" method=\"POST\">\n" +
                "USER: <input type=\"text\" name=\"Username\">\n"   +
                "<br />\n" +
                "PASSWORD: <input type=\"text\" name=\"Password\" />\n"   +
                "<input type=\"submit\" value=\"Submit\" />\n"
                + 
                "</form>\n</body>\n</html\n");
		}
		
		

      
		
		
		
		
		/*
		

String errMsg = "Testing";
     // Set response content type
try {
try {
Class.forName("oracle.jdbc.OracleDriver");
} catch (Exception ex) { }
		Connection con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "OraclePas125");
errMsg += "Con";
			Statement stmt = con.createStatement();
errMsg += "stmt";
			stmt.executeUpdate("INSERT INTO staff (name, address) VALUES ('"+ request.getParameter("first_name") + "','" + request.getParameter("last_name") +"')");
			stmt.close();
			con.close();
errMsg += "End";
		} 
		catch(SQLException ex) { 
				errMsg = errMsg + "\n--- SQLException caught ---\n"; 
				while (ex != null) { 
					errMsg += "Message: " + ex.getMessage (); 
					errMsg += "SQLState: " + ex.getSQLState (); 
					errMsg += "ErrorCode: " + ex.getErrorCode (); 
					ex = ex.getNextException(); 
					errMsg += "";
				} 
		} 
    PrintWriter out = response.getWriter();
      response.setContentType("text/html");

	  String title = "Using Post Method to Read Form Data";

      String docType =
      "<!doctype html public \"-//w3c//dtd html 4.0 " +
      "transitional//en\">\n";
      out.println(docType +
                "<html>\n" +
                "<head><title>" + title +  "</title></head>\n" +
                "<body bgcolor=\"#f0f0f0\">\n" +
                "<h1 align=\"center\">" + title + "</h1>\n" +
                "<ul>\n" +
                "  <li><b>First Name</b>: "
                + request.getParameter("first_name") + "\n" +
                "  <li><b>Last Name</b>: "
                + request.getParameter("last_name") + "\n" +
                "</ul>\n" +
                "</body></html>");
  
*/
	  }
public boolean validateLogin (String username, String password) {

		Connection conn = null;
		Statement stmt = null;

   		try {

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
		finally {
			try {
				conn.close();
			} catch (Exception e) {e.printStackTrace();}
		}

	}

}
