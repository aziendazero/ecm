package it.tredi.ecm.bootstrap;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.RagioneSocialeEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.TipoOrganizzatore;
import it.tredi.ecm.dao.repository.AccountRepository;
import it.tredi.ecm.dao.repository.PersonaRepository;
import it.tredi.ecm.dao.repository.ProviderRepository;
import it.tredi.ecm.dao.repository.SedeRepository;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.DatiAccreditamentoService;
import it.tredi.ecm.service.DisciplinaService;
import it.tredi.ecm.service.WorkflowService;

@Component
@org.springframework.context.annotation.Profile({"simone","abarducci", "tom", "joe19","dev"})
public class ProviderLoader implements ApplicationListener<ContextRefreshedEvent> {
	private final static Logger LOGGER = LoggerFactory.getLogger(ProviderLoader.class);

	@Autowired private ProviderRepository providerRepository;
	@Autowired private PersonaRepository personaRepository;
	@Autowired private AccountRepository accountRepository;
	@Autowired private SedeRepository sedeRepository;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private DisciplinaService disciplinaService;
	@Autowired private DatiAccreditamentoService datiAccreditamentoService;
	@Autowired private WorkflowService workflowService;

	@Override
	@Transactional
	public void onApplicationEvent(ContextRefreshedEvent event){
		LOGGER.info("BOOTSTRAP ECM - Inizializzazione PROVIDER...");

		Provider provider = providerRepository.findOneByPartitaIva("00578261201");

		int numeroProvider = 5;

		if(provider == null){
			for(int i = 1; i<=numeroProvider; i++) {
				String providerName = "provider" + i;
				String partitaIva = "0057826120" + i;
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
				legale.getAnagrafica().setCodiceFiscale("TRLVNI64E28H199O");
				legale.getAnagrafica().setCellulare("123456789");
				legale.getAnagrafica().setEmail("eluconi@3di.it");
				legale.getAnagrafica().setPec("demo@pec.3di.it");
				personaRepository.save(legale);

				LOGGER.info("Persona " + legale.getId() + " created");

				richiedente.setRuolo(Ruolo.RICHIEDENTE);
				legale.setRuolo(Ruolo.LEGALE_RAPPRESENTANTE);
				Account account = accountRepository.findOneByUsername(providerName).orElse(null);

				Sede sedeLegale = new Sede();
				sedeLegale.setProvincia("VENEZIA");
				sedeLegale.setComune("CHIOGGIA");
				sedeLegale.setCap("30121");
				sedeLegale.setIndirizzo("Via speranza, 35");
				sedeLegale.setTelefono("123456");
				sedeLegale.setFax("01234567");
				sedeLegale.setEmail("sedeLegale@3di.it");
				sedeLegale.setSedeLegale(true);
				sedeRepository.save(sedeLegale);

				provider = new Provider();
				provider.setDenominazioneLegale(providerName);
				provider.setHasPartitaIVA(true);
				provider.setPartitaIva(partitaIva);
				provider.setCodiceFiscale("3dInformatica" + i);
				provider.setTipoOrganizzatore(TipoOrganizzatore.AZIENDE_SANITARIE);
				provider.setRagioneSociale(RagioneSocialeEnum.SRL);
				provider.setNaturaOrganizzazione("Privata");
				provider.setNoProfit(false);
				provider.setCodiceCogeaps("ORG213");
				provider.setEmailStruttura("provider" + i + "@3di.it");
				provider.setCanInsertAccreditamentoProvvisorio(true);
				provider.setDataRinnovoInsertAccreditamentoProvvisorio(LocalDate.now().minusDays(1));

				provider.setStatus(ProviderStatoEnum.INSERITO);
				//provider.setAccount(account);
				provider.addPersona(legale);
				provider.addPersona(richiedente);
				provider.addSede(sedeLegale);
				legale.getAnagrafica().setTelefono("123456");
				providerRepository.save(provider);

				account.setProvider(provider);
				account.setNome("Amministratore");
				account.setCognome("Provider");
				accountRepository.save(account);

				Accreditamento accreditamento = null;
				try{
					accreditamento = accreditamentoService.getNewAccreditamentoForProvider(provider.getId(), AccreditamentoTipoEnum.PROVVISORIO);
				}catch (Exception ex){
					LOGGER.error("[BOOTSTRAP] - newAccreditamentoForProvider(" + provider.getId() + ")",  ex);
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
				responsabileSegreteria.getAnagrafica().setNome("Valentino");
				responsabileSegreteria.getAnagrafica().setCodiceFiscale("RSSVNT90A01A944Y");
				responsabileSegreteria.getAnagrafica().setCellulare("3331234567");
				responsabileSegreteria.getAnagrafica().setTelefono("0517654321");
				responsabileSegreteria.getAnagrafica().setEmail("vrossi@3di.com");
				responsabileSegreteria.getAnagrafica().setPec("vrossi@pec.com");
				responsabileSegreteria.setProfessione(discipline.iterator().next().getProfessione());
				responsabileSegreteria.setRuolo(Ruolo.RESPONSABILE_SEGRETERIA);
				personaRepository.save(responsabileSegreteria);

				Persona responsabileAmministrativo = new Persona();
				responsabileAmministrativo.getAnagrafica().setCognome("Biaggi");
				responsabileAmministrativo.getAnagrafica().setNome("Max");
				responsabileAmministrativo.getAnagrafica().setCodiceFiscale("BGGMXA90A01A944K");
				responsabileAmministrativo.getAnagrafica().setCellulare("3331234567");
				responsabileAmministrativo.getAnagrafica().setTelefono("0517654321");
				responsabileAmministrativo.getAnagrafica().setEmail("mbiaggi@3di.com");
				responsabileAmministrativo.getAnagrafica().setPec("mbiaggi@pec.com");
				responsabileAmministrativo.setProfessione(discipline.iterator().next().getProfessione());
				responsabileAmministrativo.setRuolo(Ruolo.RESPONSABILE_AMMINISTRATIVO);
				personaRepository.save(responsabileAmministrativo);

				Persona responsabileSistemaInformatico = new Persona();
				responsabileSistemaInformatico.getAnagrafica().setCognome("Gibernau");
				responsabileSistemaInformatico.getAnagrafica().setNome("Sete");
				responsabileSistemaInformatico.getAnagrafica().setCodiceFiscale("GBRSTE90A01A944M");
				responsabileSistemaInformatico.getAnagrafica().setCellulare("3331234567");
				responsabileSistemaInformatico.getAnagrafica().setTelefono("0517654321");
				responsabileSistemaInformatico.getAnagrafica().setEmail("sgibernau@3di.com");
				responsabileSistemaInformatico.getAnagrafica().setPec("sgibernau@pec.com");
				responsabileSistemaInformatico.setProfessione(discipline.iterator().next().getProfessione());
				responsabileSistemaInformatico.setRuolo(Ruolo.RESPONSABILE_SISTEMA_INFORMATICO);
				personaRepository.save(responsabileSistemaInformatico);

				Persona responsabileQualita = new Persona();
				responsabileQualita.getAnagrafica().setCognome("Agostini");
				responsabileQualita.getAnagrafica().setNome("Giacomo");
				responsabileQualita.getAnagrafica().setCodiceFiscale("GSTGCM90A01A944W");
				responsabileQualita.getAnagrafica().setCellulare("3331234567");
				responsabileQualita.getAnagrafica().setTelefono("0517654321");
				responsabileQualita.getAnagrafica().setEmail("gagostini@3di.com");
				responsabileQualita.getAnagrafica().setPec("gagostini@pec.com");
				responsabileQualita.setProfessione(discipline.iterator().next().getProfessione());
				responsabileQualita.setRuolo(Ruolo.RESPONSABILE_QUALITA);
				personaRepository.save(responsabileQualita);

				Persona coordinatoreComitato = new Persona();
				coordinatoreComitato.getAnagrafica().setCognome("Capirossi");
				coordinatoreComitato.getAnagrafica().setNome("Loris");
				coordinatoreComitato.getAnagrafica().setCodiceFiscale("CPRLRS90A01A944R");
				coordinatoreComitato.getAnagrafica().setCellulare("3331234567");
				coordinatoreComitato.getAnagrafica().setTelefono("0517654321");
				coordinatoreComitato.getAnagrafica().setEmail("lcapirossi@3di.com");
				coordinatoreComitato.getAnagrafica().setPec("lcapirossi@pec.com");
				coordinatoreComitato.setProfessione(discipline.iterator().next().getProfessione());
				coordinatoreComitato.setCoordinatoreComitatoScientifico(true);
				coordinatoreComitato.setRuolo(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO);
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
				componenteComitato1.setCoordinatoreComitatoScientifico(false);
				personaRepository.save(componenteComitato1);

				Persona componenteComitato2 = new Persona();
				componenteComitato2.setRuolo(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO);
				componenteComitato2.setAnagrafica(responsabileSegreteria.getAnagrafica());
				componenteComitato2.setProfessione(discipline.iterator().next().getProfessione());
				componenteComitato2.setCoordinatoreComitatoScientifico(false);
				personaRepository.save(componenteComitato2);

				Persona componenteComitato3 = new Persona();
				componenteComitato3.setRuolo(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO);
				componenteComitato3.setAnagrafica(responsabileQualita.getAnagrafica());
				componenteComitato3.setProfessione(discipline.iterator().next().getProfessione());
				componenteComitato3.setCoordinatoreComitatoScientifico(false);
				personaRepository.save(componenteComitato3);

				Persona componenteComitato4 = new Persona();
				componenteComitato4.setRuolo(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO);
				componenteComitato4.setAnagrafica(responsabileSistemaInformatico.getAnagrafica());
				componenteComitato4.setProfessione(discipline.iterator().next().getProfessione());
				componenteComitato4.setCoordinatoreComitatoScientifico(false);
				personaRepository.save(componenteComitato4);

				provider.addPersona(componenteComitato1);
				provider.addPersona(componenteComitato2);
				provider.addPersona(componenteComitato3);
				provider.addPersona(componenteComitato4);

				LOGGER.info("BOOTSTRAP ECM - PROVIDER creato");
			}
		}else{
			LOGGER.info("BOOTSTRAP ECM - PROVIDER trovato");
		}

		LOGGER.info("BOOTSTRAP ECM - AGGANCIO account fake per comunicazioni");
		//creo list di tutti i provider
		Set<Provider> providerSet = providerRepository.findAll();
		List<Provider> providerList = new ArrayList<Provider>();
		providerList.addAll(providerSet);
		//creo list di tutti gli account fake del provider
		Set<Account> accountFakeSet = accountRepository.findAllByFakeAccountComunicazioniTrue();
		//rimuovo il fake della segreteria
		Account fakeSegreteria = accountRepository.findOneByUsernameAndFakeAccountComunicazioniTrue("segreteriacomunicazioni");
		accountFakeSet.remove(fakeSegreteria);
		List<Account> accountFakeList = new ArrayList<Account>();
		accountFakeList.addAll(accountFakeSet);
		//per ogni provider assegno un utente fake per la gestione delle comunicazioni
		for(int i = 0; i <= numeroProvider-1; i++) {
			Provider p = providerList.get(i);
			Account a = accountFakeList.get(i);
			a.setUsername("provider" + p.getId() + "comunicazioni");
			a.setEmail("provider" + p.getId() + "@comunicazioni.it");
			a.setProvider(p);
			try {
				accountRepository.save(a);
				workflowService.saveOrUpdateBonitaUserByAccount(a);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LOGGER.info("BOOTSTRAP ECM - COMPLETATO account fake per comunicazioni");
	}
}