package it.tredi.ecm.web.validator;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.VerbaleValutazioneSulCampo;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.utils.Utils;

@Component
public class ValutazioneValidator {
	private static final Logger LOGGER = LoggerFactory.getLogger(ValutazioneValidator.class);

	public void validateValutazioneDatiAccreditamento(Object target, Errors errors, int sezione) {
		Set<IdFieldEnum> fieldsDaControllare = new HashSet<IdFieldEnum>();
		fieldsDaControllare = IdFieldEnum.getDatiAccreditamentoSplitBySezione(sezione);

		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = (Map<IdFieldEnum, FieldValutazioneAccreditamento>) target;
		for (Map.Entry<IdFieldEnum, FieldValutazioneAccreditamento> entry : mappa.entrySet()) {
			if(fieldsDaControllare.contains(entry.getKey()))
			{
				String key = gestisciEccezioniKey(entry.getKey().getKey());
				if(entry.getValue().getEsito() == null) {
					errors.rejectValue(key, "error.atleast_one_empty");
				}
				else
					if(entry.getValue().getEsito() == false && (entry.getValue().getNote() == null
					|| entry.getValue().getNote().isEmpty()))
						errors.rejectValue(key, "error.note_obbligatorie");
			}
		}
	}

	public void validateValutazione(Object target, Errors errors) {
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = (Map<IdFieldEnum, FieldValutazioneAccreditamento>) target;

		if(mappa.containsKey(IdFieldEnum.SEDE__FULL) || mappa.containsKey(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__FULL)){
			FieldValutazioneAccreditamento f = mappa.get(IdFieldEnum.SEDE__FULL);
			if(f == null)
				f = mappa.get(IdFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO__FULL);

			if(f.getEsito() == null) {
				errors.rejectValue("", "error.atleast_one_empty");
			}
			else
				if(f.getEsito() == false && (f.getNote() == null
				|| f.getNote().isEmpty()))
					errors.rejectValue("", "error.note_obbligatorie");
		}else{
			for (Map.Entry<IdFieldEnum, FieldValutazioneAccreditamento> entry : mappa.entrySet()) {
				String key = gestisciEccezioniKey(entry.getKey().getKey());
				if(entry.getValue().getEsito() == null) {
					errors.rejectValue(key, "error.atleast_one_empty");
				}
				else
					if(entry.getValue().getEsito() == false && (entry.getValue().getNote() == null
					|| entry.getValue().getNote().isEmpty()))
						errors.rejectValue(key, "error.note_obbligatorie");
			}
		}
	}

	public void validateValutazioneComplessiva(Object targetReferee, Object valutazioneFull, AccreditamentoStatoEnum stato, Errors errors) {
		Set<Account> refereeGroup = (Set<Account>)targetReferee;
		String valutazioneComplessiva = (String) valutazioneFull;
		if((stato == AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA_ASSEGNAMENTO) &&
				Utils.getAuthenticatedUser().isSegreteria() && (refereeGroup == null || refereeGroup.size() != 3)) {
			errors.rejectValue("refereeGroup", "error.numero_referee");
		}
		if(valutazioneComplessiva == null || valutazioneComplessiva.isEmpty()) {
			errors.rejectValue("valutazioneComplessiva", "error.empty");
		}
	}

	public void validateValutazioneComplessivaTeamLeader(Object valutazioneFull, Errors errors) {
		String valutazioneComplessiva = (String) valutazioneFull;
		if(valutazioneComplessiva == null || valutazioneComplessiva.isEmpty()) {
			errors.rejectValue("valutazioneComplessiva", "error.empty");
		}
	}

	public void validateGruppoCrecm(Object targetReferee, int refereeDaRiassegnare, Errors errors) {
		Set<Account> refereeGroup = (Set<Account>)targetReferee;
		if(Utils.getAuthenticatedUser().isSegreteria() && (refereeGroup == null || refereeGroup.size() != refereeDaRiassegnare)) {
			errors.rejectValue("refereeGroup", "error.numero_referee_riassegnamento");
		}
	}

	//gestisce le eccezioni degli input raggruppati prendendo come rejectValue il primo valore
	private String gestisciEccezioniKey(String key) {
		switch(key) {
			case "datiAccreditamento.datiEconomici.fatturatoComplessivo": return "datiAccreditamento.datiEconomici.fatturatoComplessivoValoreUno";
			case "datiAccreditamento.datiEconomici.fatturatoFormazione": return "datiAccreditamento.datiEconomici.fatturatoFormazioneValoreUno";
			case "datiAccreditamento.numeroDipendenti": return "datiAccreditamento.numeroDipendentiFormazioneTempoIndeterminato";
			default: return key;
		}
	}

