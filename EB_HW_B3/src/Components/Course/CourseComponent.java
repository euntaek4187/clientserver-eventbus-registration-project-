/**
 * Copyright(c) 2021 All rights reserved by Jungho Kim in Myungji University
 */
package Components.Course;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
public class CourseComponent {
	public static String fileName;
    protected ArrayList<Course> vCourse;
    public CourseComponent(String sCourseFileName) throws FileNotFoundException, IOException { 	
    	fileName = sCourseFileName;
        BufferedReader bufferedReader  = new BufferedReader(new FileReader(sCourseFileName));       
        this.vCourse  = new ArrayList<Course>();
        while (bufferedReader.ready()) {
            String courseInfo = bufferedReader.readLine();
            if(!courseInfo.equals("")) this.vCourse.add(new Course(courseInfo));
        }    
        bufferedReader.close();
    }
    public ArrayList<Course> getCourseList() {
        return this.vCourse;
    }
	public void deleteCourse(Course courseInfo) {
		this.vCourse.remove(courseInfo);
	}
	public Course getCourse(String courseId) {
	    for (Course course : this.vCourse) {
	        if (course.match(courseId)) return course;
	    }
	    return null;
	}
    public boolean isRegisteredCourse(String courseId) {
        for (int i = 0; i < this.vCourse.size(); i++) {
            if(((Course) this.vCourse.get(i)).match(courseId)) return true;
        }
        return false;
    }
	static void updateCourseDataToFile(CourseComponent coursesList) {
	    try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
	        for (Course course : coursesList.getCourseList()) writer.write(course.getString() + "\n");
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
}