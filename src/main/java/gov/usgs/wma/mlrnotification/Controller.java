package gov.usgs.wma.mlrnotification;

import gov.usgs.wma.mlrnotification.model.EmailRequest;
import gov.usgs.wma.mlrnotification.service.EmailService;

import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

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

	public EmailService emailService;
	
	@Value("${mlr.email.template.from}")
	private String templateFrom;
	
	@Autowired
	public Controller(EmailService emailService) {
		this.emailService = emailService;
	}
	
	@ApiResponses(value={
		@ApiResponse(code=200, message="OK"),
		@ApiResponse(code=400, message="Bad Request"),
		@ApiResponse(code=401, message="Unauthorized")
	})
	@PostMapping(value = "/email", produces = "application/json")
	public void createEmailNotification(@RequestBody EmailRequest emailJson, HttpServletResponse response)  throws IOException{
		response.setContentType("application/json;charset=UTF-8");
		
		//Check for provided "from" address
		if(emailJson.getFrom() == null || emailJson.getFrom().isEmpty()){
			emailJson.setFrom(templateFrom);
		}

		String validationStatus = emailJson.validate();

		if (validationStatus == null) {
			String sendStatus = emailService.sendEmail(emailJson);

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
