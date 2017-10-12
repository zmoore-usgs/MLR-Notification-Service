package gov.usgs.wma.mlrnotification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import gov.usgs.wma.mlrnotification.model.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailNotificationHandler {
	@Autowired
	public JavaMailSender mailSender;
	private static final Logger LOG = LoggerFactory.getLogger(EmailNotificationHandler.class);
		
	public String sendEmail(Email email){
		try {
			//Build mime message from data
			MimeMessageHelper mailBuilder = new MimeMessageHelper(mailSender.createMimeMessage(), true);

			//Set Required Parameters
			mailBuilder.setTo(email.getTo().toArray(new String[email.getTo().size()]));
			mailBuilder.setFrom(email.getFrom());
			mailBuilder.setSubject(email.getSubject());
			
			//Set mime message body based on provided data
			if(email.getHtmlBody() != null) {
				mailBuilder.setText(email.getHtmlBody(), true);
			} else {
				mailBuilder.setText(email.getTextBody(), false);
			}
			
			//Set Optional Parameters
			if(email.getCc() != null && email.getCc().size() > 0){
				mailBuilder.setCc(email.getCc().toArray(new String[email.getCc().size()]));
			}
			
			if(email.getBcc() != null && email.getBcc().size() > 0){
				mailBuilder.setBcc(email.getBcc().toArray(new String[email.getBcc().size()]));
			}
			
			if(email.getReplyTo() != null && email.getReplyTo().length() > 0){
				mailBuilder.setReplyTo(email.getReplyTo());
			}
			
			//Send mime message
			mailSender.send(mailBuilder.getMimeMessage());
		} catch(Exception ex) {
			LOG.error(ex.getMessage() + "\nStack Trace:\n" + ex.getStackTrace());
			return ex.getMessage() != null ? ex.getMessage() : ex.toString();
		}
		
		return null;
	}
}
