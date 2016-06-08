package it.tredi.ecm.service.bean;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.type.TrueFalseType;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.Costanti;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccreditamentoWrapper {
	//dati domanda di accreditamento
	private Accreditamento accreditamento;
	
	//dati provider
	private Provider provider;
	
	private Sede sedeLegale;
	private Sede sedeOperativa;
	
	private Persona legaleRappresentante;
	private Persona delegatoLegaleRappresentante;
	
	//dati domanda accreditamento
	private DatiAccreditamento datiAccreditamento;
	
	//dati anagrafiche interne provider
	private Persona responsabileSegreteria;
	private Persona responsabileAmministrativo;
	private Persona coordinatoreComitatoScientifico;
	private List<Persona> componentiComitatoScientifico = new ArrayList<Persona>();
	private Persona responsabileSistemaInformatico;
	private Persona responsabileQualita;
	
	//flag per stato avanzamento domanda
	private boolean providerStato;
	private boolean sedeLegaleStato;
	private boolean sedeOperativaStato;
	private boolean legaleRappresentanteStato;
	private boolean delegatoLegaleRappresentanteStato;
	
	private boolean datiAccreditamentoStato;
	
	private boolean responsabileSegreteriaStato;
	private boolean responsabileAmministrativoStato;
	private boolean comitatoScientificoStato;
	private boolean responsabileSistemaInformaticoStato;
	private boolean responsabileQualitaStato;
	
	private boolean attoCostitutivoStato;
	private boolean esperienzaFormazioneStato;
	private boolean utilizzoStato;
	private boolean sistemaInformaticoStato;
	private boolean pianoQualitaStato;
	private boolean dichiarazioneLegaleStato;
	
	private boolean completa;
	private boolean canSend;
	
	public void checkStati(Set<String> existFiles){
		//TODO migliorare la logica per evitare di fare troppi if
		// ad esempio inizializzare gli stati a true e poi ad ogni controllo se fallisce si mette il false sia allo stato che al valid
		// cosi facendo valid è settato in automatico senza rifare tutti i controlli
		// 
		//NON lo faccio adesso perchè voglio capire in fase di validazione della domanda come gestiremo i vari stati
		providerStato = (!provider.getRagioneSociale().isEmpty()) ? true : false;
		
		sedeLegaleStato = (sedeLegale != null && !sedeLegale.isNew()) ? true : false;
		sedeOperativaStato = (sedeLegale != null && !sedeLegale.isNew()) ? true : false;
		
		legaleRappresentanteStato = (legaleRappresentante != null && !legaleRappresentante.isNew()) ? true : false;
		delegatoLegaleRappresentanteStato = (delegatoLegaleRappresentante != null && !delegatoLegaleRappresentante.isNew()) ? true : false;
		
		datiAccreditamentoStato = (datiAccreditamento != null && !datiAccreditamento.isNew()) ? true : false;
		
		responsabileSegreteriaStato = (responsabileSegreteria != null && !responsabileSegreteria.isNew()) ? true : false;
		responsabileAmministrativoStato = (responsabileAmministrativo != null && !responsabileAmministrativo.isNew()) ? true : false;
		responsabileSistemaInformaticoStato = (responsabileSistemaInformatico != null && !responsabileSistemaInformatico.isNew()) ? true : false;
		responsabileQualitaStato = (responsabileQualita != null && !responsabileQualita.isNew()) ? true : false;
		
		//TODO comitatoScientificoStato
		
		setFilesStato(existFiles);
		checkCompleta();
	}
	
	private void setFilesStato(Set<String> existFiles){
		if(existFiles.contains(Costanti.FILE_ATTO_COSTITUTIVO))
			attoCostitutivoStato = true;
		if(existFiles.contains(Costanti.FILE_ESPERIENZA_FORMAZIONE))
			esperienzaFormazioneStato = true;
		if(existFiles.contains(Costanti.FILE_UTILIZZO))
			utilizzoStato = true;
		if(existFiles.contains(Costanti.FILE_SISTEMA_INFORMATICO))
			sistemaInformaticoStato = true;
		if(existFiles.contains(Costanti.FILE_PIANO_QUALITA))
			pianoQualitaStato = true;
		if(existFiles.contains(Costanti.FILE_DICHIARAZIONE_LEGALE))
			dichiarazioneLegaleStato = true;
	}
	
	private void checkCompleta(){
		if(providerStato &&
			 sedeLegaleStato &&
			 sedeOperativaStato &&
			 (legaleRappresentanteStato || delegatoLegaleRappresentanteStato) &&
			 datiAccreditamentoStato &&
			 responsabileSegreteriaStato &&
			 responsabileAmministrativoStato &&
			 //TODO comitatoScientificoStato /*comitatoScientificoStato &&*/
			 responsabileSistemaInformaticoStato &&
			 responsabileQualitaStato &&
			 attoCostitutivoStato &&
			 esperienzaFormazioneStato &&
			 utilizzoStato &&
			 sistemaInformaticoStato &&
			 pianoQualitaStato &&
			 dichiarazioneLegaleStato)
			completa = true;
		else
			completa = false;
	}
	
	public void checkCanSend(){
		if(accreditamento.getStato().equals(Costanti.ACCREDITAMENTO_STATO_BOZZA) && completa)
			canSend = true;
		else
			canSend = false;
	}
}
