package gov.usgs.wma.mlrnotification.email;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import gov.usgs.wma.mlrnotification.EmailNotificationHandler;
import gov.usgs.wma.mlrnotification.model.Email;
import java.util.ArrayList;
import javax.mail.internet.MimeMessage;
import org.junit.After;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
 
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmailTest {
	@Autowired
	private EmailNotificationHandler emailHandler;
	private GreenMail testSmtp;

	@Value("${spring.mail.port}")
	private int smtpPort;
		
	private final Email validEmail  = new Email();
	
	private final String validText = "test";
	private final String validAddress = "test@test.com";
	
	@Before
	public void setup() {
		ArrayList<String> toList = new ArrayList<>();
		toList.add(validAddress);
		
		//Setup mock smtp server
		testSmtp = new GreenMail(new ServerSetup(smtpPort, null, "smtp"));
		testSmtp.start();
		
		validEmail.setTo(toList);
		validEmail.setFrom(validAddress);
		validEmail.setSubject(validText);
		validEmail.setTextBody(validText);
	}
	
	@Test
	public void testEmailValidationValidRequiredData() throws Exception {
		//Send Valid Email with Valid Subject to Valid Recipient - Expect Null Response
		//Valid Html Body
		validEmail.setTextBody(null);
		validEmail.setHtmlBody(validText);
		String status = validEmail.validate();
		assertEquals(status, null);
		
		//Valid Text Body
		status = validEmail.validate();
		assertEquals(status, null);
		validEmail.setTextBody(validText);
		validEmail.setHtmlBody(null);
	}
	
	@Test
	public void testEmailValidationValidAllData() throws Exception {
		//Send Valid Email with Valid Subject to Valid Recipient - Expect Null Response
		ArrayList<String> emailList = new ArrayList<>();
		emailList.add(validAddress);
		
		validEmail.setCc(emailList);
		validEmail.setBcc(emailList);
		validEmail.setReplyTo(validAddress);
		validEmail.setFrom(validAddress);
		String status = validEmail.validate();
		assertEquals(status, null);
	}
	
	@Test
	public void testEmailValidationInvalidSubject() throws Exception {
		//Send Invalid Subject - Expect Error Response
		validEmail.setSubject("");
		String status = validEmail.validate();
		assertEquals(status, "No subject content was provided.");
		
		validEmail.setSubject(null);
		status = validEmail.validate();
		assertEquals(status, "No subject content was provided.");
		
		//Reset
		validEmail.setSubject(validText);
	}
	
	@Test
	public void testEmailValidationInvalidMessage() throws Exception {
		//Send with Invalid Message - Expect Error Response
		validEmail.setTextBody(null);
		String status = validEmail.validate();
		assertEquals(status, "No body content was provided. Exactly one of htmlBody or textBody must be set.");
		
		validEmail.setTextBody(validText);
		validEmail.setHtmlBody(validText);
		status = validEmail.validate();
		assertEquals(status, "Both a text body and HTML body were provided. Only one of these should be provided.");
		
		//Reset
		validEmail.setHtmlBody(null);
	}
	
	@Test
	public void testEmailValidationInvalidRecipient() throws Exception {
		//Send to Invalid Recipient - Expect Error Response
		ArrayList<String> toList = new ArrayList<>();
		
		validEmail.setTo(toList);
		String status = validEmail.validate();
		assertEquals(status, "No recipient email addresses provided.");
		
		validEmail.setTo(null);
		status = validEmail.validate();
		assertEquals(status, "No recipient email addresses provided.");
		
		toList.clear();
		toList.add("test");
		validEmail.setTo(toList);
		status = validEmail.validate();
		assertEquals(status, "A provided recipient email address is invalid: " + "test");
		
		toList.add(validAddress);
		validEmail.setTo(toList);
		status = validEmail.validate();
		assertEquals(status, "A provided recipient email address is invalid: " + "test");
		
		toList.clear();
		toList.add("");
		validEmail.setTo(toList);
		status = validEmail.validate();
		assertEquals(status, "A provided recipient email address is invalid: " + "");
		
		toList.clear();
		toList.add(null);
		validEmail.setTo(toList);
		status = validEmail.validate();
		assertEquals(status, "A provided recipient email address is invalid: " + null);
		
		//Reset
		toList.clear();
		toList.add(validAddress);
		validEmail.setTo(toList);
	}
	
	@Test
	public void testEmailValidationInvalidCc() throws Exception {
		//Send to Invalid Recipient - Expect Error Response
		ArrayList<String> ccList = new ArrayList<>();
				
		ccList.clear();
		ccList.add("test");
		validEmail.setCc(ccList);
		String status = validEmail.validate();
		assertEquals(status, "A provided cc email address is invalid: " + "test");
		
		ccList.add(validAddress);
		validEmail.setCc(ccList);
		status = validEmail.validate();
		assertEquals(status, "A provided cc email address is invalid: " + "test");
		
		ccList.clear();
		ccList.add("");
		validEmail.setCc(ccList);
		status = validEmail.validate();
		assertEquals(status, "A provided cc email address is invalid: " + "");
		
		ccList.clear();
		ccList.add(null);
		validEmail.setCc(ccList);
		status = validEmail.validate();
		assertEquals(status, "A provided cc email address is invalid: " + null);
		
		//Reset
		validEmail.setCc(null);
	}
	
	@Test
	public void testEmailValidationInvalidBcc() throws Exception {
		//Send to Invalid Recipient - Expect Error Response
		ArrayList<String> bccList = new ArrayList<>();
				
		bccList.clear();
		bccList.add("test");
		validEmail.setBcc(bccList);
		String status = validEmail.validate();
		assertEquals(status, "A provided bcc email address is invalid: " + "test");
		
		bccList.add(validAddress);
		validEmail.setBcc(bccList);
		status = validEmail.validate();
		assertEquals(status, "A provided bcc email address is invalid: " + "test");
		
		bccList.clear();
		bccList.add("");
		validEmail.setBcc(bccList);
		status = validEmail.validate();
		assertEquals(status, "A provided bcc email address is invalid: " + "");
		
		bccList.clear();
		bccList.add(null);
		validEmail.setBcc(bccList);
		status = validEmail.validate();
		assertEquals(status, "A provided bcc email address is invalid: " + null);
		
		//Reset
		validEmail.setBcc(null);
	}
	
	@Test
	public void testEmailValidationInvalidReplyTo() throws Exception {
		validEmail.setReplyTo("test");
		String status = validEmail.validate();
		assertEquals(status, "The provided reply to email address is invalid: " + "test");
		
		//Reset
		validEmail.setReplyTo(null);
	}
	
	
	@Test
	public void testEmailSendValidData() throws Exception {
		//Send Valid Email to Valid Recipient - Expect Valid Response and Equivalent Data
		String status = emailHandler.sendEmail(validEmail);
		assertEquals(status, null);
		
		//Verify Received Emails
		MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
		
		//Verify Email Content
		String subject = receivedMessages[0].getSubject();
		assertTrue(subject.contains(validText));
	}
	
	@Test
	public void testEmailSendValidDataWithOptional() throws Exception {
		//Send Valid Email to Valid Recipient - Expect Valid Response and Equivalent Data
		ArrayList<String> emailList = new ArrayList<>();
		emailList.add(validAddress);
		
		validEmail.setCc(emailList);
		validEmail.setBcc(emailList);
		validEmail.setReplyTo(validAddress);
		validEmail.setFrom(validAddress);
		
		String status = emailHandler.sendEmail(validEmail);
		assertEquals(status, null);
		
		//Verify Received Emails
		MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
		
		//Verify Email Content
		String subject = receivedMessages[0].getSubject();
		assertTrue(subject.contains(validText));
	}
	
	@Test
	public void testEmailSendInvalidData() throws Exception {
		ArrayList<String> toList = new ArrayList<>();
		
		//Send to Invalid Recipient - Expect Valid Response
		toList.add("test");
		validEmail.setTo(toList);
		assertEquals(emailHandler.sendEmail(validEmail), null);
		
		//Send to Missing Recipient - Expect Error Response
		toList.clear();
		toList.add(null);
		validEmail.setTo(toList);
		assertTrue(emailHandler.sendEmail(validEmail).contains("java.lang.NullPointerException"));
		
		//Verify Recieved Emails
		MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
		
		//Verify Contents of Emails 
		String subject = (String) receivedMessages[0].getSubject();
		assertTrue(subject.contains(validText));
	}
	
	@After
	public void shutdown() {
		testSmtp.stop();
	}	
}