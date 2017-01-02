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
public class SudoServlet extends HttpServlet {
	@SuppressWarnings("unchecked")
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		
        UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        
        String newInstuctor = (String) req.getParameter("new");
        String name = (String) req.getParameter("name");
        String email = (String) req.getParameter("email");
        String mdp = (String) req.getParameter("mdp");

        if(user != null) {
        	if (user.getEmail().equals("cocod.tm@gmail.com") || user.getEmail().equals("dev.coconuts@gmail.com")) {
	        	if(newInstuctor != null && newInstuctor.equals("true")) {
	        		if(name != null && email != null && mdp != null && !name.isEmpty() && !email.isEmpty() && !mdp.isEmpty()) {
		        		PersistenceManager pm = PMF.get().getPersistenceManager();
				        
				        try {
				        	MessageDigest m = MessageDigest.getInstance("MD5");
				            m.update(mdp.getBytes(),0,mdp.length());
				            String mdpHash = new BigInteger(1,m.digest()).toString(16);
				            while(mdpHash.length() < 32)
				            	mdpHash = "0"+mdpHash;
				        	
				        	int instructor = -1;
							
				        	Query query = pm.newQuery(Instructor.class);
				        	
				            try {
				                List<Instructor> results = (List<Instructor>) query.execute();
			                	instructor = 0;
			                	for (@SuppressWarnings("unused") Instructor i : results) {
			                		instructor++;
			                    }
				            } finally {
				                query.closeAll();
				            }
				        	
				        	Instructor i = new Instructor(name, email, mdpHash, instructor);
				        	pm.makePersistent(i);
				        	resp.getWriter().println("Nouvel instructeur "+name+" créé !<br><meta http-equiv=\"Refresh\" content=\"3;URL=sudo\">");
				        } catch (NoSuchAlgorithmException e) {
							e.printStackTrace();resp.getWriter().println("Erreur<br><meta http-equiv=\"Refresh\" content=\"3;URL=sudo\">");
						} finally {
				            pm.close();
				        }
	        			
	        		} else {
	        			resp.getWriter().println("<html>"+
	    			        "<head>"+
	    			        "  <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"><link rel=\"icon\" type=\"image/ico\" href=\"favicon.ico\" />"+
	    			        "  <link rel=\"stylesheet\" type=\"text/css\" href=\"css/zocial.css\">" +
	    			        "  <title>Super Utilisateur</title>"+
	    			        "</head>"+
	    			        ""+
	    			        "<body style=\"font-family: Roboto,arial,sans-serif;\">"+
	    			        "  <div style=\"float: left;\"><font size=\"6\" face=\"Comic Sans MS\">Super Utilisateur</font></div><div style=\"float: right;\"><a class=\"zocial secondary\" href=\"sudo?new=true\">Nouvel instructeur</a></div><br style=\"line-height: 35px;\"/><div style=\"float: right;\"><a class=\"zocial secondary\" href=\""+userService.createLogoutURL(req.getRequestURI())+"\">Déconnexion</a></div>");
	        			resp.getWriter().println("<br/><br/><br/>"+
	            			"<form action=\"sudo\" method=\"post\"><center><table>"+
	    	            	"<tr><td><b>Nom</b></td><td><input type=\"text\" placeholder=\"Entrez le nom\" name=\"name\" required></td></tr>"+
	            			"<tr><td><b>Adresse email</b></td><td><input type=\"text\" placeholder=\"Entrez l'adresse email\" name=\"email\" required></td></tr>"+
	            			"<tr><td><b>Mot de passe</b></td><td><input type=\"password\" placeholder=\"Entrez le mot de passe\" name=\"mdp\" required></td></tr>"+
	            			"</table><br/><input type=\"hidden\" name=\"new\" value=\"true\"><button class=\"zocial secondary\" type=\"submit\">Créer un nouvel instructeur</button></center></form></body></html>");
	        		}
	        	} else {
	        		resp.getWriter().println("<html>"+
			        "<head>"+
			        "  <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"><link rel=\"icon\" type=\"image/ico\" href=\"favicon.ico\" />"+
			        "  <link rel=\"stylesheet\" type=\"text/css\" href=\"css/zocial.css\">" +
			        "  <title>Super Utilisateur</title>"+
			        "</head>"+
			        ""+
			        "<body style=\"font-family: Roboto,arial,sans-serif;\">"+
			        "  <div style=\"float: left;\"><font size=\"6\" face=\"Comic Sans MS\">Super Utilisateur</font></div><div style=\"float: right;\"><a class=\"zocial secondary\" href=\"sudo?new=true\">Nouvel instructeur</a></div><br style=\"line-height: 35px;\"/><div style=\"float: right;\"><a class=\"zocial secondary\" href=\""+userService.createLogoutURL(req.getRequestURI())+"\">Déconnexion</a></div>"+
			        "  <br><br><center><h4>Liste des instructeurs:</h4></center>"+
			        "  <table border=\"1\" align=\"center\">" +
			        "    <tr><th>&nbsp;ID&nbsp;</th><th>&nbsp;Nom&nbsp;</th><th>&nbsp;Email&nbsp;</th><th>&nbsp;Modifier&nbsp;</th></tr>");
	
		            PersistenceManager pm = PMF.get().getPersistenceManager();
			        
			        try {
			        	Query query = pm.newQuery(Instructor.class);
			        	query.setOrdering("instructor asc");
	
			            try {
			                List<Instructor> results = (List<Instructor>) query.execute();
			                if (results.iterator().hasNext()) {
			                	for (Instructor i : results) {
			                		resp.getWriter().println(
			                		        "    <tr><td>&nbsp;"+i.getInstructorID()+"&nbsp;</td><td>&nbsp;"+i.getName()+"&nbsp;</td><td>&nbsp;"+i.getEmail()+"&nbsp;</td><td><center><a class=\"zocial secondary\" href=\"modify?instructorID="+i.getInstructorID()+"\">Modifier</a></center></td></tr>");
			                    }
			                } else {
			                	resp.getWriter().println(
			            		        "    <tr>"+
			            		        "      <td>&nbsp;Aucun instructeur :'(&nbsp;</td><td></td><td></td><td><center><a class=\"zocial secondary\" href=\"sudo?new=true\">Nouvel instructeur</a></center></td>"+
			            		        "    </tr>");
			                }
			            } finally {
			                query.closeAll();
			            }
			        } finally {
			        	resp.getWriter().println("  </table>" +
			        	"</body>"+
			        	"</html>");
			            pm.close();
			        }
	        	}
	        } else {
            	resp.getWriter().println("<h1>! Non autorisé !</h1>" +
            			"<a class=\"zocial secondary\" href=\""+userService.createLogoutURL(req.getRequestURI())+"\">Déconnexion</a>");
        	}
        } else {
            resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
        }
    }

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doGet(req, resp);
	}
}