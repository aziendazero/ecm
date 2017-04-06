package it.tredi.ecm.service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.AccreditamentoDiff;
import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamentoDiff;
import it.tredi.ecm.dao.entity.DatiEconomici;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.PersonaDiff;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.ProviderDiff;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.entity.SedeDiff;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.repository.AccreditamentoDiffRepository;
import it.tredi.ecm.dao.repository.DatiAccreditamentoDiffRepository;
import it.tredi.ecm.dao.repository.PersonaDiffRepository;
import it.tredi.ecm.dao.repository.ProviderDiffRepository;
import it.tredi.ecm.dao.repository.SedeDiffRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class DiffServiceImpl implements DiffService {
	private static Logger LOGGER = LoggerFactory.getLogger(DiffServiceImpl.class);

	@Autowired private AccreditamentoDiffRepository accreditamentoDiffRepository;
	@Autowired private ProviderDiffRepository providerDiffRepository;
	@Autowired private DatiAccreditamentoDiffRepository datiAccreditamentoDiffRepository;
	@Autowired private PersonaDiffRepository personaDiffRepository;
	@Autowired private SedeDiffRepository sedeDiffRepository;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private FieldValutazioneAccreditamentoService fieldValutazioneAccreditamentoService;

	//genera e salva una "snapshot" dell'accreditamento, utilizzata per eseguire il diff all'invio di una nuova domanda
	@Override
	public AccreditamentoDiff creaAllDiffAccreditamento(Accreditamento ultimoAccreditamento) {
		LOGGER.debug(Utils.getLogMessage("Creazione SNAPSHOT accreditamento:" + ultimoAccreditamento.getId()));

		Provider ultimoProvider = ultimoAccreditamento.getProvider();

		AccreditamentoDiff diff = new AccreditamentoDiff();

		//data creazione
		diff.setDataCreazione(LocalDateTime.now());

		//accreditamentoId
		diff.setAccreditamentoIdRiferimento(ultimoAccreditamento.getId());

		//providerId
		diff.setProviderIdRiferimento(ultimoProvider.getId());

		//files
		Set<File> files = ultimoAccreditamento.getDatiAccreditamento().getFiles();
		for(File file : files){
			if(file.isATTOCOSTITUTIVO())
				diff.setFileAttoCostitutivo(file.getId());
			else if(file.isESPERIENZAFORMAZIONE())
				diff.setFileEsperienzaFormazione(file.getId());
			else if(file.isDICHIARAZIONELEGALE())
				diff.setFileDichiarazioneLegaleRappresentante(file.getId());
			else if(file.isPIANOQUALITA())
				diff.setFilePianoQualita(file.getId());
			else if(file.isUTILIZZO())
				diff.setFileUtilizzoSedi(file.getId());
			else if(file.isSISTEMAINFORMATICO())
				diff.setFileSistemaInformatico(file.getId());
			else if(file.isDICHIARAZIONEESCLUSIONE())
				diff.setFileDichiarazioneEsclusione(file.getId());
			else if(file.isRICHIESTAACCREDITAMENTOSTANDARD())
				diff.setFileRichiestaAccreditamentoStandard(file.getId());
			else if(file.isRELAZIONEATTIVITAFORMATIVA())
				diff.setFileRelazioneAttivitaFormativa(file.getId());
		}

		//provider
		diff.setProvider(creaDiffProvider(ultimoProvider));

		//legale rappresentante
		diff.setLegaleRappresentante(creaDiffPersona(ultimoProvider.getPersonaByRuolo(Ruolo.LEGALE_RAPPRESENTANTE)));

		//delegato rappresentante
		diff.setDelegatoLegaleRappresentante(creaDiffPersona(ultimoProvider.getPersonaByRuolo(Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE)));

		//sedi
		Set<SedeDiff> sediDiff = new HashSet<SedeDiff>();
		for(Sede sede : ultimoProvider.getSedi()) {
			sediDiff.add(creaDiffSede(sede));
		}
		diff.setSedi(sediDiff);

		//dati accreditamento
		diff.setDatiAccreditamento(creaDiffDatiAccreditamento(ultimoAccreditamento.getDatiAccreditamento()));

		//responsabili
		diff.setResponsabileSegreteria(creaDiffPersona(ultimoProvider.getPersonaByRuolo(Ruolo.RESPONSABILE_SEGRETERIA)));
		diff.setResponsabileAmministrativo(creaDiffPersona(ultimoProvider.getPersonaByRuolo(Ruolo.RESPONSABILE_AMMINISTRATIVO)));
		diff.setResponsabileSistemaInformatico(creaDiffPersona(ultimoProvider.getPersonaByRuolo(Ruolo.RESPONSABILE_SISTEMA_INFORMATICO)));
		diff.setResponsabileQualita(creaDiffPersona(ultimoProvider.getPersonaByRuolo(Ruolo.RESPONSABILE_QUALITA)));

		//comitato scientifico
		Set<PersonaDiff> comitatoDiff = new HashSet<PersonaDiff>();
		for(Persona persona : ultimoProvider.getComponentiComitatoScientifico()) {
			comitatoDiff.add(creaDiffPersona(persona));
		}
		diff.setComponentiComitatoScientifico(comitatoDiff);

		accreditamentoDiffRepository.save(diff);

		return diff;
	}

	@Override
	public ProviderDiff creaDiffProvider(Provider provider) {
		LOGGER.debug(Utils.getLogMessage("Creazione SNAPSHOT provider:" + provider.getId()));

		ProviderDiff diff = new ProviderDiff();

		diff.setTipoOrganizzatore(provider.getTipoOrganizzatore());
		diff.setDenominazioneLegale(provider.getDenominazioneLegale());
		diff.setHasPartitaIVA(provider.isHasPartitaIVA());
		diff.setPartitaIva(provider.getPartitaIva());
		diff.setCodiceFiscale(provider.getCodiceFiscale());
		diff.setRagioneSociale(provider.getRagioneSociale());
		diff.setEmailStruttura(provider.getEmailStruttura());
		diff.setNaturaOrganizzazione(provider.getNaturaOrganizzazione());
		diff.setNoProfit(provider.isNoProfit());

		providerDiffRepository.save(diff);

		return diff;
	}

	@Override
	public SedeDiff creaDiffSede(Sede sede) {
		LOGGER.debug(Utils.getLogMessage("Creazione SNAPSHOT sede:" + sede.getId()));

		SedeDiff diff = new SedeDiff();

		diff.setSedeId(sede.getId());
		diff.setProvincia(sede.getProvincia());
		diff.setComune(sede.getComune());
		diff.setIndirizzo(sede.getIndirizzo());
		diff.setCap(sede.getCap());
		diff.setTelefono(sede.getTelefono());
		diff.setAltroTelefono(sede.getAltroTelefono());
		diff.setFax(sede.getFax());
		diff.setEmail(sede.getEmail());
		diff.setSedeLegale(sede.isSedeLegale());
		diff.setSedeOperativa(sede.isSedeOperativa());

		sedeDiffRepository.save(diff);

		return diff;
	}

	@Override
	public PersonaDiff creaDiffPersona(Persona persona) {
		if(persona == null)
			return null;

		LOGGER.debug(Utils.getLogMessage("Creazione SNAPSHOT persona:" + persona.getId()));

		Anagrafica anagrafica = persona.getAnagrafica();

		PersonaDiff diff = new PersonaDiff();

		diff.setPersonaId(persona.getId());
		diff.setAnagraficaId(anagrafica.getId());
		diff.setCognome(anagrafica.getCognome());
		diff.setNome(anagrafica.getNome());
		diff.setCodiceFiscale(anagrafica.getCodiceFiscale());
		diff.setStraniero(anagrafica.isStraniero());
		diff.setTelefono(anagrafica.getTelefono());
		diff.setCellulare(anagrafica.getCellulare());
		diff.setEmail(anagrafica.getEmail());
		diff.setPec(anagrafica.getPec());
		diff.setProfessione(persona.getProfessione());
		diff.setCoordinatoreComitatoScientifico(persona.isCoordinatoreComitatoScientifico());

		//files
		Set<File> files = persona.getFiles();
		for(File file : files) {
			if(file.isATTONOMINA())
				diff.setFileAttoDiNomina(file.getId());
			else if(file.isCV())
				diff.setFileCurriculumVitae(file.getId());
			else if(file.isDELEGA())
				diff.setFileDelega(file.getId());
		}

		personaDiffRepository.save(diff);

		return diff;
	}

	@Override
	public DatiAccreditamentoDiff creaDiffDatiAccreditamento(DatiAccreditamento datiAccreditamento) {
		LOGGER.debug(Utils.getLogMessage("Creazione SNAPSHOT datiAccreditamento:" + datiAccreditamento.getId()));

		DatiEconomici datiEconomici = datiAccreditamento.getDatiEconomici();

		DatiAccreditamentoDiff diff = new DatiAccreditamentoDiff();

		diff.setTipologiaAccreditamento(datiAccreditamento.getTipologiaAccreditamento());

		//procedure formative
		Set<ProceduraFormativa> procedureFormativeList = new HashSet<ProceduraFormativa>();
		procedureFormativeList.addAll(Arrays.asList(datiAccreditamento.getProcedureFormative().toArray(new ProceduraFormativa[datiAccreditamento.getProcedureFormative().size()])));
		diff.setProcedureFormative(procedureFormativeList);

		diff.setAccreditamentoPerProfessioni(datiAccreditamento.getProfessioniAccreditamento());

		//discipline
		Set<Disciplina> disciplineList = new HashSet<Disciplina>();
		disciplineList.addAll(Arrays.asList(datiAccreditamento.getDiscipline().toArray(new Disciplina[datiAccreditamento.getDiscipline().size()])));
		diff.setDiscipline(disciplineList);

		diff.setFatturatoComplessivoValoreUno(datiEconomici.getFatturatoComplessivoValoreUno());
		diff.setFatturatoComplessivoValoreDue(datiEconomici.getFatturatoComplessivoValoreDue());
		diff.setFatturatoComplessivoValoreTre(datiEconomici.getFatturatoComplessivoValoreTre());
		diff.setFatturatoFormazioneValoreUno(datiEconomici.getFatturatoFormazioneValoreUno());
		diff.setFatturatoFormazioneValoreDue(datiEconomici.getFatturatoFormazioneValoreDue());
		diff.setFatturatoFormazioneValoreTre(datiEconomici.getFatturatoFormazioneValoreTre());
		diff.setNumeroDipendentiFormazioneTempoIndeterminato(datiAccreditamento.getNumeroDipendentiFormazioneTempoIndeterminato());
		diff.setNumeroDipendentiFormazioneAltro(datiAccreditamento.getNumeroDipendentiFormazioneAltro());

		//files
		Set<File> files = datiAccreditamento.getFiles();
		for(File file : files) {
			if(file.isESTRATTOBILANCIOCOMPLESSIVO())
				diff.setFileEstrattoBilancioComplessivo(file.getId());
			else if(file.isESTRATTOBILANCIOFORMAZIONE())
				diff.setFileEstrattoBilancioFormazione(file.getId());
			else if(file.isORGANIGRAMMA())
				diff.setFileOrganigramma(file.getId());
			else if(file.isFUNZIONIGRAMMA())
				diff.setFileFunzionigramma(file.getId());
		}

		datiAccreditamentoDiffRepository.save(diff);

		return diff;
	}

	//confronta 2 diff accreditamento e genera la relativa valutazione segreteria a seconda del risultato
	@Override
	public Set<FieldValutazioneAccreditamento> confrontaDiffAccreditamento(AccreditamentoDiff diffOld, AccreditamentoDiff diffNew) {
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(diffNew.getAccreditamentoIdRiferimento());

		//creo i fieldValutazione dei campi non in comune tra i due diff
		Set<FieldValutazioneAccreditamento> fieldValutazioneModificati = new HashSet<FieldValutazioneAccreditamento>();

		LOGGER.debug(Utils.getLogMessage("Avvio procedura di confronto diff accreditamento. Old = " + diffOld.getId() + ", New = " + diffNew.getId()));

		//provider
		fieldValutazioneModificati.addAll(confrontaDiffProvider(diffOld.getProvider(), diffNew.getProvider(), accreditamento));

		//legale rappresentante
		fieldValutazioneModificati.addAll(confrontaDiffPersona(diffOld.getLegaleRappresentante(), diffNew.getLegaleRappresentante(), Ruolo.LEGALE_RAPPRESENTANTE, accreditamento));

		//delegato
		fieldValutazioneModificati.addAll(confrontaDiffPersona(diffOld.getDelegatoLegaleRappresentante(), diffNew.getDelegatoLegaleRappresentante(), Ruolo.DELEGATO_LEGALE_RAPPRESENTANTE, accreditamento));

		//sedi
		fieldValutazioneModificati.addAll(confrontaDiffAllSedi(diffOld.getSedi(), diffNew.getSedi(), accreditamento));

		//dati accreditamento
		fieldValutazioneModificati.addAll(confrontaDiffDatiAccreditamento(diffOld.getDatiAccreditamento(), diffNew.getDatiAccreditamento(), accreditamento));

		//responsabile segreteria
		fieldValutazioneModificati.addAll(confrontaDiffPersona(diffOld.getResponsabileSegreteria(), diffNew.getResponsabileSegreteria(), Ruolo.RESPONSABILE_SEGRETERIA, accreditamento));

		//responsabile amministrativo
		fieldValutazioneModificati.addAll(confrontaDiffPersona(diffOld.getResponsabileAmministrativo(), diffNew.getResponsabileAmministrativo(), Ruolo.RESPONSABILE_AMMINISTRATIVO, accreditamento));

		//responsabile sistema informatico
		fieldValutazioneModificati.addAll(confrontaDiffPersona(diffOld.getResponsabileSistemaInformatico(), diffNew.getResponsabileSistemaInformatico(), Ruolo.RESPONSABILE_SISTEMA_INFORMATICO, accreditamento));

		//responsabile qualità
		fieldValutazioneModificati.addAll(confrontaDiffPersona(diffOld.getResponsabileQualita(), diffNew.getResponsabileQualita(), Ruolo.RESPONSABILE_QUALITA, accreditamento));

		//comitato scientifico
		fieldValutazioneModificati.addAll(confrontaDiffAllComitato(diffOld.getComponentiComitatoScientifico(), diffNew.getComponentiComitatoScientifico(), accreditamento));

		//files
		fieldValutazioneModificati.addAll(confrontaDiffFiles(diffOld, diffNew,accreditamento));

		LOGGER.debug(Utils.getLogMessage("Procedura di confronto diff: SUCCESS"));

		return fieldValutazioneModificati;
	}

	//confronto dei due diff provider
	private Set<FieldValutazioneAccreditamento> confrontaDiffProvider(ProviderDiff providerOld, ProviderDiff providerNew, Accreditamento accreditamento) {
		LOGGER.debug(Utils.getLogMessage("Confronto provider"));

		Set<FieldValutazioneAccreditamento> fieldValutazioneModificati = new HashSet<FieldValutazioneAccreditamento>();

		//tipo organizzatore
		if(!Objects.equals(providerOld.getTipoOrganizzatore(), providerNew.getTipoOrganizzatore())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.PROVIDER__TIPO_ORGANIZZATORE);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//denominazione legale
		if(!Objects.equals(providerOld.getDenominazioneLegale(), providerNew.getDenominazioneLegale())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.PROVIDER__DENOMINAZIONE_LEGALE);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//partita iva
		if(providerOld.isHasPartitaIVA() != providerNew.isHasPartitaIVA()
				|| !Objects.equals(providerOld.getPartitaIva(), providerNew.getPartitaIva())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.PROVIDER__PARTITA_IVA);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//codice fiscale
		if(!Objects.equals(providerOld.getCodiceFiscale(), providerNew.getCodiceFiscale())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.PROVIDER__CODICE_FISCALE);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//ragione sociale
		if(!Objects.equals(providerOld.getRagioneSociale(), providerNew.getRagioneSociale())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.PROVIDER__RAGIONE_SOCIALE);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//email struttura
		if(!Objects.equals(providerOld.getEmailStruttura(), providerNew.getEmailStruttura())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.PROVIDER__EMAIL_STRUTTURA);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		if(!Objects.equals(providerOld.getNaturaOrganizzazione(), providerNew.getNaturaOrganizzazione())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.PROVIDER__NATURA_ORGANIZZAZIONE);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		if(providerOld.isNoProfit() != providerNew.isNoProfit()) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.PROVIDER__NO_PROFIT);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		return fieldValutazioneModificati;
	}

	//confronto dei due diff persona
	private Set<FieldValutazioneAccreditamento> confrontaDiffPersona(PersonaDiff personaOld, PersonaDiff personaNew, Ruolo ruolo, Accreditamento accreditamento) {
		LOGGER.debug(Utils.getLogMessage("Confronto persona con ruolo: " + ruolo.getNome()));

		Set<FieldValutazioneAccreditamento> fieldValutazioneModificati = new HashSet<FieldValutazioneAccreditamento>();

		//rimozione o caso in cui non ci sono i diff non gestiti
		if(personaNew == null)
			return fieldValutazioneModificati;

		Long objRef = ruolo == Ruolo.COMPONENTE_COMITATO_SCIENTIFICO ? personaNew.getPersonaId() : -1L;

		//check se stessa persona o se persona è stata aggiunta (vedi delegato legale rappresentante)
		if((personaOld == null) ||
				(personaOld.getPersonaId().longValue() != personaNew.getPersonaId().longValue())) {
			//la persona è cambiatata, aggiungo il full e basta
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(Utils.getFullFromRuolo(ruolo));
			field.setObjectReference(objRef);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}
		else if(personaOld.getAnagraficaId().longValue() != personaNew.getAnagraficaId().longValue()) {
			//alla persona è stata sostituita l'anagrafica, aggiungo il full e basta
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(Utils.getFullFromRuolo(ruolo));
			field.setObjectReference(objRef);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}
		else {
			//la persona non è cambiata, controllo campo per campo

			//cognome
			if(!Objects.equals(personaOld.getCognome(), personaNew.getCognome())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.getIdField("persona.anagrafica.cognome", ruolo));
				field.setObjectReference(objRef);
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//nome
			if(!Objects.equals(personaOld.getNome(), personaNew.getNome())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.getIdField("persona.anagrafica.nome", ruolo));
				field.setObjectReference(objRef);
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//codice fiscale
			if(!Objects.equals(personaOld.getCodiceFiscale(), personaNew.getCodiceFiscale())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.getIdField("persona.anagrafica.codiceFiscale", ruolo));
				field.setObjectReference(objRef);
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//telefono
			if(!Objects.equals(personaOld.getTelefono(), personaNew.getTelefono())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.getIdField("persona.anagrafica.telefono", ruolo));
				field.setObjectReference(objRef);
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//cellulare
			if(!Objects.equals(personaOld.getCellulare(), personaNew.getCellulare())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.getIdField("persona.anagrafica.cellulare", ruolo));
				field.setObjectReference(objRef);
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//email
			if(!Objects.equals(personaOld.getEmail(), personaNew.getEmail())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.getIdField("persona.anagrafica.email", ruolo));
				field.setObjectReference(objRef);
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//pec
			if(!Objects.equals(personaOld.getPec(), personaNew.getPec())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.getIdField("persona.anagrafica.pec", ruolo));
				field.setObjectReference(objRef);
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//professione
			if(!Objects.equals(personaOld.getProfessione(), personaNew.getProfessione())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.getIdField("persona.professione", ruolo));
				field.setObjectReference(objRef);
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//coordinatore comitato scientifico
			if(personaOld.isCoordinatoreComitatoScientifico() != personaNew.isCoordinatoreComitatoScientifico()) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.getIdField("persona.coordinatore", ruolo));
				field.setObjectReference(objRef);
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//file atto di nomina
			if(!Objects.equals(personaOld.getFileAttoDiNomina(), personaNew.getFileAttoDiNomina())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.getIdField("attoNomina", ruolo));
				field.setObjectReference(objRef);
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//file cv
			if(!Objects.equals(personaOld.getFileCurriculumVitae(), personaNew.getFileCurriculumVitae())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.getIdField("cv", ruolo));
				field.setObjectReference(objRef);
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//file delega
			if(!Objects.equals(personaOld.getFileDelega(), personaNew.getFileDelega())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.getIdField("delega", ruolo));
				field.setObjectReference(objRef);
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}
		}

		return fieldValutazioneModificati;
	}

	private Set<FieldValutazioneAccreditamento> confrontaDiffAllComitato(Set<PersonaDiff> componentiOld, Set<PersonaDiff> componentiNew,
			Accreditamento accreditamento) {
		LOGGER.debug(Utils.getLogMessage("Confronto comitato scientifico"));

		Set<FieldValutazioneAccreditamento> fieldValutazioneModificati = new HashSet<FieldValutazioneAccreditamento>();

		//la rimozione di un componente non viene gestita

		//salvo una mappa degli id delle persone nel set dei componenti old e i relativi personaDiffId
		Map<Long, PersonaDiff> componentiMapOld = new HashMap<Long, PersonaDiff>();
		for(PersonaDiff pOld : componentiOld) {
			componentiMapOld.put(pOld.getPersonaId(), pOld);
		}

		//controllo se i nuovi componenti sono presenti nel old.. se non ci sono aggiungo un full, altrimenti check campo per campo
		for(PersonaDiff pNew : componentiNew) {
			if(!componentiMapOld.containsKey(pNew.getPersonaId())) {
				//aggiungo il full e basta
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(Utils.getFullFromRuolo(Ruolo.COMPONENTE_COMITATO_SCIENTIFICO));
				field.setObjectReference(pNew.getPersonaId());
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}
			else {
				fieldValutazioneModificati.addAll(confrontaDiffPersona(componentiMapOld.get(pNew.getPersonaId()), pNew, Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, accreditamento));
			}
		}

		return fieldValutazioneModificati;
	}

	private Set<FieldValutazioneAccreditamento> confrontaDiffSede(SedeDiff sedeOld, SedeDiff sedeNew, Accreditamento accreditamento) {
		LOGGER.debug(Utils.getLogMessage("Confronto sede"));

		Set<FieldValutazioneAccreditamento> fieldValutazioneModificati = new HashSet<FieldValutazioneAccreditamento>();

		//check se stessa sede
		if((sedeOld == null) ||
				(sedeOld.getSedeId().longValue() != sedeNew.getSedeId().longValue())) {
			//la sede è cambiatata, aggiungo il full e basta
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.SEDE__FULL);
			field.setObjectReference(sedeNew.getSedeId());
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}
		else {
			//la sede non è cambiata, controllo campo per campo ( -.-)

			//provincia
			if(!Objects.equals(sedeOld.getProvincia(), sedeNew.getProvincia())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.SEDE__PROVINCIA);
				field.setObjectReference(sedeNew.getSedeId());
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//comune
			if(!Objects.equals(sedeOld.getComune(), sedeNew.getComune())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.SEDE__COMUNE);
				field.setObjectReference(sedeNew.getSedeId());
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//indirizzo
			if(!Objects.equals(sedeOld.getIndirizzo(), sedeNew.getIndirizzo())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.SEDE__INDIRIZZO);
				field.setObjectReference(sedeNew.getSedeId());
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//cap
			if(!Objects.equals(sedeOld.getCap(), sedeNew.getCap())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.SEDE__CAP);
				field.setObjectReference(sedeNew.getSedeId());
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//telefono
			if(!Objects.equals(sedeOld.getTelefono(), sedeNew.getTelefono())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.SEDE__TELEFONO);
				field.setObjectReference(sedeNew.getSedeId());
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//altroTelefono non esiste più?

			//fax
			if(!Objects.equals(sedeOld.getFax(), sedeNew.getFax())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.SEDE__FAX);
				field.setObjectReference(sedeNew.getSedeId());
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//email
			if(!Objects.equals(sedeOld.getEmail(), sedeNew.getEmail())) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.SEDE__EMAIL);
				field.setObjectReference(sedeNew.getSedeId());
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//sede legale
			if(sedeOld.isSedeLegale() != sedeNew.isSedeLegale()) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.SEDE__IS_LEGALE);
				field.setObjectReference(sedeNew.getSedeId());
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}

			//sede operativa
			if(sedeOld.isSedeOperativa() != sedeNew.isSedeOperativa()) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.SEDE__IS_OPERATIVA);
				field.setObjectReference(sedeNew.getSedeId());
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}
		}

		return fieldValutazioneModificati;
	}

	private Set<FieldValutazioneAccreditamento> confrontaDiffAllSedi(Set<SedeDiff> sediOld, Set<SedeDiff> sediNew, Accreditamento accreditamento) {
		LOGGER.debug(Utils.getLogMessage("Confronto sedi"));

		Set<FieldValutazioneAccreditamento> fieldValutazioneModificati = new HashSet<FieldValutazioneAccreditamento>();

		//la rimozione di una sede non viene gestita

		//salvo una mappa degli id delle sedi nel set delle sedi old e i relativi sedeDiffId
		Map<Long, SedeDiff> sediMapOld = new HashMap<Long, SedeDiff>();
		for(SedeDiff sOld : sediOld) {
			sediMapOld.put(sOld.getSedeId(), sOld);
		}

		//controllo se le nuove sedi sono presenti nel old.. se non ci sono aggiungo un full, altrimenti check campo per campo
		for(SedeDiff sNew : sediNew) {
			if(!sediMapOld.containsKey(sNew.getSedeId())) {
				//aggiungo il full e basta
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setIdField(IdFieldEnum.SEDE__FULL);
				field.setObjectReference(sNew.getSedeId());
				field.setModificatoInIntegrazione(true);
				fieldValutazioneAccreditamentoService.save(field);
				fieldValutazioneModificati.add(field);
			}
			else {
				fieldValutazioneModificati.addAll(confrontaDiffSede(sediMapOld.get(sNew.getSedeId()), sNew, accreditamento));
			}
		}

		return fieldValutazioneModificati;
	}

	private Set<FieldValutazioneAccreditamento> confrontaDiffDatiAccreditamento(DatiAccreditamentoDiff datiOld, DatiAccreditamentoDiff datiNew,
			Accreditamento accreditamento) {
		LOGGER.debug(Utils.getLogMessage("Confronto dati di accreditamento"));

		Set<FieldValutazioneAccreditamento> fieldValutazioneModificati = new HashSet<FieldValutazioneAccreditamento>();

		//generale/settoriale tipologia procedure formative
		if(!Objects.equals(datiOld.getTipologiaAccreditamento(), datiNew.getTipologiaAccreditamento())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__TIPOLOGIA_ACCREDITAMENTO);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//procedure formative
		if(!Objects.equals(datiOld.getProcedureFormative(), datiNew.getProcedureFormative())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__PROCEDURE_FORMATIVE);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//generale/settoriale professioni/discipline
		if(!Objects.equals(datiOld.getAccreditamentoPerProfessioni(), datiNew.getAccreditamentoPerProfessioni())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__PROFESSIONI_ACCREDITAMENTO);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//discipline
		if(!Objects.equals(datiOld.getDiscipline(), datiNew.getDiscipline())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__PROCEDURE_FORMATIVE);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//fatturato complessivo valore uno
		if(!Objects.equals(datiOld.getFatturatoComplessivoValoreUno(), datiNew.getFatturatoComplessivoValoreUno())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_UNO);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//fatturato complessivo valore due
		if(!Objects.equals(datiOld.getFatturatoComplessivoValoreDue(), datiNew.getFatturatoComplessivoValoreDue())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_DUE);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//fatturato complessivo valore tre
		if(!Objects.equals(datiOld.getFatturatoComplessivoValoreTre(), datiNew.getFatturatoComplessivoValoreTre())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_COMPLESSIVO_TRE);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//fatturato formazione valore uno
		if(!Objects.equals(datiOld.getFatturatoFormazioneValoreUno(), datiNew.getFatturatoFormazioneValoreUno())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_UNO);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//fatturato formazione valore due
		if(!Objects.equals(datiOld.getFatturatoFormazioneValoreDue(), datiNew.getFatturatoFormazioneValoreDue())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_DUE);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//fatturato formazione valore tre
		if(!Objects.equals(datiOld.getFatturatoFormazioneValoreTre(), datiNew.getFatturatoFormazioneValoreTre())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__FATTURATO_FORMAZIONE_TRE);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//file estratto bilancio complessivo
		if(!Objects.equals(datiOld.getFileEstrattoBilancioComplessivo(), datiNew.getFileEstrattoBilancioComplessivo())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_COMPLESSIVO);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//file estratto bilancio formazione
		if(!Objects.equals(datiOld.getFileEstrattoBilancioFormazione(), datiNew.getFileEstrattoBilancioFormazione())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_FORMAZIONE);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//numero dipendenti formazione tempo indeterminato
		if(!Objects.equals(datiOld.getNumeroDipendentiFormazioneTempoIndeterminato(), datiNew.getNumeroDipendentiFormazioneTempoIndeterminato())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_TEMPO_INDETERMINATO);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//numero dipendenti formazione altro
		if(!Objects.equals(datiOld.getNumeroDipendentiFormazioneAltro(), datiNew.getNumeroDipendentiFormazioneAltro())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__NUMERO_DIPENDENTI_FORMAZIONE_ALTRO);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//file organigramma
		if(!Objects.equals(datiOld.getFileOrganigramma(), datiNew.getFileOrganigramma())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__ORGANIGRAMMA);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//file funzionigramma
		if(!Objects.equals(datiOld.getFileFunzionigramma(), datiNew.getFileFunzionigramma())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__FUNZIONIGRAMMA);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		return fieldValutazioneModificati;
	}

	private Set<FieldValutazioneAccreditamento> confrontaDiffFiles(AccreditamentoDiff diffOld, 	AccreditamentoDiff diffNew, Accreditamento accreditamento) {
		LOGGER.debug(Utils.getLogMessage("Confronto file accreditamento"));

		Set<FieldValutazioneAccreditamento> fieldValutazioneModificati = new HashSet<FieldValutazioneAccreditamento>();

		//file atto costitutivo
		if(!Objects.equals(diffOld.getFileAttoCostitutivo(), diffNew.getFileAttoCostitutivo())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ATTO_COSTITUIVO);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//file dichiarazione esclusione
		if(!Objects.equals(diffOld.getFileDichiarazioneEsclusione(), diffNew.getFileDichiarazioneEsclusione())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_ESCLUSIONE);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//file esperienza formazione
		if(!Objects.equals(diffOld.getFileEsperienzaFormazione(), diffNew.getFileEsperienzaFormazione())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ESPERIENZA_FORMAZIONE);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//file utilizzo sedi
		if(!Objects.equals(diffOld.getFileUtilizzoSedi(), diffNew.getFileUtilizzoSedi())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__UTILIZZO);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//file sistema informatico
		if(!Objects.equals(diffOld.getFileSistemaInformatico(), diffNew.getFileSistemaInformatico())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__SISTEMA_INFORMATICO);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//file piano qualità
		if(!Objects.equals(diffOld.getFilePianoQualita(), diffNew.getFilePianoQualita())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__PIANO_QUALITA);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//file dichiarazione legale rappresentante
		if(!Objects.equals(diffOld.getFileDichiarazioneLegaleRappresentante(), diffNew.getFileDichiarazioneLegaleRappresentante())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_LEGALE);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//file richiesta accreditamento standard
		if(!Objects.equals(diffOld.getFileRichiestaAccreditamentoStandard(), diffNew.getFileRichiestaAccreditamentoStandard())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RICHIESTA_ACCREDITAMENTO_STANDARD);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		//file relazione attività formativa
		if(!Objects.equals(diffOld.getFileRelazioneAttivitaFormativa(), diffNew.getFileRelazioneAttivitaFormativa())) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setIdField(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RELAZIONE_ATTIVITA_FORMATIVA);
			field.setModificatoInIntegrazione(true);
			fieldValutazioneAccreditamentoService.save(field);
			fieldValutazioneModificati.add(field);
		}

		return fieldValutazioneModificati;
	}

	@Override
	public AccreditamentoDiff findLastDiffByProviderId(Long providerId) {
		return accreditamentoDiffRepository.findFirstByProviderIdRiferimentoOrderByDataCreazioneDesc(providerId);
	}
}
