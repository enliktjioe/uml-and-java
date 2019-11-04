package EnrollmentManager;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EnrollmentDAO {

	public static boolean enrollmentExists(String studentID, String courseTitle){
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
					"SELECT * FROM enrollment WHERE studentID = '"+studentID+"' AND title = '"+courseTitle+"';");
			while(result.next()){
				output= true;
			}			
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return output;
	}


	public static boolean insertEnrollment(String studentID, String courseTitle){
		Connection con = null; 
		Statement stmt = null; 
		int result = 0; 
		try { 
			Class.forName("org.hsqldb.jdbc.JDBCDriver"); 
			con = DriverManager.getConnection( "jdbc:hsqldb:hsql://localhost/universitydb", "SA", ""); 
			stmt = con.createStatement(); 
			result = stmt.executeUpdate("INSERT INTO enrollment VALUES ('"+studentID+"','"+courseTitle+"');"); 
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

	public static List<Student> getStudents(String courseTitle){
		Connection con = null;
		Statement stmt = null;
		ResultSet result = null;
		ArrayList<Student> output = new ArrayList<Student>();
		try {
			Class.forName("org.hsqldb.jdbc.JDBCDriver");
			con = DriverManager.getConnection(
					"jdbc:hsqldb:hsql://localhost/universitydb", "SA", "");
			stmt = con.createStatement();
			result = stmt.executeQuery(
					"SELECT studentID FROM enrollment WHERE title = '"+courseTitle+"'");

			while(result.next()){
				Student s = new Student();
				s.setID(result.getString("studentID"));
				output.add(s);
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return output;
	}
}