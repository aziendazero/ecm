package it.tredi.ecm.config;
import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import it.tredi.ecm.service.bean.CurrentUser;

//@Component
public class UserChangePasswordCheckFilter extends GenericFilterBean  {
	protected final Logger LOGGER = LoggerFactory.getLogger(UserChangePasswordCheckFilter.class);

	public void destroy() {
	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		/* Should redirect occur or it shouldn't. */
		boolean redirect = false;

		//logger.info("UserChangePasswordCheckFilter says Hi!");

		if (!(request instanceof HttpServletRequest)) {
			throw new ServletException("Can only process HttpServletRequest");
		}

		if (!(response instanceof HttpServletResponse)) {
			throw new ServletException("Can only process HttpServletResponse");
		}
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication != null){
			/* Korisnik class implements UserDetails. */
			if( authentication.getPrincipal() instanceof CurrentUser) {
				CurrentUser currentUser = (CurrentUser) authentication.getPrincipal();

				if(!currentUser.getAccount().isCredentialsNonExpired()) 
					redirect = true;
			} 

			/* PromenaLozinke.htm is handled by SimpleFormController, after submiting of
		      form don't want to redirect. */    
			if(((HttpServletRequest)request).getServletPath().startsWith("/user/changePassword")) 
				redirect = false;
		}else{
			redirect = false;
		}


		/* If redirect is true redirect user to page for changing password,
	            if it's not just doFilter. */        
		if(redirect)
		{
			LOGGER.info("Redirect in corso");
			ServletContext context = ((HttpServletRequest)request).getSession().getServletContext();
			RequestDispatcher rd = context.getRequestDispatcher("/user/changePassword");
			if(rd != null) {
				LOGGER.info("ok!");
				rd.forward(request, response);
			}
			
			//((HttpServletResponse)response).sendRedirect("/user/changePassword");
			
		}
		else
			chain.doFilter(request, response);
	}
}