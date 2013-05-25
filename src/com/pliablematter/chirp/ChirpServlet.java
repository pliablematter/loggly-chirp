package com.pliablematter.chirp;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ChirpServlet extends HttpServlet {

	private static final long serialVersionUID = -8702080337557349003L;
	
	private static Logger logger = Logger.getLogger(ChirpServlet.class.getName());

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		try {
			Properties props = new Properties();
			Session session = Session.getDefaultInstance(props, null);
			
			String fromAddress = System.getProperty("chirp.from-address");
			String toAddress = System.getProperty("chirp.to-address");
			
			// Validate system properties
			if(fromAddress == null || toAddress == null) {
				logger.severe("chirp.from-address and chirp.to-address must be defined in appengine-web.xml");
				response.setStatus(500);
				return;
			}
			
			// Build subject with format: Loggly Notification : [subject] : [last_state_change]
			StringBuilder subject = new StringBuilder();
			subject.append("Loggly Notification");
			String s = request.getParameter("subject");
			if(s != null) {
				subject.append(" : ");
				subject.append(s);
			}
			String lsc = request.getParameter("last_state_change");
			if(lsc != null) {
				subject.append(" : ");
				subject.append(lsc);
			}

			// Build body with all params
			StringBuilder body = new StringBuilder();
			@SuppressWarnings("unchecked")
			Enumeration<String> parameters = (Enumeration<String>) request.getParameterNames();
			while(parameters.hasMoreElements()) {
				String key = parameters.nextElement();
				String value = request.getParameter(key);
				body.append(key);
				body.append(": ");
				body.append(value);
				body.append("\n");
			}

			// Send the message
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(fromAddress));
			message.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
			message.setSubject(subject.toString());
			message.setText(body.toString());
			Transport.send(message);
			
		} catch (Exception e) {
			logger.log(Level.SEVERE, "Exception while processing POST request", e);
			throw new RuntimeException(e);
		}
	}
}
