package com.coconuts.isaelasbordes.server;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Student {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
    @Persistent
    private String deviceRegistrationID;

	@Persistent
    private String name;

	@Persistent
    private String email;

	@Persistent
    private int instructor;

    public Student(String deviceRegistrationID, String name, String email, int instructor) {
        this.deviceRegistrationID = deviceRegistrationID;
        this.name = name;
        this.email = email;
        this.instructor = instructor;
    }

    // Accessors for the fields.  JDO doesn't use these, but your application does.

    public Key getKey() {
        return key;
    }

    public String getDeviceRegistrationID() {
		return deviceRegistrationID;
	}

	public void setDeviceRegistrationID(String deviceRegistrationID) {
		this.deviceRegistrationID = deviceRegistrationID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public int getInstructor() {
		return instructor;
	}

	public void setInstructor(int instructor) {
		this.instructor = instructor;
	}
	
	private static Student webApp = new Student("0000000000000000", "Application web", "webapp@volmoteurlasbordes.appspot.com", Instructor.getAllInstructor().getInstructorID());
	public  static Student getWebAppStudent() {
		return webApp;
	}
}