package com.coconuts.isaelasbordes.server;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WebIOSServlet extends HttpServlet {
	private static final long serialVersionUID = 770260676398954172L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.sendRedirect("https://volmoteurlasbordes.appspot.com/ios/");
    }
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		doPost(req, resp);
	}
}