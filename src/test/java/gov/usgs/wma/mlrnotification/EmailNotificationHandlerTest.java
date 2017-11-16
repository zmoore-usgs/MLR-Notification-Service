package gov.usgs.wma.mlrnotification;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import gov.usgs.wma.mlrnotification.model.Email;
import java.util.ArrayList;
import javax.mail.internet.MimeMessage;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class EmailNotificationHandlerTest {
	
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
