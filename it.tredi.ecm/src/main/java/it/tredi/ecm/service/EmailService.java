package it.tredi.ecm.service;

import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
	public void send(SimpleMailMessage mailMessage);
}
