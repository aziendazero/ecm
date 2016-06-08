package it.tredi.ecm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {
	private static Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Autowired
	private JavaMailSender javaMailSender;

	@Override
	public void send(SimpleMailMessage mailMessage) {
		LOGGER.info("Sending email");
		javaMailSender.send(mailMessage);
	}

}
