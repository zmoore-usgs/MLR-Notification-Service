package gov.usgs.wma.mlrnotification.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import gov.usgs.wma.mlrnotification.model.EmailRequest;
import java.nio.charset.Charset;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.MimeMessageHelper;

@Service
public class EmailService {
	@Autowired 
	public JavaMailSender mailSender;
	
	private static final Logger LOG = LoggerFactory.getLogger(EmailService.class);
	public static final String DEFAULT_ATTACHMENT_FILENAME = "attachment";

	public String sendEmail(EmailRequest email){
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
			
			String attachmentFileName = null == email.getAttachmentFileName() ? DEFAULT_ATTACHMENT_FILENAME : email.getAttachmentFileName();
			String attachment = email.getAttachment();
			if(null != attachment) {
				InputStreamSource attachmentSource = new ByteArrayResource(attachment.getBytes(Charset.forName("UTF-8")));
				mailBuilder.addAttachment(attachmentFileName, attachmentSource);
			}
			
			//Send mime message
			mailSender.send(mailBuilder.getMimeMessage());
		} catch(Exception ex) {
			LOG.error("Error sending email", ex);
			return ex.getMessage() != null ? ex.getMessage() : ex.toString();
		}
		
		return null;
	}
}
