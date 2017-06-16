package it.tredi.ecm.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.audit.entity.ProviderAudit;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.BaseEntity;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.MotivazioneProrogaEnum;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.dao.enumlist.ProviderStatoEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.repository.ProviderRepository;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.service.bean.ProviderRegistrationWrapper;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.ImpostazioniProviderWrapper;
import it.tredi.ecm.web.bean.RicercaProviderWrapper;
import javassist.bytecode.analysis.Util;

@Service
public class ProviderServiceImpl implements ProviderService {

	private final Logger LOGGER = Logger.getLogger(ProviderService.class);

	private final String AMMINISTRATORE_PROVIDER_ACCOUNT_NOME = "Amministratore";
	private final String AMMINISTRATORE_PROVIDER_ACCOUNT_COGNOME = "Provider";

	@Autowired private ProviderRepository providerRepository;
	@Autowired private PersonaService personaService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private ProfileAndRoleService profileAndRoleService;
	@Autowired private AccountService accountService;
	@Autowired private FileService fileService;
	@Autowired private AlertEmailService alertEmailService;
	@PersistenceContext EntityManager entityManager;
	@Autowired private EcmProperties ecmProperties;
	@Autowired private ProtocolloService protocolloService;
	@Autowired private AuditService auditService;
	@Autowired private AuditReportProviderService auditReportProviderService;
	@Autowired private RelazioneAnnualeService relazioneAnnualeService;
	@Autowired private ReportRitardiService reportRitardiService;
	@Autowired private PianoFormativoService pianoFormativoService;

	@Override
	public Provider getProvider() {
		LOGGER.info("Retrieving Provider for current user...");
		CurrentUser currentUser = Utils.getAuthenticatedUser();
		if(currentUser != null){
			//Provider multi account
			//Provider provider = providerRepository.getProviderByAccountId(currentUser.getAccount().getId());
			if(currentUser.getAccount().getProvider() != null){
				LOGGER.info("Found Provider (" + currentUser.getAccount().getProvider().getId() +")");
				return providerRepository.findOne(currentUser.getAccount().getProvider().getId());
				//return currentUser.getAccount().getProvider();
			}
			LOGGER.info("Provider not found");
		}else{
			LOGGER.info("User not sign in");
			return new Provider();
		}
		return null;
	}

	@Override
	public Provider getProvider(Long id){
		LOGGER.info("Retrieving Provider (" + id +")");
		return providerRepository.findOne(id);
	}

	@Override
	public Provider getProviderByCodiceFiscale(String codiceFiscale) {
		LOGGER.info("Retrieving Provider (" + codiceFiscale +")");
		return providerRepository.findOneByCodiceFiscale(codiceFiscale);
	}

	@Override
	public Provider getProviderByPartitaIva(String partitaIva) {
		LOGGER.info("Retrieving Provider (" + partitaIva +")");
		return providerRepository.findOneByPartitaIva(partitaIva);
	}

	@Override
	public Set<Provider> getAll(){
		LOGGER.info("Retrieving all Providers");
		Set<Provider> list = new HashSet<Provider>(providerRepository.findAll());
		return list;
	}

	@Override
	public Set<Provider> getAllNotInserito() {
		LOGGER.info("Recupero tutti i Providers non in stato inserito");
		return providerRepository.findAllByStatusNot(ProviderStatoEnum.INSERITO);
	}

	@Override
	public Set<Provider> getAllAttivi() {
		LOGGER.info("Recupero tutti i Providers attivi");
		return providerRepository.findAllByStatusIn(Arrays.asList(ProviderStatoEnum.ACCREDITATO_STANDARD,ProviderStatoEnum.ACCREDITATO_PROVVISORIAMENTE));
	}

	@Override
	@Transactional
	public void save(Provider provider) {
		LOGGER.info("Saving Provider");
		/*
		if(provider.getAccount().isNew()){
			try{
				accountService.save(provider.getAccount());
			}catch (Exception ex){
				LOGGER.error("Impossibile salvare il Provider. Errore durante creazione Account",ex);
			}
		}
		*/
		providerRepository.save(provider);
		auditService.commitForCurrentUser(new ProviderAudit(provider));
		auditReportProviderService.auditAccreditamentoProvider(provider);

	}

