/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gov.usgs.wma.mlrnotification;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 *
 * @author zmoore
 */
@Service
public class EmailNotificationHandler {
	@Autowired
	public JavaMailSender mailSender;
	
	@Value("${mlrEmailTemplateText}")
	private String templateText;
	
	@Value("${mlrEmailTemplateFrom}")
	private String templateFrom;
	
	public String getTemplateText() {
		return templateText;
	}
	
	public String getTemplateFrom() {
		return templateFrom;
	}
	
	public String sendEmail(String subject, String message, String recipient){
		SimpleMailMessage email = new SimpleMailMessage();
		
		//Validate Parameters
		String validationStatus = validateMessageParameters(subject, message, recipient);
		
		if(validationStatus != null){
			return validationStatus;
		}
		
		//Build Email Text
		String fullText = templateText + message;
		
		//Build Email
		email.setFrom(templateFrom);
		email.setTo(recipient);
		email.setSubject(subject);
		email.setText(fullText);
		
		//Send Email
		try {
			mailSender.send(email);
		} catch (Exception ex) {
			return ex.getMessage() != null ? ex.getMessage() : "Unhandled Exception: " + ex.toString();
		}
		
		return null;
	}
	
	public String validateMessageParameters(String subject, String message, String recipient) {
		//Validate Recipient
		if(!EmailValidator.getInstance(false).isValid(recipient)){
			return "The provided recipient email address is invalid.";
		}
		
		//Validate Subject
		if(subject == null || subject.length() == 0){
			return "No subject content recieved.";
		}
		
		//Validate Message
		if(message == null || message.length() == 0){
			return "No message content recieved.";
		}
		
		return null;
	}
}
