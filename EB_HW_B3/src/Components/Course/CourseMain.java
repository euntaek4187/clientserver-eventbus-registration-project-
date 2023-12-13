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
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import Framework.Event;
import Framework.EventId;
import Framework.EventQueue;
import Framework.RMIEventBus;
public class CourseMain {
	public static String fileName = "C:\\Users\\Owner\\Documents\\명지대학교\\2023년\\2학기\\클라이언트서버프로그래밍\\clientserver-eventbus-registration-project-\\EB_HW_B3\\src\\Courses.txt";
	public static void main(String[] args) throws FileNotFoundException, IOException, NotBoundException, InterruptedException {
		RMIEventBus eventBus = (RMIEventBus) Naming.lookup("EventBus");
		long componentId = eventBus.register();
		System.out.println("CourseMain (ID:" + componentId + ") is successfully registered...");
		CourseComponent coursesList = new CourseComponent(fileName);
		Event event = null;
		boolean done = false;
		while (!done) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			EventQueue eventQueue = eventBus.getEventQueue(componentId);
			for (int i = 0; i < eventQueue.getSize(); i++) {
				event = eventQueue.getEvent();
				switch (event.getEventId()) {
				case ListCourses:
					printLogEvent("Get", event);
					Thread.sleep(100);
					eventBus.sendEvent(new Event(EventId.ClientOutput, makeCourseList(coursesList)));
					break;
				case AddCourses:
					printLogEvent("Get", event);
					Thread.sleep(100);
					eventBus.sendEvent(new Event(EventId.ClientOutput, addCourse(coursesList, event.getMessage())));
					break;
				case DeleteCourses:
					printLogEvent("Get", event);
					Thread.sleep(100);
					eventBus.sendEvent(new Event(EventId.ClientOutput, deleteCourse(coursesList, event.getMessage())));
					break;
				case CheckPrerequisiteValidation:
					printLogEvent("Get", event);
				    checkPrerequisiteValidation(event.getMessage(), coursesList, eventBus);
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
	private static void checkPrerequisiteValidation(String message, CourseComponent coursesList, RMIEventBus eventBus) throws InterruptedException, RemoteException {
	    String[] parts = message.split(" ");
	    String studentID = parts[0];
	    String courseID = parts[parts.length - 1];
	    ArrayList<String> studentCompletedCoursesList = new ArrayList<>(Arrays.asList(parts).subList(1, parts.length - 1));
	    if (!coursesList.isRegisteredCourse(courseID)) {
	        System.out.println("[Faild] course: " + courseID + " is not exist");
	        eventBus.sendEvent(new Event(EventId.ClientOutput, "[Faild] course: " + courseID + " is not exist"));
	    } else {
	        ArrayList<String> coursePrerequisiteCoursesList = coursesList.getCourse(courseID).getprerequisiteCourses();
	        if (studentCompletedCoursesList.containsAll(coursePrerequisiteCoursesList)) {
	            Thread.sleep(100);
	            System.out.println("[Success] success: All prerequisites satisfied.");
	            eventBus.sendEvent(new Event(EventId.RegistrationStudentCourse, studentID + " " + courseID));
	        } else {
	        	Thread.sleep(100);
	            System.out.println("[Faild] Not all prerequisites are satisfied.");
	            eventBus.sendEvent(new Event(EventId.ClientOutput, "[Faild] student: " + studentID + " is not all prerequisites are satisfied. course: " + courseID));
	        }
	    }
	}
	private static String addCourse(CourseComponent coursesList, String message) {
		Course course = new Course(message);
		if (!coursesList.isRegisteredCourse(course.courseId)) {
			coursesList.vCourse.add(course);
			CourseComponent.updateCourseDataToFile(coursesList);
			return "[Success] course: "+ course.courseId +" is successfully added.";
		} else return "[Faild] course: "+ course.courseId +" is already registered.";
	}
	private static String makeCourseList(CourseComponent coursesList) {
		String returnString = "[Success] show CourseList \n";
		for (int j = 0; j < coursesList.vCourse.size(); j++) returnString += coursesList.getCourseList().get(j).getString() + "\n";
		return returnString;
	}
	private static String deleteCourse(CourseComponent coursesList, String message) {
	    if (!coursesList.isRegisteredCourse(message)) return "[Faild] course: " + message + " is not exist";
	    else coursesList.deleteCourse(coursesList.getCourse(message));
		CourseComponent.updateCourseDataToFile(coursesList);
	    return "[Success] course: " + message + " is deleted";
	}
	private static void printLogEvent(String comment, Event event) {
		System.out.println("\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage());
	}
}