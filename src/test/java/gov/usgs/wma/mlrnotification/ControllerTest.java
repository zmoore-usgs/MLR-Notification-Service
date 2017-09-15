package gov.usgs.wma.mlrnotification;

import gov.usgs.wma.mlrnotification.email.EmailTest;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
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
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@RunWith(SpringRunner.class)
@WebMvcTest(Controller.class)
@AutoConfigureMockMvc(secure=false)
public class ControllerTest {	
	@Autowired
	private MockMvc mvc;
	
	@MockBean
	private EmailNotificationHandler emailHandler;
	
	private final String MOCK_ERROR_RESPONSE_400 = "error_400";
	private final String MOCK_ERROR_RESPONSE_500 = "error_500";
	private final String MISSING_PARAMETER_RESPONSE = "Required String parameter";
	
	@Before
	public void setup() {
		//Setup mock emailHandler.validateEmailParameters
		given(emailHandler.validateEmailParameters(EmailTest.VALID_CONTENT, EmailTest.VALID_CONTENT, EmailTest.VALID_EMAIL)).willReturn(null);
		given(emailHandler.validateEmailParameters(EmailTest.VALID_CONTENT, EmailTest.VALID_CONTENT, EmailTest.INVALID_EMAIL)).willReturn(MOCK_ERROR_RESPONSE_400);
		given(emailHandler.validateEmailParameters(EmailTest.VALID_CONTENT, EmailTest.VALID_CONTENT, EmailTest.EMPTY_CONTENT)).willReturn(MOCK_ERROR_RESPONSE_400);
		given(emailHandler.validateEmailParameters(EmailTest.VALID_CONTENT, EmailTest.VALID_CONTENT, null)).willReturn(MOCK_ERROR_RESPONSE_400);
		given(emailHandler.validateEmailParameters(EmailTest.VALID_CONTENT, EmailTest.EMPTY_CONTENT, EmailTest.VALID_EMAIL)).willReturn(MOCK_ERROR_RESPONSE_400);
		given(emailHandler.validateEmailParameters(EmailTest.VALID_CONTENT, null, EmailTest.VALID_EMAIL)).willReturn(MOCK_ERROR_RESPONSE_400);
		given(emailHandler.validateEmailParameters(EmailTest.EMPTY_CONTENT, EmailTest.VALID_CONTENT, EmailTest.VALID_EMAIL)).willReturn(MOCK_ERROR_RESPONSE_400);
		given(emailHandler.validateEmailParameters(null, EmailTest.VALID_CONTENT, EmailTest.VALID_EMAIL)).willReturn(MOCK_ERROR_RESPONSE_400);
		
		//Setup mock emailHandler.sendEmail
		given(emailHandler.sendEmail(EmailTest.VALID_CONTENT, EmailTest.VALID_CONTENT, EmailTest.INVALID_EMAIL)).willReturn(MOCK_ERROR_RESPONSE_400);
		given(emailHandler.sendEmail(EmailTest.VALID_CONTENT, EmailTest.VALID_CONTENT, EmailTest.EMPTY_CONTENT)).willReturn(MOCK_ERROR_RESPONSE_400);
		given(emailHandler.sendEmail(EmailTest.VALID_CONTENT, EmailTest.EMPTY_CONTENT, EmailTest.VALID_EMAIL)).willReturn(null);
		given(emailHandler.sendEmail(EmailTest.EMPTY_CONTENT, EmailTest.VALID_CONTENT, EmailTest.VALID_EMAIL)).willReturn(null);
	}
	
	@Test
	public void testEmailControllerValidData() throws Exception {
		given(emailHandler.sendEmail(EmailTest.VALID_CONTENT, EmailTest.VALID_CONTENT, EmailTest.VALID_EMAIL)).willReturn(null);
		//Valid Subject, Message, and Recipient
		mvc.perform(post("/notification/email")
				.param("subject", EmailTest.VALID_CONTENT)
				.param("message", EmailTest.VALID_CONTENT)
				.param("recipient", EmailTest.VALID_EMAIL))
				.andExpect(status().isOk());
	}
	