	@Override
	public ProviderRegistrationWrapper getProviderRegistrationWrapper() {
		ProviderRegistrationWrapper providerRegistrationWrapper = new ProviderRegistrationWrapper();
		Provider provider = new Provider();
		providerRegistrationWrapper.setProvider(provider);

		if(provider.isNew()){
			File delega = new File();
			providerRegistrationWrapper.setDelega(delega);
			providerRegistrationWrapper.setLegale(new Persona(Ruolo.LEGALE_RAPPRESENTANTE));
		}else{
			Persona richiedente = personaService.getPersonaByRuolo(Ruolo.RICHIEDENTE, provider.getId());
			if(richiedente == null){
				richiedente = new Persona(Ruolo.RICHIEDENTE);
				provider.addPersona(richiedente);
			}

			Persona legale = personaService.getPersonaByRuolo(Ruolo.LEGALE_RAPPRESENTANTE, provider.getId());
			if(legale == null){
				legale = new Persona(Ruolo.LEGALE_RAPPRESENTANTE);
				provider.addPersona(legale);
			}

			File delega = new File();
			providerRegistrationWrapper.setDelega(delega);
			providerRegistrationWrapper.setLegale(legale);
		}

		return providerRegistrationWrapper;
	}

	@Override
	@Transactional
	public void saveProviderRegistrationWrapper(ProviderRegistrationWrapper providerRegistrationWrapper) throws Exception {
		Provider provider = providerRegistrationWrapper.getProvider();
		Account account = providerRegistrationWrapper.getAccount();
		Persona legale = providerRegistrationWrapper.getLegale();
		if(providerRegistrationWrapper.isDelegato())
			legale.setRuolo(Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE);
		else
			legale.setRuolo(Ruolo.LEGALE_RAPPRESENTANTE);
		File delega = providerRegistrationWrapper.getDelega();

		provider.setStatus(ProviderStatoEnum.INSERITO);
		provider.setCanInsertAccreditamentoProvvisorio(true);
		provider.setDataRinnovoInsertAccreditamentoProvvisorio(LocalDate.now().minusDays(1));
		providerRepository.saveAndFlush(provider);
		provider.setCodiceCogeaps(provider.getId().toString());
		save(provider);

		if(account.getProfiles().isEmpty()){
			Optional<Profile> providerProfile = profileAndRoleService.getProfileByProfileEnum(ProfileEnum.PROVIDER);
			if(providerProfile.isPresent())
				account.getProfiles().add(providerProfile.get());
			Optional<Profile> providerAdminEnumProfile = profileAndRoleService.getProfileByProfileEnum(ProfileEnum.PROVIDERUSERADMIN);
			if(providerAdminEnumProfile.isPresent())
				account.getProfiles().add(providerAdminEnumProfile.get());
		}
		account.setNome(AMMINISTRATORE_PROVIDER_ACCOUNT_NOME);
		account.setCognome(AMMINISTRATORE_PROVIDER_ACCOUNT_COGNOME);
		account.setProvider(provider);
		accountService.save(account);

		//Delegato consentito solo per alcuni tipi di Provider
		if(providerRegistrationWrapper.isDelegato()){
			delega.setTipo(FileEnum.FILE_DELEGA);
			fileService.save(delega);
			legale.addFile(delega);
		}

		//Genero il fake account per la gestione delle comunicazioni
		generaAccountComunicazioni(provider);

		provider.addPersona(legale);
		personaService.save(legale);
	}

	//Genera il fake account per la gestione delle comunicazioni
	private void generaAccountComunicazioni(Provider provider) throws Exception {
		Account accountComunicazioni = new Account();
		accountComunicazioni.setChangePassword(false);
		accountComunicazioni.setCodiceFiscale(null);
		accountComunicazioni.setCognome("Provider" + provider.getId());
		accountComunicazioni.setDataScadenzaPassword(null);
		accountComunicazioni.setEmail("provider" + provider.getId() + "@comunicazioni.it");
		accountComunicazioni.setEnabled(true);
		accountComunicazioni.setExpiresDate(null);
		accountComunicazioni.setLocked(false);
		accountComunicazioni.setNome("Comunicazioni");
		accountComunicazioni.setNote(null);
		//admin
		accountComunicazioni.setPassword("$2a$10$JCx8DPs0l0VNFotVGkfW/uRyJzFfc8HkTi5FQy0kpHSpq7W4iP69.");
		accountComunicazioni.setUsername("provider" + provider.getId() + "comunicazioni");
		//accountComunicazioni.setUsernameWorkflow("provider" + provider.getId() + "comunicazioni");
		accountComunicazioni.setValutazioniNonDate(0);
		accountComunicazioni.setProvider(provider);
		accountComunicazioni.setFakeAccountComunicazioni(true);
		Optional<Profile> providerProfile = profileAndRoleService.getProfileByProfileEnum(ProfileEnum.PROVIDER_ACCOUNT_COMUNICAZIONI);
		if(providerProfile.isPresent())
			accountComunicazioni.getProfiles().add(providerProfile.get());
		accountService.save(accountComunicazioni);
	}

