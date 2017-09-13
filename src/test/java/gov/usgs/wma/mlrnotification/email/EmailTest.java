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

	@Test
	public void testEmailValidation() throws Exception {
		//Send Valid Email with Valid Subject to Valid Recipient - Expect Null Response
		String status = emailHandler.validateMessageParameters("test", "test", "test@localhost.com");
		assertEquals(status, null);
		
		//Send Invalid Subject - Expect Error Response
		status = emailHandler.validateMessageParameters("", "test", "test@localhost.com");
		assertEquals(status, "No subject content recieved.");
		status = emailHandler.validateMessageParameters(null, "test", "test@localhost.com");
		assertEquals(status, "No subject content recieved.");
		
		//Send with Invalid Message - Expect Error Response
		status = emailHandler.validateMessageParameters("test", "", "test@localhost.com");
		assertEquals(status, "No message content recieved.");
		status = emailHandler.validateMessageParameters("test", null, "test@localhost.com");
		assertEquals(status, "No message content recieved.");
		
		//Send to Invalid Recipient - Expect Error Response
		status = emailHandler.validateMessageParameters("Valid", "test", "testlocalhostcom");
		assertEquals(status, "The provided recipient email address is invalid.");
		status = emailHandler.validateMessageParameters("Valid", "test", "");
		assertEquals(status, "The provided recipient email address is invalid.");
		status = emailHandler.validateMessageParameters("Valid", "test", null);
		assertEquals(status, "The provided recipient email address is invalid.");
	}
	
	@Test
	public void testEmailSend() throws Exception {
		testSmtp = new GreenMail(new ServerSetup(smtpPort, null, "smtp"));
		testSmtp.start();
		String testText = "test";		
		
		//Send Valid Email to Valid Recipient - Expect Valid Response and Equivalent Data
		String status = emailHandler.sendEmail(testText, "test", "test@localhost.com");
		MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
		assertEquals(1, receivedMessages.length);
		String content = (String) receivedMessages[0].getContent();
		assertTrue(content.contains(emailHandler.getTemplateText() + testText));
		assertEquals(status, null);
		
		//Send  with Invalid Subject - Expect Valid Response
		assertEquals(emailHandler.sendEmail("", "test", "test@localhost.com"), null);
		
		//Send with Missing Subject - Expect Valid Response
		assertEquals(emailHandler.sendEmail(null, "test", "test@localhost.com"), null);
		
		//Send with Invalid Message - Expect Valid Response
		assertEquals(emailHandler.sendEmail("test", "", "test@localhost.com"), null);
		
		//Send with Missing Message - Expect Valid Response
		assertEquals(emailHandler.sendEmail("test", null, "test@localhost.com"), null);
		
		//Send to Invalid Recipient - Expect Valid Response
		assertEquals(emailHandler.sendEmail("test", "test", "testlocalhostcom"), null);
		
		//Send to Missing Recipient - Expect Error Response
		assertTrue(emailHandler.sendEmail("test", "test", "").contains("Could not parse mail"));
		assertTrue(emailHandler.sendEmail("test", "test", null).contains("java.lang.NullPointerException"));
		
		testSmtp.stop();
		
		//Send Valid Email to Valid Recipient with Server Disabled - Expect Error Response
		assertTrue(emailHandler.sendEmail("test", "test", "test@localhost.com").contains("Mail server connection failed"));
	}
}