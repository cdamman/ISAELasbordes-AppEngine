package com.coconuts.isaelasbordes.server;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class DelServlet extends HttpServlet {
	private static final long serialVersionUID = 8545025866644387342L;

	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		
		HttpSession session = req.getSession(true);
        String deviceId = (String) req.getParameter("deviceID");
        String date = (String) req.getParameter("date");
        String confirm = (String) req.getParameter("confirm");
        String all = (String) req.getParameter("all");

		Instructor instructor = ConnectionTools.instructorAuthorized(session, null, null);
        if(instructor != null) {
        	PersistenceManager pm = PMF.get().getPersistenceManager();
	        try {
	        	if(all != null && all.equals("true")) {
	        		if(confirm == null) {
		                resp.getWriter().println("<script type=\"text/javascript\" language=\"javascript\">" +
						"if (confirm(\"Supprimer tous les vols ?\")) {" +
						"	window.location.replace(\"del?all=true&confirm=true\");" +
						"}" +
						"else {" +
						"	window.location.replace(\"instructor\");" +
						"}" +
		                "</script>");
		        	} else if(confirm.equals("true")) {
		        		Query query = pm.newQuery(Student.class, "instructor == "+instructor.getInstructorID());
			        	query.setOrdering("instructor asc");
			        	query.setOrdering("name asc");

			            try {
							List<Student> results = (List<Student>) query.execute();
			                if (results.iterator().hasNext()) {
			                	for (Student s : results) {
			        	        	Query query2 = pm.newQuery(FlightKML.class, "deviceRegistrationID == '"+s.getDeviceRegistrationID()+"'");
						        	query2.setOrdering("date desc");
			        	        	try {
				        	        	List<FlightKML> results2 = (List<FlightKML>) query2.execute();
						                if (results2.iterator().hasNext()) {
						                	for (FlightKML f : results2) {
						                		resp.getWriter().println("Suppression du vol du "+f.getHumanDate()+" ... ");
						                		pm.deletePersistent(f);
						                		resp.getWriter().print("OK<br>");
						                	}
						                	resp.getWriter().println("<meta http-equiv=\"Refresh\" content=\"2;URL=instructor\">");
						                } else {
						                    resp.getWriter().println("Aucun vol trouvé...<br><meta http-equiv=\"Refresh\" content=\"2;URL=instructor\">");
						                }
			        	            } finally {
			        	                query2.closeAll();
			        	            }
			                    }
			                } else {
			                    resp.getWriter().println("Aucun vol trouvé...<br><meta http-equiv=\"Refresh\" content=\"2;URL=instructor\">");
			                }
			            } finally {
			                query.closeAll();
			            }
		        	} else {
			        	resp.getWriter().println("Erreur<br>");
			        }
	        	} else {
	    			if (deviceId == null || date == null) {
						resp.getWriter().println("Error null");
					} else if (deviceId.isEmpty() || date.isEmpty()) {
						resp.getWriter().println("Error empty");
					} else {
						if(confirm == null) {
			                resp.getWriter().println("<script type=\"text/javascript\" language=\"javascript\">" +
							"if (confirm(\"Supprimer le vol du "+date+" ?\")) {" +
							"	window.location.replace(\"del?deviceID="+deviceId+"&date="+date+"&confirm=true\");" +
							"}" +
							"else {" +
							"	window.location.replace(\"instructor\");" +
							"}" +
			                "</script>");
			        	} else if(confirm.equals("true")) {
					        Query q = pm.newQuery(FlightKML.class, "deviceRegistrationID == '"+deviceId+"' && date == '"+date+"'");
					        
					        try {
								List<FlightKML> results = (List<FlightKML>) q.execute();
						    	if(!results.isEmpty() && results.size() == 1) {
						    		FlightKML flight = results.iterator().next();
			                		resp.getWriter().println("Suppression du vol du "+flight.getHumanDate()+" ... ");
			                		pm.deletePersistent(flight);
			                		resp.getWriter().print("OK<br><meta http-equiv=\"Refresh\" content=\"2;URL=instructor\">");
						    	} else {
				                    resp.getWriter().println("Aucun vol trouvé...<br><meta http-equiv=\"Refresh\" content=\"2;URL=instructor\">");
						    	}
				            } finally {
				                q.closeAll();
				            }
			        	}
					}
	        	}
	        } finally {
	            pm.close();
	        }
        } else {
        	resp.getWriter().println("<h1>! Non autorisé !</h1>");
    	}
    }
}