package gov.usgs.wma.mlrnotification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Value("${mlrNotificationServicePwd}")
	private String pwd;

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				.antMatchers("/notification/**").permitAll()
				.antMatchers("/swagger-ui.html", "/swagger-resources/**", "/webjars/**", "/v2/**").permitAll()
				.antMatchers("/health/**").permitAll()
				.anyRequest().fullyAuthenticated()
			.and()
				.formLogin().defaultSuccessUrl("/swagger-ui.html", true)
			.and()
				.logout().logoutSuccessUrl("/swagger-ui.html")
			.and()
				.formLogin().permitAll()
			.and()
				.logout().permitAll()
			.and()
				.csrf().disable()
		;
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
		auth
		.inMemoryAuthentication()
		.withUser("user").password(pwd).roles("ACTUATOR");
	}

}
