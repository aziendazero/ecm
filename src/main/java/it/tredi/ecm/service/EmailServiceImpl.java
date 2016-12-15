package it.tredi.ecm.service;

import java.util.HashSet;
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

import it.tredi.ecm.dao.entity.AlertEmail;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.AlertTipoEnum;
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

	@Override
	public void inviaAlertScadenzaReInvioIntegrazioneAccreditamento(AlertEmail alert) throws Exception {
		LOGGER.info("inviaAlertScadenzaReInvioIntegrazioneAccreditamento " + alert.getTipo());
		Context context = new Context();
		String subject = "Avviso Scadenza Reinvio Integrazione Domanda Accreditamento";

		if(alert.getTipo() == AlertTipoEnum.SCADENZA_REINVIO_INTEGRAZIONI_ACCREDITAMENTO_PROVVISORIO){
			context.setVariable("isStandard", false);
			context.setVariable("isPreavvisoDiRigetto", false);
			subject += " Provvisorio";
		}

		if(alert.getTipo() == AlertTipoEnum.SCADENZA_REINVIO_INTEGRAZIONI_PREAVVISO_DI_RIGETTO_ACCREDITAMENTO_PROVVISORIO){
			context.setVariable("isStandard", false);
			context.setVariable("isPreavvisoDiRigetto", true);
			subject += " Provvisorio";
		}

		if(alert.getTipo() == AlertTipoEnum.SCADENZA_REINVIO_INTEGRAZIONI_ACCREDITAMENTO_STANDARD){
			context.setVariable("isStandard", true);
			context.setVariable("isPreavvisoDiRigetto", false);
			subject += " Standard";
		}

		if(alert.getTipo() == AlertTipoEnum.SCADENZA_REINVIO_INTEGRAZIONI_PREAVVISO_DI_RIGETTO_ACCREDITAMENTO_STANDARD){
			context.setVariable("isStandard", true);
			context.setVariable("isPreavvisoDiRigetto", true);
			subject += " Standard";
		}

		String message = templateEngine.process("alertScadenzaReinvioIntegrazioniAccreditamento", context);

		for(String dst : alert.getDestinatari()){
			send(ecmProperties.getEmailSegreteriaEcm(), dst, subject, message, true);
		}
	}

	@Override
	public void inviaAlertScadenzaAccreditamento(AlertEmail alert) throws Exception {
		LOGGER.info("inviaAlertScadenzaAccreditamento " + alert.getTipo());
		Context context = new Context();
		String subject = "Avviso Scadenza Accreditamento";

		if(alert.getTipo() == AlertTipoEnum.SCADENZA_ACCREDITAMENTO_PROVVISORIO){
			context.setVariable("isStandard", false);
			subject += " Provvisorio";
		}

		if(alert.getTipo() == AlertTipoEnum.SCADENZA_ACCREDITAMENTO_STANDARD){
			context.setVariable("isStandard", true);
			subject += " Standard";
		}

		String message = templateEngine.process("alertScadenzaAccreditamento", context);

		for(String dst : alert.getDestinatari()){
			send(ecmProperties.getEmailSegreteriaEcm(), dst, subject, message, true);
		}
	}

	@Override
	public void inviaConfermaReInvioIntegrazioniAccreditamento(boolean isStandard, boolean isPreavvisoRigetto, Provider provider) throws Exception {
		LOGGER.info("inviaConfermaReInvioIntegrazioneAccreditamento (standard/preavvisoRigetto): " + isStandard + "/" + isPreavvisoRigetto);
		Context context = new Context();
		String subject = "Conferma Reinvio Integrazioni Domanda Accreditamento";
		context.setVariable("isStandard", isStandard);
		context.setVariable("isPreavvisoDiRigetto", isPreavvisoRigetto);

		if(isStandard)
			subject += " Standard";
		else
			subject += " Provvisorio";

		String message = templateEngine.process("confermaInvioIntegrazioni", context);

		Set<String> destinatari = new HashSet<String>();

		if(provider.getLegaleRappresentante() != null)
			destinatari.add(provider.getLegaleRappresentante().getAnagrafica().getEmail());
		if(provider.getDelegatoLegaleRappresentante() != null)
			destinatari.add(provider.getDelegatoLegaleRappresentante().getAnagrafica().getEmail());

		for(String dst : destinatari){
			send(ecmProperties.getEmailSegreteriaEcm(), dst, subject, message, true);
		}
	}

	@Override
	public void inviaAlertScadenzaPagamento(AlertEmail alert) throws Exception {
		LOGGER.info("inviaAlertScadenzaPagamento");
		Context context = new Context();
		String subject = "Scadenza termini per il pagamento del contributo annuo";
		String message = templateEngine.process("alertScadenzaContributoAnnuo", context);

		for(String dst : alert.getDestinatari()){
			send(ecmProperties.getEmailSegreteriaEcm(), dst, subject, message, true);
		}
	}

	@Override
	public void inviaAlertScadenzaPFA(AlertEmail alert) throws Exception {
		LOGGER.info("inviaAlertScadenzaPFA");
		Context context = new Context();
		String subject = "Avviso Scadenza Compilazione Piano Formativo Annuale";

		String message = templateEngine.process("alertScadenzaPFA", context);

		for(String dst : alert.getDestinatari()){
			send(ecmProperties.getEmailSegreteriaEcm(), dst, subject, message, true);
		}
	}

	@Override
	public void inviaAlertScadenzaRelazioneAnnuale(AlertEmail alert) throws Exception {
		LOGGER.info("inviaAlertScadenzaRelazioneAnnuale");
		Context context = new Context();
		String subject = "Avviso Scadenza Inserimento Relazione Annuale";

		String message = templateEngine.process("alertScadenzaRelazioneAnnuale", context);

		for(String dst : alert.getDestinatari()){
			send(ecmProperties.getEmailSegreteriaEcm(), dst, subject, message, true);
		}
	}

	@Override
	public void inviaAlertScadenzaPagamentoRendicontazioneEvento(AlertEmail alert) throws Exception {
		LOGGER.info("inviaAlertScadenzaPagamentoRendicontazioneEvento");
		Context context = new Context();
		String subject = "Avviso Scadenza Pagamento e Rendicontazione Evento";

		context.setVariable("eventoIdentificativo", alert.getEvento().getCodiceIdentificativo());
		context.setVariable("eventoTitolo", alert.getEvento().getTitolo());

		String message = templateEngine.process("alertScadenzaPagamentoRendicontazioneEvento", context);

		for(String dst : alert.getDestinatari()){
			send(ecmProperties.getEmailSegreteriaEcm(), dst, subject, message, true);
		}
	}

	@Override
	public void inviaAlertScadenzaValutazioneReferee(AlertEmail alert) throws Exception {
		LOGGER.info("inviaAlertScadenzaValutazioneReferee");
		Context context = new Context();
		String subject = "Avviso Scadenza Valutazione Domanda Accreditamento";

		context.setVariable("applicationBaseUrl", ecmProperties.getApplicationBaseUrl());
		context.setVariable("provider", alert.getProvider());

		String message = templateEngine.process("alertScadenzaValutazioneReferee", context);

		for(String dst : alert.getDestinatari()){
			send(ecmProperties.getEmailSegreteriaEcm(), dst, subject, message, true);
		}
	}

	@Override
	public void inviaAlertScadenzaInvioAccreditamentoStandard(AlertEmail alert) throws Exception {
		LOGGER.info("inviaAlertScadenzaInvioAccreditamentoStandard");
		Context context = new Context();
		String subject = "Avviso Scadenza Invio Accreditamento Standard";

		String message = templateEngine.process("inviaAlertScadenzaInvioAccreditamentoStandard", context);

		for(String dst : alert.getDestinatari()){
			send(ecmProperties.getEmailSegreteriaEcm(), dst, subject, message, true);
		}

	}
}
