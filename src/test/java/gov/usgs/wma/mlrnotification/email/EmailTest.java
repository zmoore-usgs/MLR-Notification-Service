package gov.usgs.wma.mlrnotification.email;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import org.junit.After;
import org.junit.Before;
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
 
    @Before
    public void testSmtpInit(){
        testSmtp = new GreenMail(new ServerSetup(smtpPort, null, "smtp"));
        testSmtp.start();
    }
	
	@Test
	public void testEmailValidation() throws Exception {
		//Send Valid Email to Valid Recipient - Expect Valid Response and Equivalent Data
		String status1 = emailHandler.validateMessageParameters("test", "test@localhost.com");
		assertEquals(status1, null);
		
		//Send Invalid Email to Valid Recipient - Expect Error Response
		String status2 = emailHandler.validateMessageParameters("", "test@localhost.com");
		assertEquals(status2, "No message content recieved.");
		String status3 = emailHandler.validateMessageParameters(null, "test@localhost.com");
		assertEquals(status3, "No message content recieved.");
		
		//Send Valid Email to Invalid Recipient - Expect Error Response
		String status4 = emailHandler.validateMessageParameters("Valid", "testlocalhostcom");
		assertEquals(status4, "The provided recipient email address is invalid.");
		String status5 = emailHandler.validateMessageParameters("Valid", "");
		assertEquals(status5, "The provided recipient email address is invalid.");
		String status6 = emailHandler.validateMessageParameters("Valid", null);
		assertEquals(status6, "The provided recipient email address is invalid.");
	}
	
	@Test
	public void testEmailService() throws Exception {
		String testText = "test";		
		
		//Send Valid Email to Valid Recipient - Expect Valid Response and Equivalent Data
		String status1 = emailHandler.sendEmail(testText, "test@localhost.com");
		MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
		assertEquals(1, receivedMessages.length);
		String content = (String) receivedMessages[0].getContent();
		
		assertTrue(content.contains(emailHandler.getTemplateText() + testText));
		assertEquals(status1, null);
		
		//Send Invalid Email to Valid Recipient - Expect Error Response
		String status2 = emailHandler.sendEmail("", "test@localhost.com");
		assertEquals(status2, "No message content recieved.");
		String status3 = emailHandler.sendEmail(null, "test@localhost.com");
		assertEquals(status3, "No message content recieved.");
		
		//Send Valid Email to Invalid Recipient - Expect Error Response
		String status4 = emailHandler.sendEmail("Valid", "testlocalhostcom");
		assertEquals(status4, "The provided recipient email address is invalid.");
		String status5 = emailHandler.sendEmail("Valid", "");
		assertEquals(status5, "The provided recipient email address is invalid.");
		String status6 = emailHandler.sendEmail("Valid", null);
		assertEquals(status6, "The provided recipient email address is invalid.");
	}
 
    @After
    public void cleanup(){
        testSmtp.stop();
    }
}