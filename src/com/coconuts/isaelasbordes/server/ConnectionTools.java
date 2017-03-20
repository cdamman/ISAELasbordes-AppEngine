package com.coconuts.isaelasbordes.server;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpSession;

public class ConnectionTools {
	public static Instructor instructorAuthorized(HttpSession session, String email, String mdp) {
		Instructor response = null;
		if(session != null) {
			String instructorID = (String) session.getAttribute("instructorID");
			if (instructorID != null) {
				if(instructorID.equals(""+Instructor.getTestInstructor().getInstructorID())) {
					response = Instructor.getTestInstructor();
				} else {
					PersistenceManager pm = PMF.get().getPersistenceManager();
	        		try {
						Query q = pm.newQuery(Instructor.class, "instructor == "+instructorID);
				        
				    	@SuppressWarnings("unchecked")
						List<Instructor> results = (List<Instructor>) q.execute();
				    	if(!results.isEmpty() && results.size() == 1) {
				    		response = results.iterator().next();
				    	}
		            } finally {
		                pm.close();
		            }
				}
			} else {
				if(email != null && mdp != null) {
		        	if(!email.isEmpty() && !mdp.isEmpty()) {
		        		try {
				        	MessageDigest m = MessageDigest.getInstance("MD5");
				            m.update(mdp.getBytes(),0,mdp.length());
				            String mdpHash = new BigInteger(1,m.digest()).toString(16);
				            while(mdpHash.length() < 32)
				            	mdpHash = "0"+mdpHash;
				            
							if(email.equals(Instructor.getTestInstructor().getEmail())
									&& mdpHash.equals(Instructor.getTestInstructor().getMdpHash())) {
								response = Instructor.getTestInstructor();
					    		session.setAttribute("instructorID", String.valueOf(response.getInstructorID()));
							} else {
								PersistenceManager pm = PMF.get().getPersistenceManager();
				        		try {
									Query q = pm.newQuery(Instructor.class, "email == '"+email+"' && mdpHash == '"+mdpHash+"'");
							        
							    	@SuppressWarnings("unchecked")
									List<Instructor> results = (List<Instructor>) q.execute();
							    	if(!results.isEmpty() && results.size() == 1) {
							    		response = results.iterator().next();
							    		session.setAttribute("instructorID", String.valueOf(response.getInstructorID()));
							    	}
					            } finally {
					                pm.close();
					            }
							}
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						}
		        	}
				}
			}
		}
		return response;
	}

	public static ArrayList<Student> studentAuthorized(HttpSession session, String email, String mdp) {
		ArrayList<Student> response = new ArrayList<Student>();
		if(session != null) {
			String emailSession = (String) session.getAttribute("email");
			if (emailSession != null) {
				if(emailSession.equals(Student.getWebAppStudent().getEmail())) {
					response.add(Student.getWebAppStudent());
				} else {
					PersistenceManager pm = PMF.get().getPersistenceManager();
	        		try {
						Query q = pm.newQuery(Student.class, "email == '"+emailSession+"'");
				        
				    	@SuppressWarnings("unchecked")
				    	List<Student> results = (List<Student>) q.execute();
				    	for(Student s : results) {
				    		response.add(s);
				    	}
		            } finally {
		                pm.close();
		            }
				}
			} else {
				if(email != null) {
		        	if(!email.isEmpty()) {
		        		if(email.equals(Student.getWebAppStudent().getEmail())) {
		        			response.add(Student.getWebAppStudent());
				    		session.setAttribute("email", email);
		        		} else {
			    	        PersistenceManager pm = PMF.get().getPersistenceManager();
			        		try {
								Query q = pm.newQuery(Student.class, "email == '"+email+"'");
						        
						    	@SuppressWarnings("unchecked")
						    	//if(mdp != null && mdp.equals("volmoteurlasbordes")) {
							    	List<Student> results = (List<Student>) q.execute();
						    		if(!results.isEmpty()) {
								    	for(Student s : results) {
								    		response.add(s);
								    	}
							    		session.setAttribute("email", email);
						    		}
						    	//}
				            } finally {
				                pm.close();
				            }
		        		}
		        	}
				}
			}
		}
		return response;
	}
}
