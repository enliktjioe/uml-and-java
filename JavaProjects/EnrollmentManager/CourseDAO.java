package EnrollmentManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CourseDAO {

	public static boolean courseWithTitleExists(String title){
		Connection con = null;
		Statement stmt = null;
		ResultSet result = null;
		boolean output = false;
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			con = DriverManager.getConnection(
					"jdbc:hsqldb:hsql://localhost/universitydb", "SA", "");
			stmt = con.createStatement();
			result = stmt.executeQuery(
					"SELECT * FROM course WHERE title = '"+title+"'");
			while(result.next()){
				output= true;
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return output;
	}


	public static boolean insertCourse(Course course){
		Connection con = null; 
		Statement stmt = null; 
		int result = 0; 
		try { 
			Class.forName("org.hsqldb.jdbc.JDBCDriver"); 
			con = DriverManager.getConnection( "jdbc:hsqldb:hsql://localhost/universitydb", "SA", ""); 
			stmt = con.createStatement(); 
			result = stmt.executeUpdate("INSERT INTO course VALUES ('"+course.getTitle()+"')"); 
			con.commit();
		}catch (Exception e) { 
			e.printStackTrace(System.out); 
		} 
		if(result == 0){
			return false;
		}
		else{
			return true;
		}
	}
}