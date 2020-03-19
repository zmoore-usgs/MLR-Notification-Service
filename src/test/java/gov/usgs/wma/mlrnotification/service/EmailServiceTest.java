package gov.usgs.wma.mlrnotification.service;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import gov.usgs.wma.mlrnotification.model.EmailRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.StringUtils;

@SpringBootTest
@ActiveProfiles("test")
public class EmailServiceTest {
	
	@Autowired
	private EmailService emailService;

	private GreenMail testSmtp;

	@Value("${spring.mail.port}")
	private int smtpPort;
		
	private EmailRequest validEmail;
	
	private final String validText = "test";
	private final String validAddress = "test@test.com";
	
	@BeforeEach
	public void setup() {
		ArrayList<String> toList = new ArrayList<>();
		toList.add(validAddress);
		
		//Setup mock smtp server
		testSmtp = new GreenMail(new ServerSetup(smtpPort, null, "smtp"));
		testSmtp.start();
		
		validEmail = new EmailRequest();
		validEmail.setTo(toList);
		validEmail.setFrom(validAddress);
		validEmail.setSubject(validText);
		validEmail.setTextBody(validText);
	}
	
	
	@Test
	public void testEmailSendValidData() throws Exception {
		//Send Valid Email to Valid Recipient - Expect Valid Response and Equivalent Data
		String status = emailService.sendEmail(validEmail);
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
		
		String status = emailService.sendEmail(validEmail);
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
		assertEquals(emailService.sendEmail(validEmail), null);
		
		//Send to Missing Recipient - Expect Error Response
		toList.clear();
		toList.add(null);
		validEmail.setTo(toList);
		assertTrue(emailService.sendEmail(validEmail).contains("java.lang.NullPointerException"));
		
		//Verify Recieved Emails
		MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
		
		//Verify Contents of Emails 
		String subject = (String) receivedMessages[0].getSubject();
		assertTrue(subject.contains(validText));
	}
	
	/**
	 * based off of https://stackoverflow.com/a/1748229
	 * @param message
	 * @return Map of String file name to String file contents
	 * @throws MessagingException
	 * @throws FileNotFoundException
	 * @throws IOException 
	 */
	private Map<String, String> getAttachments (Message message) throws MessagingException, FileNotFoundException, IOException {
		Map<String, String> attachments = new HashMap<>();
		Multipart multipart = (Multipart) message.getContent();

		for (int i = 0; i < multipart.getCount(); i++) {
			BodyPart bodyPart = multipart.getBodyPart(i);
			if (!Part.ATTACHMENT.equalsIgnoreCase(bodyPart.getDisposition())
				&& !StringUtils.hasText(bodyPart.getFileName())) {
				continue; // dealing with attachments only
			}

			String fileName = bodyPart.getFileName();

			try(InputStream is = bodyPart.getInputStream(); Scanner s = new Scanner(is);) {
				s.useDelimiter("\\A");
				String fileContents = s.hasNext() ? s.next() : "";
				s.close();
				attachments.put(fileName, fileContents);
			}
		}
		return attachments;
	}
	
	@Test
	public void testEmailSendValidDataWithAttachment() throws Exception {
		//Send Valid Email to Valid Recipient - Expect Valid Response and Equivalent Data
		final String ATTACHED_DATA = "H20";
		validEmail.setAttachment(ATTACHED_DATA);
		String status = emailService.sendEmail(validEmail);
		assertEquals(status, null);
		
		//Verify Received Emails
		MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
		assertEquals(1, receivedMessages.length);
		MimeMessage msg = receivedMessages[0];
		
		//Verify Email Content
		String subject = msg.getSubject();
		assertTrue(subject.contains(validText));
		
		//Verify attachment
		Map<String, String> attachments = getAttachments(msg);
		assertEquals(1, attachments.size());
		String actualData = attachments.get(EmailService.DEFAULT_ATTACHMENT_FILENAME);
		assertEquals(ATTACHED_DATA, actualData);
	}
	
	@Test
	public void testEmailSendValidDataWithCustomFileNameAttachment() throws Exception {
		//Send Valid Email to Valid Recipient - Expect Valid Response and Equivalent Data
		final String ATTACHED_DATA = "WATER";
		validEmail.setAttachment(ATTACHED_DATA);
		final String ATTACHED_FILE_NAME = "mlr.json";
		validEmail.setAttachmentFileName(ATTACHED_FILE_NAME);
		
		String status = emailService.sendEmail(validEmail);
		assertEquals(status, null);
		
		//Verify Received Emails
		MimeMessage[] receivedMessages = testSmtp.getReceivedMessages();
		assertEquals(1, receivedMessages.length);
		MimeMessage msg = receivedMessages[0];
		
		//Verify Email Content
		String subject = msg.getSubject();
		assertTrue(subject.contains(validText));
		
		//Verify attachment
		Map<String, String> attachments = getAttachments(msg);
		assertEquals(1, attachments.size());
		String actualData = attachments.get(ATTACHED_FILE_NAME);
		assertEquals(ATTACHED_DATA, actualData);
	}
	
	@AfterEach
	public void shutdown() {
		testSmtp.stop();
	}		
}
