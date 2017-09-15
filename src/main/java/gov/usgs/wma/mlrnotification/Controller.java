package gov.usgs.wma.mlrnotification;

import java.io.IOException;
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

	@PostMapping(value = "/email", produces = "application/json")
	public void createEmailNotification(@RequestParam("subject") String subject, @RequestParam("message") String message, @RequestParam("recipient") String recipient, HttpServletResponse response)  throws IOException{
		String validationStatus = emailHandler.validateEmailParameters(subject, message, recipient);
		response.setContentType("application/json;charset=UTF-8");
		
		if(validationStatus != null){
			response.sendError(HttpStatus.BAD_REQUEST.value(), validationStatus);
		} else {
			String sendStatus = emailHandler.sendEmail(subject, message, recipient);
			
			if(sendStatus != null){
				response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), sendStatus);
			} else {
				response.setStatus(HttpStatus.OK.value());
			}
		}
	}
}
