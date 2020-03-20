import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
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

@WebServlet(name = "TestServlet", urlPatterns = { "/upload" }, loadOnStartup = 1)
@MultipartConfig(fileSizeThreshold = 6291456, // 6 MB
		maxFileSize = 10485760L, // 10 MB
		maxRequestSize = 20971520L // 20 MB
)
public class TestServlet extends HttpServlet {
	// variables needed in class
	private String img_caption;
	private String img_longitude;
	private String img_latitude;
	private String img_date;
	private String user;
	private String DB_URL;
	private String fileName;
	private String filePath;

	public void init() {
		filePath = System.getProperty("catalina.base") + "/webapps/photogallery/data/";
		DB_URL = "jdbc:sqlite:"+System.getProperty("catalina.base") + "\\webapps\\photogallery\\WEB-INF\\classes\\PhotoGallery.db";
	}


	// have this in case Tomcat servlet 3.0 (where getSubmitedFileName is undefined) used instead of 3.1
	private static String getSubmittedFileName(Part part) {
		for (String cd : part.getHeader("content-disposition").split(";")) {
			if (cd.trim().startsWith("filename")) {
				String file_Name = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
				return file_Name.substring(file_Name.lastIndexOf('/') + 1).substring(file_Name.lastIndexOf('\\') + 1); // MSIE fix.
			}
		}
		return null;
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = response.getWriter();

	}
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
	    PrintWriter out = response.getWriter();

		// check login credentials
		String username = request.getParameter("Username");
		String password = request.getParameter("Password");
		if (validateLogin(username, password)) 
		{
			// login is valid
			out.println("login succeeded");
			
			Part filePart = request.getPart("image");
		
			// only do next steps if an image came in the multipart data package
			if (filePart != null) {
				// pull required data out of multiform data package
				fileName = getSubmittedFileName(filePart);
				img_caption = request.getParameter("caption");
				img_longitude = request.getParameter("longitude");
				img_latitude = request.getParameter("latitude");
				img_date = request.getParameter("date");
				user = username; // put into "test" user for now

				// other data or variables needed
				InputStream fileContent = filePart.getInputStream();
				Connection conn = null;
				Statement stmt = null;
				
				try {
					// put information into database
					//Class.forName("org.sqlite.JDBC");
					conn = DriverManager.getConnection(DB_URL);
					stmt = conn.createStatement();

					String sql =  "INSERT INTO photodata (path, caption, latitude, longitude, date, UID) VALUES " +
						   String.format("(\"%s\", \"%s\", \"%s\", \"%s\", \"%s\", (SELECT UID FROM users WHERE Username = \"%s\"));",
								   fileName,
								   img_caption,
								   img_latitude,
								   img_longitude,
								   img_date,
								   user);
					//out.println(sql);
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
					   filePart.write(filePath + File.separator + fileName); // move picture to directory
					   //Files.move(Paths.get(filePathTemp + fileName), Paths.get(filePath + fileName));
					} catch (Exception e) {}

					//response.sendRedirect("/photogallery/PhotoGallery");
				}
			}
		}
		else
		{
			// login is invalid
			out.println("login failed");
		}

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