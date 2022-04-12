package com.contactmanager.Service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;


@Service   
public class EmailService {
	public boolean sendEmail(String to,String subject,String message) {
		
		String host = "smtp.gmail.com";
		
		Properties properties = System.getProperties();
		
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable","true" );
		properties.put("mail.smtp.auth", "true");
		
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
				return new PasswordAuthentication("iwatamakiterstue@gmail.com", "Khaleeltaj123");
			}
		});
		
		session.setDebug(true);
		try {
			MimeMessage msg = new MimeMessage(session);
			msg.setFrom("iwatamakiterstue@gmail.com");
			msg.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			msg.setSubject(subject);
			msg.setText(message);
			Transport.send(msg);
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}
}
