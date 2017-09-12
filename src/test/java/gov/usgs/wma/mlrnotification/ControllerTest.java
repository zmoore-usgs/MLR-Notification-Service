package gov.usgs.wma.mlrnotification;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

@RunWith(SpringRunner.class)
@WebMvcTest(Controller.class)
@AutoConfigureMockMvc(secure=false)
public class ControllerTest {	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private EmailNotificationHandler emailHandler;
	
	@Test
	public void testEmailController() throws Exception {
		String validEmail = "test@test.com";
		String invalidEmail = "testtestcom";
		String emptyEmail = "";
		String nullEmail = null;
		String validContent = "test";
		String emptyContent = "";
		String nullContent = null;
		
		given(emailHandler.sendEmail(validContent, validContent, validEmail)).willReturn(null);
		given(emailHandler.sendEmail(validContent, validContent, invalidEmail)).willReturn("error");
		given(emailHandler.sendEmail(validContent, validContent, emptyEmail)).willReturn("error");
		given(emailHandler.sendEmail(validContent, validContent, nullEmail)).willReturn("error");
		given(emailHandler.sendEmail(validContent, emptyContent, validEmail)).willReturn("error");
		given(emailHandler.sendEmail(validContent, nullContent, validEmail)).willReturn("error");
		given(emailHandler.sendEmail(emptyContent, validContent, validEmail)).willReturn("error");
		given(emailHandler.sendEmail(nullContent, validContent, validEmail)).willReturn("error");
		
		//Valid Subject, Message, and Recipient
		mvc.perform(post("/notification/email")
				.param("subject", validContent)
				.param("message", validContent)
				.param("recipient", validEmail))
				.andExpect(status().isOk());
		
		//Invalid Subject
		mvc.perform(post("/notification/email")
				.param("subject", emptyContent)
				.param("message", validContent)
				.param("recipient", "test@test.com"))
				.andExpect(status().is5xxServerError());
		mvc.perform(post("/notification/email")
				.param("message", validContent)
				.param("recipient", "test@test.com"))
				.andExpect(status().is4xxClientError());
		
		//Invalid Message
		mvc.perform(post("/notification/email")
				.param("subject", validContent)
				.param("message", emptyContent)
				.param("recipient", "test@test.com"))
				.andExpect(status().is5xxServerError());
		mvc.perform(post("/notification/email")
				.param("subject", validContent)
				.param("recipient", "test@test.com"))
				.andExpect(status().is4xxClientError());
		
		//Invalid Recipient
		mvc.perform(post("/notification/email")
				.param("subject", validContent)
				.param("message", validContent)
				.param("recipient", "testtestcom"))
				.andExpect(status().is5xxServerError());
		mvc.perform(post("/notification/email")
				.param("subject", validContent)
				.param("message", validContent)
				.param("recipient", emptyContent))
				.andExpect(status().is5xxServerError());
		mvc.perform(post("/notification/email")
				.param("subject", validContent)
				.param("message", validContent))
				.andExpect(status().is4xxClientError());
	}
}