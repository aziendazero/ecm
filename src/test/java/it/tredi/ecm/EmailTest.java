package it.tredi.ecm;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import it.tredi.ecm.dao.entity.AlertEmail;
import it.tredi.ecm.dao.enumlist.AlertTipoEnum;
import it.tredi.ecm.service.EmailService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.ProviderService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("demo")
@WithUserDetails("test1")
@Rollback(false)

//@Ignore
public class EmailTest {

	@Autowired private EmailService emailService;
	@Autowired private ProviderService providerService;
	@Autowired private EventoService eventoService;

	@Test
	@Transactional
	public void inviaEmail() throws Exception{
		String pr = "Provider1";
		String ms = "mlcarlino@3di.it";
		String ms1 = "eluconi@3di.it";
		Long acreditamentoId_P = 666834L;
		Long acreditamentoId_S = 698415L;
		Long providerId = 193L;
		Long eventoId = 200381L;
		
		Set<String> s = new HashSet<String>();
		s.add(ms);
		//s.add(ms1);		
		

		// Email dritti
		
		
		emailService.inviaNotificaAReferee(ms, pr);
		emailService.inviaConvocazioneACommissioneECM(s);
		emailService.inviaNotificaASegreteriaMancataValutazioneReferee(ms, pr);
		emailService.inviaAlertErroreDiSistema("Error");
		emailService.inviaNotificaATeamLeader(ms, pr);
		emailService.inviaConvocazioneValutazioneSulCampo(s, LocalDateTime.now(), "provider2");
		emailService.inviaNotificaFirmaResponsabileSegreteriaEcm(s, acreditamentoId_P);
		emailService.inviaNotificaFirmaResponsabileSegreteriaEcm(s, acreditamentoId_S);
		
		emailService.inviaConfermaReInvioIntegrazioniAccreditamento(false, true, providerService.getProvider(providerId));
		emailService.inviaConfermaReInvioIntegrazioniAccreditamento(false, false, providerService.getProvider(providerId));
		emailService.inviaConfermaReInvioIntegrazioniAccreditamento(true, true, providerService.getProvider(providerId));
		emailService.inviaConfermaReInvioIntegrazioniAccreditamento(true, false, providerService.getProvider(providerId));
		emailService.inviaNotificaNuovaComunicazioneForProvider(pr, ms);

		
		// alerts
		AlertEmail a = null;
		
		a = new AlertEmail();
		a.setDestinatari(s);
		a.setEvento(eventoService.getEvento(eventoId));
		a.setProvider(providerService.getProvider(providerId));
		a.setTipo(AlertTipoEnum.SCADENZA_REINVIO_INTEGRAZIONI_ACCREDITAMENTO_PROVVISORIO);
		emailService.inviaAlertScadenzaReInvioIntegrazioneAccreditamento(a);
		
		a = new AlertEmail();
		a.setDestinatari(s);
		a.setEvento(eventoService.getEvento(eventoId));
		a.setProvider(providerService.getProvider(providerId));
		a.setTipo(AlertTipoEnum.SCADENZA_REINVIO_INTEGRAZIONI_PREAVVISO_DI_RIGETTO_ACCREDITAMENTO_PROVVISORIO);
		emailService.inviaAlertScadenzaReInvioIntegrazioneAccreditamento(a);
		
		a = new AlertEmail();
		a.setDestinatari(s);
		a.setEvento(eventoService.getEvento(eventoId));
		a.setProvider(providerService.getProvider(providerId));
		a.setTipo(AlertTipoEnum.SCADENZA_REINVIO_INTEGRAZIONI_ACCREDITAMENTO_STANDARD);
		emailService.inviaAlertScadenzaReInvioIntegrazioneAccreditamento(a);
				
		a = new AlertEmail();
		a.setDestinatari(s);
		a.setEvento(eventoService.getEvento(eventoId));
		a.setProvider(providerService.getProvider(providerId));
		a.setTipo(AlertTipoEnum.SCADENZA_REINVIO_INTEGRAZIONI_PREAVVISO_DI_RIGETTO_ACCREDITAMENTO_STANDARD);
		emailService.inviaAlertScadenzaReInvioIntegrazioneAccreditamento(a);
		
		
		a = new AlertEmail();
		a.setDestinatari(s);
		a.setEvento(eventoService.getEvento(eventoId));
		a.setProvider(providerService.getProvider(providerId));
		a.setTipo(AlertTipoEnum.SCADENZA_ACCREDITAMENTO_PROVVISORIO);
		emailService.inviaAlertScadenzaAccreditamento(a);

		a = new AlertEmail();
		a.setDestinatari(s);
		a.setEvento(eventoService.getEvento(eventoId));
		a.setProvider(providerService.getProvider(providerId));
		a.setTipo(AlertTipoEnum.SCADENZA_ACCREDITAMENTO_STANDARD);
		emailService.inviaAlertScadenzaAccreditamento(a);
		
		
		a = new AlertEmail();
		a.setDestinatari(s);
		a.setEvento(eventoService.getEvento(eventoId));
		a.setProvider(providerService.getProvider(providerId));
		
		emailService.inviaAlertScadenzaPagamento(a);
		emailService.inviaAlertScadenzaPFA(a);
		emailService.inviaAlertScadenzaRelazioneAnnuale(a);		
		emailService.inviaAlertScadenzaPagamentoRendicontazioneEvento(a);		
		emailService.inviaAlertScadenzaValutazioneReferee(a);		
		emailService.inviaAlertScadenzaInvioAccreditamentoStandard(a);		
		
	}

}
