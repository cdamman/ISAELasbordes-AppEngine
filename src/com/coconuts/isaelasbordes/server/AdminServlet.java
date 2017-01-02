package com.coconuts.isaelasbordes.server;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 7550898445174346625L;

	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		
		HttpSession session = req.getSession(true);
		String email = (String) req.getParameter("email");
		String mdp = (String) req.getParameter("mdp");
		String show = (String) req.getParameter("show");
		boolean showAll = false;
		if(show != null && show.equals("all")) showAll = true;

		String logout = (String) req.getParameter("logout");
		if(logout != null && logout.equals("true")) {
			session.removeAttribute("instructorID");
			session.invalidate();
			session = req.getSession(true);
		}

		Instructor instructor = ConnectionTools.instructorAuthorized(session, email, mdp);
        if(instructor != null) {
        	resp.getWriter().println("<html>"+
	        "<head>"+
	        "  <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"><link rel=\"icon\" type=\"image/ico\" href=\"favicon.ico\" />"+
	        "  <link rel=\"stylesheet\" type=\"text/css\" href=\"css/zocial.css\">" +
	        "  <title>Zone pilote instructeur</title>"+
	        "</head>"+
	        ""+
	        "<body style=\"font-family: Roboto,arial,sans-serif;\">"+
	        "  <div style=\"float: left;\"><font size=\"6\" face=\"Comic Sans MS\">Zone pilote instructeur</font></div><div style=\"float: right;\">Bienvenue "+instructor.getName()+"</div><br style=\"line-height: 22px;\"/><div style=\"float: right;\"><a class=\"zocial secondary\" href =\"modify?instructorID="+instructor.getInstructorID()+"\">Modifier mon compte</a></div><br style=\"line-height: 35px;\"/><div style=\"float: right;\"><a class=\"zocial secondary\" href=\"admin?logout=true\">Déconnexion</a></div>");

        	if(!showAll)
        		resp.getWriter().println("  <br><br><center><h4>Vols de mes élèves / <a href=\"admin?show=all\">Tous les vols</a></h4></center>"+
            	        "  <table border=\"1\" align=\"center\">" +
            	        "    <tr><th>&nbsp;Détenteur du téléphone&nbsp;</th><th>&nbsp;Nom du vol&nbsp;</th><th>&nbsp;Date&nbsp;</th><th>&nbsp;Voir&nbsp;</th><th>&nbsp;Télécharger&nbsp;</th><th>&nbsp;Envoyer&nbsp;</th><th>&nbsp;Supprimer&nbsp;</th></tr>");
        	else
        		resp.getWriter().println("  <br><br><center><h4><a href=\"admin?show=student\">Vols de mes élèves</a> / Tous les vols</h4></center>"+
            	        "  <table border=\"1\" align=\"center\">" +
        				"    <tr><th>&nbsp;Détenteur du téléphone&nbsp;</th><th>&nbsp;Nom du vol&nbsp;</th><th>&nbsp;Date&nbsp;</th><th>&nbsp;Voir&nbsp;</th><th>&nbsp;Télécharger&nbsp;</th><th>&nbsp;Envoyer&nbsp;</th><th>&nbsp;Instructeur&nbsp;</th></tr>");

            PersistenceManager pm = PMF.get().getPersistenceManager();
	        
	        try {
	        	Query query = null;
	        	boolean noFlights = true;
        		query = pm.newQuery(FlightKML.class);
	        	query.setOrdering("date desc");

	            try {
					List<FlightKML> results = (List<FlightKML>) query.execute();
	                if (results.iterator().hasNext()) {
	                	for (FlightKML f : results) {
	                		Query query2 = null;
        		        	if(!showAll)
        		        		query2 = pm.newQuery(Student.class, "deviceRegistrationID == '"+f.getDeviceRegistrationID()+"' && (instructor == "+instructor.getInstructorID()+" || instructor == -1)");
        		        	else
        		        		query2 = pm.newQuery(Student.class, "deviceRegistrationID == '"+f.getDeviceRegistrationID()+"'");
        		        	try {
		        	        	List<Student> results2 = (List<Student>) query2.execute();
		        	        	if (results2.iterator().hasNext()) {
		        		        	if(!showAll) {
					                	for (Student s : results2) {
				                			resp.getWriter().println(
					            		        "    <tr>"+
					            		        "      <td>&nbsp;"+s.getName()+"&nbsp;</td><td>&nbsp;"+f.getFlightName()+"&nbsp;</td><td>&nbsp;"+f.getHumanDate()+"&nbsp;</td><td><center><a class=\"zocial secondary\" href =\"see?deviceID="+f.getDeviceRegistrationID()+"&date="+f.getDate()+"&name="+s.getName()+"\" target=\"_blank\">Voir</a></center></td><td><center><a class=\"zocial secondary\" href =\"get?deviceID="+f.getDeviceRegistrationID()+"&date="+f.getDate()+"\">Télécharger</a></center></td><td><center><a class=\"zocial secondary\" href =\""+generateMail(s.getEmail(), s.getName(), f.getDeviceRegistrationID(), f.getDate(), f.getHumanDate())+"\" target=\"_blank\">Envoyer</a></center></td><td><center><a class=\"zocial secondary\" href =\"del?deviceID="+f.getDeviceRegistrationID()+"&date="+f.getDate()+"\">Supprimer</a></center></td>"+
					            		        "    </tr>");
				                			noFlights = false;
					                	}
		        		        	} else {
			        	        		int lastInstructorIteratorID = -2;
			        	        		String instructorName = "";
			        	        		
					                	for (Student s : results2) {
					    	        		if(lastInstructorIteratorID != s.getInstructor()) {
						    	        		Query query3 = pm.newQuery(Instructor.class, "instructor == "+s.getInstructor());
									        	query3.setOrdering("instructor asc");
						        	        	try {
							        	        	List<Instructor> results3 = (List<Instructor>) query3.execute();
							        	        	if(!results3.isEmpty() && results3.size() == 1) {
							        	        		instructorName = results3.iterator().next().getName();
							        	        	}
						        	            } finally {
						        	                query3.closeAll();
						        	            }
						        	        	lastInstructorIteratorID = s.getInstructor();
					    	        		}
					    	        		
					    	        		if(!instructorName.isEmpty()) {
					    	        			if(s.getInstructor() == Instructor.getAllInstructor().getInstructorID())
					    	        				instructorName = Instructor.getAllInstructor().getName();
					    	        		}
					    	        		if(!instructorName.isEmpty()) {
						    	        		resp.getWriter().println(
						            		        "    <tr>"+
						            		        "      <td>&nbsp;"+s.getName()+"&nbsp;</td><td>&nbsp;"+f.getFlightName()+"&nbsp;</td><td>&nbsp;"+f.getHumanDate()+"&nbsp;</td><td><center><a class=\"zocial secondary\" href =\"see?deviceID="+f.getDeviceRegistrationID()+"&date="+f.getDate()+"&name="+s.getName()+"\" target=\"_blank\">Voir</a></center></td><td><center><a class=\"zocial secondary\" href =\"get?deviceID="+f.getDeviceRegistrationID()+"&date="+f.getDate()+"\">Télécharger</a></center></td><td><center><a class=\"zocial secondary\" href =\""+generateMail(s.getEmail(), s.getName(), f.getDeviceRegistrationID(), f.getDate(), f.getHumanDate())+"\" target=\"_blank\">Envoyer</a></center></td><td>&nbsp;"+instructorName+"&nbsp;</td>"+
						            		        "    </tr>");
					                			noFlights = false;
					    	        		}
					                	}
		        		        	}
		        	        	}
	        	            } finally {
	        	                query2.closeAll();
	        	            }
	                    }
	                }
	            } finally {
	                query.closeAll();
	            }
                if(noFlights) {
	        		resp.getWriter().println(
        		        "    <tr>"+
        		        "      <td>&nbsp;Pas de vols :'(&nbsp;</td><td></td><td></td><td></td><td></td><td></td><td></td>"+
        		        "    </tr>");
                }
	        } finally {
	        	if(!showAll)
		        	resp.getWriter().println("  </table>" +
			        	"  <br>" +
			        	"  <center><a class=\"zocial secondary\" href =\"del?all=true\">Supprimer tous les enregistrements des vols de mes élèves</a></center>"+
			        	"  <br>" +
			        	"</body>"+
			        	"</html>");
	        	else
		        	resp.getWriter().println("  </table>" +
			        	"</body>"+
			        	"</html>");
	            pm.close();
	        }
        } else {
        	resp.getWriter().println("<html><head>"+
        			"  <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"><link rel=\"icon\" type=\"image/ico\" href=\"favicon.ico\" />"+
        			"  <link rel=\"stylesheet\" type=\"text/css\" href=\"css/zocial.css\">" +
        			"  <title>Zone pilote instructeur</title>"+
        			"</head><body style=\"font-family: Roboto,arial,sans-serif;\"><div style=\"float: left;\"><font size=\"6\" face=\"Comic Sans MS\">Zone pilote instructeur</font></div>" +
        			"<br><br><center><h3>Connexion</h3></center>"+
        			"<form action=\"admin\" method=\"post\"><center><table>"+
        			"<tr><td><b>Adresse email</b></td><td><input type=\"text\" placeholder=\"Entrez votre adresse email\" name=\"email\" required></td></tr>"+
        			"<tr><td><b>Mot de passe</b></td><td><input type=\"password\" placeholder=\"Entrez votre mot de passe\" name=\"mdp\" required></td></tr>"+
        			"</table><br/><button class=\"zocial secondary\" type=\"submit\">Se connecter</button></center></form></body></html>");
    	}
    }
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req, resp);
	}
	
	public static String generateMail(String email, String name, String deviceID, String date, String humanDate) {
		return "mailto:"+email+"?subject=Fichier KML de ton vol du "+humanDate+"&body=Salut "+name+" !%0D%0A%0D%0ALe fichier KML de ton vol peut être téléchargé ici:%0D%0Ahttps://isaelasbordes.appspot.com/get?deviceID="+deviceID+"%26date="+date+"%0D%0A%0D%0AA bientôt à Lasbordes !";
	}
}