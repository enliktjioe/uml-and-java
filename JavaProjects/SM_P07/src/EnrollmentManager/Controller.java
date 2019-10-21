package EnrollmentManager;

import java.util.List;

/**
 * @(#) EnrollmentManager.Controller.java
 */

public class Controller
{
	public static boolean createCourse( String courseName )
	{
		if (courseName != null){
			Course co = new Course();
			co.setTitle(courseName);
			CourseDAO.insertCourse();
			return true;
		}

		else{
			return false;
		}
	}
	
	public static boolean registerStudent( String studentID )
	{
		return false;
	}
	
	public static List<Student> getStudents(String courseTitle )
	{

		return null;
	}
	
	public static int enrollStudent( String studentID, String courseTitle )
	{
		return 0;
	}
	
	
}
