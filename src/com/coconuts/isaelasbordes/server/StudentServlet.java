package com.coconuts.isaelasbordes.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class StudentServlet extends HttpServlet {
	private static final long serialVersionUID = 7550898445174346625L;

	@SuppressWarnings("unchecked")
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		
		HttpSession session = req.getSession(true);
		String email = (String) req.getParameter("email");
		String mdp = (String) req.getParameter("mdp");

		String logout = (String) req.getParameter("logout");
		if(logout != null && logout.equals("true")) {
			session.removeAttribute("deviceRegistrationID");
			session.invalidate();
			session = req.getSession(true);
		}
		
		boolean noFlights = true;

		ArrayList<Student> students = ConnectionTools.studentAuthorized(session, email, mdp);
        if(students != null && !students.isEmpty()) {
        	resp.getWriter().println("<html>"+
	        "<head>"+
	        "  <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"><link rel=\"icon\" type=\"image/ico\" href=\"favicon.ico\" />"+
	        "  <link rel=\"stylesheet\" type=\"text/css\" href=\"css/zocial.css\">" +
	        "  <title>Zone pilote élève</title>"+
	        "</head>"+
	        ""+
	        "<body style=\"font-family: Roboto,arial,sans-serif;\">"+
	        "  <div style=\"float: left;\"><font size=\"6\" face=\"Comic Sans MS\">Zone pilote élève</font></div><div style=\"float: right;\">Bienvenue "+students.get(0).getName()+"</div><br style=\"line-height: 22px;\"/><div style=\"float: right;\"><a class=\"zocial secondary\" href =\"student?logout=true\">Déconnexion</a></div>");

        	resp.getWriter().println("  <br><br><center><h4>Mes vols</h4></center>"+
        	        "  <table border=\"1\" align=\"center\">" +
    				"    <tr><th>&nbsp;Nom du vol&nbsp;</th><th>&nbsp;Date&nbsp;</th><th>&nbsp;Voir&nbsp;</th><th>&nbsp;Télécharger&nbsp;</th></tr>");

            PersistenceManager pm = PMF.get().getPersistenceManager();
	        
	        try {
	            for(Student student : students) {
			        Query query = pm.newQuery(FlightKML.class, "deviceRegistrationID == '"+student.getDeviceRegistrationID()+"'");
		        	query.setOrdering("date desc");
		        	try {
			        	List<FlightKML> results2 = (List<FlightKML>) query.execute();
			        	if (!results2.isEmpty()) {
		                	for (FlightKML f : results2) {
			    	        	resp.getWriter().println(
		            		        "    <tr>"+
		            		        "      <td>&nbsp;"+f.getFlightName()+"&nbsp;</td><td>&nbsp;"+f.getHumanDate()+"&nbsp;</td><td><center><a class=\"zocial secondary\" href =\"see?deviceID="+f.getDeviceRegistrationID()+"&date="+f.getDate()+"&name="+student.getName()+"\" target=\"_blank\">Voir</a></center></td><td><center><a class=\"zocial secondary\" href =\"get?deviceID="+f.getDeviceRegistrationID()+"&date="+f.getDate()+"\">Télécharger</a></center></td>"+
		            		        "    </tr>");
		                	}
		                	noFlights = false;
			        	}
		            } finally {
		                query.closeAll();
		            }
	            }
	        } finally {
	        	if(noFlights)
	        		resp.getWriter().println(
            		        "    <tr>"+
            		        "      <td>&nbsp;Pas de vols :'(&nbsp;</td><td></td><td></td><td></td>"+
            		        "    </tr>");
	        	resp.getWriter().println("  </table>" +
		        	"</body>"+
		        	"</html>");
	            pm.close();
	        }
        } else {
        	resp.getWriter().println("<html><head>"+
        			"  <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"><link rel=\"icon\" type=\"image/ico\" href=\"favicon.ico\" />"+
        			"  <link rel=\"stylesheet\" type=\"text/css\" href=\"css/zocial.css\">"+
        			"  <title>Zone pilote élève</title>"+
        			"</head><body style=\"font-family: Roboto,arial,sans-serif;\"><div style=\"float: left;\"><font size=\"6\" face=\"Comic Sans MS\">Zone pilote élève</font></div>" +
        			"<br><br><center><h3>Connexion<br><span style=\"font-size: 0.8em;\">Vols enregistrés avec l'application</span></h3></center>"+
        			"<form action=\"student\" method=\"post\"><center><table>"+
        			"<tr><td><b>Adresse email</b></td><td><input type=\"text\" placeholder=\"Entrez votre adresse email\" name=\"email\" required></td></tr>"+
        			//"<tr><td><b>Mot de passe</b></td><td><input type=\"password\" placeholder=\"Entrez votre mot de passe\" name=\"mdp\" required></td></tr>"+
        			"</table><br/><button class=\"zocial secondary\" type=\"submit\">Se connecter</button></center></form>" +
        			//"<center><hr width=\"400em\" style=\"margin-bottom: 1em;\"><h3>Connexion<br><span style=\"font-size: 0.8em;\">Vols enregistrés avec l'application web</span></h3><a class=\"zocial secondary\" href=\"student?email=webapp@isaelasbordes.appspot.com\">Se connecter</a></center>" +
        			"</body></html>");
    	}
    }
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req, resp);
	}
}