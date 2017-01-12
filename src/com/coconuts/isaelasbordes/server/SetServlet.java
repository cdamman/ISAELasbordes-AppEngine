package com.coconuts.isaelasbordes.server;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//USAGE: http://127.0.0.1:8888/set?deviceId=####&name=Corentin&email=cocod.tm@gmail.com&instructor=0

public class SetServlet extends HttpServlet {
	private static final long serialVersionUID = -1408050888501698802L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("text/plain");

        String deviceId = (String) req.getParameter("deviceId");
        String name = (String) req.getParameter("name");
        String email = (String) req.getParameter("email");
        int instructor = -1;
        try {
        	instructor = Integer.valueOf((String) req.getParameter("instructor"));
        } catch(NumberFormatException e) {
        	resp.getWriter().println("Error null");
        	return;
        }
        
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
			if (deviceId == null || name == null || email == null || instructor == -1) {
				resp.getWriter().println("Error null");
			} else if (deviceId.isEmpty() || name.isEmpty() || email.isEmpty() || instructor == -1) {
				resp.getWriter().println("Error empty");
			} else {
				Student s = null;
				
	        	Query query = pm.newQuery(Student.class, "deviceRegistrationID == '"+deviceId+"' || email == '"+email+"'");
	        	
	            try {
	                @SuppressWarnings("unchecked")
					List<Student> results = (List<Student>) query.execute();
                	if(!results.isEmpty()) {
                		if(results.size() == 1) {
				    		s = results.iterator().next();
				    		s.setName(name);
				    		s.setEmail(email);
				    		s.setDeviceRegistrationID(deviceId);
				    		s.setInstructor(instructor);
							resp.getWriter().println("OK");
                		} else {
    						resp.getWriter().println("Erreur: élèves multiples");
                		}
			    	} else {
						s = new Student(deviceId, name, email, instructor);
						pm.makePersistent(s);
						resp.getWriter().println("OK");
			    	}
	            } finally {
	                query.closeAll();
	            }
			}
        } finally {
            pm.close();
        }
    }
}