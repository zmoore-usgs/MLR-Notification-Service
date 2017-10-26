package gov.usgs.wma.mlrnotification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.usgs.wma.mlrnotification.model.Email;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
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
	private Logger log = Logger.getLogger(Controller.class);
	
	@Autowired
	public EmailNotificationHandler emailHandler;
	
	@Value("${mlrEmailTemplateFrom}")
	private String templateFrom;

	@PostMapping(value = "/email", produces = "application/json")
	public void createEmailNotification(@RequestBody String emailJson, HttpServletResponse response)  throws IOException{
		response.setContentType("application/json;charset=UTF-8");
		
		//Deserialize Email
		ObjectMapper mapper = new ObjectMapper();
		TypeReference<Email> emailType = new TypeReference<Email>(){};
		Email email;
		
		try {
			email = mapper.readValue(emailJson, emailType);
		} catch(Exception e) {
			log.error(e.getMessage());
			response.sendError(HttpStatus.BAD_REQUEST.value(),"Unable to parse request body as email JSON. Body: " + emailJson);
			return;
		}
		
		//Check for user provded "from" address
		if(email.getFrom() == null || email.getFrom().length() == 0){
			email.setFrom(templateFrom);
		}
		
		String validationStatus = email.validate();
		
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
