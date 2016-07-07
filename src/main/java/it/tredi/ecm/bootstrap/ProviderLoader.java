package it.tredi.ecm.bootstrap;

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
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.StatusProvider;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.PersonaRepository;
import it.tredi.ecm.dao.repository.ProviderRepository;
import it.tredi.ecm.dao.repository.SedeRepository;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.DatiAccreditamentoService;
import it.tredi.ecm.service.DisciplinaService;
import it.tredi.ecm.utils.Utils;

@Component
@org.springframework.context.annotation.Profile("dev")
public class ProviderLoader implements ApplicationListener<ContextRefreshedEvent> {
	private final static Logger LOGGER = LoggerFactory.getLogger(ProviderLoader.class);
	
	@Autowired private ProviderRepository providerRepository;
	@Autowired private PersonaRepository personaRepository;
	@Autowired private AccountRepository accountRepository;
	@Autowired private SedeRepository sedeRepository;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private DisciplinaService disciplinaService;
	@Autowired private DatiAccreditamentoService datiAccreditamentoService;
	
//	@Autowired
//	public ProviderLoader(ProviderRepository providerRepository, PersonaRepository personaRepository, AccountRepository accountRepository) {
//		this.providerRepository = providerRepository;
//		this.personaRepository = personaRepository;
//		this.accountRepository = accountRepository;
//	}
//	
	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event){
		LOGGER.info("BOOTSTRAP ECM - Inizializzazione PROVIDER...");
		
		Provider provider = providerRepository.findOneByPartitaIva("00578261208");
		
		if(provider == null){
			Persona richiedente = new Persona();
			richiedente.getAnagrafica().setCognome("Rossi");
			richiedente.getAnagrafica().setNome("Mario");
			richiedente.getAnagrafica().setCodiceFiscale("MRARSS86H01Z112R");
			richiedente.getAnagrafica().setCellulare("3331234567");
			richiedente.getAnagrafica().setTelefono("0517654321");
			richiedente.getAnagrafica().setEmail("mrossi@3di.com");
			richiedente.getAnagrafica().setPec("mrossi@pec.com");
			personaRepository.save(richiedente);
			
			LOGGER.info("Persona " + richiedente.getId() + " created");
			
			Persona legale = new Persona();
			legale.getAnagrafica().setCognome("Verdi");
			legale.getAnagrafica().setNome("Giuseppe");
			legale.getAnagrafica().setCodiceFiscale("VRDGPP80H17D969I");
			legale.getAnagrafica().setCellulare("123456789");
			legale.getAnagrafica().setEmail("gverdi@3di.it");
			legale.getAnagrafica().setPec("gverdi@pec.it");
			personaRepository.save(legale);
			
			LOGGER.info("Persona " + legale.getId() + " created");
			
			richiedente.setRuolo(Ruolo.RICHIEDENTE);
			legale.setRuolo(Ruolo.LEGALE_RAPPRESENTANTE);
			Account account = accountRepository.findOneByUsername("admin").orElse(null);
			
			Sede sedeLegale = new Sede();
			sedeLegale.setProvincia("Venezia");
			sedeLegale.setComune("comuneA - 1");
			sedeLegale.setCap("11111");
			sedeLegale.setIndirizzo("Via speranza, 35");
			sedeLegale.setTelefono("123456");
			sedeLegale.setFax("01234567");
			sedeLegale.setEmail("sedeLegale@3di.it");
			sedeRepository.save(sedeLegale);
			
			provider = new Provider();
			provider.setDenominazioneLegale("3D Informatica");
			provider.setPartitaIva("00578261208");
			provider.setTipoOrganizzatore(TipoOrganizzatore.AZIENDE_SANITARIE);
			provider.setRagioneSociale("srl");
			provider.setNaturaOrganizzazione("Privata");
			provider.setNoProfit(false);
			
			provider.setStatus(StatusProvider.INSERITO);
			provider.setAccount(account);
			provider.addPersona(legale);
			provider.setSedeLegale(sedeLegale);
			provider.setSedeOperativa(sedeLegale);
			legale.getAnagrafica().setTelefono("123456");
			providerRepository.save(provider);
			
			Accreditamento accreditamento = null;
			try{
				accreditamento = accreditamentoService.getNewAccreditamentoForProvider(provider.getId());
			}catch (Exception ex){
				Utils.logError(LOGGER, "[BOOTSTRAP] - newAccreditamentoForProvider(" + provider.getId() + ")",  ex);
			}
			
			DatiAccreditamento datiAccreditamento = new DatiAccreditamento();
			datiAccreditamento.setTipologiaAccreditamento("Settoriale");
			Set<ProceduraFormativa> pF = new HashSet<ProceduraFormativa>();
			pF.add(ProceduraFormativa.FSC);
			pF.add(ProceduraFormativa.FAD);
			datiAccreditamento.setProcedureFormative(pF);
			datiAccreditamento.setProfessioniAccreditamento("Generale");
			
			Set<Disciplina> discipline = disciplinaService.getAllDiscipline();
			datiAccreditamento.setDiscipline(discipline);
			datiAccreditamento.setNumeroDipendentiFormazioneTempoIndeterminato(10);
			datiAccreditamento.setNumeroDipendentiFormazioneAltro(4);
			datiAccreditamento.setAccreditamento(accreditamento);
			datiAccreditamentoService.save(datiAccreditamento, accreditamento.getId());
			
			Persona responsabileSegreteria = new Persona();
			responsabileSegreteria.getAnagrafica().setCognome("Rossi");
			responsabileSegreteria.getAnagrafica().setNome("Mario");
			responsabileSegreteria.getAnagrafica().setCodiceFiscale("MRARSS86H01Z112A");
			responsabileSegreteria.getAnagrafica().setCellulare("3331234567");
			responsabileSegreteria.getAnagrafica().setTelefono("0517654321");
			responsabileSegreteria.getAnagrafica().setEmail("mrossi@3di.com");
			responsabileSegreteria.getAnagrafica().setPec("mrossi@pec.com");
			responsabileSegreteria.setProfessione(discipline.iterator().next().getProfessione());
			responsabileSegreteria.setRuolo(Ruolo.RESPONSABILE_SEGRETERIA);
			personaRepository.save(responsabileSegreteria);
			
			Persona responsabileAmministrativo = new Persona();
			responsabileAmministrativo.getAnagrafica().setCognome("Rossi");
			responsabileAmministrativo.getAnagrafica().setNome("Mario");
			responsabileAmministrativo.getAnagrafica().setCodiceFiscale("MRARSS86H01Z112B");
			responsabileAmministrativo.getAnagrafica().setCellulare("3331234567");
			responsabileAmministrativo.getAnagrafica().setTelefono("0517654321");
			responsabileAmministrativo.getAnagrafica().setEmail("mrossi@3di.com");
			responsabileAmministrativo.getAnagrafica().setPec("mrossi@pec.com");
			responsabileAmministrativo.setProfessione(discipline.iterator().next().getProfessione());
			responsabileAmministrativo.setRuolo(Ruolo.RESPONSABILE_AMMINISTRATIVO);
			personaRepository.save(responsabileAmministrativo);
			
			Persona responsabileSistemaInformatico = new Persona();
			responsabileSistemaInformatico.getAnagrafica().setCognome("Rossi");
			responsabileSistemaInformatico.getAnagrafica().setNome("Mario");
			responsabileSistemaInformatico.getAnagrafica().setCodiceFiscale("MRARSS86H01Z112C");
			responsabileSistemaInformatico.getAnagrafica().setCellulare("3331234567");
			responsabileSistemaInformatico.getAnagrafica().setTelefono("0517654321");
			responsabileSistemaInformatico.getAnagrafica().setEmail("mrossi@3di.com");
			responsabileSistemaInformatico.getAnagrafica().setPec("mrossi@pec.com");
			responsabileSistemaInformatico.setProfessione(discipline.iterator().next().getProfessione());
			responsabileSistemaInformatico.setRuolo(Ruolo.RESPONSABILE_SISTEMA_INFORMATICO);
			personaRepository.save(responsabileSistemaInformatico);
			
			Persona responsabileQualita = new Persona();
			responsabileQualita.getAnagrafica().setCognome("Rossi");
			responsabileQualita.getAnagrafica().setNome("Mario");
			responsabileQualita.getAnagrafica().setCodiceFiscale("MRARSS86H01Z112D");
			responsabileQualita.getAnagrafica().setCellulare("3331234567");
			responsabileQualita.getAnagrafica().setTelefono("0517654321");
			responsabileQualita.getAnagrafica().setEmail("mrossi@3di.com");
			responsabileQualita.getAnagrafica().setPec("mrossi@pec.com");
			responsabileQualita.setProfessione(discipline.iterator().next().getProfessione());
			responsabileQualita.setRuolo(Ruolo.RESPONSABILE_QUALITA);
			personaRepository.save(responsabileQualita);
			
			Persona coordinatoreComitato = new Persona();
			coordinatoreComitato.getAnagrafica().setCognome("Rossi");
			coordinatoreComitato.getAnagrafica().setNome("Mario");
			coordinatoreComitato.getAnagrafica().setCodiceFiscale("MRARSS86H01Z112E");
			coordinatoreComitato.getAnagrafica().setCellulare("3331234567");
			coordinatoreComitato.getAnagrafica().setTelefono("0517654321");
			coordinatoreComitato.getAnagrafica().setEmail("mrossi@3di.com");
			coordinatoreComitato.getAnagrafica().setPec("mrossi@pec.com");
			coordinatoreComitato.setProfessione(discipline.iterator().next().getProfessione());
			coordinatoreComitato.setRuolo(Ruolo.COORDINATORE_COMITATO_SCIENTIFICO);
			personaRepository.save(coordinatoreComitato);
			
			provider.addPersona(responsabileSegreteria);
			provider.addPersona(responsabileAmministrativo);
			provider.addPersona(responsabileSistemaInformatico);
			provider.addPersona(responsabileQualita);
			provider.addPersona(coordinatoreComitato);
			
			Persona componenteComitato1 = new Persona();
			componenteComitato1.setRuolo(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO);
			componenteComitato1.setAnagrafica(responsabileAmministrativo.getAnagrafica());
			componenteComitato1.setProfessione(discipline.iterator().next().getProfessione());
			personaRepository.save(componenteComitato1);
			
			Persona componenteComitato2 = new Persona();
			componenteComitato2.setRuolo(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO);
			componenteComitato2.setAnagrafica(responsabileSegreteria.getAnagrafica());
			componenteComitato2.setProfessione(discipline.iterator().next().getProfessione());
			personaRepository.save(componenteComitato2);
			
			Persona componenteComitato3 = new Persona();
			componenteComitato3.setRuolo(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO);
			componenteComitato3.setAnagrafica(responsabileQualita.getAnagrafica());
			componenteComitato3.setProfessione(discipline.iterator().next().getProfessione());
			personaRepository.save(componenteComitato3);
			
			Persona componenteComitato4 = new Persona();
			componenteComitato4.setRuolo(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO);
			componenteComitato4.setAnagrafica(responsabileSistemaInformatico.getAnagrafica());
			componenteComitato4.setProfessione(discipline.iterator().next().getProfessione());
			personaRepository.save(componenteComitato4);
			
			provider.addPersona(componenteComitato1);
			provider.addPersona(componenteComitato2);
			provider.addPersona(componenteComitato3);
			provider.addPersona(componenteComitato4);
			
			LOGGER.info("BOOTSTRAP ECM - PROVIDER creato");
		}else{
			LOGGER.info("BOOTSTRAP ECM - PROVIDER trovato");
		}
	}
}