	@Override
	public Long getProviderIdByAccountId(Long accountId) {
		return providerRepository.getIdByAccountId(accountId);
	}

	@Override
	public boolean canInsertPianoFormativo(Long providerId) {
		Provider provider = providerRepository.findOne(providerId);
		return provider.canInsertPianoFormativo();
	}

	@Override
	public boolean canInsertAccreditamentoStandard(Long providerId) {
		Provider provider = providerRepository.findOne(providerId);
		return provider.canInsertAccreditamentoStandard();
	}
	@Override
	public boolean canInsertAccreditamentoProvvisorio(Long providerId) {
		Provider provider = providerRepository.findOne(providerId);
		return provider.canInsertAccreditamentoProvvisorio();
	}
	@Override
	public boolean canInsertEvento(Long providerId) {
		Provider provider = providerRepository.findOne(providerId);
		return provider.canInsertEvento();
	}

	@Override
	public boolean canInsertRelazioneAnnuale(Long providerId) {
		Provider provider = providerRepository.findOne(providerId);
		boolean relazioneAnnualeInseritaAnnoCorrente = relazioneAnnualeService.isRelazioneAnnualeInseritaAnnoCorrente(providerId);
		return provider.canInsertRelazioneAnnuale() && !relazioneAnnualeInseritaAnnoCorrente;
	}

//	@Override
//	public boolean canInsertRelazioneAnnuale(Long providerId) {
//		int annoRiferimento = LocalDate.now().getYear();
//		if(!LocalDate.now().isAfter(LocalDate.of(annoRiferimento, 4, 30)))
//			return true;
//		else{
//			Boolean b = providerRepository.canInsertRelazioneAnnuale(providerId);
//			return (b != null) ? b.booleanValue() : false;
//		}
//	}

	@Override
	public boolean hasAlreadySedeLegaleProvider(Provider provider, Sede sede) {
		boolean result = false;
		for (Sede s : provider.getSedi()) {
			if(s.isSedeLegale() && !s.equals(sede))
				result = true;
		}
		return result;
	}

