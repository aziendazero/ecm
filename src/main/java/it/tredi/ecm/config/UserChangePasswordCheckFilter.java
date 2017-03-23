package it.tredi.ecm.config;
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import it.tredi.ecm.service.bean.CurrentUser;

@Component
public class UserChangePasswordCheckFilter extends GenericFilterBean  {

	protected final Logger LOGGER = LoggerFactory.getLogger(UserChangePasswordCheckFilter.class);

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		boolean redirect = false;

		if (!(request instanceof HttpServletRequest)) {
			throw new ServletException("Can only process HttpServletRequest");
		}

		if (!(response instanceof HttpServletResponse)) {
			throw new ServletException("Can only process HttpServletResponse");
		}

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if(authentication != null && (authentication instanceof UsernamePasswordAuthenticationToken)){
			if( authentication.getPrincipal() instanceof CurrentUser) {
				CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();
				if(currentUser.getAccount().isPasswordExpired())
					redirect = true;
			}

			if(((HttpServletRequest)request).getServletPath().startsWith("/user/changePassword"))
				redirect = false;
		}else{
			redirect = false;
		}

		if(redirect)
		{
			LOGGER.info("Redirect in corso");
			LOGGER.info("contextPath: " + ((HttpServletRequest)request).getContextPath());
			((HttpServletResponse)response).sendRedirect(((HttpServletRequest)request).getContextPath() +  "/user/changePassword");
		}
		else{
			chain.doFilter(request, response);
		}
	}
}