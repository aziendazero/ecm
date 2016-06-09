package it.tredi.ecm.web.bean;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.type.TrueFalseType;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Professione;
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
	private String comitatoScientificoErrorMessage;
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
		
		checkComitatoScientifico();
		setFilesStato(existFiles);
		checkCompleta();
	}
	
	/*
	 * [A] Almeno 5 componenti (coordinatore incluso) 
	 * [B] Almeno 5 professionisti Sanitari ??? //TODO controllo comitato scientifico
	 * [C] Se settato "Generale" -> Almeno 2 professioni diverse tra i componenti
	 * [D] Se settato "Settoriale" -> Almeno 2 professioni analoghe a quelle selezionate (almeno che non sia stata selezionate solo 1 professione)
	 * */
	private void checkComitatoScientifico(){
		comitatoScientificoStato = true;
		//[A]
		if(componentiComitatoScientifico.size() < 4 || (coordinatoreComitatoScientifico == null || coordinatoreComitatoScientifico.isNew())){
			comitatoScientificoStato = false;
			comitatoScientificoErrorMessage = "error.numero_minimo_comitato";
		}
		else{
			// mi assicuro che sono stati gia' inseriti i dati relativi all'accreditamento
			if(datiAccreditamentoStato){
				//Individuo distintamente le professioni dei componenti del comitato
				Set<Professione> professioniDeiComponenti = new HashSet<Professione>();
				for(Persona p : componentiComitatoScientifico){
					professioniDeiComponenti.add(p.getProfessione());
				}
				
				//[C]
				if(datiAccreditamento.getProfessioniAccreditamento().equalsIgnoreCase("generale")){
					if(professioniDeiComponenti.size() < 2){
						comitatoScientificoStato = false;
						comitatoScientificoErrorMessage = "error.numero_minimo_professioni";
					}
				}//[D]
				else{
					if(datiAccreditamento.getProfessioniSelezionate().size() > 1){
						professioniDeiComponenti.retainAll(datiAccreditamento.getProfessioniSelezionate());
						if(professioniDeiComponenti.size() < 2){
							comitatoScientificoStato = false;
							comitatoScientificoErrorMessage = "error.numero_minimo_professioni_settoriale";
						}
					}
				}
			}else{
				comitatoScientificoStato = false;
				comitatoScientificoErrorMessage = "error.datiaccreditamento_mancanti";
			}
		}
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
