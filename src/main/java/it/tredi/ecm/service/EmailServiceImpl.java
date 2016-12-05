package it.tredi.ecm.service;

import java.util.Set;

import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import it.tredi.ecm.service.bean.EcmProperties;

@Service
public class EmailServiceImpl implements EmailService {
	private static Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);

	@Autowired private JavaMailSender javaMailSender;
	@Autowired private EcmProperties ecmProperties;
	@Autowired private SpringTemplateEngine templateEngine;

	@Override
	public void send(SimpleMailMessage mailMessage) {
		LOGGER.info("Sending email");
		javaMailSender.send(mailMessage);
	}

	@Override
	public void send(String from, String to, String subject, String body, boolean isHtml) throws Exception{
		LOGGER.info("Invio email da template");
		MimeMessage message = javaMailSender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message, true);
		helper.setFrom(from);
		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText(body, isHtml);
		javaMailSender.send(message);
	}

	@Override
	public void inviaNotificaAReferee(String referee, String provider) throws Exception {
		LOGGER.info("Invio notifica a Referee");
		Context context = new Context();
		context.setVariable("applicationBaseUrl", ecmProperties.getApplicationBaseUrl());
		context.setVariable("provider", provider);
		String message = templateEngine.process("assegnaDomandaReferee", context);

		send(ecmProperties.getEmailSegreteriaEcm(), referee, "Assegnamento Domanda da Valutare", message, true);
	}

	@Override
	public void inviaConvocazioneACommissioneECM(Set<String> commissione) throws Exception {
		LOGGER.info("Invio convocazione a Commissione Ecm");
		Context context = new Context();
		context.setVariable("applicationBaseUrl", ecmProperties.getApplicationBaseUrl());
		String message = templateEngine.process("convocazioneCommissione", context);

		for(String email : commissione){
			send(ecmProperties.getEmailSegreteriaEcm(),email, "Convocazione Seduta", message, true);
		}
	}

	@Override
	public void inviaNotificaASegreteriaMancataValutazioneReferee(String segreteria, String provider) throws Exception{
		LOGGER.info("Invio Notifica a " + segreteria + " per mancata valutazione dei referee");
		Context context = new Context();
		context.setVariable("applicationBaseUrl", ecmProperties.getApplicationBaseUrl());
		context.setVariable("provider",provider );
		String message = templateEngine.process("notificaSegreteriaMancataValutazioneReferee", context);
		send(ecmProperties.getEmailSegreteriaEcm(), segreteria, "Mancata Valutazione Referee", message, true);
	}

	@Override
	public void inviaAlertErroreDiSistema(String alert) throws Exception {
		LOGGER.info("Invio Alert errore di Sistema");
		Context context = new Context();
		context.setVariable("alert", alert);
		String message = templateEngine.process("alertErroreDiSistema", context);
		send(ecmProperties.getEmailSegreteriaEcm(), ecmProperties.getEmailSegreteriaEcm(), "Errore di Sistema Applicativo ECM", message, true);

	}
}
