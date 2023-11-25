/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in MyungJi University 
 */

package Components.Registration;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import Components.Course.Course;
import Components.Course.CourseComponent;
import Components.Student.Student;
import Components.Student.StudentComponent;
import Framework.Event;
import Framework.EventId;
import Framework.EventQueue;
import Framework.RMIEventBus;

public class RegistrationMain {
	public static void main(String args[]) throws FileNotFoundException, IOException, NotBoundException {
		RMIEventBus eventBus = (RMIEventBus) Naming.lookup("EventBus");
		long componentId = eventBus.register();
		System.out.println("** RegistrationMain(ID:" + componentId + ") is successfully registered. \n");
		
		Event event = null;
		boolean done = false;
		while (!done) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			EventQueue eventQueue = eventBus.getEventQueue(componentId);
			for (int i = 0; i < eventQueue.getSize(); i++) {
				event = eventQueue.getEvent();
				switch (event.getEventId()) {
				case Registration:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, registration(event.getMessage())));
					break;
				default:
					break;
				}
			}
		}
	}

	private static String registration(String message) throws FileNotFoundException, IOException {
		StudentComponent studentsList = new StudentComponent("C:\\Users\\Owner\\Documents\\명지대학교\\2023년\\2학기\\클라이언트서버프로그래밍\\clientserver-eventbus-registration-project-\\EB_HW_B\\src\\Students.txt");
		CourseComponent coursesList = new CourseComponent("C:\\Users\\Owner\\Documents\\명지대학교\\2023년\\2학기\\클라이언트서버프로그래밍\\clientserver-eventbus-registration-project-\\EB_HW_B\\src\\Courses.txt");
		String studentFile = "";
		String studentID = message.split(" ")[0];
		String courseID = message.split(" ")[1];
		Student student = null;
		Course course = null;
		if(!studentsList.isRegisteredStudent(studentID)) return "student: " + message+ " is not exist";
		if(!coursesList.isRegisteredCourse(courseID)) return "course: " + message+ " is not exist";
		ArrayList<Student> studentList = studentsList.getStudentList();
		ArrayList<Course> courseList = coursesList.getCourseList();
		for(Course courseInfo : courseList) {
			if (courseInfo.getString().split(" ")[0].equals(courseID)) course = courseInfo;
		}
		for(Student studentInfo : studentList) {
			if (studentInfo.getString().split(" ")[0].equals(studentID)) {
				student = studentInfo;
				if (student.getCompletedCourses().containsAll(course.getprerequisiteCourses())) {
					studentFile += studentInfo.getString() + " " + courseID + "\n";
				} else {
					return "선수과목 충족을 하지못해 강의를 신청할 수 없습니다.";
				}
			} else {
				studentFile += studentInfo.getString() + "\n";
			}
		}
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Owner\\Documents\\명지대학교\\2023년\\2학기\\클라이언트서버프로그래밍\\clientserver-eventbus-registration-project-\\EB_HW_B\\src\\Students.txt"))) {
	        writer.write(studentFile);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
		return "student: " + studentID+ " is register course: "+ courseID;

	}

	private static void printLogEvent(String comment, Event event) {
		System.out.println(
				"\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage());
	}
}
