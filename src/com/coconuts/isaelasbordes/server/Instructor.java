package com.coconuts.isaelasbordes.server;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Instructor {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

	@Persistent
    private String name;

	@Persistent
    private String mdpHash;

	@Persistent
    private String email;

	@Persistent
    private int instructor;

    public Instructor(String name, String email, String mdpHash, int instructor) {
        this.name = name;
        this.email = email;
        this.mdpHash = mdpHash;
        this.instructor = instructor;
    }

    // Accessors for the fields.  JDO doesn't use these, but your application does.

    public Key getKey() {
        return key;
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

	public int getInstructorID() {
		return instructor;
	}

	public String getMdpHash() {
		return mdpHash;
	}

	public void setMdpHash(String mdpHash) {
		this.mdpHash = mdpHash;
	}

    private static Instructor allInstructor  = new Instructor("Tous", "all@isaelasbordes.appspot.com", "a181a603769c1f98ad927e7367c7aa51", -1);
    private static Instructor testInstructor = new Instructor("Sans instructeur", "test@isaelasbordes.appspot.com", "098f6bcd4621d373cade4e832627b4f6", 0);
    public  static Instructor getAllInstructor() {
    	return allInstructor;
    }
    public  static Instructor getTestInstructor() {
    	return testInstructor;
    }
}