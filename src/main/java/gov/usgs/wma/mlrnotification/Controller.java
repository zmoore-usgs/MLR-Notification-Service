package gov.usgs.wma.mlrnotification;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notification")
public class Controller {
	@Autowired
	public EmailNotificationHandler emailHandler;

	@PostMapping("/email")
	public String createEmailNotification(@RequestParam("subject") String subject, @RequestParam("message") String message, @RequestParam("recipient") String recipient, HttpServletResponse response) {
		String responseStr = "{}";
		String validationStatus = emailHandler.validateMessageParameters(subject, message, recipient);
		
		if(validationStatus != null){
			response.setStatus(HttpStatus.BAD_REQUEST.value());
			responseStr = "{\"error\":{\"message\":\"" + validationStatus + "\"}}";
		} else {
			String sendStatus = emailHandler.sendEmail(subject, message, recipient);
			
			if(sendStatus != null){
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
				responseStr = "{\"error\":{\"message\":\"" + sendStatus + "\"}}";
			} else {
				response.setStatus(HttpStatus.OK.value());
			}
		}
		
		response.setContentType("application/json;charset=UTF-8");
		return responseStr;
	}
}
