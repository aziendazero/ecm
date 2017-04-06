package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.Set;

import org.springframework.mail.SimpleMailMessage;

import it.tredi.ecm.dao.entity.AlertEmail;
import it.tredi.ecm.dao.entity.Provider;

public interface EmailService {
	public void send(SimpleMailMessage mailMessage);
	public void send(String from, String to, String subject, String body, boolean isHtml) throws Exception;

	public void inviaNotificaAReferee(String referee, String provider) throws Exception;
	public void inviaConvocazioneACommissioneECM(Set<String> commissione) throws Exception;
	public void inviaNotificaASegreteriaMancataValutazioneReferee(String segreteria, String provider) throws Exception;

	public void inviaAlertErroreDiSistema(String alert) throws Exception;

	public void inviaAlertScadenzaReInvioIntegrazioneAccreditamento(AlertEmail alert) throws Exception;
	public void inviaAlertScadenzaAccreditamento(AlertEmail alert) throws Exception;
	public void inviaConfermaReInvioIntegrazioniAccreditamento(boolean isStandard, boolean isPreavvisoRigetto, Provider provider) throws Exception;
	public void inviaAlertScadenzaPagamento(AlertEmail alert) throws Exception;
	public void inviaAlertScadenzaPFA(AlertEmail alert) throws Exception;
	public void inviaAlertScadenzaRelazioneAnnuale(AlertEmail alert) throws Exception;
	public void inviaAlertScadenzaPagamentoRendicontazioneEvento(AlertEmail alert) throws Exception;
	public void inviaAlertScadenzaValutazioneReferee(AlertEmail alert) throws Exception;
	public void inviaAlertScadenzaInvioAccreditamentoStandard(AlertEmail alert) throws Exception;

	public void inviaNotificaATeamLeader(String referee, String provider) throws Exception;
	public void inviaConvocazioneValutazioneSulCampo(Set<String> valutatori,LocalDate data, String provider) throws Exception;
	public void inviaNotificaNuovaComunicazioneForProvider(String fullName, String email) throws Exception;
}
