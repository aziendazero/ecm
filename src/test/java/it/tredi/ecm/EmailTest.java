package it.tredi.ecm;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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

import it.tredi.ecm.service.EmailService;
import it.tredi.ecm.service.ProviderService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("papa")
@WithUserDetails("Segreteria")
@Rollback(false)
@Ignore
public class EmailTest {

	@Autowired private EmailService emailService;

	@Test
	public void inviaEmail() throws Exception{
		Set<String> s = new HashSet<String>();
		s.add("lpapa@3dial.eu");

		emailService.inviaNotificaATeamLeader("lpapa@3dial.eu", "Segreteria ECM");
		emailService.inviaConvocazioneValutazioneSulCampo(s, LocalDateTime.now(), "Segreteria ECM");
//
		emailService.inviaConvocazioneACommissioneECM(s);
		emailService.inviaNotificaAReferee("lpapa@3dial.eu", "Segreteria ECM");
		emailService.inviaNotificaASegreteriaMancataValutazioneReferee("lpapa@3dial.eu", "Segreteria ECM");
		emailService.inviaNotificaATeamLeader("lpapa@3dial.eu", "Segreteria ECM");
	}

}
