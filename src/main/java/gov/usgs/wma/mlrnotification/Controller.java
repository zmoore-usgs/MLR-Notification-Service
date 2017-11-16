package gov.usgs.wma.mlrnotification;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.usgs.wma.mlrnotification.model.Email;
import io.swagger.annotations.Api;
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

@Api(tags="Notification Service")
@RestController
@RequestMapping("/notification")
public class Controller {
	private Logger log = Logger.getLogger(Controller.class);
	
	@Autowired
	public EmailNotificationHandler emailHandler;
	
	@Value("${mlrEmailTemplateFrom}")
	private String templateFrom;

	@PostMapping(value = "/email", produces = "application/json")
	public void createEmailNotification(@RequestBody Email emailJson, HttpServletResponse response)  throws IOException{
		response.setContentType("application/json;charset=UTF-8");
		
		//Check for user provded "from" address
		if(emailJson.getFrom() == null || emailJson.getFrom().length() == 0){
			emailJson.setFrom(templateFrom);
		}
		String validationStatus = emailJson.validate();
		if (validationStatus == null) {
			String sendStatus = emailHandler.sendEmail(emailJson);

			if(sendStatus != null){
				response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), sendStatus);
			} else {
				response.setStatus(HttpStatus.OK.value());
			} 
		} else {			
				response.sendError(HttpStatus.BAD_REQUEST.value(), validationStatus);			
		}
	}
}
