package it.tredi.ecm.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

public class AuthenticationFailureListener implements AuthenticationFailureHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(AuthenticationFailureListener.class);
    private static final String BAD_CREDENTIALS_MESSAGE = "bad_credentials_message";
    private static final String CREDENTIALS_EXPIRED_MESSAGE = "credentials_expired_message";
    private static final String DISABLED_MESSAGE = "disabled_message";
    private static final String LOCKED_MESSAGE = "locked_message";

    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, org.springframework.security.core.AuthenticationException ex) throws IOException, ServletException {
        // TODO Auto-generated method stub
        String userName = req.getParameter("j_username");
        LOGGER.info("[AuthenticationFailure]:" + " [Username]:" + userName + " [Error message]:" + ex.getMessage());

        if (ex instanceof CredentialsExpiredException) {
            res.sendRedirect("/user/changePassword");
        }
    }
    
    
}