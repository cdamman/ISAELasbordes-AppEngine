package com.coconuts.isaelasbordes.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.appengine.api.datastore.Blob;

public class SendServlet extends HttpServlet {
	private static final long serialVersionUID = 3383006676925147590L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ServletFileUpload upload = new ServletFileUpload();
		try {
			FileItemIterator iter = upload.getItemIterator(req);
	        FileItemStream flightItem = iter.next();
	        InputStream flightStream = flightItem.openStream();
	
	        // construct our entity objects
	        Blob flightBlob = new Blob(IOUtils.toByteArray(flightStream));

        	System.out.println(flightItem.getName());
	        
        	String[] splitedName = flightItem.getName().split("_");
        	String deviceID = splitedName[1];
            String date = splitedName[2];
            
            FlightKML myFlight;
	        
	        PersistenceManager pm = PMF.get().getPersistenceManager();
	        try {
	            Query q = pm.newQuery(FlightKML.class, "deviceRegistrationID == '"+deviceID+"' && date == '"+date+"'");
	            
	        	@SuppressWarnings("unchecked")
				List<FlightKML> results = (List<FlightKML>) q.execute();
	        	if(!results.isEmpty()) {
	        		if(results.size() == 1) {
		        		myFlight = results.iterator().next();
		        		if(splitedName.length > 4) {
			        		myFlight.setFileKML(flightBlob, splitedName[3]);
		        		} else {
			        		myFlight.setFileKML(flightBlob);
		        		}
	        		} else {
	        	        resp.setContentType("text/plain");
	        	    	resp.getWriter().println("Erreur: fichiers multiples");
	        	    	return;
	        		}
	        	} else {
	        		
	        		String name = "Vol";
	        		try {
						name = searchName(flightBlob);
					} catch (ParserConfigurationException | SAXException e) {
						e.printStackTrace();
					}
	        		myFlight = new FlightKML(deviceID, date, name, flightBlob);
	    	        pm.makePersistent(myFlight);
	        	}
	        } finally {
	        	pm.close();
	        }
	        
	        // respond to query
	        resp.setContentType("text/plain");
	    	resp.getWriter().println("OK");
		} catch (FileUploadException e) {
	        resp.setContentType("text/plain");
	    	resp.getWriter().println("Erreur d'upload");
		}
    }
	
	public static String searchName(Blob flightBlob) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = db.parse(new ByteArrayInputStream(flightBlob.getBytes()));
		
		// Search for Elements to modify
	    NodeList nl;
	    
	    Element name = null;
	    nl = doc.getDocumentElement().getElementsByTagName("Document").item(0).getChildNodes();
	    if (nl != null) {
            for (int i = 0; i < nl.getLength(); i++) {
                if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                    Element child = (Element) nl.item(i);
                    if (child.getNodeName().equals("name")) {
                    	name = child;
                    }
                }
            }
        }
	    if(name == null) throw new ParserConfigurationException("Name not found");
	    return name.getTextContent();
	}
}