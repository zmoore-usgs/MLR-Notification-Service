package gov.usgs.wma.mlrnotification.email;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
 
import javax.annotation.Resource;
import javax.mail.Message;
import javax.mail.MessagingException;
 
import static org.junit.Assert.assertEquals;
import org.springframework.test.context.junit4.SpringRunner;
 
@RunWith(SpringRunner.class)
public class EmailTest {
 
    @Resource
    private JavaMailSenderImpl emailSender;
 
    private GreenMail testSmtp;
 
    @Before
    public void testSmtpInit(){
        testSmtp = new GreenMail(ServerSetupTest.SMTP);
        testSmtp.start();
 
        //don't forget to set the test port!
        emailSender.setPort(3025);
        emailSender.setHost("localhost");
    }
 
    @Test
    public void testEmail() throws InterruptedException, MessagingException {
        SimpleMailMessage message = new SimpleMailMessage();
 
        message.setFrom("test@sender.com");
        message.setTo("test@receiver.com");
        message.setSubject("test subject");
        message.setText("test message");
        emailSender.send(message);
         
        Message[] messages = testSmtp.getReceivedMessages();
        assertEquals(1, messages.length);
        assertEquals("test subject", messages[0].getSubject());
        String body = GreenMailUtil.getBody(messages[0]).replaceAll("=\r?\n", "");
        assertEquals("test message", body);
    }
 
    @After
    public void cleanup(){
        testSmtp.stop();
    }
}