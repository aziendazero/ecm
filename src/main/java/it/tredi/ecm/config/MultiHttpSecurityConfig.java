package it.tredi.ecm.config;

import javax.inject.Inject;

import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas20ServiceTicketValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import it.tredi.ecm.service.AccountServiceImpl;
import it.tredi.ecm.service.CasUserDetailService;

@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MultiHttpSecurityConfig {

    @Configuration
    @Order(1)
    public static class SecurityCASConfig extends WebSecurityConfigurerAdapter {

    	private static Logger LOGGER = LoggerFactory.getLogger(SecurityCASConfig.class);

        private static final String CAS_URL_LOGIN = "cas.service.login";
        private static final String CAS_URL_PREFIX = "cas.url.prefix";
        private static final String CAS_SERVICE_URL = "app.service.security";
        private static final String CAS_URL_VALIDATION = "cas.service.validation";

        private static final String CAS_URL_LOGOUT = "cas.service.logout";
        private static final String APP_SERVICE_HOME = "app.service.home";

        @Inject private Environment env;

        @Bean
    	public ServiceProperties serviceProperties() {
    		ServiceProperties sp = new ServiceProperties();
    		sp.setService(env.getRequiredProperty(CAS_SERVICE_URL));
    		sp.setSendRenew(false);
    		return sp;
    	}

        @Bean
        public CasAuthenticationProvider casAuthenticationProvider() {
            CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
            casAuthenticationProvider.setAuthenticationUserDetailsService(casUserDetailsService());
            casAuthenticationProvider.setServiceProperties(serviceProperties());
            casAuthenticationProvider.setTicketValidator(cas20ServiceTicketValidator());
            casAuthenticationProvider.setKey("an_id_for_this_auth_provider_only");
            return casAuthenticationProvider;
        }

        @Bean
    	public AuthenticationUserDetailsService<CasAssertionAuthenticationToken> casUserDetailsService() {
    		return new CasUserDetailService();
    	}

        @Bean
    	public Cas20ServiceTicketValidator cas20ServiceTicketValidator() {
    		return new Cas20ServiceTicketValidator(env.getRequiredProperty(CAS_URL_VALIDATION));
    	}

        @Bean
    	public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
    		CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
    		casAuthenticationFilter.setAuthenticationManager(authenticationManager());
    		casAuthenticationFilter.setFilterProcessesUrl("/j_spring_cas_security_check");
    	//	casAuthenticationFilter.setSessionAuthenticationStrategy(sessionStrategy());
    		return casAuthenticationFilter;
    	}

        @Bean
    	public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
    		CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
    		casAuthenticationEntryPoint.setLoginUrl(env.getRequiredProperty(CAS_URL_LOGIN));
    		casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
    		return casAuthenticationEntryPoint;
    	}

        @Bean
    	public SingleSignOutFilter singleSignOutFilter() {
    		SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
    		singleSignOutFilter.setCasServerUrlPrefix(env.getRequiredProperty(CAS_URL_PREFIX));
    		return singleSignOutFilter;
    	}

    	@Bean
    	public LogoutFilter requestCasGlobalLogoutFilter() {
    		LOGGER.info("Cas Logout Filter");
    		LogoutFilter logoutFilter = new LogoutFilter(env.getRequiredProperty(CAS_URL_LOGOUT) + "?service="
    				+ env.getRequiredProperty(APP_SERVICE_HOME), new SecurityContextLogoutHandler());

    		logoutFilter.setFilterProcessesUrl("/cas/logout");
    		//logoutFilter.setLogoutRequestMatcher(new AntPathRequestMatcher("/cas/logout", "GET"));
    		return logoutFilter;
    	}

		@Override
	    protected void configure(HttpSecurity http) throws Exception {
			http
				.antMatcher("/cas/**")
				.authorizeRequests()
					.anyRequest().authenticated()
					.and()
				.addFilterAfter(new CsrfCookieGeneratorFilter(), CsrfFilter.class)
				.exceptionHandling()
					.authenticationEntryPoint(casAuthenticationEntryPoint())
					.and()
				.addFilter(casAuthenticationFilter());

			http
				.addFilterBefore(requestCasGlobalLogoutFilter(), LogoutFilter.class)
				.logout()
					//.logoutUrl("/cas/logout").logoutSuccessUrl("/")
					.logoutSuccessUrl("/")
					.invalidateHttpSession(true)
					.deleteCookies("remember-me")
					.deleteCookies("JSESSIONID");
	    }

		@Inject
		public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			auth.authenticationProvider(casAuthenticationProvider());
		}

        @Override
        public void configure(WebSecurity web) throws Exception {
             web.ignoring().antMatchers("/", "favicon.ico", "/images/**", "/gentella/**", "/bootstrap/**", "/shared/**", "/main", "/spinJS/**", "/backToTop/**", "/bootstrapSelect/**");
        }
    }

    @Configuration
    @Order(2)
    public static class SecurityConfig extends WebSecurityConfigurerAdapter {
    	private static Logger LOGGER = LoggerFactory.getLogger(SecurityConfig.class);

        @Autowired
        private UserDetailsService userDetailsService;

        private static final String CAS_URL_LOGOUT = "cas.service.logout";
        private static final String APP_SERVICE_HOME = "app.service.home";
        private static final String CAS_URL_PREFIX = "cas.url.prefix";

        @Inject private Environment env;

        @Bean
    	public SingleSignOutFilter singleSignOutFilter() {
    		SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
    		singleSignOutFilter.setCasServerUrlPrefix(env.getRequiredProperty(CAS_URL_PREFIX));
    		return singleSignOutFilter;
    	}

//    	@Bean
//    	public LogoutFilter requestCasGlobalLogoutFilter() {
//    		LogoutFilter logoutFilter = new LogoutFilter(env.getRequiredProperty(CAS_URL_LOGOUT) + "?service="
//    				+ env.getRequiredProperty(APP_SERVICE_HOME), new SecurityContextLogoutHandler());
//    		//logoutFilter.setFilterProcessesUrl("/cas/logout");
//    		// logoutFilter.setFilterProcessesUrl("/j_spring_cas_security_logout");
//    		logoutFilter.setLogoutRequestMatcher(new AntPathRequestMatcher("/logout", "POST"));
//    		return logoutFilter;
//    	}

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.authorizeRequests()
             .antMatchers("/j_spring_cas_security_check", "/", "favicon.ico", "/images/**", "/bootstrap/**", "/crlcu-multiselect/**", "/gentella/**", "/engineering/**", "/clockpicker/**", "/shared/**", "/main", "/providerRegistration", "/confirmRegistration", "/user/resetPassword", "/file/upload", "/spinJS/**", "/backToTop/**", "/bootstrapSelect/**", "/workflow/**","/cas/**","/bugfix/**")
             	.permitAll()
             .anyRequest().authenticated()
             .and()
                 .formLogin()
                     .loginPage("/login").failureUrl("/login?error")
                     .usernameParameter("username").passwordParameter("password")
                     .permitAll()
             .and()
                 .logout()
                     .logoutUrl("/logout").logoutSuccessUrl("/")
                     .invalidateHttpSession(true)
 					 .deleteCookies("remember-me")
 					 .deleteCookies("JSESSIONID")
                     .permitAll()
             .and()
                 .exceptionHandling().accessDeniedPage("/403")
             .and()
                 .rememberMe();

            http.csrf().ignoringAntMatchers("/engineering/firma/back")
             .and()
                 .headers().frameOptions().sameOrigin();

           //http
            	//.addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class)
            	//.addFilterBefore(requestCasGlobalLogoutFilter(), LogoutFilter.class);
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
        }

        @Override
        public void configure(WebSecurity web) throws Exception {
             web.ignoring().antMatchers("/", "/gentella/**", "favicon.ico", "/images/**", "/bootstrap/**", "/shared/**", "/main", "/spinJS/**", "/backToTop/**", "/bootstrapSelect/**","/bugfix/**");
        }
    }
}
