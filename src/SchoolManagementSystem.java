import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This application will keep track of things like what classes are offered by
 * the school, and which students are registered for those classes and provide
 * basic reporting. This application interacts with a database to store and
 * retrieve data.
 */
public class SchoolManagementSystem {

	//#5 perfect
	public static void getAllClassesByInstructor(String first_name, String last_name) {
		Connection connection = null;
		Statement sqlStatement = null;
		ResultSet result = null;
		try {
			/* Your logic goes here */
			connection = Database.getDatabaseConnection();
			sqlStatement = connection.createStatement();
			String query = "SELECT instructors.first_name,\n" + 
					"instructors.last_name,\n" + 
					"academic_titles.title,\n" + 
					"classes.code,\n" + 
					"classes.name as class_name,\n" + 
					"terms.name   as term\n" + 
					"FROM class_sections\n" + 
					"INNER JOIN classes\n" + 
					"ON class_sections.class_id = classes.class_id\n" + 
					"INNER JOIN terms\n" + 
					"ON class_sections.term_id = terms.term_id\n" + 
					"INNER JOIN instructors\n" + 
					"ON instructors.instructor_id = class_sections.instructor_id\n" + 
					"INNER JOIN academic_titles\n" + 
					"ON instructors.academic_title_id = academic_titles.academic_title_id\n" + 
					String.format("WHERE instructors.first_name='%s' AND instructors.last_name='%s'", first_name, last_name);
			result = sqlStatement.executeQuery(query);
			ResultSetMetaData rsmd = result.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			System.out.println("First Name | Last Name | Title | Code | Name | Term"); 
			System.out.println("--------------------------------------------------------------------------------");
			while (result.next()) {
			    for (int i = 1; i <= columnsNumber; i++) {
			        if (i > 1) System.out.print(" | ");
			        String columnValue = result.getString(i);
			        System.out.print(columnValue);
			    }
			    System.out.println("");
			}	
		} catch (SQLException sqlException) {
			System.out.println("Failed to get class sections");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (sqlStatement != null)
					sqlStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}

	}
	
