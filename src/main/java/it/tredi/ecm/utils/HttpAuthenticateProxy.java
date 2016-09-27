package it.tredi.ecm.utils;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

public class HttpAuthenticateProxy extends Authenticator {

	protected String proxyUserName;
	protected String proxyPassword;

	public HttpAuthenticateProxy(String proxyUserName, String proxyPassword) {
		super();
		this.proxyUserName = proxyUserName;
		this.proxyPassword = proxyPassword;
	}

	protected PasswordAuthentication getPasswordAuthentication() {
		// username, password
		// sets http authentication
		return new PasswordAuthentication(proxyUserName, proxyPassword.toCharArray());
	}

}	