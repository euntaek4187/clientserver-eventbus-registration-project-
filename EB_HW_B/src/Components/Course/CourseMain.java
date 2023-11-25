/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in MyungJi University 
 */
package Components.Course;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.util.ArrayList;

import Components.Student.Student;
import Components.Student.StudentComponent;
import Framework.Event;
import Framework.EventId;
import Framework.EventQueue;
import Framework.RMIEventBus;

public class CourseMain {
	public static void main(String[] args) throws FileNotFoundException, IOException, NotBoundException {
		RMIEventBus eventBus = (RMIEventBus) Naming.lookup("EventBus");
		long componentId = eventBus.register();
		System.out.println("CourseMain (ID:" + componentId + ") is successfully registered...");

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
				case ListCourses:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, makeCourseList()));
					break;
				case RegisterCourses:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, registerCourse(event.getMessage())));
					break;
				case DeleteCourses:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, deleteCourse(event.getMessage())));
					break;
				case QuitTheSystem:
					eventBus.unRegister(componentId);
					done = true;
					break;
				default:
					break;
				}
			}
		}
	}
	private static String registerCourse(String message) throws FileNotFoundException, IOException {
		CourseComponent coursesList = new CourseComponent("C:\\Users\\Owner\\Documents\\명지대학교\\2023년\\2학기\\클라이언트서버프로그래밍\\clientserver-eventbus-registration-project-\\EB_HW_B\\src\\Courses.txt");
		Course course = new Course(message);
		if (!coursesList.isRegisteredCourse(course.courseId)) {
			String courseInfo = "";
			for (int j = 0; j < coursesList.vCourse.size(); j++) {
				courseInfo += coursesList.getCourseList().get(j).getString() + "\n";
			}
			courseInfo += course.getString() + "\n";
		    try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Owner\\Documents\\명지대학교\\2023년\\2학기\\클라이언트서버프로그래밍\\clientserver-eventbus-registration-project-\\EB_HW_B\\src\\Courses.txt"))) {
		        writer.write(courseInfo);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
			return "This course is successfully added.";
		} else
			return "This course is already registered.";
	}
	private static String makeCourseList() throws FileNotFoundException, IOException {
		CourseComponent coursesList = new CourseComponent("C:\\Users\\Owner\\Documents\\명지대학교\\2023년\\2학기\\클라이언트서버프로그래밍\\clientserver-eventbus-registration-project-\\EB_HW_B\\src\\Courses.txt");
		String returnString = "";
		for (int j = 0; j < coursesList.vCourse.size(); j++) {
			returnString += coursesList.getCourseList().get(j).getString() + "\n";
		}
		return returnString;
	}
	private static String deleteCourse(String message) throws FileNotFoundException, IOException {
		CourseComponent coursesList = new CourseComponent("C:\\Users\\Owner\\Documents\\명지대학교\\2023년\\2학기\\클라이언트서버프로그래밍\\clientserver-eventbus-registration-project-\\EB_HW_B\\src\\Courses.txt");
	    String courseInfo = "";
	    boolean isExist = false;
	    for(Course courseLine : coursesList.getCourseList()) {
	        if(!courseLine.getString().split(" ")[0].equals(message)) {
	            courseInfo += courseLine.getString() + "\n";
	        } else isExist = true;
	    }
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Owner\\Documents\\명지대학교\\2023년\\2학기\\클라이언트서버프로그래밍\\clientserver-eventbus-registration-project-\\EB_HW_B\\src\\Courses.txt"))) {
	        writer.write(courseInfo);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    if (isExist) return "course: " + message+ " is deleted";
	    else return "course: " + message+ " is not exist";
	}
	private static void printLogEvent(String comment, Event event) {
		System.out.println(
				"\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage());
	}
}
