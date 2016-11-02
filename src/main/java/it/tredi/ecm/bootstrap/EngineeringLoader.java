package it.tredi.ecm.bootstrap;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.EventoRepository;
import it.tredi.ecm.dao.repository.ProfileRepository;
import it.tredi.ecm.dao.repository.ProviderRepository;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.DatiAccreditamentoService;

@Component
@org.springframework.context.annotation.Profile("dev")
public class EngineeringLoader implements ApplicationListener<ContextRefreshedEvent> {

		private final static Logger LOGGER = LoggerFactory.getLogger(EngineeringLoader.class);

		@Autowired private ProviderRepository providerRepository;
		@Autowired private EventoRepository eventoRepository;
		@Autowired private AccountRepository accountRepository;
		@Autowired private ProfileRepository profileRepository;
		@Autowired private AccreditamentoService accreditamentoService;
		@Autowired private DatiAccreditamentoService datAccreditamentoService;

		@Override
		@Transactional
		public void onApplicationEvent(ContextRefreshedEvent event) {
			LOGGER.info("BOOTSTRAP ECM - Inizializzazione ENGINEERING...");

			//Provider provider = providerRepository.findOneByPartitaIva("01234567890");
			Provider provider = providerRepository.findOneByCodiceFiscale("ENGINEERINGSRL");

			if(provider == null) {
				//Profilo
				Profile profile = new Profile();
				profile.setProfileEnum(ProfileEnum.ENGINEERING);
				profileRepository.save(profile);
				
				//Provider
				provider = new Provider();
				provider.setDenominazioneLegale("Engineering s.r.l.");
				provider.setCodiceFiscale("engineeringSRL");
				provider.setHasPartitaIVA(true);
				provider.setPartitaIva("01234567897");
				provider.setTipoOrganizzatore(TipoOrganizzatore.AZIENDE_SANITARIE);
				provider.setStatus(ProviderStatoEnum.INSERITO);
				//provider.setAccount(account);
				providerRepository.save(provider);

				//Account
				Account account = new Account();
				account.setEmail("demo@eng.it");
				account.setUsername("engineering-dev");
				account.setPassword("$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.");
				account.setEnabled(true);
				account.setChangePassword(false);
				account.setExpiresDate(null);
				account.setLocked(false);
				account.getProfiles().add(profile);
				account.setDataScadenzaPassword(LocalDate.parse("2017-12-31"));
				account.setNome("Amministratore");
				account.setCognome("Provider");
				account.setProvider(provider);
				accountRepository.save(account);

				//Accreditamento
				Accreditamento accreditamento = null;
				try{
					accreditamento = accreditamentoService.getNewAccreditamentoForProvider(provider.getId(),AccreditamentoTipoEnum.PROVVISORIO);
				}catch(Exception ex){
					LOGGER.error("BOOTSTRAP ECM - Creazione nuova domanda di accreditamento");
					return;
				}
				
				DatiAccreditamento datiAccreditamento = new DatiAccreditamento();
				Set<ProceduraFormativa> procedure = new HashSet<ProceduraFormativa>();
				procedure.add(ProceduraFormativa.FAD);
				procedure.add(ProceduraFormativa.RES);
				procedure.add(ProceduraFormativa.FSC);
				datiAccreditamento.setProcedureFormative(procedure);
				datiAccreditamento.setNumeroDipendentiFormazioneAltro(4);
				datiAccreditamento.setNumeroDipendentiFormazioneTempoIndeterminato(10);
				datAccreditamentoService.save(datiAccreditamento, accreditamento.getId());
				
				//Eventi
				Evento evento = new Evento();
				evento.setCosto(500.99);
				evento.setTitolo("Evento1");
				evento.setProceduraFormativa(ProceduraFormativa.FAD);
				evento.setPagato(false);
				evento.setProvider(provider);
				evento.setAccreditamento(accreditamento);
				eventoRepository.save(evento);

				Evento evento2 = new Evento();
				evento2.setCosto(200.00);
				evento2.setTitolo("Evento2");
				evento2.setProceduraFormativa(ProceduraFormativa.RES);
				evento2.setPagato(false);
				evento2.setProvider(provider);
				evento2.setAccreditamento(accreditamento);
				eventoRepository.save(evento2);

				Evento evento3 = new Evento();
				evento3.setCosto(1200.28);
				evento3.setTitolo("Evento3");
				evento3.setProceduraFormativa(ProceduraFormativa.FSC);
				evento3.setPagato(true);
				evento3.setProvider(provider);
				evento3.setAccreditamento(accreditamento);
				eventoRepository.save(evento3);
				LOGGER.info("BOOTSTRAP ECM - ENGINEERING creato...");
			}
			else {
				LOGGER.info("BOOTSTRAP ECM - ENGINEERING trovato...");
			}
		}
}
