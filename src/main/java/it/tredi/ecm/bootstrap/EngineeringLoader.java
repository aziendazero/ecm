package it.tredi.ecm.bootstrap;

import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.StatusProvider;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.EventoRepository;
import it.tredi.ecm.dao.repository.PersonaRepository;
import it.tredi.ecm.dao.repository.ProfileRepository;
import it.tredi.ecm.dao.repository.ProviderRepository;

@Component
@org.springframework.context.annotation.Profile("dev")
public class EngineeringLoader implements ApplicationListener<ContextRefreshedEvent> {

		private final static Logger LOGGER = LoggerFactory.getLogger(EngineeringLoader.class);

		@Autowired
		private ProviderRepository providerRepository;
		@Autowired
		private EventoRepository eventoRepository;
		@Autowired
		private AccountRepository accountRepository;
		@Autowired
		private ProfileRepository profileRepository;

		@Override
		@Transactional
		public void onApplicationEvent(ContextRefreshedEvent event) {
			LOGGER.info("ENGINEERING DATABASE - Initializing...");

			Provider provider = providerRepository.findOneByPartitaIva("01234567890");

			if(provider == null) {
				Profile engineeringProfile = new Profile();
				engineeringProfile.setName("ENGINEERING");
				profileRepository.save(engineeringProfile);
				
				//Account
				Account account = new Account();
				account.setEmail("demo@eng.it");
				account.setUsername("engineering");
				account.setPassword("$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.");
				account.setEnabled(true);

				account.setChangePassword(false);
				account.setExpiresDate(null);
				account.setLocked(false);
				account.getProfiles().add(engineeringProfile);
				
				accountRepository.save(account);

				//Provider
				provider = new Provider();
				provider.setDenominazioneLegale("Engineering s.r.l.");
				provider.setCodiceFiscale("");
				provider.setPartitaIva("01234567890");
				provider.setTipoOrganizzatore(TipoOrganizzatore.AZIENDE_SANITARIE);
				provider.setStatus(StatusProvider.INSERITO);

				provider.setAccount(account);
				providerRepository.save(provider);

				//Eventi
				Evento evento = new Evento();
				evento.setCosto(500.99);
				evento.setTitolo("Evento1");
				evento.setProceduraFormativa(ProceduraFormativa.FAD);
				evento.setPagato(false);
				evento.setProvider(provider);

				eventoRepository.save(evento);

				Evento evento2 = new Evento();
				evento2.setCosto(200.00);
				evento2.setTitolo("Evento2");
				evento2.setProceduraFormativa(ProceduraFormativa.RES);
				evento2.setPagato(false);
				evento2.setProvider(provider);

				eventoRepository.save(evento2);

				Evento evento3 = new Evento();
				evento3.setCosto(1200.28);
				evento3.setTitolo("Evento3");
				evento3.setProceduraFormativa(ProceduraFormativa.FSC);
				evento3.setPagato(true);
				evento3.setProvider(provider);

				eventoRepository.save(evento3);
			}
			else {
				LOGGER.info("Engineering Database not EMPTY");
			}
		}
}
