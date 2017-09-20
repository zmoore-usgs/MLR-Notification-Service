package gov.usgs.wma.mlrnotification;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailNotificationHandler {
	@Autowired
	public JavaMailSender mailSender;
	
	@Value("${MLR_EMAIL_TEMPLATE_TEXT:}")
	private String templateText;
	
	@Value("${MLR_EMAIL_TEMPLATE_FROM}")
	private String templateFrom;
	
	public String getTemplateText() {
		return templateText;
	}
	
	public String getTemplateFrom() {
		return templateFrom;
	}
	
	public String sendEmail(String subject, String message, String recipient){
		SimpleMailMessage email = new SimpleMailMessage();
						
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
			return ex.getMessage() != null ? ex.getMessage() : ex.toString();
		}
		
		return null;
	}
	
	public String validateEmailParameters(String subject, String message, String recipient) {
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
