package it.tredi.ecm.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.Ruolo;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.repository.FieldValutazioneAccreditamentoRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class FieldValutazioneAccreditamentoServiceImpl implements FieldValutazioneAccreditamentoService {
	private static Logger LOGGER = LoggerFactory.getLogger(FieldValutazioneAccreditamentoServiceImpl.class);

	@Autowired private FieldValutazioneAccreditamentoRepository fieldValutazioneAccreditamentoRepository;
	@Autowired private ValutazioneService valutazioneService;
	@Autowired private SedeService sedeService;
	@Autowired private PersonaService personaService;

	@Override
	public Set<FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamento(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero delle valutazioni per la domanda di accreditamento: " + accreditamentoId));
		return fieldValutazioneAccreditamentoRepository.findAllByAccreditamentoId(accreditamentoId);
	}

	@Override
	public Set<FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamentoAndObject(Long accreditamentoId, Long objectReference) {
		LOGGER.debug(Utils.getLogMessage("Recupero lista di FieldValutazioneAccreditamento per Domanda Accreditamento: " + accreditamentoId + " riferiti all'oggetto: " + objectReference));
		return fieldValutazioneAccreditamentoRepository.findAllByAccreditamentoIdAndObjectReference(accreditamentoId, objectReference);
	}

	@Override
	public Map<IdFieldEnum, FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamentoAsMap(
			Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero delle valutazioni as MAP per la domanda di accreditamento: " + accreditamentoId));
		Set<FieldValutazioneAccreditamento> fieldValutazioni = getAllFieldValutazioneForAccreditamento(accreditamentoId);

		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		for(FieldValutazioneAccreditamento field : fieldValutazioni){
			mappa.put(field.getIdField(), field);
		}
		return mappa;
	}

	@Override
	public Map<IdFieldEnum, FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamentoAndObjectAsMap(
			Long accreditamentoId, Long objectReference) {
		LOGGER.debug(Utils.getLogMessage("Recupero delle valutazioni as MAP per la domanda di accreditamento: " + accreditamentoId + " riferiti all'oggetto: " + objectReference));
		Set<FieldValutazioneAccreditamento> fieldValutazioni = getAllFieldValutazioneForAccreditamentoAndObject(accreditamentoId, objectReference);

		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		for(FieldValutazioneAccreditamento field : fieldValutazioni){
			mappa.put(field.getIdField(), field);
		}

		return mappa;
	}

	@Override
	public Map<IdFieldEnum, FieldValutazioneAccreditamento> filterFieldValutazioneByObjectAsMap(Set<FieldValutazioneAccreditamento> set, Long id) {
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		for (FieldValutazioneAccreditamento f : set) {
			if(f.getObjectReference() == id) {
				mappa.put(f.getIdField(), f);
			}
		}
		return mappa;
	}

	@Override
	public Map<IdFieldEnum, FieldValutazioneAccreditamento> filterFieldValutazioneBySubSetAsMap(Set<FieldValutazioneAccreditamento> set, SubSetFieldEnum subset) {
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		for (FieldValutazioneAccreditamento f : set) {
			if(f.getIdField().getSubSetField().equals(subset))
				mappa.put(f.getIdField(), f);
		}
		return mappa;
	}

	@Override
	public Map<IdFieldEnum, FieldValutazioneAccreditamento> putSetFieldValutazioneInMap(Set<FieldValutazioneAccreditamento> set) {
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		for (FieldValutazioneAccreditamento f : set) {
			mappa.put(f.getIdField(), f);
		}
		return mappa;
	}

	@Override
	@Transactional
	public void save(FieldValutazioneAccreditamento valutazione) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio FieldValutazioni per la domanda di accreditamento"));
		fieldValutazioneAccreditamentoRepository.save(valutazione);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		LOGGER.debug(Utils.getLogMessage("Eliminazione FieldValutazione " + id));
		fieldValutazioneAccreditamentoRepository.delete(id);
	}

	@Override
	@Transactional
	public Collection<FieldValutazioneAccreditamento> saveMapList(Map<IdFieldEnum, FieldValutazioneAccreditamento> valutazioneAsMap) {
		LOGGER.debug(Utils.getLogMessage("Salvataggio FieldValutazioni per la domanda di accreditamento"));
		fieldValutazioneAccreditamentoRepository.save(valutazioneAsMap.values());
		return valutazioneAsMap.values();
	}

	//recupera tutte le valutazioni della segreteria non storicizzate per l'accreditamento ID
	//utile per vedere quali campi applicare dell'integrazione dei provider
	@Override
	public Set<FieldValutazioneAccreditamento> getAllFieldValutazioneForAccreditamentoBySegreteriaNotStoricizzato(Long accreditamentoId) {
		LOGGER.debug(Utils.getLogMessage("Recupero la valutazione attiva della segreteria per l'accreditamento id: " + accreditamentoId));
		Valutazione valutazioneSegreteria = valutazioneService.getValutazioneSegreteriaForAccreditamentoIdNotStoricizzato(accreditamentoId);
		return valutazioneSegreteria.getValutazioni();
	}

	@Override
	public Set<FieldValutazioneAccreditamento> getValutazioniDefault(Accreditamento accreditamento) {
		Set<FieldValutazioneAccreditamento> defaults = new HashSet<FieldValutazioneAccreditamento>();
		//campi di default senza object ref.
		for(IdFieldEnum id : IdFieldEnum.values()) {
			if(id.getSubSetField() != SubSetFieldEnum.SEDE
					&& id.getSubSetField() != SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO
					&& id.isDefaultVal()) {
				//se l'id appartiene a quelli del delegato legale prima di inserirlo controllo che questi esista
				if(id.getSubSetField() == SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE
						&& accreditamento.getProvider().getDelegatoLegaleRappresentante() == null)
					continue;
				else {
					FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
					field.setAccreditamento(accreditamento);
					field.setEsito(true);
					field.setIdField(id);
					save(field);
					defaults.add(field);
				}
			}
		}
		//campi ripetibili (con object ref.)
		//sedi
		for(Sede sede : accreditamento.getProvider().getSedi()) {
			for(IdFieldEnum idSede : IdFieldEnum.getAllForSubset(SubSetFieldEnum.SEDE)) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setEsito(true);
				field.setIdField(idSede);
				field.setObjectReference(sede.getId());
				save(field);
				defaults.add(field);
			}
		}
		//componenti comitato
		for(Persona persona : accreditamento.getProvider().getComponentiComitatoScientifico()) {
			for(IdFieldEnum idComponenteScietifico : IdFieldEnum.getAllForSubset(SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO)) {
				if(idComponenteScietifico.isDefaultVal()) {
					FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
					field.setAccreditamento(accreditamento);
					field.setEsito(true);
					field.setIdField(idComponenteScietifico);
					field.setObjectReference(persona.getId());
					save(field);
					defaults.add(field);
				}
			}
		}
		/*ALLEGATI*/
		//allegati
		boolean noDichiarazioneEsclusione = true;
		boolean noEsperienzaFormazione = true;
		boolean noEstrattoBilancioComplessivo = true;
		for(File file : accreditamento.getDatiAccreditamento().getFiles()) {
			if(file.isDICHIARAZIONEESCLUSIONE())
				noDichiarazioneEsclusione = false;
			if(file.isESPERIENZAFORMAZIONE())
				noEsperienzaFormazione = false;
			if(file.isESTRATTOBILANCIOCOMPLESSIVO())
				noEstrattoBilancioComplessivo = false;
			if(accreditamento.isStandard()) {
				//TODO nuovi allegati facoltativi standard
			}
		}
		if(noDichiarazioneEsclusione) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setEsito(true);
			field.setIdField(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__DICHIARAZIONE_ESCLUSIONE);
			save(field);
			defaults.add(field);
		}
		if(noEsperienzaFormazione) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setEsito(true);
			field.setIdField(IdFieldEnum.ACCREDITAMENTO_ALLEGATI__ESPERIENZA_FORMAZIONE);
			save(field);
			defaults.add(field);
		}
		if(noEstrattoBilancioComplessivo) {
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setEsito(true);
			field.setIdField(IdFieldEnum.DATI_ACCREDITAMENTO__ESTRATTO_BILANCIO_COMPLESSIVO);
			save(field);
			defaults.add(field);
		}
		return defaults;
	}

	@Override
	public Set<FieldValutazioneAccreditamento> createAllFieldValutazioneAndSetEsito(boolean b, Accreditamento accreditamento) {
		return createAllFieldValutazioneAndSetEsitoAndEnabled(b, true, accreditamento);
	}

	@Override
	public Set<FieldValutazioneAccreditamento> createAllFieldValutazioneAndSetEsitoAndEnabled(boolean b, boolean c, Accreditamento accreditamento) {
		Set<FieldValutazioneAccreditamento> allFieldsValutazione = new HashSet<FieldValutazioneAccreditamento>();
		for(IdFieldEnum id : IdFieldEnum.values()) {
			//gestisco i ripetibili a parte
			if(id.getSubSetField() == SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO
					|| id.getSubSetField() == SubSetFieldEnum.SEDE)
				continue;
			//inserisco la valutazione sul campo solo se sono nello stato giusto
			if(!accreditamento.isValutazioneSulCampo()) {
				if(id.getSubSetField() == SubSetFieldEnum.VALUTAZIONE_SUL_CAMPO)
					continue;
			}
			//allegati relativi alla parte standard
			//con else if non entra mai
			//else if(accreditamento.isProvvisorio()) {
			if(accreditamento.isProvvisorio()) {
				if(id == IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RICHIESTA_ACCREDITAMENTO_STANDARD
						|| id == IdFieldEnum.ACCREDITAMENTO_ALLEGATI__RELAZIONE_ATTIVITA_FORMATIVA)
					continue;
			}
			//full esclusi
			if(id.getIdEcm() == -1)
				continue;
			//se si tratta di un idFeild relativo al delegato legale, controllo che questi esista
			if(id.getSubSetField() == SubSetFieldEnum.DELEGATO_LEGALE_RAPPRESENTANTE
					&& accreditamento.getProvider().getDelegatoLegaleRappresentante() == null)
				continue;
			FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
			field.setAccreditamento(accreditamento);
			field.setEsito(b);
			field.setEnabled(c);
			field.setIdField(id);
			save(field);
			allFieldsValutazione.add(field);
		}
		//ripetibili
		Long providerId = accreditamento.getProvider().getId();
		for(Sede sede : sedeService.getSediFromIntegrazione(providerId)) {
			for(IdFieldEnum idFESede : IdFieldEnum.getAllForSubset(SubSetFieldEnum.SEDE)) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setEsito(b);
				field.setEnabled(c);
				field.setIdField(idFESede);
				field.setObjectReference(sede.getId());
				save(field);
				allFieldsValutazione.add(field);
			}
		}
		for(Persona persona : personaService.getComponentiComitatoScientificoFromIntegrazione(providerId)) {
			for(IdFieldEnum idFECompScie : IdFieldEnum.getAllForSubset(SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO)) {
				FieldValutazioneAccreditamento field = new FieldValutazioneAccreditamento();
				field.setAccreditamento(accreditamento);
				field.setEsito(b);
				field.setEnabled(c);
				field.setIdField(idFECompScie);
				field.setObjectReference(persona.getId());
				save(field);
				allFieldsValutazione.add(field);
			}
		}
		return allFieldsValutazione;
	}

	@Override
	public Set<FieldValutazioneAccreditamento> getValutazioniNonDefault(Valutazione valutazione) {
		Set<FieldValutazioneAccreditamento> result = new HashSet<FieldValutazioneAccreditamento>();
		result.addAll(valutazione.getValutazioni());
		Iterator<FieldValutazioneAccreditamento> iter = result.iterator();
		while(iter.hasNext()) {
			FieldValutazioneAccreditamento fva = iter.next();
			if(fva.getIdField().isDefaultVal())
				iter.remove();
		}
		return result;
	}

}