	@Override
	@Transactional
	public void saveFromIntegrazione(Provider provider) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio Provider da Integrazione"));
		save(provider);
	}

	@Override
	public void bloccaFunzionalitaForPagamento(Long providerId) {
		LOGGER.debug("Blocco canInsertPianoFormativo e canInsertEventi per Provider: " + providerId);
		Provider provider = getProvider(providerId);
		provider.setCanInsertPianoFormativo(false);
		provider.setCanInsertEvento(false);
		save(provider);
	}

	@Override
	public void abilitaFunzionalitaAfterPagamento(Long providerId) {
		LOGGER.debug("Abilito canInsertPianoFormativo e canInsertEventi per Provider: " + providerId);
		Provider provider = getProvider(providerId);
		provider.setCanInsertPianoFormativo(true);
		provider.setCanInsertEvento(true);
		save(provider);
	}

	@Override
	public String getCodiceFiscaleLegaleRappresentantePerVerificaFirmaDigitale(Long providerId) {
		LOGGER.debug("Recupero CodiceFiscale Legale Rappresentante per verifica firma digitale del Provider: " + providerId);

		Persona persona = personaService.getPersonaByRuolo(Ruolo.LEGALE_RAPPRESENTANTE, providerId);
		if(persona != null && persona.getAnagrafica() != null){
			return persona.getAnagrafica().getCodiceFiscale();
		}

		LOGGER.debug("Legale Rappresentante non presente per il Provider: " + providerId);
		return "";
	}

	@Override
	public String getCodiceFiscaleDelegatoLegaleRappresentantePerVerificaFirmaDigitale(Long providerId) {
		LOGGER.debug("Recupero CodiceFiscale Delegato Legale Rappresentante per verifica firma digitale del Provider: " + providerId);

		Persona persona = personaService.getPersonaByRuolo(Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, providerId);
		if(persona != null && persona.getAnagrafica() != null){
			return persona.getAnagrafica().getCodiceFiscale();
		}

		LOGGER.debug("Delegato Legale Rappresentante non presente per il Provider: " + providerId);
		return "";
	}

	@Override
	public String getEmailLegaleRappresentante(Long providerId) {
		LOGGER.debug("Recupero Email Legale Rappresentante del Provider: " + providerId);

		Persona persona = personaService.getPersonaByRuolo(Ruolo.LEGALE_RAPPRESENTANTE, providerId);
		if(persona != null && persona.getAnagrafica() != null){
			return persona.getAnagrafica().getEmail();
		}

		LOGGER.debug("Legale Rappresentante non presente per il Provider: " + providerId);
		return "";
	}

	@Override
	public String getEmailDelegatoLegaleRappresentante(Long providerId) {
		LOGGER.debug("Recupero Email Delegato Legale Rappresentante del Provider: " + providerId);

		Persona persona = personaService.getPersonaByRuolo(Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, providerId);
		if(persona != null && persona.getAnagrafica() != null){
			return persona.getAnagrafica().getEmail();
		}

		LOGGER.debug("Delegato Legale Rappresentante non presente per il Provider: " + providerId);
		return "";
	}

	/*
	 * Per la ricerca dei provider in realta si fanno ricerche anche su accreditamenti, sede, quotaAnnuale
	 *
	 * Alla fine si individuano gli id dei provider e si restituiscono SOLO quelli in comune a tutte le ricerche
	 *
	 * */
	@Override
	public List<Provider> cerca(RicercaProviderWrapper wrapper) throws Exception {
		String query = "";
		HashMap<String, Object> params = new HashMap<String, Object>();
		query ="SELECT p FROM Provider p";

		/* INFO RELATIVE AI PROVIDER */
		//PROVIDER ID
		if(wrapper.getCampoIdProvider() != null){
			query = Utils.QUERY_AND(query, "p.id = :providerId");
			params.put("providerId", wrapper.getCampoIdProvider());
		}

		//DENOMINAZIONE LEGALE
		if(wrapper.getDenominazioneLegale() != null && !wrapper.getDenominazioneLegale().isEmpty()){
			query = Utils.QUERY_AND(query, "UPPER(p.denominazioneLegale) LIKE :denominazioneLegale");
			params.put("denominazioneLegale", "%" + wrapper.getDenominazioneLegale().toUpperCase() + "%");
		}

		//TIPO ORGANIZZATORE
		if(wrapper.getTipoOrganizzatoreSelezionati() != null && !wrapper.getTipoOrganizzatoreSelezionati().isEmpty()){
			query = Utils.QUERY_AND(query, "p.tipoOrganizzatore IN :tipoOrganizzatoreSelezionati");
			params.put("tipoOrganizzatoreSelezionati", wrapper.getTipoOrganizzatoreSelezionati());
		}

		//STATO DEL PROVIDER
		if(wrapper.getStatoProvider() != null && !wrapper.getStatoProvider().isEmpty()){
			query = Utils.QUERY_AND(query, "p.status IN :statiProviderSelezionati");
			params.put("statiProviderSelezionati", wrapper.getStatoProvider());
		}

		/* INFO RELATIVE ALL'ACCREDITAMENTO */
		String query_accreditamento ="";
		HashMap<String, Object> query_accreditamento_params = new HashMap<String, Object>();

		if( (wrapper.getProceduraFormativaSelezionate() != null && !wrapper.getProceduraFormativaSelezionate().isEmpty()) ||
			(wrapper.getAccreditamentoTipoSelezionati() != null && !wrapper.getAccreditamentoTipoSelezionati().isEmpty()) ||
			(wrapper.getAccreditamentoStatoSelezionati() != null && !wrapper.getAccreditamentoStatoSelezionati().isEmpty()) ||
			(wrapper.getDataFineAccreditamentoStart() != null) ||
			(wrapper.getDataFineAccreditamentoEnd() != null)	)
		{
			query_accreditamento = "SELECT a.provider FROM Accreditamento a JOIN a.datiAccreditamento d JOIN d.procedureFormative pF";

			//PROCEDURA FORMATIVA
			if(wrapper.getProceduraFormativaSelezionate() != null && !wrapper.getProceduraFormativaSelezionate().isEmpty()){
				query_accreditamento = Utils.QUERY_AND(query_accreditamento, "pF IN (:procedureFormativeSelezionate)");
				//query_accreditamento = Utils.QUERY_AND(query_accreditamento, "d.id IN (SELECT dati.id FROM DatiAccreditamento dati JOIN dati.procedureFormative pF WHERE pF IN :procedureFormativeSelezionate)");
				query_accreditamento_params.put("procedureFormativeSelezionate", wrapper.getProceduraFormativaSelezionate());
			}

			//TIPO ACCREDITAMENTO
			if(wrapper.getAccreditamentoTipoSelezionati() != null && !wrapper.getAccreditamentoTipoSelezionati().isEmpty()){
				query_accreditamento = Utils.QUERY_AND(query_accreditamento, "a.tipoDomanda IN :accreditamentoTipoSelezionati");
				query_accreditamento_params.put("accreditamentoTipoSelezionati", wrapper.getAccreditamentoTipoSelezionati());
			}

			//STATO ACCREDITAMENTO
			if(wrapper.getAccreditamentoStatoSelezionati() != null && !wrapper.getAccreditamentoStatoSelezionati().isEmpty()){
				query_accreditamento = Utils.QUERY_AND(query_accreditamento, "a.stato IN :accreditamentoStatoSelezionati");
				query_accreditamento_params.put("accreditamentoStatoSelezionati", wrapper.getAccreditamentoStatoSelezionati());
			}

			//DATA ACCREDITAMENTO
			if(wrapper.getDataFineAccreditamentoStart() != null){
				query_accreditamento = Utils.QUERY_AND(query_accreditamento, "a.dataFineAccreditamento >= :dataFineAccreditamentoStart");
				query_accreditamento_params.put("dataFineAccreditamentoStart", wrapper.getDataFineAccreditamentoStart());
			}

			if(wrapper.getDataFineAccreditamentoEnd() != null){
				query_accreditamento = Utils.QUERY_AND(query_accreditamento, "a.dataFineAccreditamento <= :dataFineAccreditamentoEnd");
				query_accreditamento_params.put("dataFineAccreditamentoEnd", wrapper.getDataFineAccreditamentoEnd());
			}
		}

		/* INFO RELATIVE ALLE SEDI */
		String query_sede ="";
		HashMap<String, Object> query_sede_params = new HashMap<String, Object>();

		if(wrapper.getProvinciaSelezionate() != null && !wrapper.getProvinciaSelezionate().isEmpty()){
			query_sede = "SELECT s.provider from Sede s JOIN s.provider WHERE s.sedeLegale = true";
			query_sede = Utils.QUERY_AND(query_sede,"s.provincia IN :provinciaSelezionate");
			query_sede_params.put("provinciaSelezionate", wrapper.getProvinciaSelezionate());
		}

		/* INFO RELATIVE AL PAGAMENTO */
		String query_quota_annuale ="";
		HashMap<String, Object> query_quota_annuale_params = new HashMap<String, Object>();

		if(wrapper.getPagato() != null){
			query_quota_annuale = "SELECT p FROM QuotaAnnuale q JOIN q.provider p";
			query_quota_annuale = Utils.QUERY_AND(query_quota_annuale, "q.pagato = :pagato");
			query_quota_annuale_params.put("pagato",wrapper.getPagato().booleanValue());
		}


		LOGGER.info(Utils.getLogMessage("Cerca Provider: " + query));
		List<Provider> result = executeQuery(query, params, Provider.class);

		List<Provider> resultFromAccreditamento = new ArrayList<Provider>();
		List<Provider> resultFromSede = new ArrayList<Provider>();
		List<Provider> resultFromQuotaAnnuale = new ArrayList<Provider>();

		List<Provider> resultFromAccreditamentoDaScartare = new ArrayList<Provider>();

		if(!query_accreditamento.isEmpty()){
			LOGGER.info(Utils.getLogMessage("Cerca Provider: " + query_accreditamento));
			resultFromAccreditamento = executeQuery(query_accreditamento, query_accreditamento_params, Provider.class);

			//se viene selezionato solo accreditamento provvisorio fitro per quelli ke hanno SOLO il provvisorio
			if(wrapper.getAccreditamentoTipoSelezionati() != null && wrapper.getAccreditamentoTipoSelezionati().size() == 1 && wrapper.getAccreditamentoTipoSelezionati().contains(AccreditamentoTipoEnum.PROVVISORIO)){
				String query_accreditamento_da_scartare = "";
				HashMap<String, Object> query_accreditamento_params_da_scartare = new HashMap<String, Object>();

				query_accreditamento_da_scartare = "SELECT a.provider FROM Accreditamento a JOIN a.datiAccreditamento d JOIN d.procedureFormative pF";
				query_accreditamento_da_scartare = Utils.QUERY_AND(query_accreditamento_da_scartare, "a.tipoDomanda IN :accreditamentoTipoSelezionati");
				query_accreditamento_params_da_scartare.put("accreditamentoTipoSelezionati", AccreditamentoTipoEnum.STANDARD);
				resultFromAccreditamentoDaScartare = executeQuery(query_accreditamento_da_scartare, query_accreditamento_params_da_scartare, Provider.class);
				resultFromAccreditamento.removeAll(resultFromAccreditamentoDaScartare);
			}
		}

		if(!query_sede.isEmpty()){
			LOGGER.info(Utils.getLogMessage("Cerca Provider: " + query_sede));
			resultFromSede = executeQuery(query_sede, query_sede_params, Provider.class);
		}

		if(!query_quota_annuale.isEmpty()){
			LOGGER.info(Utils.getLogMessage("Cerca Provider: " + query_quota_annuale));
			resultFromQuotaAnnuale = executeQuery(query_quota_annuale, query_quota_annuale_params, Provider.class);
		}

		if(!query_accreditamento.isEmpty())
			result.retainAll(resultFromAccreditamento);

		if(!query_sede.isEmpty())
			result.retainAll(resultFromSede);

		if(!query_quota_annuale.isEmpty())
			result.retainAll(resultFromQuotaAnnuale);

		return result;
	}

	private <T extends BaseEntity> List<T> executeQuery(String query, HashMap<String,Object> params, Class<T> className){
		List<T> result = new ArrayList<T>();

		Query q = entityManager.createQuery(query, className);
		Iterator<Entry<String, Object>> iterator = params.entrySet().iterator();
		while(iterator.hasNext()){
			Map.Entry<String, Object> pairs = iterator.next();
			q.setParameter(pairs.getKey(), pairs.getValue());
			LOGGER.info(Utils.getLogMessage(pairs.getKey() + ": " + pairs.getValue()));
		}

		System.out.println(q.getMaxResults());

		return q.getResultList();
	}

	@Override
	@Transactional
	public void abilitaCanInsertAccreditamentoStandard(Long providerId, LocalDate dataFine) throws Exception{
		LOGGER.info("Abilitazione insertAccreditamentoStandard per Provider: " + providerId);

		Provider provider = getProvider(providerId);
		if(provider == null){
			throw new Exception("Provider non trovato");
		}

		provider.setCanInsertAccreditamentoStandard(true);
		provider.setDataScadenzaInsertAccreditamentoStandard(dataFine);

		save(provider);

		alertEmailService.creaAlertInvioDomandaStandardForProvider(provider);
	}

	@Override
	public void disabilitaCanInsertAccreditamentoStandard(Long providerId) throws Exception {
		LOGGER.info("Disabilitazione insertAccreditamentoStandard per Provider: " + providerId);

		Provider provider = getProvider(providerId);
		if(provider == null){
			throw new Exception("Provider non trovato");
		}

		provider.setCanInsertAccreditamentoStandard(false);
		provider.setDataScadenzaInsertAccreditamentoStandard(LocalDate.now());

		save(provider);
	}

	@Override
	public void updateImpostazioni(Long providerId, ImpostazioniProviderWrapper wrapper) throws Exception {
		LOGGER.info("Update delle impostazioni per il Provider: " + providerId);

		Provider provider = getProvider(providerId);
		if(provider == null){
			throw new Exception("Provider non trovato");
		}

		boolean sendAlertInvioDomandaStandard = false;

		//flag permessi
		provider.setCanInsertPianoFormativo(wrapper.getCanInsertPianoFormativo());
		provider.setCanInsertEvento(wrapper.getCanInsertEventi());

		if(provider.canInsertAccreditamentoStandard() == false && wrapper.getCanInsertDomandaStandard() == true){
			//abilito il provider all'inserimento della domanda standard
			//invio notifica email + setto flag per controllare che alla scadenza l'ha inviata
			sendAlertInvioDomandaStandard = true;
			provider.setInviatoAccreditamentoStandard(false);
		}else if(provider.canInsertAccreditamentoStandard() == true && wrapper.getCanInsertDomandaStandard() == false){
			//sto disabilitando il provider all'invio della domanda standard
			//reset del flag per individuarlo come domanda non inviata alla scadenza
			provider.setInviatoAccreditamentoStandard(null);
		}

		provider.setCanInsertAccreditamentoStandard(wrapper.getCanInsertDomandaStandard());
		provider.setCanInsertAccreditamentoProvvisorio(wrapper.getCanInsertDomandaProvvisoria());
		provider.setCanInsertRelazioneAnnuale(wrapper.getCanInsertRelazioneAnnuale());
		provider.setMyPay(wrapper.getCanMyPay());
		//date scadenza permessi
		if(provider.isCanInsertPianoFormativo()) {
			if(provider.getDataScadenzaInsertPianoFormativo() != null &&
					!provider.getDataScadenzaInsertPianoFormativo().isEqual(wrapper.getDataScadenzaInsertPianoFormativo())) {
				reportRitardiService.createReport(
						MotivazioneProrogaEnum.INSERIMENTO_PIANO_FORMATIVO,
						null,
						provider.getDataScadenzaInsertPianoFormativo(),
						wrapper.getDataScadenzaInsertPianoFormativo(),
						LocalDate.now(),
						!pianoFormativoService.exist(providerId, LocalDate.now().getYear()),
						provider.getId());
			}
			provider.setDataScadenzaInsertPianoFormativo(wrapper.getDataScadenzaInsertPianoFormativo());
		}
		if(provider.isCanInsertAccreditamentoStandard()) {
			if(provider.getDataScadenzaInsertAccreditamentoStandard() != null &&
					!provider.getDataScadenzaInsertAccreditamentoStandard().isEqual(wrapper.getDataScadenzaInsertDomandaStandard())) {
				reportRitardiService.createReport(
						MotivazioneProrogaEnum.INSERIMENTO_DOMANDA_STANDARD,
						null,
						provider.getDataScadenzaInsertAccreditamentoStandard(),
						wrapper.getDataScadenzaInsertDomandaStandard(),
						LocalDate.now(),
						(provider.getInviatoAccreditamentoStandard() == null || !provider.getInviatoAccreditamentoStandard().booleanValue()),
						provider.getId());
			}
			provider.setDataScadenzaInsertAccreditamentoStandard(wrapper.getDataScadenzaInsertDomandaStandard());
		}
		//qua il report non serve
		if(provider.isCanInsertAccreditamentoProvvisorio())
			provider.setDataRinnovoInsertAccreditamentoProvvisorio(wrapper.getDataRinnovoInsertDomandaProvvisoria());
		if(provider.isCanInsertRelazioneAnnuale()) {
			if(provider.getDataScadenzaInsertRelazioneAnnuale() != null &&
					!provider.getDataScadenzaInsertRelazioneAnnuale().isEqual(wrapper.getDataScadenzaInsertRelazioneAnnuale())) {
				reportRitardiService.createReport(
						MotivazioneProrogaEnum.INSERIMENTO_RELAZIONE_ANNUALE,
						null,
						provider.getDataScadenzaInsertRelazioneAnnuale(),
						wrapper.getDataScadenzaInsertRelazioneAnnuale(),
						LocalDate.now(),
						!relazioneAnnualeService.isRelazioneAnnualeInseritaAnnoCorrente(providerId),
						provider.getId());
			}
			provider.setDataScadenzaInsertRelazioneAnnuale(wrapper.getDataScadenzaInsertRelazioneAnnuale());
		}
		//status provider
		provider.setStatus(wrapper.getStato());

		save(provider);

		if(sendAlertInvioDomandaStandard){
			alertEmailService.creaAlertInvioDomandaStandardForProvider(provider);

		}
	}

	/* Metodo chiamato dal thread per modificare la data di permesso di inserimento
	 * del piano formativo.
	 * Prima di ogni cosa calcola la data di default di inserimento del piano formativo, giorno e mese sono settate
	 * nella property, mentre l'anno è l'anno corrente.
	 * Se la data di scadenza di insermento del piano formativo dei provider accreditati è null,
	 * è diversa dalla data di default ed è trascorsa, viene risettata la data di default.
	 * precendentemente calcolata.
	 * Ciò garantisce che al passare dell'anno ogni data (passata) venga aggiornata.
	 * */
	@Override
	public void eseguiUpdateDataPianoFormativo() {

		int currentYear = LocalDate.now().getYear();
		LocalDate defaultDate = LocalDate.of(currentYear, ecmProperties.getPianoFormativoMeseFineModifica(), ecmProperties.getPianoFormativoGiornoFineModifica());
		Set<ProviderStatoEnum> statiProvider = new HashSet<ProviderStatoEnum>(Arrays.asList(ProviderStatoEnum.ACCREDITATO_PROVVISORIAMENTE, ProviderStatoEnum.ACCREDITATO_STANDARD));
		Set<Provider> providersUpdate = providerRepository.findAllProviderToUpdateDataPianoFormativo(statiProvider, defaultDate, LocalDate.now());
		for(Provider p : providersUpdate) {
			LOGGER.info("Update data inserimento piano formativo per il Provider " + p.getId());
			p.setDataScadenzaInsertPianoFormativo(defaultDate);
			save(p);
		}
	}

	@Override
	public void eseguiUpdateDataDomandaStandard() {

		Set<ProviderStatoEnum> statiProvider = new HashSet<ProviderStatoEnum>(Arrays.asList(ProviderStatoEnum.ACCREDITATO_PROVVISORIAMENTE, ProviderStatoEnum.ACCREDITATO_STANDARD));
		Set<Provider> providersUpdate = providerRepository.findAllProviderToUpdateDataDomandaStandard(statiProvider, LocalDate.now());
		for(Provider p : providersUpdate) {
			LOGGER.info("Reset data inserimento domanda standard per il Provider " + p.getId());
			p.setDataScadenzaInsertAccreditamentoStandard(null);
			p.setCanInsertAccreditamentoStandard(false);
			save(p);
		}

	}

	@Override
	public void eseguiUpdateDataRelazioneAnnuale() {

		int currentYear = LocalDate.now().getYear();
		LocalDate defaultDate = LocalDate.of(currentYear, ecmProperties.getRelazioneAnnualeMeseFineModifica(), ecmProperties.getRelazioneAnnualeGiornoFineModifica());
		Set<ProviderStatoEnum> statiProvider = new HashSet<ProviderStatoEnum>(Arrays.asList(ProviderStatoEnum.ACCREDITATO_PROVVISORIAMENTE, ProviderStatoEnum.ACCREDITATO_STANDARD));
		Set<Provider> providersUpdate = providerRepository.findAllProviderToUpdateDataRelazioneAnnuale(statiProvider, defaultDate, LocalDate.now());
		for(Provider p : providersUpdate) {
			LOGGER.info("Update data inserimento relazione annuale per il Provider " + p.getId());
			p.setDataScadenzaInsertRelazioneAnnuale(defaultDate);
			save(p);
		}
	}

	@Override
	public int countAllProviderInadempienti() {
		LOGGER.info("Conteggio dei provider che non hanno completato da domanda di accreditamento in tempo");
		//query sui provider se hanno data scadenza can insert domanda standard ed è scaduta sono inadempienti
		return providerRepository.countAllProviderInadempienti();
	}

	@Override
	public Set<Provider> getAllProviderInadempienti() {
		LOGGER.info("Recupero i provider che non hanno completato da domanda di accreditamento in tempo");
		//query sui provider se hanno data scadenza can insert domanda standard ed è scaduta sono inadempienti
		return providerRepository.getAllProviderInadempienti();
	}


	@Override
	public void bloccaProvider(Long providerId, ImpostazioniProviderWrapper wrapper) throws Exception {

		File allegatoDecadenza = fileService.getFile(wrapper.getAllegatoDecadenza().getId());
		if(allegatoDecadenza == null || allegatoDecadenza.isNew()) {
			throw new Exception("File da protocollare per blocco Provider non valido!");
		}

		allegatoDecadenza.setOperatoreProtocollo(Utils.getAuthenticatedUser().getAccount());
		fileService.save(allegatoDecadenza);

		//allega il file di decadenza
		Accreditamento accreditamento = accreditamentoService.getLastAccreditamentoForProviderId(providerId);
		accreditamento.setFileDecadenza(allegatoDecadenza);
		accreditamentoService.save(accreditamento);

		protocolloService.protocollaBloccoProviderInUscita(providerId, allegatoDecadenza, wrapper.getMotivazioneDecadenza());
	}

	@Override
	public String controllaComitato(Set<Persona> componentiComitatoScientifico, boolean fromValutazioneSegreteria) {
		if(componentiComitatoScientifico.size() < 5)
			return fromValutazioneSegreteria ? "error.numero_minimo_comitato_integrazione" : "error.numero_minimo_comitato";
		int counterCoordinatori = 0;
		int counterProfessioniSanitarie = 0;
		for(Persona p : componentiComitatoScientifico) {
			if(p.isCoordinatoreComitatoScientifico()) {
				counterCoordinatori++;
			}
			if(p.getProfessione().isSanitaria())
				counterProfessioniSanitarie++;
		}
		if(counterCoordinatori != 1)
			return fromValutazioneSegreteria ? "error.numero_coordinatore_integrazione" : "error.numero_coordinatore";
		if(counterProfessioniSanitarie < 5) {
			return fromValutazioneSegreteria ? "error.numero_minimo_professionisti_sanitari_integrazione" : "error.numero_minimo_professionisti_sanitari";
		}
		return null;
	}

	@Override
	public String controllaSedi(Set<Sede> sedi) {
		int counterSediLegali = 0;
		for(Sede s : sedi) {
			if(s.isSedeLegale())
				counterSediLegali++;
		}
		if(counterSediLegali != 1)
			return "error.sede_legale_integrazione";
		return null;
	}
}
