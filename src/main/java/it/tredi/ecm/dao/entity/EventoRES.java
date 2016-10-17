package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderBy;

import it.tredi.ecm.dao.enumlist.TipoMetodologiaEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoRESEnum;
import it.tredi.ecm.dao.enumlist.VerificaApprendimentoRESEnum;
import it.tredi.ecm.dao.enumlist.VerificaPresenzaPartecipantiEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@DiscriminatorValue(value = "RES")
public class EventoRES extends Evento{
	@Embedded
	private SedeEvento sedeEvento;

	//comprese tra dataInizio e dataFine
	@ElementCollection
	private Set<LocalDate> dateIntermedie = new HashSet<LocalDate>();

	@Enumerated(EnumType.STRING)
	@Column(name = "tipologia_evento_res")
	private TipologiaEventoRESEnum tipologiaEvento;
	private boolean workshopSeminariEcm;
	private String titoloConvegno;

	@OneToMany(mappedBy="eventoDocente" , cascade=CascadeType.ALL, orphanRemoval=true)
	private List<PersonaEvento> docenti = new ArrayList<PersonaEvento>();

	private String razionale;
	@ElementCollection
	private List<String> risultatiAttesi = new ArrayList<String>();

	@OneToMany(mappedBy="eventoRES", cascade=CascadeType.ALL, orphanRemoval=true)
	@OrderBy("giorno ASC")
	private List<ProgrammaGiornalieroRES> programma = new ArrayList<ProgrammaGiornalieroRES>();

	@ElementCollection
	private Set<VerificaApprendimentoRESEnum> verificaApprendimento;

	private boolean confermatiCrediti;

	@ElementCollection
	private Set<VerificaPresenzaPartecipantiEnum> verificaPresenzaPartecipanti;

	private String materialeDurevoleRilasciatoAiPratecipanti;

	private Boolean soloLinguaItaliana;
	private String linguaStranieraUtilizzata;
	private Boolean esisteTraduzioneSimultanea;

	private Boolean verificaRicaduteFormative;
	private String descrizioneVerificaRicaduteFormative;
	@OneToOne
	private File documentoVerificaRicaduteFormative;
	
	public float calcoloDurata(List<ProgrammaGiornalieroRES> programma){
		this.durata = 0.0f;
		for(ProgrammaGiornalieroRES progrGior : programma){
			for(DettaglioAttivitaRES dett : progrGior.getProgramma()){
				if(!dett.isPausa() && dett.getOreAttivita()!= null)
					durata += dett.getOreAttivita();
			}
		}
		return durata;
	}

	public float calcoloCreditiFormativi(List<ProgrammaGiornalieroRES> programma){
		this.crediti = 0.0f;

		if(tipologiaEvento == TipologiaEventoRESEnum.CONVEGNO_CONGRESSO){
			crediti = (0.20f * durata);
			if(crediti > 5.0f)
				crediti = 5.0f;
		}

		if(tipologiaEvento == TipologiaEventoRESEnum.WORKSHOP_SEMINARIO){
			crediti = 1 * durata;
			if(crediti > 50f)
				crediti = 50f;
		}

		if(tipologiaEvento == TipologiaEventoRESEnum.CORSO_AGGIORNAMENTO){
			float creditiFrontale = 0f;
			float oreFrontale = 0f;
			float creditiInterattiva = 0f;
			float oreInterattiva = 0f;

			for(ProgrammaGiornalieroRES progrGio : programma) {
				for(DettaglioAttivitaRES a : progrGio.getProgramma()){
					if(a.getMetodologiaDidattica().getMetodologia() == TipoMetodologiaEnum.FRONTALE){
						oreFrontale ++;
					}else{
						oreInterattiva ++;
					}
				}
			}

			//metodologia frontale
			if(numeroPartecipanti >=1 && numeroPartecipanti <=20){
				creditiFrontale = oreFrontale * 1.0f;
				creditiFrontale = (creditiFrontale + (creditiFrontale*0.20f));
			}else if(numeroPartecipanti >=21 && numeroPartecipanti <= 50){
				//TODO 25% decrescente
			}else if(numeroPartecipanti >=51 && numeroPartecipanti <=100){
				creditiFrontale = oreFrontale * 1.0f;
			}else if(numeroPartecipanti >= 101 && numeroPartecipanti <= 150){
				creditiFrontale = oreFrontale * 0.75f;
			}else if(numeroPartecipanti >= 151 && numeroPartecipanti <= 200){
				creditiFrontale = oreFrontale * 0.5f;
			}

			//metodologia interattiva
			creditiInterattiva = oreInterattiva * 1.5f;

			crediti = creditiFrontale + creditiInterattiva;
		}

		return crediti;
	}
}