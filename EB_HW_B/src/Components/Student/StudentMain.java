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
import java.util.ArrayList;

import Framework.Event;
import Framework.EventId;
import Framework.EventQueue;
import Framework.RMIEventBus;

public class StudentMain {
	public static void main(String args[]) throws FileNotFoundException, IOException, NotBoundException {
		RMIEventBus eventBus = (RMIEventBus) Naming.lookup("EventBus");
		long componentId = eventBus.register();
		System.out.println("** StudentMain(ID:" + componentId + ") is successfully registered. \n");

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
				case ListStudents:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, makeStudentList()));
					break;
				case RegisterStudents:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, registerStudent(event.getMessage())));
					break;
				case DeleteStudents:
					printLogEvent("Get", event);
					eventBus.sendEvent(new Event(EventId.ClientOutput, deleteStudent(event.getMessage())));
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
	private static String registerStudent(String message) throws FileNotFoundException, IOException {
		StudentComponent studentsList = new StudentComponent("C:\\Users\\Owner\\Documents\\명지대학교\\2023년\\2학기\\클라이언트서버프로그래밍\\clientserver-eventbus-registration-project-\\EB_HW_B\\src\\Students.txt");
		Student student = new Student(message);
		if (!studentsList.isRegisteredStudent(student.studentId)) {
			String studentInfo = "";
			for (int j = 0; j < studentsList.vStudent.size(); j++) {
				studentInfo += studentsList.getStudentList().get(j).getString() + "\n";
			}
			studentInfo += student.getString() + "\n";
		    try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Owner\\Documents\\명지대학교\\2023년\\2학기\\클라이언트서버프로그래밍\\clientserver-eventbus-registration-project-\\EB_HW_B\\src\\Students.txt"))) {
		        writer.write(studentInfo);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
			return "This student is successfully added.";
		} else
			return "This student is already registered.";
	}
	private static String makeStudentList() throws FileNotFoundException, IOException {
		StudentComponent studentsList = new StudentComponent("C:\\Users\\Owner\\Documents\\명지대학교\\2023년\\2학기\\클라이언트서버프로그래밍\\clientserver-eventbus-registration-project-\\EB_HW_B\\src\\Students.txt");
		String returnString = "";
		for (int j = 0; j < studentsList.vStudent.size(); j++) {
			returnString += studentsList.getStudentList().get(j).getString() + "\n";
		}
		return returnString;
	}
	private static String deleteStudent(String message) throws FileNotFoundException, IOException {
		StudentComponent studentsList = new StudentComponent("C:\\Users\\Owner\\Documents\\명지대학교\\2023년\\2학기\\클라이언트서버프로그래밍\\clientserver-eventbus-registration-project-\\EB_HW_B\\src\\Students.txt");
	    String studentInfo = "";
	    boolean isExist = false;
	    for(Student studentLine : studentsList.getStudentList()) {
	        if(!studentLine.getString().split(" ")[0].equals(message)) {
	            studentInfo += studentLine.getString() + "\n";
	        } else isExist = true;
	    }
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter("C:\\Users\\Owner\\Documents\\명지대학교\\2023년\\2학기\\클라이언트서버프로그래밍\\clientserver-eventbus-registration-project-\\EB_HW_B\\src\\Students.txt"))) {
	        writer.write(studentInfo);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    if (isExist) return "student: " + message+ " is deleted";
	    else return "student: " + message+ " is not exist";
	}


	private static void printLogEvent(String comment, Event event) {
		System.out.println(
				"\n** " + comment + " the event(ID:" + event.getEventId() + ") message: " + event.getMessage());
	}
}
