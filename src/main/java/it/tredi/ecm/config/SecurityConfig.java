package it.tredi.ecm.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
		 .antMatchers("/", "/gentella/**", "/shared/**", "/main", "/providerRegistration", "/user/resetPassword").permitAll()
		 
//		 .antMatchers("/user/{id}/edit").hasAuthority("USER_EDIT")
//		 .antMatchers("/user/save").hasAuthority("USER_EDIT")
//		 .antMatchers("/user/list").hasAuthority("USER_READ_ALL")
		 
		 .antMatchers("/admin/**").hasAuthority("ADMIN")
         .anyRequest().fullyAuthenticated()
         .and()
			 .formLogin()
			 	.loginPage("/login").failureUrl("/login?error")
			 	.usernameParameter("username").passwordParameter("password")	
			 	.permitAll()
         .and()
	         .logout()
	         	.logoutUrl("/logout").logoutSuccessUrl("/")
	         	.deleteCookies("remember-me")
	         	.permitAll()
	     .and()
	     	.exceptionHandling().accessDeniedPage("/403")
	     .and()
	     	.rememberMe();
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(userDetailsService)
			.passwordEncoder(new BCryptPasswordEncoder());
	}
}
