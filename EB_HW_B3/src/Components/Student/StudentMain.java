/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in MyungJi University 
 */
package Components.Student;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import Framework.Event;
import Framework.EventId;
import Framework.EventQueue;
import Framework.RMIEventBus;
public class StudentMain {
	public static String fileName = "C:\\Users\\Owner\\Documents\\명지대학교\\2023년\\2학기\\클라이언트서버프로그래밍\\clientserver-eventbus-registration-project-\\EB_HW_B3\\src\\Students.txt";
	public static void main(String args[]) throws FileNotFoundException, IOException, NotBoundException, InterruptedException {
		RMIEventBus eventBus = (RMIEventBus) Naming.lookup("EventBus");
		long componentId = eventBus.register();
		System.out.println("** StudentMain(ID:" + componentId + ") is successfully registered. \n");
		StudentComponent studentsList = new StudentComponent(fileName);
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
				case ListStudents:
					printLogEvent("Get", event);
					Thread.sleep(100);
					eventBus.sendEvent(new Event(EventId.ClientOutput, makeStudentList(studentsList)));
					break;
				case AddStudents:
					printLogEvent("Get", event);
					Thread.sleep(100);
					eventBus.sendEvent(new Event(EventId.ClientOutput, addStudent(studentsList, event.getMessage())));
					break;
				case DeleteStudents:
					printLogEvent("Get", event);
					Thread.sleep(100);
					eventBus.sendEvent(new Event(EventId.ClientOutput, deleteStudent(studentsList, event.getMessage())));
					break;
				case CheckStudentValidation:
					printLogEvent("Get", event);
				    checkStudentValidation(event.getMessage(), studentsList, eventBus);
					break;
				case RegistrationStudentCourse:
					printLogEvent("Get", event);
					registrationStudentCourse(studentsList, event.getMessage(), eventBus);
					break;
				case QuitTheSystem:
					printLogEvent("Get", event);
					eventBus.unRegister(componentId);
					done = true;
					break;
				default:
					break;
				}
			}
		}
	}
	private static void checkStudentValidation(String message, StudentComponent studentsList, RMIEventBus eventBus) throws InterruptedException, RemoteException {
	    String studentID = message.split(" ")[0];
	    String courseID = message.split(" ")[1];
	    if (!studentsList.isRegisteredStudent(studentID)) {
	        System.out.println("[Faild] student: " + studentID + " is not exist");
	        eventBus.sendEvent(new Event(EventId.ClientOutput, "[Faild] student: " + studentID + " is not exist"));
	    } else {
	        ArrayList<String> studentCompletedCoursesList = studentsList.getStudent(studentID).getCompletedCourses();
	        if (studentCompletedCoursesList.contains(courseID)) eventBus.sendEvent(new Event(EventId.ClientOutput, "[Faild] student: " + studentID + " is already register courseID: " + courseID));
	        else {
	            String newMessage = studentID + " " + String.join(" ", studentCompletedCoursesList) + " " + courseID;
	            Thread.sleep(100);
	            eventBus.sendEvent(new Event(EventId.CheckPrerequisiteValidation, newMessage));
	        }
	    }
	}
	private static void registrationStudentCourse(StudentComponent studentsList, String message, RMIEventBus eventBus) throws RemoteException, InterruptedException {
	    String studentID = message.split(" ")[0];
	    String courseID = message.split(" ")[1];
	    Student student = studentsList.getStudent(studentID);
        student.getCompletedCourses().add(courseID);
        StudentComponent.updateStudentDataToFile(studentsList);
        Thread.sleep(100);
		eventBus.sendEvent(new Event(EventId.ClientOutput, "[Success] student: " + studentID + " is register courseID: " + courseID));
	}
	private static String addStudent(StudentComponent studentsList, String message) {
		Student student = new Student(message);
		if (!studentsList.isRegisteredStudent(student.studentId)) {
			studentsList.vStudent.add(student);
	        StudentComponent.updateStudentDataToFile(studentsList);
			return "[Success] student: " + student.studentId + " is successfully added.";
		} else return "[Faild] student: " + student.studentId + " is already registered.";
	}
	private static String makeStudentList(StudentComponent studentsList) {
		String returnString = "[Success] show StudentList \n";
		for (int j = 0; j < studentsList.vStudent.size(); j++) returnString += studentsList.getStudentList().get(j).getString() + "\n";
		return returnString;
	}
	private static String deleteStudent(StudentComponent studentsList, String message) {
	    if (!studentsList.isRegisteredStudent(message)) return "[Faild] student: " + message + " is not exist";
	    else studentsList.deleteStudent(studentsList.getStudent(message));
        StudentComponent.updateStudentDataToFile(studentsList);
	    return "[Success] student: " + message + " is deleted";
	}
	private static void printLogEvent(String comment, Event event) {
		System.out.println("\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage());
	}
}