package gov.usgs.wma.mlrnotification.email;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.Test;
import org.junit.runner.RunWith;
import gov.usgs.wma.mlrnotification.EmailNotificationHandler;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
	
	public static final String VALID_CONTENT = "test";
	public static final String EMPTY_CONTENT = "";
	public static final String VALID_EMAIL = "test@test.com";
	public static final String INVALID_EMAIL = "testtestcom";
	
	@Test
	public void testEmailValidationValidData() throws Exception {
		//Send Valid Email with Valid Subject to Valid Recipient - Expect Null Response
		String status = emailHandler.validateEmailParameters(VALID_CONTENT, VALID_CONTENT, VALID_EMAIL);
		assertEquals(status, null);
	}
	
	@Test
	public void testEmailValidationInvalidSubject() throws Exception {
		//Send Invalid Subject - Expect Error Response
		String status = emailHandler.validateEmailParameters(EMPTY_CONTENT, VALID_CONTENT, VALID_EMAIL);
		assertEquals(status, "No subject content recieved.");
		status = emailHandler.validateEmailParameters(null, VALID_CONTENT, VALID_EMAIL);
		assertEquals(status, "No subject content recieved.");
	}
	
	@Test
	public void testEmailValidationInvalidMessage() throws Exception {
		//Send with Invalid Message - Expect Error Response
		String status = emailHandler.validateEmailParameters(VALID_CONTENT, EMPTY_CONTENT, VALID_EMAIL);
		assertEquals(status, "No message content recieved.");
		status = emailHandler.validateEmailParameters(VALID_CONTENT, null, VALID_EMAIL);
		assertEquals(status, "No message content recieved.");
	}
	
	@Test
	public void testEmailValidationInvalidRecipient() throws Exception {
		//Send to Invalid Recipient - Expect Error Response
		String status = emailHandler.validateEmailParameters(VALID_CONTENT, VALID_CONTENT, INVALID_EMAIL);
		assertEquals(status, "The provided recipient email address is invalid.");
		status = emailHandler.validateEmailParameters(VALID_CONTENT, VALID_CONTENT, EMPTY_CONTENT);
		assertEquals(status, "The provided recipient email address is invalid.");
		status = emailHandler.validateEmailParameters(VALID_CONTENT, VALID_CONTENT, null);
		assertEquals(status, "The provided recipient email address is invalid.");
	}
	
	@Test
	public void testEmailSendValidData() throws Exception {
		//Setup mock smtp server
		testSmtp = new GreenMail(new ServerSetup(smtpPort, null, "smtp"));
		testSmtp.start();
		
		//Send Valid Email to Valid Recipient - Expect Valid Response and Equivalent Data
		String status = emailHandler.sendEmail(VALID_CONTENT, VALID_CONTENT, VALID_EMAIL);
		assertEquals(status, null);
		
		//Verify Received Emails
		MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
		assertEquals(1, receivedMessages.length);
		
		//Verify Email Content
		String content = (String) receivedMessages[0].getContent();
		assertTrue(content.contains(emailHandler.getTemplateText() + VALID_CONTENT));
		
		//Stop mock smtp server
		testSmtp.stop();
	}
	
	@Test
	public void testEmailSendInvalidSubject() throws Exception {
		//Setup mock smtp server
		testSmtp = new GreenMail(new ServerSetup(smtpPort, null, "smtp"));
		testSmtp.start();
		
		//Send  with Empty Subject - Expect Valid Response
		assertEquals(emailHandler.sendEmail(EMPTY_CONTENT, VALID_CONTENT, VALID_EMAIL), null);
		//Send with Missing Subject - Expect Valid Response
		assertEquals(emailHandler.sendEmail(null, VALID_CONTENT, VALID_EMAIL), null);
		
		//Verify Recieved Emails
		MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
		assertEquals(2, receivedMessages.length);
		
		//Verify Contents of Emails 
		String content = (String) receivedMessages[0].getContent();
		assertTrue(content.contains(emailHandler.getTemplateText() + VALID_CONTENT));
		content = (String) receivedMessages[1].getContent();
		assertTrue(content.contains(emailHandler.getTemplateText() + VALID_CONTENT));
		
		//Stop mock smtp server
		testSmtp.stop();
	}
	
	@Test
	public void testEmailSendInvalidMessage() throws Exception {
		//Setup mock smtp server
		testSmtp = new GreenMail(new ServerSetup(smtpPort, null, "smtp"));
		testSmtp.start();
		
		//Send with Empty Message - Expect Valid Response
		assertEquals(emailHandler.sendEmail(VALID_CONTENT, EMPTY_CONTENT, VALID_EMAIL), null);
		//Send with Missing Message - Expect Valid Response
		assertEquals(emailHandler.sendEmail(VALID_CONTENT, null, VALID_EMAIL), null);
		
		//Verify Recieved Emails
		MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
		assertEquals(2, receivedMessages.length);
		
		//Verify Subjects of Emails 
		String subject = (String) receivedMessages[0].getSubject();
		assertTrue(subject.contains(VALID_CONTENT));
		subject = (String) receivedMessages[1].getSubject();
		assertTrue(subject.contains(VALID_CONTENT));
		
		//Stop mock smtp server
		testSmtp.stop();
	}
	
	@Test
	public void testEmailSendInvalidRecipient() throws Exception {
		//Setup mock smtp server
		testSmtp = new GreenMail(new ServerSetup(smtpPort, null, "smtp"));
		testSmtp.start();
		
		//Send to Invalid Recipient - Expect Valid Response
		assertEquals(emailHandler.sendEmail(VALID_CONTENT, VALID_CONTENT, INVALID_EMAIL), null);

		//Send to Empty Recipient - Expect Error Response
		assertTrue(emailHandler.sendEmail(VALID_CONTENT, VALID_CONTENT, EMPTY_CONTENT).contains("Could not parse mail"));
		
		//Send to Missing Recipient - Expect Error Response
		assertTrue(emailHandler.sendEmail(VALID_CONTENT, VALID_CONTENT, null).contains("java.lang.NullPointerException"));
		
		//Verify Recieved Emails
		MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
		assertEquals(1, receivedMessages.length);
		
		//Verify Contents of Emails 
		String content = (String) receivedMessages[0].getContent();
		assertTrue(content.contains(emailHandler.getTemplateText() + VALID_CONTENT));
		
		//Stop mock smtp server
		testSmtp.stop();
	}
	
	@Test
	public void testEmailSendInvalidServer() throws Exception {
		//Send Valid Email to Valid Recipient with Server Disabled - Expect Error Response
		assertTrue(emailHandler.sendEmail(VALID_CONTENT, VALID_CONTENT, VALID_EMAIL).contains("Mail server connection failed"));
	}
}