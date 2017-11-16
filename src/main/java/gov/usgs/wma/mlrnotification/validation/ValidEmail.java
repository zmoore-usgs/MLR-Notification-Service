package gov.usgs.wma.mlrnotification.validation;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;
 
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
 
import javax.validation.Constraint;
import javax.validation.Payload;
 
@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = gov.usgs.wma.mlrnotification.validation.ValidEmailValidator.class)
@Documented
public @interface ValidEmail {
 
	String message() default "Not a valid Email";
 
	Class<?>[] groups() default {};
 
	Class<? extends Payload>[] payload() default {};
 
	int min() default 5;
}

