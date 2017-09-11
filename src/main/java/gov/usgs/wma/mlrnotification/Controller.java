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

	@PostMapping("/email")
	public void createEmailNotification(@RequestParam("message") String message, @RequestParam("recipient") String recipient, HttpServletResponse response) {		
		String status = emailHandler.sendEmail(message, recipient);
		
		if(status != null){
			status = "An error occurred while sending the email notification: " + status;
			
			try {
				response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), status);
			} catch (IOException ex){
				response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
			}
		}	
	}
}
