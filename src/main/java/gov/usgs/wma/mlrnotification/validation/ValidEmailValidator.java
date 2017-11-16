package gov.usgs.wma.mlrnotification.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
 
import org.apache.commons.validator.routines.EmailValidator;

public class ValidEmailValidator implements ConstraintValidator<ValidEmail, String> {
 
	private int min;
 
	@Override
	public void initialize(ValidEmail validEmail) {
		min = validEmail.min();
	}
 
	@Override
	public boolean isValid(String email, ConstraintValidatorContext context) {
		
		if (email.length() < min) {
			return false;
		}
 
		if (!EmailValidator.getInstance().isValid(email)) {
			return false;
		}
 
		return true;
	}
 
}
