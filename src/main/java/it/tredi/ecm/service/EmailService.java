package it.tredi.ecm.service;

import java.util.Set;

import org.springframework.mail.SimpleMailMessage;

public interface EmailService {
	public void send(SimpleMailMessage mailMessage);
	public void send(String from, String to, String subject, String body, boolean isHtml) throws Exception;

	public void inviaNotificaAReferee(String referee, String provider) throws Exception;
	public void inviaConvocazioneACommissioneECM(Set<String> commissione) throws Exception;
	public void inviaNotificaASegreteriaMancataValutazioneReferee(String segreteria, String provider) throws Exception;

	public void inviaAlertErroreDiSistema(String alert) throws Exception;
}
