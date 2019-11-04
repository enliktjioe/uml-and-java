package EnrollmentManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class StudentDAO {

	public static boolean studentWithIDExists(String studentID){
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
					"SELECT * FROM student WHERE studentID = '"+studentID+"'");
			while(result.next()){
				output= true;
			}
		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		return output;
	}


	public static boolean insertStudent(Student student){
		Connection con = null; 
		Statement stmt = null; 
		int result = 0; 
		try { 
			Class.forName("org.hsqldb.jdbc.JDBCDriver"); 
			con = DriverManager.getConnection( "jdbc:hsqldb:hsql://localhost/universitydb", "SA", ""); 
			stmt = con.createStatement(); 
			result = stmt.executeUpdate("INSERT INTO student VALUES ('"+student.getID()+"')"); 
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