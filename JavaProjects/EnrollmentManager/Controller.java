package EnrollmentManager;
import java.util.List;

public class Controller {

	public static boolean createCourse(String courseTitle) {
		boolean exists = CourseDAO.courseWithTitleExists(courseTitle);
		if(!exists){
			Course course = new Course();
			course.setTitle(courseTitle);
			CourseDAO.insertCourse(course);	 
		}
		return exists;
	}

	public static boolean registerStudent(String studentID) {
		boolean exists = StudentDAO.studentWithIDExists(studentID);
		if(!exists){
			Student student = new Student();
			student.setID(studentID);
			StudentDAO.insertStudent(student);	 
		}
		return exists;
	}

	public static int enrollStudent(String studentID, String courseTitle) {
		
		boolean studentExists = StudentDAO.studentWithIDExists(studentID);
		if(!studentExists)
			return -1;
		
		boolean courseExists = CourseDAO.courseWithTitleExists(courseTitle);
		if(!courseExists)
			return -2;
		
		boolean enrollmentExists = EnrollmentDAO.enrollmentExists(studentID, courseTitle);
		if(enrollmentExists)
			return -3;
		
		EnrollmentDAO.insertEnrollment(studentID, courseTitle);		
		
		return 0;
	
	}

	
	public static List<Student> getStudents(String courseTitle) {
		return EnrollmentDAO.getStudents(courseTitle);
	}
}
