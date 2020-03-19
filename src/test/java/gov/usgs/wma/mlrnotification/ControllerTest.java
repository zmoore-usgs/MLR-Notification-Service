package gov.usgs.wma.mlrnotification;

import gov.usgs.wma.mlrnotification.model.EmailRequest;
import gov.usgs.wma.mlrnotification.service.EmailService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(authorities = "test")
@ActiveProfiles("test")
public class ControllerTest {

	@Autowired
    private WebApplicationContext context;

	private MockMvc mvc;
	
	@MockBean
	private EmailService emailService;
	
	private final String MOCK_ERROR_RESPONSE_500 = "error_500";
	private final String validEmailJsonWithSender = "{\"to\": [\"test@test.com\"], \"from\": \"test@test.net\", \"textBody\": \"test\", \"subject\": \"test\"}";
	private final String validEmailJsonWithoutSender = "{\"to\": [\"test@test.com\"], \"textBody\": \"test\", \"subject\": \"test\"}";
	private final String validEmailJsonWithoutSenderNoServer = "{\"to\": [\"test@test.com\"], \"textBody\": \"test2\", \"subject\": \"test\"}";
	private final String invalidEmailJson = "{\"from\": \"test@test.net\", \"textBody\": \"test\", \"subject\": \"test\"}";
	private final String malformedEmailJson = "{\"from\": \"test@test.net\" \"textBody\": \"test\", \"subject\": \"test\"}";
	private final String validEmailJsonWithOptional = "{\"to\": [\"test@test.com\"], \"from\": \"test@test.net\", \"htmlBody\": \"test\", \"subject\": \"test\", \"cc\": [\"test@test.com\"], \"bcc\": [\"test@test.com\"], \"replyTo\": \"test@test.net\"}";
	
	@BeforeEach
	public void setup() {
		mvc = MockMvcBuilders
			.webAppContextSetup(context)
			.apply(springSecurity()) 
			.build();
	}

	@Test
	public void testEmailControllerValidDataWithSender() throws Exception {
		given(emailService.sendEmail(any(EmailRequest.class))).willReturn(null);
		//Valid Subject, Message, Recipient, and Sender
		mvc.perform(post("/notification/email")
				.content(validEmailJsonWithSender)
				.contentType("application/json"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testEmailControllerValidDataWithoutSender() throws Exception {
		given(emailService.sendEmail(any(EmailRequest.class))).willReturn(null);
		//Valid Subject, Message, Recipient, and Sender
		mvc.perform(post("/notification/email")
				.content(validEmailJsonWithoutSender)
				.contentType("application/json"))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testEmailControllerValidDataNoServer() throws Exception {
		given(emailService.sendEmail(any(EmailRequest.class))).willReturn(MOCK_ERROR_RESPONSE_500);
		
		//Valid Subject, Message, and Recipient
		MvcResult result = mvc.perform(post("/notification/email")
				.content(validEmailJsonWithoutSenderNoServer)
				.contentType("application/json"))
				.andDo(print())
				.andExpect(status().is5xxServerError())
				.andReturn();
		assertTrue(result.getResponse().getErrorMessage().contains(MOCK_ERROR_RESPONSE_500));
	}
	
	@Test
	public void testEmailControllerValidDataWithOptional() throws Exception {
		given(emailService.sendEmail(any(EmailRequest.class))).willReturn(null);
		//Valid Subject, Message, Recipient, and Sender
		mvc.perform(post("/notification/email")
				.content(validEmailJsonWithOptional)
				.contentType("application/json"))
				.andExpect(status().isOk());
	}

	@Test
	public void testEmailControllerInvalidEmail() throws Exception {
		given(emailService.sendEmail(any(EmailRequest.class))).willReturn(null);
		//Invalid Subject
		MvcResult result = mvc.perform(post("/notification/email")
				.content(malformedEmailJson)
				.contentType("application/json"))
				.andDo(print())
				.andExpect(status().is4xxClientError())
				.andReturn();
		assertTrue(result.getResponse().getStatus() == 400);
		
		MvcResult result2 = mvc.perform(post("/notification/email")
				.content(invalidEmailJson)
				.contentType("application/json"))
				.andDo(print())
				.andExpect(status().is4xxClientError())
				.andReturn();
		assertTrue(result2.getResponse().getErrorMessage().contains("No recipient email addresses provided."));
	}
}