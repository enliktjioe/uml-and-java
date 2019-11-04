package EnrollmentManager;
import java.util.List;
import java.util.Scanner;

public class Boundary {

	
	public static void main(String[] args) {
		System.out.println("Do you want to initialize the database? (Y/N)");
		Scanner scan= new Scanner(System.in);
		String line = scan.nextLine();
		if(line.equals("Y") || line.equals("y")){
			DatabaseManager.resetDatabase();
			DatabaseManager.initializeDatabase();
		}
		while(true){
			System.out.println("Select an option:");
			System.out.println("1. Create a new course");
			System.out.println("2. Register a new student");
			System.out.println("3. Enroll an existing student in an existing course");
			System.out.println("4. Get students");
			System.out.println("5. Exit");
			int choice = new Integer(scan.nextLine());
			switch (choice){ 
			case 1:
				System.out.println("Insert course title:");
				boolean courseExists = Controller.createCourse(scan.nextLine());
				if(!courseExists)
					System.out.println("Course created!");
				else
					System.out.println("Course already exists!");
				break;
			case 2:
				System.out.println("Insert student ID:");
				boolean studentExists = Controller.registerStudent(scan.nextLine());
				if(!studentExists)
					System.out.println("Student registered!");
				else
					System.out.println("Student already exists!");
				break;
			case 3:
				System.out.println("Insert course title:");
				String courseTitle = scan.nextLine();
				System.out.println("Insert student ID:");
				String studentID = scan.nextLine();
				int enrolled = Controller.enrollStudent(studentID, courseTitle);
				if(enrolled == 0)
					System.out.println("Student enrolled in the course!");
				if(enrolled == -1)
					System.out.println("Student does not exist!");
				if(enrolled == -2)
					System.out.println("Course does not exist!");
				if(enrolled == -3)
					System.out.println("Student already enrolled in the course!");
				break;
			case 4:
				System.out.println("Insert course title:");
				String course = scan.nextLine();
				List<Student> students = Controller.getStudents(course);
				System.out.println("Students enrolled for "+course+":");
				for(Student s : students){
					System.out.println(s.getID());
				}
				break;
			default:
				choice = 5;
				break;
			}
			if(choice == 5)
				break;
		}
		scan.close();
	}
}
