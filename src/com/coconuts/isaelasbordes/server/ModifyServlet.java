package com.coconuts.isaelasbordes.server;

import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class ModifyServlet extends HttpServlet {
	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
        
        String instructorID = (String) req.getParameter("instructorID");
        String name = (String) req.getParameter("name");
        String email = (String) req.getParameter("email");
        String mdp = (String) req.getParameter("mdp");

        UserService userService = null;
        boolean isAdmin = false;
        
        Instructor instr = ConnectionTools.instructorAuthorized(req.getSession(true), null, null);
        boolean isInstructor = (instr != null && instructorID != null && String.valueOf(instr.getInstructorID()).equals(instructorID));

    	if (!isInstructor) {
            userService = UserServiceFactory.getUserService();
            User user = userService.getCurrentUser();
            isAdmin = (user != null && (user.getEmail().equals("cocod.tm@gmail.com") || user.getEmail().equals("dev.coconuts@gmail.com")));
    	}
    	
    	if(isAdmin || isInstructor) {
    		if(name != null && email != null && mdp != null && !name.isEmpty() && !email.isEmpty() && !mdp.isEmpty()) {
        		PersistenceManager pm = PMF.get().getPersistenceManager();
		        
		        try {
		        	MessageDigest m = MessageDigest.getInstance("MD5");
		            m.update(mdp.getBytes(),0,mdp.length());
		            String mdpHash = new BigInteger(1,m.digest()).toString(16);
		            while(mdpHash.length() < 32)
		            	mdpHash = "0"+mdpHash;
		            
                	Instructor instructor = null;
					
		        	Query query = pm.newQuery(Instructor.class, "instructor == "+instructorID);
		        	
		            try {
		                List<Instructor> results = (List<Instructor>) query.execute();
	                	if(!results.isEmpty() && results.size() == 1) {
				    		instructor = results.iterator().next();
				    	} else {
				    		if(isAdmin)
				    			resp.getWriter().println("Aucun instructeur trouvé...<br><meta http-equiv=\"Refresh\" content=\"3;URL=admin\">");
				    		else
				    			resp.getWriter().println("Aucun instructeur trouvé...<br><meta http-equiv=\"Refresh\" content=\"3;URL=instructor\">");
				    		return;
				    	}
		            } finally {
		                query.closeAll();
		            }

		        	instructor.setEmail(email);
		        	instructor.setName(name);
		        	instructor.setMdpHash(mdpHash);
		        } catch (NoSuchAlgorithmException e) {
	    			e.printStackTrace();
		    		if(isAdmin)
		    			resp.getWriter().println("Erreur<br><meta http-equiv=\"Refresh\" content=\"3;URL=admin\">");
		    		else
		    			resp.getWriter().println("Erreur<br><meta http-equiv=\"Refresh\" content=\"3;URL=instructor\">");
				} finally {
		            pm.close();
		        }
	    		if(isAdmin)
	    			resp.getWriter().println("Instructeur "+name+" modifié !<br><meta http-equiv=\"Refresh\" content=\"3;URL=admin\">");
	    		else
			        resp.getWriter().println("Instructeur "+name+" modifié !<br><meta http-equiv=\"Refresh\" content=\"3;URL=instructor\">");
    		} else {
	    		PersistenceManager pm = PMF.get().getPersistenceManager();
		        
		        try {
		    		Instructor instructor = null;
		        	Query query = pm.newQuery(Instructor.class, "instructor == "+instructorID);
		        	
		            try {
		                List<Instructor> results = (List<Instructor>) query.execute();
	                	if(!results.isEmpty() && results.size() == 1) {
				    		instructor = results.iterator().next();
				    	} else {
				    		if(isAdmin)
				    			resp.getWriter().println("Aucun instructeur trouvé...<br><meta http-equiv=\"Refresh\" content=\"3;URL=admin\">");
				    		else
				    			resp.getWriter().println("Aucun instructeur trouvé...<br><meta http-equiv=\"Refresh\" content=\"3;URL=instructor\">");
				    		return;
				    	}
		            } finally {
		                query.closeAll();
		            }
		            if(isAdmin)
		    			resp.getWriter().println("<html>"+
					        "<head>"+
					        "  <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"><link rel=\"icon\" type=\"image/ico\" href=\"favicon.ico\" />"+
					        "  <link rel=\"stylesheet\" type=\"text/css\" href=\"css/zocial.css\">" +
					        "  <title>Administrateur</title>"+
					        "</head>"+
					        ""+
					        "<body style=\"font-family: Roboto,arial,sans-serif;\">"+
					        "  <div style=\"float: left;\"><font size=\"6\" face=\"Comic Sans MS\">Administrateur</font></div><div style=\"float: right;\"><a class=\"zocial secondary\" href =\"admin?new=true\">Nouvel instructeur</a></div><br style=\"line-height: 35px;\"/><div style=\"float: right;\"><a class=\"zocial secondary\" href =\""+userService.createLogoutURL(req.getRequestURI())+"\">Déconnexion</a></div>");
		    		else
		    			resp.getWriter().println("<html>"+
	    			        "<head>"+
	    			        "  <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"><link rel=\"icon\" type=\"image/ico\" href=\"favicon.ico\" />"+
	    			        "  <link rel=\"stylesheet\" type=\"text/css\" href=\"css/zocial.css\">" +
	    			        "  <title>Zone pilote instructeur</title>"+
	    			        "</head>"+
	    			        ""+
	    			        "<body style=\"font-family: Roboto,arial,sans-serif;\">"+
	    			        "  <div style=\"float: left;\"><font size=\"6\" face=\"Comic Sans MS\">Zone pilote instructeur</font></div><div style=\"float: right;\">Bienvenue "+instructor.getName()+"</div><br style=\"line-height: 22px;\"/><div style=\"float: right;\"><a class=\"zocial secondary\" href =\"modify?instructorID="+instructor.getInstructorID()+"\">Modifier mon compte</a></div><br style=\"line-height: 35px;\"/><div style=\"float: right;\"><a class=\"zocial secondary\" href =\"instructor?logout=true\">Déconnexion</a></div>");
		            
	    			resp.getWriter().println("<br/><br/><br/>"+
	        			"<form action=\"modify\" method=\"post\"><center><table>"+
		            	"<tr><td><b>Nom</b></td><td><input type=\"text\" placeholder=\"Entrez votre nom\" name=\"name\" value=\""+instructor.getName()+"\" required></td></tr>"+
	        			"<tr><td><b>Adresse email</b></td><td><input type=\"text\" placeholder=\"Entrez votre nouvelle adresse email\" name=\"email\" value=\""+instructor.getEmail()+"\"required></td></tr>"+
	        			"<tr><td><b>Mot de passe</b></td><td><input type=\"password\" placeholder=\"Entrez votre nouveau mot de passe\" name=\"mdp\" required></td></tr>"+
	        			"</table><br/><input type=\"hidden\" name=\"instructorID\" value=\""+instructorID+"\"><button type=\"submit\" class=\"zocial secondary\">Modifier l'instructeur</button></center></form></body></html>");
		        } finally {
		            pm.close();
		        }
    		}
        } else {
        	resp.getWriter().println("<h1>! Non autorisé !</h1>");
        }
    }

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}
}