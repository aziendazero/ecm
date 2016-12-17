package it.tredi.ecm;

import java.time.LocalDate;
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
@ActiveProfiles("demo")
@WithUserDetails("Provider")
@Rollback(false)
@Ignore
public class EmailTest {

	@Autowired private EmailService emailService;

	@Test
	public void inviaEmail() throws Exception{
		Set<String> s = new HashSet<String>();
		s.add("dpranteda@3di.it");

//		emailService.inviaNotificaATeamLeader("dpranteda@3di.it", "provider1");
		emailService.inviaConvocazioneValutazioneSulCampo(s, LocalDate.now(), "provider2");
//
//		emailService.inviaConvocazioneACommissioneECM(s);
//		emailService.inviaNotificaAReferee("dpranteda@3di.it", "provider1");
//		emailService.inviaNotificaASegreteriaMancataValutazioneReferee("dpranteda@3di.it", "provider1");
//		emailService.inviaNotificaATeamLeader("dpranteda@3di.it", "provider1");
	}

}