	public void validateValutazioneSulCampo(VerbaleValutazioneSulCampo verbaleValutazioneSulCampo, String valutazioneComplessiva, Errors errors, String prefix, AccreditamentoStatoEnum stato) {

		if(stato == AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA_ASSEGNAMENTO) {
			if(verbaleValutazioneSulCampo.getGiorno() == null)
				errors.rejectValue(prefix + "giorno", "error.empty");
			else if(verbaleValutazioneSulCampo.getGiorno().isBefore(LocalDate.now()))
				errors.rejectValue(prefix + "giorno", "error.data_non_valida_verbale");
			if(verbaleValutazioneSulCampo.getTeamLeader() == null)
				errors.rejectValue(prefix + "teamLeader", "error.empty");
			if(verbaleValutazioneSulCampo.getOsservatoreRegionale() == null)
				errors.rejectValue(prefix + "osservatoreRegionale", "error.empty");
			if(verbaleValutazioneSulCampo.getComponentiSegreteria() == null || verbaleValutazioneSulCampo.getComponentiSegreteria().isEmpty())
				errors.rejectValue(prefix + "componentiSegreteria", "error.empty");
			if(verbaleValutazioneSulCampo.getReferenteInformatico() == null && verbaleValutazioneSulCampo.getAccreditamento().getDatiAccreditamento().getProcedureFormative().contains(ProceduraFormativa.FAD))
				errors.rejectValue(prefix + "referenteInformatico", "error.empty");
		}
	}

	public Map<String, String> validateValutazioneSulCampo(Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa) {
		Map<String, String> mappaErroriValutazione = new HashMap<String, String>();
		for (Map.Entry<IdFieldEnum, FieldValutazioneAccreditamento> entry : mappa.entrySet()) {
			String key = entry.getKey().getKey();
			if(entry.getValue().getEsito() == null) {
				mappaErroriValutazione.put(key, "error.atleast_one_empty");
			}
			else
				if(entry.getValue().getEsito() == false && (entry.getValue().getNote() == null
				|| entry.getValue().getNote().isEmpty()))
					mappaErroriValutazione.put(key, "error.note_obbligatorie");
		}
		return mappaErroriValutazione;
	}

	public void validateEditVerbale(VerbaleValutazioneSulCampo verbale, Errors errors, String prefix) {
		if(verbale.getGiorno() == null)
			errors.rejectValue(prefix + "giorno", "error.empty");
		//hanno chiesto di toglierla! ( ._.)
//		else if(verbale.getGiorno().isBefore(LocalDate.now()))
//			errors.rejectValue(prefix + "giorno", "error.data_non_valida_verbale");
		if(verbale.getTeamLeader() == null)
			errors.rejectValue(prefix + "teamLeader", "error.empty");
		if(verbale.getOsservatoreRegionale() == null)
			errors.rejectValue(prefix + "osservatoreRegionale", "error.empty");
		if(verbale.getComponentiSegreteria() == null || verbale.getComponentiSegreteria().isEmpty())
			errors.rejectValue(prefix + "componentiSegreteria", "error.empty");
		if(verbale.getReferenteInformatico() == null && verbale.getAccreditamento().getDatiAccreditamento().getProcedureFormative().contains(ProceduraFormativa.FAD))
			errors.rejectValue(prefix + "referenteInformatico", "error.empty");
	}

	public void validateSottoscriventeValutazioneSulCampo(VerbaleValutazioneSulCampo verbaleValutazioneSulCampo, Errors errors, String prefix) {
		if(verbaleValutazioneSulCampo.getCartaIdentita() == null || verbaleValutazioneSulCampo.getCartaIdentita().isNew()) {
			errors.rejectValue(prefix + "cartaIdentita", "error.empty");
		}
		if(verbaleValutazioneSulCampo.getIsPresenteLegaleRappresentante() == null) {
			errors.rejectValue(prefix + "isPresenteLegaleRappresentante", "error.empty");
		}
		else if(!verbaleValutazioneSulCampo.getIsPresenteLegaleRappresentante()) {
			if(verbaleValutazioneSulCampo.getDelegato().getCognome().isEmpty())
				errors.rejectValue(prefix + "delegato.cognome", "error.empty");
			if(verbaleValutazioneSulCampo.getDelegato().getNome().isEmpty())
				errors.rejectValue(prefix + "delegato.nome", "error.empty");
			if(verbaleValutazioneSulCampo.getDelegato().getCodiceFiscale().isEmpty())
				errors.rejectValue(prefix + "delegato.codiceFiscale", "error.empty");
			if(verbaleValutazioneSulCampo.getDelegato().getDelega() == null || verbaleValutazioneSulCampo.getDelegato().getDelega().isNew())
				errors.rejectValue(prefix + "delegato.delega", "error.empty");
		}
	}
}
