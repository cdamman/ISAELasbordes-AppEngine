package com.coconuts.isaelasbordes.server;

import java.io.IOException;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Blob;

public class GetServlet extends HttpServlet {
	private static final long serialVersionUID = 5024830714006005415L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String deviceId = (String) req.getParameter("deviceID");
        String date = (String) req.getParameter("date");
        
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
        	if (deviceId == null || date == null) {
				resp.getWriter().println("Error null");
			} else if (deviceId.isEmpty() || date.isEmpty()) {
				resp.getWriter().println("Error empty");
			} else {
		        Query q = pm.newQuery(FlightKML.class, "deviceRegistrationID == '"+deviceId+"' && date == '"+date+"'");

	            try {
			    	@SuppressWarnings("unchecked")
					List<FlightKML> results = (List<FlightKML>) q.execute();
			    	if(!results.isEmpty() && results.size() == 1) {
			    		FlightKML f = results.iterator().next();
			            Blob b = f.getFileKML();
			            resp.addHeader("Content-Disposition", "attachment; filename=flight_"+f.getDate()+".kml");
			            resp.addHeader("Access-Control-Allow-Origin", "*");
			            resp.setContentType("application/vnd.google-earth.kml+xml; charset=utf-8");
			            resp.getOutputStream().write(b.getBytes());
			            resp.getOutputStream().close();
			    	} else {
				        resp.setContentType("text/plain");
				    	resp.getWriter().println("Erreur: vol non trouvé ou fichiers multiples");
			    	}
	            } finally {
	                q.closeAll();
	            }
			}
        } finally {
            pm.close();
        }
    }
}