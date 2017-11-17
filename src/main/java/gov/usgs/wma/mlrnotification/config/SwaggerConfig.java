package gov.usgs.wma.mlrnotification.config;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.google.common.base.Predicates;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Profile("swagger")
public class SwaggerConfig {
	
	@Bean
	public Docket gatewayApi() {
		return new Docket(DocumentationType.SWAGGER_2)
				.tags(new Tag("Notification Service", "Email "))
				.useDefaultResponseMessages(false)
				.select() 
					.paths(Predicates.or(PathSelectors.ant("/notification/**"), PathSelectors.ant("/info/**"), PathSelectors.ant("/health/**")))
				.build()
				.securitySchemes(Collections.singletonList(apiKey()))
		;
	}
	
	private ApiKey apiKey() {
		return new ApiKey("mykey", "Authorization", "header");
	}
}
