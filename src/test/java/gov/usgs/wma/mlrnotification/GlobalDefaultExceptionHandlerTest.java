package gov.usgs.wma.mlrnotification;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.mock.http.MockHttpInputMessage;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;

public class GlobalDefaultExceptionHandlerTest {
	@Mock
	WebRequest request;

	private GlobalDefaultExceptionHandler controller = new GlobalDefaultExceptionHandler();

	@BeforeEach
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void handleUncaughtExceptionTest() throws IOException {
		HttpServletResponse response = new MockHttpServletResponse();
		String expected = "Something bad happened. Contact us with Reference Number: ";
		Map<String, String> actual = controller.handleUncaughtException(new RuntimeException(), request, response);
		assertEquals(expected, actual.get(GlobalDefaultExceptionHandler.ERROR_MESSAGE_KEY).substring(0, expected.length()));
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getStatus());
	}

	@Test
	public void handleAccessDeniedException() throws IOException {
		HttpServletResponse response = new MockHttpServletResponse();
		String expected = "You are not authorized to perform this action.";
		Map<String, String> actual = controller.handleUncaughtException(new AccessDeniedException("haha"), request, response);
		assertEquals(expected, actual.get(GlobalDefaultExceptionHandler.ERROR_MESSAGE_KEY));
		assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatus());
	}

	@Test
	public void handleMissingServletRequestParameterException() throws IOException {
		HttpServletResponse response = new MockHttpServletResponse();
		String expected = "Required String parameter 'parm' is not present";
		Map<String, String> actual = controller.handleUncaughtException(new MissingServletRequestParameterException("parm", "String"), request, response);
		assertEquals(expected, actual.get(GlobalDefaultExceptionHandler.ERROR_MESSAGE_KEY));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
	}

	@Test
	public void handleHttpMediaTypeNotSupportedException() throws IOException {
		HttpServletResponse response = new MockHttpServletResponse();
		String expected = "no way";
		Map<String, String> actual = controller.handleUncaughtException(new HttpMediaTypeNotSupportedException(expected), request, response);
		assertEquals(expected, actual.get(GlobalDefaultExceptionHandler.ERROR_MESSAGE_KEY));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
	}
	
	@Test
	public void handleHttpMessageNotReadableException() throws IOException {
		HttpServletResponse response = new MockHttpServletResponse();
		String expected = "Some123$Mes\tsage!!.";
		Map<String, String> actual = controller.handleUncaughtException(new HttpMessageNotReadableException(expected, new MockHttpInputMessage("test".getBytes())), request, response);
		assertEquals(expected, actual.get(GlobalDefaultExceptionHandler.ERROR_MESSAGE_KEY));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
	}

	@Test
	public void handleMultilineHttpMessageNotReadableException() throws IOException {
		HttpServletResponse response = new MockHttpServletResponse();
		String expected = "ok to see";
		Map<String, String> actual = controller.handleUncaughtException(new HttpMessageNotReadableException("ok to see\nhide this\nand this", new MockHttpInputMessage("test".getBytes())), request, response);
		assertEquals(expected, actual.get(GlobalDefaultExceptionHandler.ERROR_MESSAGE_KEY));
		assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
	}	
}