	@Test
	public void testEmailControllerValidDataNoServer() throws Exception {
		given(emailHandler.sendEmail(EmailTest.VALID_CONTENT, EmailTest.VALID_CONTENT, EmailTest.VALID_EMAIL)).willReturn(MOCK_ERROR_RESPONSE_500);
		//Valid Subject, Message, and Recipient
		MvcResult result = mvc.perform(post("/notification/email")
				.param("subject", EmailTest.VALID_CONTENT)
				.param("message", EmailTest.VALID_CONTENT)
				.param("recipient", EmailTest.VALID_EMAIL))
				.andDo(print())
				.andExpect(status().is5xxServerError())
				.andReturn();
		assertTrue(result.getResponse().getErrorMessage().contains(MOCK_ERROR_RESPONSE_500));
	}

	@Test
	public void testEmailControllerInvalidSubject() throws Exception {
		//Invalid Subject
		MvcResult result = mvc.perform(post("/notification/email")
				.param("subject", EmailTest.EMPTY_CONTENT)
				.param("message", EmailTest.VALID_CONTENT)
				.param("recipient", "test@test.com"))
				.andDo(print())
				.andExpect(status().is4xxClientError())
				.andReturn();
		assertTrue(result.getResponse().getErrorMessage().contains(MOCK_ERROR_RESPONSE_400));
		result = mvc.perform(post("/notification/email")
				.param("message", EmailTest.VALID_CONTENT)
				.param("recipient", "test@test.com"))
				.andDo(print())
				.andExpect(status().is4xxClientError())
				.andReturn();
		assertTrue(result.getResponse().getErrorMessage().contains(MISSING_PARAMETER_RESPONSE));
	}
	
	@Test
	public void testEmailControllerInvalidMessage() throws Exception {
		//Invalid Message
		MvcResult result = mvc.perform(post("/notification/email")
				.param("subject", EmailTest.VALID_CONTENT)
				.param("message", EmailTest.EMPTY_CONTENT)
				.param("recipient", "test@test.com"))
				.andDo(print())
				.andExpect(status().is4xxClientError())
				.andReturn();
		assertTrue(result.getResponse().getErrorMessage().contains(MOCK_ERROR_RESPONSE_400));
		 result = mvc.perform(post("/notification/email")
				.param("subject", EmailTest.VALID_CONTENT)
				.param("recipient", "test@test.com"))
				.andDo(print())
				.andExpect(status().is4xxClientError())
				.andReturn();
		assertTrue(result.getResponse().getErrorMessage().contains(MISSING_PARAMETER_RESPONSE));
	}
	
	@Test 
	public  void testEmailControllerInvalidRecipient() throws Exception {
		//Invalid Recipient
		MvcResult result = mvc.perform(post("/notification/email")
				.param("subject", EmailTest.VALID_CONTENT)
				.param("message", EmailTest.VALID_CONTENT)
				.param("recipient", "testtestcom"))
				.andDo(print())
				.andExpect(status().is4xxClientError())
				.andReturn();
		assertTrue(result.getResponse().getErrorMessage().contains(MOCK_ERROR_RESPONSE_400));
		 result = mvc.perform(post("/notification/email")
				.param("subject", EmailTest.VALID_CONTENT)
				.param("message", EmailTest.VALID_CONTENT)
				.param("recipient", EmailTest.EMPTY_CONTENT))
				.andDo(print())
				.andExpect(status().is4xxClientError())
				.andReturn();
		assertTrue(result.getResponse().getErrorMessage().contains(MOCK_ERROR_RESPONSE_400));
		 result = mvc.perform(post("/notification/email")
				.param("subject", EmailTest.VALID_CONTENT)
				.param("message", EmailTest.VALID_CONTENT))
				.andDo(print())
				.andExpect(status().is4xxClientError())
				.andReturn();
		assertTrue(result.getResponse().getErrorMessage().contains(MISSING_PARAMETER_RESPONSE));
	}
}