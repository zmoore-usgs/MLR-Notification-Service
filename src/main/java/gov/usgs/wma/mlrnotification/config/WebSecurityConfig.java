package gov.usgs.wma.mlrnotification.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.httpBasic().disable()
			.csrf().disable()
			.cors().and()
			.authorizeRequests()
				.antMatchers("/swagger-resources/**", "/webjars/**", "/v2/**", "/public").permitAll()
				.antMatchers("/version", "/info**", "/health/**", "/favicon.ico", "/swagger-ui.html").permitAll()
				.antMatchers("/actuator/health").permitAll()
				.anyRequest().authenticated()
			.and().oauth2ResourceServer().authenticationEntryPoint(standardAuthEntryPoint()).jwt(
				jwt -> jwt.jwtAuthenticationConverter(keycloakJwtConverter())
			)
		;
	}

	@Bean
	public TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler();
	}

	@Bean
	public WebMvcConfigurer corsConfigurer(){
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry registry){
				registry.addMapping("/**").allowedOrigins("*").allowCredentials(true).allowedMethods("GET","PUT","POST","DELETE");
			}
		};
	}

	@Bean
	public AuthenticationEntryPoint standardAuthEntryPoint() {
		return new AuthenticationEntryPoint(){
		
			@Override
			public void commence(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException authException) throws IOException, ServletException {
						final Map<String, Object> mapBodyException = new HashMap<>() ;

						mapBodyException.put("error_message", authException.getMessage()) ;
						response.setContentType("application/json") ;
						response.setStatus(HttpServletResponse.SC_UNAUTHORIZED) ;
						new ObjectMapper().writeValue(response.getOutputStream(), mapBodyException) ;
			}
		};
	}

	private Converter<Jwt, AbstractAuthenticationToken> keycloakJwtConverter() {
		JwtAuthenticationConverter jwtAuthenticationConverter =
				new JwtAuthenticationConverter();
	
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter
				(new KeycloakJWTAuthorityMapper());
			
		return jwtAuthenticationConverter;
	}
}