package com.coconuts.isaelasbordes.server;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class FlightKML {
    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String deviceRegistrationID;
    
    @Persistent
    private String date;

    @Persistent
    private String lastWhen;
    
    @Persistent
    private String nameFlight;

    @Persistent
    private Blob fileKML;

    public FlightKML(String deviceRegistrationID, String date, String nameFlight, Blob fileKML) {
        this.deviceRegistrationID = deviceRegistrationID; 
        this.date = date;
        this.nameFlight = nameFlight;
        this.fileKML = fileKML;
        this.lastWhen = date;
    }
    
    public Blob getFileKML() {
    	return fileKML;
    }
    
    public void setFileKML(Blob fileKML) {
    	this.fileKML = fileKML;
    }
    
    public void setFileKML(Blob fileKML, String dateEnd) {
		SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.FRANCE);
		try { // N'enregistrer QUE si on ne remonte pas le temps...
			if(df1.parse(dateEnd).after(df1.parse(this.lastWhen))) {
				this.fileKML = fileKML;
				this.lastWhen = dateEnd;
			}
		} catch (ParseException e) {}
    }

	public String getDeviceRegistrationID() {
		return deviceRegistrationID;
	}

	public void setDeviceRegistrationID(String deviceRegistrationID) {
		this.deviceRegistrationID = deviceRegistrationID;
	}

	public String getDate() {
		return date;
	}

	public String getHumanDate() {
		SimpleDateFormat df1 = new SimpleDateFormat("yyyyMMdd-HHmmss", Locale.FRANCE);
		SimpleDateFormat df2 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.FRANCE);
		
		try {
			return df2.format(df1.parse(date));
		} catch (ParseException e) {
			return date;
		}
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getFlightName() {
		return nameFlight;
	}

	public void setFlightName(String nameFlight) {
		this.nameFlight = nameFlight;
	}
}