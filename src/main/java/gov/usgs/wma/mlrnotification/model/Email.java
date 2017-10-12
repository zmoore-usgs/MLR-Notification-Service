package gov.usgs.wma.mlrnotification.model;

import java.util.List;
import org.apache.commons.validator.routines.EmailValidator;

/**
 *
 * @author zmoore
 */
public class  Email {
	private List<String> to;
	private List<String> cc;
	private List<String> bcc;
	
	private String from;
	private String subject;
	private String replyTo;
	private String textBody;
	private String htmlBody;
	
	public List<String> getTo() {
		return to;
	}
	
	public List<String> getCc() {
		return cc;
	}
	
	public List<String> getBcc() {
		return bcc;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public String getReplyTo() {
		return replyTo;
	}
	
	public String getTextBody() {
		return textBody;
	}
	
	public String getHtmlBody() {
		return htmlBody;
	}
	
	public void setTo(List<String> to) {
		this.to = to;
	}
	
	public void setCc(List<String> cc) {
		this.cc = cc;
	}
	
	public void setBcc(List<String> bcc) {
		this.bcc = bcc;
	}
	
	public void setFrom(String from) {
		this.from = from;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	public void setReplyTo(String replyTo) {
		this.replyTo = replyTo;
	}
	
	public void setTextBody(String textBody) {
		this.textBody = textBody;
	}
	
	public void setHtmlBody(String htmlBody) {
		this.htmlBody = htmlBody;
	}
	
	public String validateRequiredParameters() {
		//Validate To
		if(getTo() != null && getTo().size() > 0) {
			for(String recipient : getTo()) {
				if(!EmailValidator.getInstance(false).isValid(recipient)) {
					return "A provided recipient email address is invalid: " + recipient;
				}
			}
		} else {
			return "No recipient email addresses provided.";
		}
		
		//Validate From
		if(getFrom() != null && getFrom().length() > 0) {
			if(!EmailValidator.getInstance(false).isValid(getFrom())) {
				return "The provided sender email address is invalid: " + getFrom();
			}
		} else {
			return "No sender email addresses provided.";
		}
				
		//Validate Subject
		if(getSubject() == null || getSubject().length() == 0) {
			return "No subject content was provided.";
		}
		
		//Validate Body
		if(getTextBody() != null && getHtmlBody() != null) {
			return "Both a text body and HTML body were provided. Only one of these should be provided.";
		} else if(getTextBody() == null && getHtmlBody() == null) {
			return "No body content was provided. Exactly one of htmlBody or textBody must be set.";
		}
		
		return null;
	}
	
	public String validateOptionalParameters() {
		//Validate CC
		if(getCc() != null && getCc().size() > 0) {
			for(String cc : getCc()) {
				if(!EmailValidator.getInstance(false).isValid(cc)) {
					return "A provided cc email address is invalid: " + cc;
				}
			}
		}
		
		//Validate BCC
		if(getBcc() != null && getBcc().size() > 0) {
			for(String bcc : getBcc()) {
				if(!EmailValidator.getInstance(false).isValid(bcc)) {
					return "A provided bcc email address is invalid: " + bcc;
				}
			}
		}
		
		//Validate ReplyTo
		if(getReplyTo() != null && getReplyTo().length() > 0) {
			if(!EmailValidator.getInstance(false).isValid(getReplyTo())) {
				return "The provided reply to email address is invalid: " + getReplyTo();
			}
		}
		
		return null;
	}
	
	public String validate() {
		String required =  validateRequiredParameters();
		return required == null ? validateOptionalParameters() : required;
	}
	
	public boolean isValid() {
		return validate() == null;
	}
}
