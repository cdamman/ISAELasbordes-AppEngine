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

	@SuppressWarnings("unchecked")
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

	        	Query queryDeviceId = pm.newQuery(Student.class, "deviceRegistrationID == '"+deviceId+"'");
	        	Query queryEmail    = pm.newQuery(Student.class, "email == '"+email+"'");
	        	
	            try {
					List<Student> resultsDeviceId = (List<Student>) queryDeviceId.execute();
					List<Student> resultsEmail    = (List<Student>) queryEmail.execute();
					
					/* Cas:
					 * - Etudiant pas encore inscrit
					 *   - Nominal
					 *   - Nouvel email deja existant
					 * - Etudiant inscrit qui change son email
					 *   - Nominal
					 *   - Meme email
					 *   - Nouvel email deja existant
					 */
                	if(!resultsDeviceId.isEmpty()) { // inscrit ...
                		if(resultsDeviceId.size() == 1) { // ... et unique
                			s = resultsDeviceId.iterator().next();
                			if(resultsEmail.isEmpty()) { // nominal
                				s.setName(name);
			    				s.setEmail(email);
			    				s.setInstructor(instructor);
					    		resp.getWriter().println("OK");
                			} else if(resultsEmail.size() == 1) { // existant
	                			// si c'est les memes ...
				    			if(s.getKey().getId() == resultsEmail.iterator().next().getKey().getId()) {
				    				s.setName(name);
				    				s.setEmail(email);
				    				s.setInstructor(instructor);
						    		resp.getWriter().println("OK");
					    		} else { // ... sinon c'est que differents !
		    						resp.getWriter().println("Erreur: email deja assigne");
					    		}
                			}
                		} else {
    						resp.getWriter().println("Erreur: eleves multiples");
                		}
			    	} else { // pas encore inscrit
			    		if(!resultsEmail.isEmpty()) { // email deja assignee
    						resp.getWriter().println("Erreur: email deja assigne");
			    		} else { // nominal
			    			s = new Student(deviceId, name, email, instructor);
							pm.makePersistent(s);
							resp.getWriter().println("OK");
			    		}
			    	}
	            } finally {
	            	queryDeviceId.closeAll();
	            	queryEmail.closeAll();
	            }
			}
        } finally {
            pm.close();
        }
    }
}