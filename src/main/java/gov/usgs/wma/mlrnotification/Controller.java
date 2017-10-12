package gov.usgs.wma.mlrnotification;

import gov.usgs.wma.mlrnotification.model.Email;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/notification")
public class Controller {
	@Autowired
	public EmailNotificationHandler emailHandler;
	
	@Value("${mlrEmailTemplateFrom}")
	private String templateFrom;

	@PostMapping(value = "/email", produces = "application/json")
	public void createEmailNotification(@RequestBody Email email, HttpServletResponse response)  throws IOException{
		//Check for user provded "from" address
		if(email.getFrom() == null || email.getFrom().length() == 0){
			email.setFrom(templateFrom);
		}
		
		String validationStatus = email.validate();
		response.setContentType("application/json;charset=UTF-8");
		
		if(validationStatus != null){
			response.sendError(HttpStatus.BAD_REQUEST.value(), validationStatus);
		} else {
			String sendStatus = emailHandler.sendEmail(email);
			
			if(sendStatus != null){
				response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), sendStatus);
			} else {
				response.setStatus(HttpStatus.OK.value());
			}
		}
	}
}