	//#9 appears to work correctly
	public static void submitGrade(String studentId, String classSectionID, String grade) {
		Connection connection = null;
		Statement sqlStatement = null;

		try {
			/* Your logic goes here */
			connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            String query = ("UPDATE class_registrations\n" + String.format("SET grade_id=convert_to_grade_point('%s')", grade) +
                    String.format("WHERE student_id='%s' AND class_section_id='%s'", studentId, classSectionID));
            sqlStatement.executeUpdate(query);
            System.out.println("Grade has been submitted!");
		} catch (SQLException sqlException) {
			System.out.println("Failed to submit grade");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (sqlStatement != null)
					sqlStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}
	
	
	public static void registerStudent(String studentId, String classSectionID) {
		Connection connection = null;
		Statement sqlStatement = null;
		ResultSet result = null;
		try {
			/* Your logic goes here */
			connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            String query = "INSERT INTO class_registrations (class_section_id, student_id, grade_id)" +
                    String.format("VALUES ('%s', '%s', 1)", classSectionID, studentId);
            sqlStatement.executeUpdate(query);
            String getClassRegistrationQuery = (
                    "SELECT * FROM class_registrations " +
                            String.format("WHERE class_section_id='%s' AND student_id='%s'", classSectionID, studentId)
            );
            result = sqlStatement.executeQuery(query);
            System.out.println("Class Registration ID | Student ID | Class Section ID");
            System.out.println("--------------------------------------------------------------------------------");
            ResultSetMetaData rsmd = result.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (result.next()) {
			    for (int i = 1; i <= columnsNumber; i++) {
			        if (i > 1) System.out.print(" | ");
			        String columnValue = result.getString(i);
			        System.out.print(columnValue);
			    }
			    System.out.println("");
			}
            
		} catch (SQLException sqlException) {
			System.out.println("Failed to register student");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (sqlStatement != null)
					sqlStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}
	
	//perfect!
	public static void deleteStudent(String studentId) {
		Connection connection = null;
		Statement sqlStatement = null;

		try {
			/* Your logic goes here */
			connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            String update = String.format("DELETE FROM students WHERE student_id='%s'", studentId);
            sqlStatement.executeUpdate(update);
            System.out.println("--------------------------------------------------------------------------------");
            System.out.println(String.format("Student with id: %s was deleted", studentId));   
            
		} catch (SQLException sqlException) {
			System.out.println("Failed to delete student");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (sqlStatement != null)
					sqlStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	//perfect!
	public static void createNewStudent(String firstName, String lastName, String birthdate) {
		Connection connection = null;
		Statement sqlStatement = null;
		ResultSet result = null;
		try {
			/* Your logic goes here */
			connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            String update = "INSERT INTO students (first_name, last_name, birthdate) " 
            + String.format("VALUES ('%s', '%s','%s')", firstName, lastName, birthdate);
            sqlStatement.executeUpdate(update);
            
            String query = "Select * FROM students " 
            + String.format("WHERE students.first_name='%s' AND students.last_name='%s'", firstName, lastName);
            result = sqlStatement.executeQuery(query);
            System.out.println("Student ID | First Name | Last Name | Birthdate");
            System.out.println("--------------------------------------------------------------------------------");
            ResultSetMetaData rsmd = result.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (result.next()) {
			    for (int i = 1; i <= columnsNumber; i++) {
			        if (i > 1) System.out.print(" | ");
			        String columnValue = result.getString(i);
			        System.out.print(columnValue);
			    }
			    System.out.println("");
			}
            
		} catch (SQLException sqlException) {
			System.out.println("Failed to create student");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (sqlStatement != null)
					sqlStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}

	}
	
	//perfect
	public static void listAllClassRegistrations() {
		Connection connection = null;
		Statement sqlStatement = null;
		ResultSet result = null;
		try {
			/* Your logic goes here */
			connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            String query = "SELECT students.student_id, class_sections.class_section_id, students.first_name, students.last_name,classes.code, classes.name, terms.name, grades.letter_grade FROM class_registrations \n" + 
            		"INNER JOIN students ON students.student_id=class_registrations.student_id\n" + 
            		"INNER JOIN class_sections ON class_sections.class_section_id=class_registrations.class_section_id\n" + 
            		"INNER JOIN classes ON classes.class_id=class_sections.class_id\n" + 
            		"INNER JOIN terms ON class_sections.term_id=terms.term_id\n" + 
            		"INNER JOIN grades ON grades.grade_id=class_registrations.grade_id;"; 
            result = sqlStatement.executeQuery(query);
            System.out.println("Student ID | class_section_id | First Name | Last Name | Code | Name | Term | Letter Grade");
            System.out.println("--------------------------------------------------------------------------------");
            ResultSetMetaData rsmd = result.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (result.next()) {
			    for (int i = 1; i <= columnsNumber; i++) {
			        if (i > 1) System.out.print(" | ");
			        String columnValue = result.getString(i);
			        System.out.print(columnValue);
			    }
			    System.out.println("");
			}
			
		} catch (SQLException sqlException) {
			System.out.println("Failed to get class sections");
			System.out.println(sqlException.getMessage());
		} finally {
			try {
				if (sqlStatement != null)
					sqlStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}
	
	//perfect!
	public static void listAllClassSections() {
		Connection connection = null;
		Statement sqlStatement = null;
		ResultSet result = null;
		try {
			/* Your logic goes here */
			connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            String query = "SELECT class_sections.class_section_id, classes.code, classes.name, terms.name FROM class_sections " +
                    "INNER JOIN classes ON classes.class_id=class_sections.class_id INNER JOIN terms ON class_sections.term_id=terms.term_id";
            result = sqlStatement.executeQuery(query);
            System.out.println("Class Section ID | Code | Name | term");
            System.out.println("--------------------------------------------------------------------------------");
            ResultSetMetaData rsmd = result.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (result.next()) {
			    for (int i = 1; i <= columnsNumber; i++) {
			        if (i > 1) System.out.print(" | ");
			        String columnValue = result.getString(i);
			        System.out.print(columnValue);
			    }
			    System.out.println("");
			}
			
		} catch (SQLException sqlException) {
			System.out.println("Failed to get class sections");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (sqlStatement != null)
					sqlStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}
	
	//perfect!
	public static void listAllClasses() {
		Connection connection = null;
		Statement sqlStatement = null;
		ResultSet result = null;
		try {
			/* Your logic goes here */
			connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            String query = "SELECT * FROM classes";
            result = sqlStatement.executeQuery(query);
            System.out.println("Class ID | Code | Name | Description");
            System.out.println("--------------------------------------------------------------------------------");
			ResultSetMetaData rsmd = result.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			String columnValue;
			while (result.next()) {
			    for (int i = 1; i <= columnsNumber - 1; i++) {
			        if (i > 1) System.out.print(" | ");
			        if(i == 2) {
				        columnValue = result.getString(4);

			        } else if(i == 3) {
				        columnValue = result.getString(2);
			        } else if(i == 4) {
				        columnValue = result.getString(3);
			        } 
			        else {
			        columnValue = result.getString(i);
			        }
			        System.out.print(columnValue);
			    }
			    System.out.println("");
			}	
            
		} catch (SQLException sqlException) {
			System.out.println("Failed to get students");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (sqlStatement != null)
					sqlStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	//looks to work perfectly!
	public static void listAllStudents() {
		Connection connection = null;
		Statement sqlStatement = null;
		ResultSet result = null;
		try {
			/* Your logic goes here */
			connection = Database.getDatabaseConnection();
            sqlStatement = connection.createStatement();
            String query = "SELECT * FROM students";
            result = sqlStatement.executeQuery(query);
			System.out.println("Student ID | First Name | Last Name | Birthdate");
			System.out.println("--------------------------------------------------------------------------------");
			ResultSetMetaData rsmd = result.getMetaData();
			int columnsNumber = rsmd.getColumnCount();
			while (result.next()) {
			    for (int i = 1; i <= columnsNumber; i++) {
			        if (i > 1) System.out.print(" | ");
			        String columnValue = result.getString(i);
			        System.out.print(columnValue);
			    }
			    System.out.println("");
			}	
			
		} catch (SQLException sqlException) {
			System.out.println("Failed to get students");
			System.out.println(sqlException.getMessage());

		} finally {
			try {
				if (sqlStatement != null)
					sqlStatement.close();
			} catch (SQLException se2) {
			}
			try {
				if (connection != null)
					connection.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}

	/***
	 * Splits a string up by spaces. Spaces are ignored when wrapped in quotes.
	 *
	 * @param command - School Management System cli command
	 * @return splits a string by spaces.
	 */
	public static List<String> parseArguments(String command) {
		List<String> commandArguments = new ArrayList<String>();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
		while (m.find()) commandArguments.add(m.group(1).replace("\"", ""));
		return commandArguments;
	}

	public static void main(String[] args) {
		System.out.println("Welcome to the School Management System");
		System.out.println("-".repeat(80));

		Scanner scan = new Scanner(System.in);
		String command = "";

		do {
			System.out.print("Command: ");
			command = scan.nextLine();
			;
			List<String> commandArguments = parseArguments(command);
			command = commandArguments.get(0);
			commandArguments.remove(0);

			if (command.equals("help")) {
				System.out.println("-".repeat(38) + "Help" + "-".repeat(38));
				System.out.println("test connection \n\tTests the database connection");

				System.out.println("list students \n\tlists all the students");
				System.out.println("list classes \n\tlists all the classes");
				System.out.println("list class_sections \n\tlists all the class_sections");
				System.out.println("list class_registrations \n\tlists all the class_registrations");
				System.out.println("list instructor <first_name> <last_name>\n\tlists all the classes taught by that instructor");


				System.out.println("delete student <studentId> \n\tdeletes the student");
				System.out.println("create student <first_name> <last_name> <birthdate> \n\tcreates a student");
				System.out.println("register student <student_id> <class_section_id>\n\tregisters the student to the class section");

				System.out.println("submit grade <studentId> <class_section_id> <letter_grade> \n\tcreates a student");
				System.out.println("help \n\tlists help information");
				System.out.println("quit \n\tExits the program");
			} else if (command.equals("test") && commandArguments.get(0).equals("connection")) {
				Database.testConnection();
			} else if (command.equals("list")) {
				if (commandArguments.get(0).equals("students")) listAllStudents();
				if (commandArguments.get(0).equals("classes")) listAllClasses();
				if (commandArguments.get(0).equals("class_sections")) listAllClassSections();
				if (commandArguments.get(0).equals("class_registrations")) listAllClassRegistrations();

				if (commandArguments.get(0).equals("instructor")) {
					getAllClassesByInstructor(commandArguments.get(1), commandArguments.get(2));
				}
			} else if (command.equals("create")) {
				if (commandArguments.get(0).equals("student")) {
					createNewStudent(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
				}
			} else if (command.equals("register")) {
				if (commandArguments.get(0).equals("student")) {
					registerStudent(commandArguments.get(1), commandArguments.get(2));
				}
			} else if (command.equals("submit")) {
				if (commandArguments.get(0).equals("grade")) {
					submitGrade(commandArguments.get(1), commandArguments.get(2), commandArguments.get(3));
				}
			} else if (command.equals("delete")) {
				if (commandArguments.get(0).equals("student")) {
					deleteStudent(commandArguments.get(1));
				}
			} else if (!(command.equals("quit") || command.equals("exit"))) {
				System.out.println(command);
				System.out.println("Command not found. Enter 'help' for list of commands");
			}
			System.out.println("-".repeat(80));
		} while (!(command.equals("quit") || command.equals("exit")));
		System.out.println("Bye!");
	}
}

