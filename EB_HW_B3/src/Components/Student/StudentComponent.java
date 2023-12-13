/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in MyungJi University 
 */
package Components.Student;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
public class StudentComponent {
	public static String fileName;
	protected ArrayList<Student> vStudent;
	public StudentComponent(String sStudentFileName) throws FileNotFoundException, IOException {
		fileName = sStudentFileName;
		BufferedReader bufferedReader = new BufferedReader(new FileReader(sStudentFileName));
		this.vStudent = new ArrayList<Student>();
		while (bufferedReader.ready()) {
			String stuInfo = bufferedReader.readLine();
			if (!stuInfo.equals("")) this.vStudent.add(new Student(stuInfo));
		}
		bufferedReader.close();
	}
	public ArrayList<Student> getStudentList() {
		return vStudent;
	}
	public void setvStudent(ArrayList<Student> vStudent) {
		this.vStudent = vStudent;
	}
	public void deleteStudent(Student studentInfo) {
		this.vStudent.remove(studentInfo);
	}
	public Student getStudent(String studentId) {
	    for (Student student : this.vStudent) {
	        if (student.match(studentId)) return student;
	    }
	    return null;
	}
	public boolean isRegisteredStudent(String sSID) {
		for (int i = 0; i < this.vStudent.size(); i++) {
			if (((Student) this.vStudent.get(i)).match(sSID)) return true;
		}
		return false;
	}
	public static void updateStudentDataToFile(StudentComponent studentsList) {
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
	        for (Student student : studentsList.getStudentList()) writer.write(student.getString() + "\n");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}