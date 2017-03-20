package com.coconuts.isaelasbordes.server;

import java.io.IOException;
import java.util.ArrayList;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SeeServlet extends HttpServlet {
	private static final long serialVersionUID = 8545025866644387342L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    	resp.setContentType("text/html");
		resp.setCharacterEncoding("utf-8");
		
		HttpSession session = req.getSession(true);
        String deviceId = (String) req.getParameter("deviceID");
        String date = (String) req.getParameter("date");

		Instructor instructor = ConnectionTools.instructorAuthorized(session, null, null);
		ArrayList<Student> students = ConnectionTools.studentAuthorized(session, null, null);
        if(instructor != null || (students != null && !students.isEmpty())) {
        	PersistenceManager pm = PMF.get().getPersistenceManager();
	        try {
    			if (deviceId == null || date == null) {
					resp.getWriter().println("Error null");
				} else if (deviceId.isEmpty() || date.isEmpty()) {
					resp.getWriter().println("Error empty");
				} else {
			        see(resp, (String) req.getParameter("name"), deviceId, date);
	        	}
	        } finally {
	            pm.close();
	        }
        } else {
        	resp.getWriter().println("<h1>! Non autorisé !</h1>");
    	}
    }
	
	private void see(HttpServletResponse resp, String name, String deviceId, String date) throws IOException {
		resp.getWriter().println("<!DOCTYPE html>");
		resp.getWriter().println("<html>");
		resp.getWriter().println("  <head>");
		resp.getWriter().println("    <meta name=\"viewport\" content=\"initial-scale=1.0\">");
		resp.getWriter().println("    <meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"><link rel=\"icon\" type=\"image/ico\" href=\"favicon.ico\" />");
		if(name != null && !name.isEmpty()) {
			resp.getWriter().println("    <title>Vol de "+name+"</title>");
		} else {
			resp.getWriter().println("    <title>Vol</title>");
		}
		resp.getWriter().println("    <style>");
		resp.getWriter().println("      html, body {");
		resp.getWriter().println("        height: 100%;");
		resp.getWriter().println("        margin: 0;");
		resp.getWriter().println("        padding: 0;");
		resp.getWriter().println("      }");
		resp.getWriter().println("      #map {");
		resp.getWriter().println("        height: 92.5%;");
		resp.getWriter().println("        clear: both;");
		resp.getWriter().println("      }");
		resp.getWriter().println("    </style>");
		resp.getWriter().println("    <script type=\"text/javascript\" src=\"ajax.js\"></script>");
		resp.getWriter().println("    <script type=\"text/javascript\" src=\"loadKML.js\"></script>");
		resp.getWriter().println("    <script type=\"text/javascript\" src=\"RotateIcon.js\"></script>");
		resp.getWriter().println("  </head>");
		resp.getWriter().println("  <body style=\"font-family: Roboto,arial,sans-serif;\">");
		resp.getWriter().println("    <div style=\"margin: 0.5em; height: 5%\">");
		resp.getWriter().println("      <div style=\"float: left;\">Heure: <span id=\"tme\"></span><br/>Cap: <span id=\"hdg\"></span></div><div style=\"float: right;\">Vitesse sol: <span id=\"spd\"></span><br/>Altitude: <span id=\"alt\"></span></div>");
		resp.getWriter().println("      <div style=\"text-align:center;\"><b id=\"name\">Vol de "+name+"</b></div>");
		resp.getWriter().println("      <div style=\"text-align:center;\"><a href=\"https://volmoteurlasbordes.appspot.com/get?deviceID="+deviceId+"&date="+date+"\">https://isaelasbordes.appspot.com/get?deviceID="+deviceId+"&date="+date+"</a></div>");
		resp.getWriter().println("    </div>");
		resp.getWriter().println("    <div id=\"map\"></div>");
		resp.getWriter().println("    <script>");
		resp.getWriter().println("");
		resp.getWriter().println("	var map;");
		resp.getWriter().println("	var URL;");
		resp.getWriter().println("	var kmlLayer;");
		resp.getWriter().println("	var plane = 'data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABgAAAAYCAYAAADgdz34AAAEAUlEQVRIS7WVe0yVZRzHP+97zntucF443JLTRW4hEBRXTZtwKkZEZFSUbS4x52aZ5fpD/aO51sqmznVxzTFQdOsiLCcbIyeaBoyrtHDhHGgCCxabxu0gHA7n1p46bCQWZPT8+z7P83mf7+/7+/4k/ucl/Yv7FSAdCAd+An5ZzNnFAgzALq1WW2wymULtdvsPwEdA+0KQxQLWKIpSXlpampSVlcm654ro7+vbC3wAOP8JsljAM6qqljW3XLTGx6/gqbwnqa+/8BXwJjC+FIACVVXLz37XZo1PSOTZAhvNTQ1f+gH2JQGYzWp5zek2a1x8Ii8/b6O1ZYkBgWa1vPJUmzU2LpGNr9joaF9aQH5AYNCRihOt90bHJPL6Jhs/djR8AWwDbv1XicKAFw3GgPc+r2iLjIpJ5u0ta7jS1VoL7AOa7xYgvL8a2KDojMWpmXlBb+0+TqAazPnTR6k8/j7DNwcuApXASWAQ8N0O+zubpgDrgJKIyAejt+6s0kbFpYJPwusDnQ7GR0fobK/l2KGSCaARqADqgMm5kNsBZqAAeC3AHGHLyn5VX7L9IKPD4Jv3b6DoYGpigtL9BQz0dvR5PM4zQBlwBZgRoLmAx4XWwPrVubvCHs3dQVCIFbf7r5vmSSCBx+NisK+dqsOFrmnHuLhcGKAa6J8FFEqSZo/ZErVybeEBEjJeYMYJsgSS/Kew01PDKEoAssbA1K1fMRgtaBXjHx+FbGKfToEzJ7Zx9VLlmNMxKuqzXQBESr5jtsTuK9jSJBkDl+H1gKIHnxf6Lldz/VIFY791O7OLv9GHWVO58HURE2PXsUY/QVz6Vu5ZnsSMA7xekGWwj/Ry6lCsqM3GWcBOc2jihzkb2iSva4bpyV4Ge2p8V9v3OoCbwoqy1vBw9vqmZEtkBs0n87nRX3cOCAGiTGqMmmLbr6jhj6A3RSJrTdR8ohGAzQKgEZmi1QV9poan4bD3uqYnh3p8Xtdlfxx/D4TJGmNZRlFTjBqRTmdtPiMDdcKa7wIJwGPAQ1p9cJohwGo1Ba/gRm+1aMCi2Rpk+zeL4GoAOoFuYNgvYaGk0X+akNf8QGB4Bj3ncrEPnf9WSAtc8xc+zg/LADL95w/OAsQrRMeKbBdkt/+QDtgh+sEYkpl0f3aDJGtMOCeu0X823uEDAfkYaJ3jLj1gEr4AHAvNg2TgqOm+TSsjso7hmTNavK5hhhpT8DqHBGAPMHWnyFgIkAlyRdCqlhTFsgr3+M+4x+rRLduMrJcZa8vBPdYoBs8bgCjqvLUQIA1JrtAtP5DqGjqMz9k7Ip4NWDShL5l8jm68U12iqcRkuytAILAbjX4tHmc/IBK0C3gayEHSKPg8IuyqANedXvA7XXdWTTbEB70AAAAASUVORK5CYII=';");
		resp.getWriter().println("	var planeMarker;");
		resp.getWriter().println("	var firstTime = true;");
		resp.getWriter().println("	");
		resp.getWriter().println("	function initMap() {");
		resp.getWriter().println("	  map = new google.maps.Map(document.getElementById('map'), {");
		resp.getWriter().println("	    zoom: 12,");
		resp.getWriter().println("	    center: {lat: 43.584, lng: 1.503},");
		resp.getWriter().println("	    mapTypeId: google.maps.MapTypeId.HYBRID");
		resp.getWriter().println("	  });");
		resp.getWriter().println("	  ");
		resp.getWriter().println("	  kmlLayer = new google.maps.KmlLayer();");
		resp.getWriter().println("	  kmlLayer.setOptions({preserveViewport: true});");
		resp.getWriter().println("	  planeMarker = new google.maps.Marker();");
		resp.getWriter().println("	  ");
		resp.getWriter().println("	  google.maps.event.addListener(kmlLayer, 'defaultviewport_changed', function() {");
		resp.getWriter().println("	    if(firstTime) {");
		resp.getWriter().println("	      map.setCenter(kmlLayer.getDefaultViewport().getCenter());");
		resp.getWriter().println("	      firstTime = false;");
		resp.getWriter().println("	    }");
		resp.getWriter().println("	   });");
		resp.getWriter().println("	  loadKML();");
		resp.getWriter().println("	}");
		resp.getWriter().println("	");
		resp.getWriter().println("	function loadKML() {");
		resp.getWriter().println("	  URL = 'https://volmoteurlasbordes.appspot.com/get?deviceID="+deviceId+"&date="+date+"&dummy='+(new Date()).getTime();");
		resp.getWriter().println("	  loadLastPosition();");
		resp.getWriter().println("	}");
		resp.getWriter().println("	");
		resp.getWriter().println("	setInterval(loadKML, 10000);");
		resp.getWriter().println("");
		resp.getWriter().println("    </script>");
		resp.getWriter().println("    <script async defer");
		resp.getWriter().println("        src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyDQheTY6_0xMQQQdD4L1XCx4dDsd2MErzw&callback=initMap\">");
		resp.getWriter().println("    </script>");
		resp.getWriter().println("  </body>");
		resp.getWriter().println("</html>");
	}
}