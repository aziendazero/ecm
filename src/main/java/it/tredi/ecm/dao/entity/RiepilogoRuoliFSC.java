package it.tredi.ecm.dao.entity;

import java.time.Duration;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.javers.core.metamodel.annotation.TypeName;

import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TematicheInteresseEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaGruppoFSCEnum;
import it.tredi.ecm.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@TypeName("RiepilogoRuoliFSC")
@Getter
@Setter
@Embeddable
public class RiepilogoRuoliFSC {

	public static final float TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_VERSIONE_UNO = 30f;
	public static final float TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_TUTOR_VERSIONE_UNO = 50f;
	public static final float GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_UNO = 50f;
	public static final float PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_UNO = 50f;
	public static final float ATTIVITA_DI_RICERCA_MAX_CREDITI_VERSIONE_UNO = 3f;
	public static final float AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_UNO = 50f;

	public static final float TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_VERSIONE_DUE = 50f;
	public static final float TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_TUTOR_VERSIONE_DUE = 50f;
	public static final float TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_RESPONSABILESCIENTIFICI_ESPERTO_COORDINATORE_VERSIONE_DUE = 50f;
	public static final float GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE = 50f;
	public static final float AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_DUE = 50f;
	public static final float PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE = 50f;
	public static final float ATTIVITA_DI_RICERCA_MAX_CREDITI_VERSIONE_DUE = 50f;

	public static final long GIORNI_DURATA_FSC_6_MESI = 180;
	public static final long GIORNI_DURATA_FSC_12_MESI = 365;
	public static final long GIORNI_DURATA_FSC_24_MESI = 730;

	//id 1024 "Non rientra in uno degli obiettivi regionali (1)"
	public static final long NON_RIENTRA_NEGLI_OBIETTIVI_REGIONALI_ID = 1024L;

	@Enumerated(EnumType.STRING)
	private RuoloFSCEnum ruolo;
	private float tempoDedicato;
	private float crediti;
	private int numeroPartecipanti;

	public RiepilogoRuoliFSC(){}
	public RiepilogoRuoliFSC(RuoloFSCEnum ruolo){
		this.ruolo = ruolo;
		this.tempoDedicato = 0.0f;
		this.crediti = 0.0f;
	}

	public RiepilogoRuoliFSC(RuoloFSCEnum ruolo, float tempoDedicato, float crediti){
		this.ruolo = ruolo;
		this.tempoDedicato = tempoDedicato;
		this.crediti = crediti;
	}

	public RiepilogoRuoliFSC(RuoloFSCEnum ruolo, float tempoDedicato, float crediti, int numeroPartecipanti){
		this.ruolo = ruolo;
		this.tempoDedicato = tempoDedicato;
		this.crediti = crediti;
		this.numeroPartecipanti = numeroPartecipanti;
	}

	public void clear(){
		this.tempoDedicato = 0.0f;
		this.crediti = 0.0f;
		this.numeroPartecipanti = 0;
	}

	public void setTempoDedicato(float t){
		this.tempoDedicato = Utils.getRoundedFloatValue(t, 2);
	}

	public void addTempo(float tempo){
		this.tempoDedicato += tempo;
	}

	public void addCrediti(float crediti){
		this.crediti += crediti;
	}

	public void calcolaCreditiVersioneUno(TipologiaEventoFSCEnum tipologiaEvento, float f){
		if(tipologiaEvento != null && ruolo != null){
			switch(tipologiaEvento){
				case TRAINING_INDIVIDUALIZZATO:
					{
						/*
						 * PARTECIPANTI:	1 credito ogni ora (max 30) NON FRAZIONABILE
						 * TUTOR:			1 credito ogni 6 ore (max 50)
						 * ESPERTO:			1 credito ogni ora (max 'crediti evento')
						 * COORDINATORE		1 credito ogni ora (max 'crediti evento')
						 *
						 * */
						switch(ruolo.getRuoloBase())
						{
							case PARTECIPANTE:  crediti = 1 * (int) tempoDedicato;
												//crediti = (int) crediti;
												crediti = (crediti > TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_VERSIONE_UNO) ? TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_VERSIONE_UNO : crediti;
								break;

							case TUTOR: 		crediti = 1 * (int) (tempoDedicato/6);
												crediti = (crediti > TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_TUTOR_VERSIONE_UNO) ? TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_TUTOR_VERSIONE_UNO : crediti;
								break;

							case ESPERTO: 		crediti = 1 * (int) tempoDedicato;
												crediti = (crediti > f) ? f : crediti;
								break;

							case COORDINATORE: 	crediti = 1 * (int) tempoDedicato;
												crediti = (crediti > f) ? f : crediti;
								break;

							default:			crediti = 0.0f;
								break;
						}
					}
					break;

				case GRUPPI_DI_MIGLIORAMENTO:
				{
					/*
					 * PARTECIPANTI:	1 credito ogni 2 ore (max 50) NON FRAZIONABILE
					 * COORDINATORE		1 credito ogni ora (max 'crediti evento')
					 *
					 * */
					switch(ruolo.getRuoloBase())
					{
						case PARTECIPANTE: 	crediti = 1 * (int) (tempoDedicato/2);
											//crediti = (int) crediti;
											crediti = (crediti > GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_UNO) ? GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_UNO : crediti;
							break;

						case COORDINATORE:  crediti = 1 * (int) tempoDedicato;
											crediti = (crediti > f) ? f : crediti;
							break;

						default:	crediti = 0.0f;
							break;
					}
				}
				break;

				case PROGETTI_DI_MIGLIORAMENTO:
				{
					/*
					 * PARTECIPANTI:	0.5 credito ogni ora (max 50) NON FRAZIONABILE
					 * ESPERTO:			1 credito ogni ora (max 'crediti evento')
					 * COORDINATORE:	1 credito ogni ora (max 'crediti evento')
					 * RESPONSABILE:	1 credito ogni ora (max 'crediti evento')
					 *
					 * */
					switch(ruolo.getRuoloBase())
					{
						case PARTECIPANTE:  crediti = 0.5f * (int) tempoDedicato;
											//crediti = (int) crediti;
											crediti = (crediti > PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_UNO) ? PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_UNO : crediti;

							break;

						case ESPERTO: crediti = 1 * (int) tempoDedicato;
											crediti = (crediti > f) ? f : crediti;
							break;

						case COORDINATORE: crediti = 1 * (int) tempoDedicato;
											crediti = (crediti > f) ? f : crediti;
							break;

						case RESPONSABILE: crediti = 1 * (int) tempoDedicato;
											crediti = (crediti > f) ? f : crediti;
							break;

						default:	crediti = 0.0f;
							break;
					}
				}
				break;

				case ATTIVITA_DI_RICERCA:
				{
					/*
					 * PARTECIPANTI:	1 credito ogni ora (max 3)
					 * COORDINATORE:	1 credito ogni ora (max 'crediti evento')
					 *
					 * */
					switch(ruolo.getRuoloBase())
					{
						case PARTECIPANTE: crediti = ATTIVITA_DI_RICERCA_MAX_CREDITI_VERSIONE_UNO;
							break;

						case COORDINATORE: crediti = 1 * (int) tempoDedicato;
											crediti = (crediti > f) ? f : crediti;
							break;

						default:	crediti = 0.0f;
							break;
					}
				}
				break;

				case AUDIT_CLINICO_ASSISTENZIALE:
				{
					/*
					 * PARTECIPANTI:	2 crediti ogni 2 ore (max 50) NON FRAZIONABILE
					 * COORDINATORE:	1 credito ogni ora (max 'crediti evento')
					 *
					 * */
					switch(ruolo.getRuoloBase())
					{
						case PARTECIPANTE: crediti = 2 * (int) (tempoDedicato/2);
											crediti = (int) crediti;
											crediti = (crediti > AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_UNO) ? AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_UNO : crediti;
							break;

						case COORDINATORE: crediti = 1 * (int) tempoDedicato;
											crediti = (crediti > f) ? f : crediti;
							break;

						default:	crediti = 0.0f;
							break;
					}
				}
				break;

			default: break;
			}

			crediti = Utils.getRoundedFloatValue(crediti, 1);

		}else{
			crediti = 0.0f;
		}
	}

	private boolean checkIsCoordinatoreEnabled(EventoFSC evento) {
		for (PersonaEvento pers : evento.getCoordinatori()) {
			if(pers.getIdentificativoPersonaRuoloEvento().getRuoloFSCCoordinatore() == ruolo) {
				return pers.isSvolgeAttivitaDiDocenza();
			}
		}
		return false;
	}

	public void calcolaCreditiVersioneDue(EventoFSC evento, float f){
		/*
		public enum RuoloFSCBaseEnum {
			PARTECIPANTE(1,"P"),
			TUTOR(2,"T"),
			ESPERTO(3,"D"),
			COORDINATORE(4,"D"),
			RESPONSABILE(5,"D"),

			//Sono stati aggiunti
			RESPONSABILE_SCIENTIFICO(6,"D"),
			COORDINATORE_X(7,"D");
		}

		public enum RuoloFSCEnum {
			...

			//Sono stati aggiunti
			RESPONSABILE_SCIENTIFICO_A(15,"Responsabile scientifico A", RuoloFSCBaseEnum.RESPONSABILE_SCIENTIFICO),
			RESPONSABILE_SCIENTIFICO_B(16,"Responsabile scientifico B", RuoloFSCBaseEnum.RESPONSABILE_SCIENTIFICO),
			RESPONSABILE_SCIENTIFICO_C(17,"Responsabile scientifico C", RuoloFSCBaseEnum.RESPONSABILE_SCIENTIFICO),

			COORDINATORE_A(18,"Coordinatore A", RuoloFSCBaseEnum.COORDINATORE_X),
			COORDINATORE_B(19,"Coordinatore B", RuoloFSCBaseEnum.COORDINATORE_X),
			COORDINATORE_C(20,"Coordinatore C", RuoloFSCBaseEnum.COORDINATORE_X);
		}
		 */
		TipologiaEventoFSCEnum tipologiaEvento = evento.getTipologiaEventoFSC();
		float moltiplicatore;

		if(tipologiaEvento != null && ruolo != null){
			switch(tipologiaEvento){
				case TRAINING_INDIVIDUALIZZATO:
					{
						/*
						 * PARTECIPANTI:				1.5 credito ogni ora (max 50) NON FRAZIONABILE, incremento di 0.3 crediti/ora se il provider seleziona uno degli obiettivi regionali (campo 14, sezione 1)
						 * TUTOR:						1 credito ogni ora (max 50)
						 Coordinatore, responsabile scientifico ed esperto:
						 * ESPERTO:						1 credito ogni mezz'ora (max 50)
						 * NO COORDINATORE					1 credito ogni mezz'ora (max 50)
						 * COORDINATORE_X				1 credito ogni mezz'ora (max 50)
						 * NO RESPONSABILE					1 credito ogni mezz'ora (max 50)
						 * RESPONSABILE_SCIENTIFICO		1 credito ogni mezz'ora (max 50)
						 *
						 * */
						switch(ruolo.getRuoloBase())
						{
							case PARTECIPANTE:
								if(evento.getObiettivoRegionale() == null || evento.getObiettivoRegionale().getId() == NON_RIENTRA_NEGLI_OBIETTIVI_REGIONALI_ID)
									crediti = 1.5f * (int) tempoDedicato;
								else
									crediti = 1.8f * (int) tempoDedicato;
								crediti = (crediti > TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_VERSIONE_DUE) ? TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_VERSIONE_DUE : crediti;
								break;
							case TUTOR:
								crediti = 1 * (int) tempoDedicato;
								crediti = (crediti > TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_TUTOR_VERSIONE_DUE) ? TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_TUTOR_VERSIONE_DUE : crediti;
								break;
							case ESPERTO:
							//case COORDINATORE:
							//case RESPONSABILE:
							case RESPONSABILE_SCIENTIFICO:
								crediti = 1 * (int) (tempoDedicato * 2);
								crediti = (crediti > TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_RESPONSABILESCIENTIFICI_ESPERTO_COORDINATORE_VERSIONE_DUE) ? TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_RESPONSABILESCIENTIFICI_ESPERTO_COORDINATORE_VERSIONE_DUE : crediti;
								break;
							case COORDINATORE_X:
								if(checkIsCoordinatoreEnabled(evento)) {
									crediti = 1 * (int) (tempoDedicato * 2);
									crediti = (crediti > TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_RESPONSABILESCIENTIFICI_ESPERTO_COORDINATORE_VERSIONE_DUE) ? TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_RESPONSABILESCIENTIFICI_ESPERTO_COORDINATORE_VERSIONE_DUE : crediti;
								} else {
									crediti = 0.0f;
								}
								break;
							default:
								crediti = 0.0f;
								break;
						}
					}
					break;

				case GRUPPI_DI_MIGLIORAMENTO:
					{
						/*
						 * PARTECIPANTI:				1 credito ogni ora (max 50) NON FRAZIONABILE
						 * 								se in "Tipologia gruppo" non è selezionato il valore "Comitati aziendali permanenti" allora
														incremento di 0,3 crediti per ora, cumulabili tra loro nel caso di:
															a. redazione di un documento conclusivo quale ad es. linee guida, procedure, protocolli, indicazioni operative: inserire in sezione 3 un nuovo campo (finalizzato all'incremento crediti per partecipanti) con la seguente domanda: "È prevista la redazione di un documento conclusivo quale ad es. linee guida, procedure, protocolli, indicazioni operative?" Scelta tra Sì o No. Se il provider sceglie Sì, attribuire l'incremento del credito di 0,3 crediti/ora. Se sceglie No, la piattaforma non deve attribuire l'incremento del credito.
															b. partecipazione di un docente/tutor esperto esterno al gruppo di miglioramento, che validi le attività del gruppo: inserire la domanda, in sezione 3 : "È presente un Tutor esperto esterno che validi le attività del gruppo? scelta tra Sì o No. Se il provider sceglie Sì, il sistema attribuisce l'aumento del credito di 0,3 ora. Se sceglie No, il sistema non deve attribuire l'incremento del credito. Al Docente/tutor esperto non vengono attribuiti crediti.
															c. se il provider ha selezionato nella sezione 1 campo 14, un obiettivo strategico regionale.

						 Responsabile scientifico/coordinatore
						 * NO RESPONSABILE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 * RESPONSABILE_SCIENTIFICO		1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 * NO COORDINATORE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 * COORDINATORE_X				1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 * ESPERTO:						1 credito ogni mezz'ora (max 50)
						 *
						 * */
						switch(ruolo.getRuoloBase())
						{
							case PARTECIPANTE:
								if(evento.getTipologiaGruppo() != null && evento.getTipologiaGruppo() != TipologiaGruppoFSCEnum.COMITATI_AZIENDALI_PERMANENTI) {
									moltiplicatore = 1f;
									if(evento.getPrevistaRedazioneDocumentoConclusivo() != null && evento.getPrevistaRedazioneDocumentoConclusivo().booleanValue()) {
										moltiplicatore += 0.3f;
									}
									if(evento.getPresenteTutorEspertoEsternoValidatoreAttivita() != null && evento.getPresenteTutorEspertoEsternoValidatoreAttivita().booleanValue()) {
										moltiplicatore += 0.3f;
									}
									if(evento.getObiettivoRegionale() != null && evento.getObiettivoRegionale().getId() != NON_RIENTRA_NEGLI_OBIETTIVI_REGIONALI_ID) {
										moltiplicatore += 0.3f;
									}
								} else {
									//evento.getTipologiaGruppo() == null || evento.getTipologiaGruppo() == TipologiaGruppoFSCEnum.COMITATI_AZIENDALI_PERMANENTI
									moltiplicatore = 1f;
								}

								crediti = moltiplicatore * (int) tempoDedicato;
								crediti = (crediti > GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE) ? GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE : crediti;
								break;

							//case RESPONSABILE:
							case RESPONSABILE_SCIENTIFICO:
							//case COORDINATORE:
							case ESPERTO:
								crediti = 1 * (int) (tempoDedicato * 2);
								crediti = (crediti > GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE) ? GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE : crediti;
								break;
							case COORDINATORE_X:
								if(checkIsCoordinatoreEnabled(evento)) {
									crediti = 1 * (int) (tempoDedicato * 2);
									crediti = (crediti > GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE) ? GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE : crediti;
								} else {
									crediti = 0.0f;
								}
								break;
							default:
								crediti = 0.0f;
								break;
						}
					}
					break;

				case AUDIT_CLINICO_ASSISTENZIALE:
					{
						/*
						 * PARTECIPANTI:				1 credito ogni ora (max 50) NON FRAZIONABILE
														se in "Tipologia gruppo" non è selezionato il valore "Comitati aziendali permanenti" allora
														incremento di 0,3 crediti per ora, cumulabili tra loro nel caso di:
															a. Partecipazione di un docente/tutor esperto, esterno al gruppo di miglioramento, che validi le attività del gruppo: Inserire la domanda, in sezione 3: "È presente un Tutor esperto esterno che validi le attività del gruppo? scelta tra Sì o NO. Se il provider sceglie SI il sistema attribuisce l'aumento del credito di 0,3 ora. Se sceglie No il sistema non deve attribuire l'incremento del credito.
															b. se il provider ha selezionato nella sezione 1 campo 14, un obiettivo strategico regionale.
						responsabile scientifico e coordinatore
						 * NO RESPONSABILE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 * RESPONSABILE_SCIENTIFICO		1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 * NO COORDINATORE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 * COORDINATORE_X				1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 *
						 * */
						switch(ruolo.getRuoloBase())
						{
							case PARTECIPANTE:
								moltiplicatore = 1f;
								if(evento.getPresenteTutorEspertoEsternoValidatoreAttivita() != null && evento.getPresenteTutorEspertoEsternoValidatoreAttivita().booleanValue()) {
									moltiplicatore += 0.3f;
								}
								if(evento.getObiettivoRegionale() != null && evento.getObiettivoRegionale().getId() != NON_RIENTRA_NEGLI_OBIETTIVI_REGIONALI_ID) {
									moltiplicatore += 0.3f;
								}

								crediti = moltiplicatore * (int) tempoDedicato;
								crediti = (crediti > AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_DUE) ? AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_DUE : crediti;
								break;
							//case RESPONSABILE:
							case RESPONSABILE_SCIENTIFICO:
							//case COORDINATORE:
								crediti = 1 * (int) (tempoDedicato * 2);
								crediti = (crediti > AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_DUE) ? AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_DUE : crediti;
								break;
							case COORDINATORE_X:
								if(checkIsCoordinatoreEnabled(evento)) {
									crediti = 1 * (int) (tempoDedicato * 2);
									crediti = (crediti > AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_DUE) ? AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_DUE : crediti;
								} else {
									crediti = 0.0f;
								}
								break;
							default:
								crediti = 0.0f;
								break;
						}
					}
					break;
				case PROGETTI_DI_MIGLIORAMENTO:
				{
					/*
					 * PARTECIPANTI:				1 credito ogni ora (max 50) NON FRAZIONABILE
													se in "Tipologia gruppo" non è selezionato il valore "Comitati aziendali permanenti" allora
													incremento di 0,3 crediti per ora, cumulabili tra loro nel caso di:
														a. redazione di un documento conclusivo quale ad es. linee guida, procedure, protocolli, indicazioni operative: inserire in sezione 3 un nuovo campo (finalizzato all'incremento crediti per partecipanti) con la seguente domanda: "È prevista la redazione di un documento conclusivo quale ad es. linee guida, procedure, protocolli, indicazioni operative?" Scelta tra Sì o No. Se il provider sceglie Sì, attribuire l'incremento del credito di 0,3 crediti/ora. Se sceglie No, la piattaforma non deve attribuire l'incremento del credito.
														b. partecipazione di un docente/tutor esperto esterno al gruppo di miglioramento, che validi le attività del gruppo: inserire la domanda, in sezione 3 : "È presente un Tutor esperto esterno che validi le attività del gruppo? scelta tra Sì o No. Se il provider sceglie Sì, il sistema attribuisce l'aumento del credito di 0,3 ora. Se sceglie No, il sistema non deve attribuire l'incremento del credito. Al Docente/tutor esperto non vengono attribuiti crediti.
														c. se il provider ha selezionato nella sezione 1 campo 14, un obiettivo strategico regionale.

					 responsabile scientifico, coordinatore e esperto
					 * ESPERTO						1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * NO RESPONSABILE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * RESPONSABILE_SCIENTIFICO		1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * NO COORDINATORE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * COORDINATORE_X				1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 *
					 * */
					switch(ruolo.getRuoloBase())
					{
						case PARTECIPANTE:
							moltiplicatore = 1f;
							if(evento.getPrevistaRedazioneDocumentoConclusivo() != null && evento.getPrevistaRedazioneDocumentoConclusivo().booleanValue()) {
								moltiplicatore += 0.3f;
							}
							if(evento.getPresenteTutorEspertoEsternoValidatoreAttivita() != null && evento.getPresenteTutorEspertoEsternoValidatoreAttivita().booleanValue()) {
								moltiplicatore += 0.3f;
							}
							if(evento.getObiettivoRegionale() != null && evento.getObiettivoRegionale().getId() != NON_RIENTRA_NEGLI_OBIETTIVI_REGIONALI_ID) {
								moltiplicatore += 0.3f;
							}

							crediti = moltiplicatore * (int) tempoDedicato;
							crediti = (crediti > PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE) ? PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE : crediti;
							break;
						case ESPERTO:
						//case RESPONSABILE:
						case RESPONSABILE_SCIENTIFICO:
						//case COORDINATORE:
							crediti = 1 * (int) (tempoDedicato * 2);
							crediti = (crediti > PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE) ? PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE : crediti;
							break;
						case COORDINATORE_X:
							if(checkIsCoordinatoreEnabled(evento)) {
								crediti = 1 * (int) (tempoDedicato * 2);
								crediti = (crediti > PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE) ? PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE : crediti;
							} else {
								crediti = 0.0f;
							}
							break;
						default:
							crediti = 0.0f;
							break;
					}
				}
				break;




				case ATTIVITA_DI_RICERCA:
				{
					/*
					 * PARTECIPANTI:	sperimentazioni fino a sei mesi: 5 crediti
					 * 					sperimentazioni di durata superiore a sei mesi e fino a dodici mesi: 10 crediti
					 * 					sperimentazioni oltre i dodici mesi, non oltre i 24 mesi: 20 crediti
					 Coordinatore/responsabile scientifico:
					 * NO RESPONSABILE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * RESPONSABILE_SCIENTIFICO		1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * NO COORDINATORE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * COORDINATORE_X				1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * ESPERTO:						1 credito ogni mezz'ora (max 50)
					 * */
					switch(ruolo.getRuoloBase())
					{
						case PARTECIPANTE:
							//evento.getdu
							//if(evento.getDataInizio() != null && evento.getDataFine().isAfter(evento.getDataInizio().plusDays(ecmProperties.getGiorniMaxEventoFSC())))
							if(evento.getDataInizio() != null && evento.getDataFine() != null) {
								//long daysBetween = DAYS.between(evento.getDataInizio(), evento.getDataFine());
								long daysBetween = Duration.between(evento.getDataInizio().atStartOfDay(),evento.getDataFine().plusDays(1).atStartOfDay()).toDays();
								if(daysBetween <= GIORNI_DURATA_FSC_6_MESI) {
									crediti = 5f;
								} else if(daysBetween > GIORNI_DURATA_FSC_6_MESI && daysBetween <= GIORNI_DURATA_FSC_12_MESI) {
									crediti = 10f;
								} else if(daysBetween > GIORNI_DURATA_FSC_12_MESI) {
									crediti = 20f;
								}
							} else {
								crediti = 0.0f;
							}
							break;
						case ESPERTO:
						//case RESPONSABILE:
						case RESPONSABILE_SCIENTIFICO:
						//case COORDINATORE:
							crediti = 1 * (int) (tempoDedicato * 2);
							crediti = (crediti > ATTIVITA_DI_RICERCA_MAX_CREDITI_VERSIONE_DUE) ? ATTIVITA_DI_RICERCA_MAX_CREDITI_VERSIONE_DUE : crediti;
							break;
						case COORDINATORE_X:
							if(checkIsCoordinatoreEnabled(evento)) {
								crediti = 1 * (int) (tempoDedicato * 2);
								crediti = (crediti > ATTIVITA_DI_RICERCA_MAX_CREDITI_VERSIONE_DUE) ? ATTIVITA_DI_RICERCA_MAX_CREDITI_VERSIONE_DUE : crediti;
							} else {
								crediti = 0.0f;
							}
							break;
						default:
							crediti = 0.0f;
							break;
					}
				}
				break;

			default: break;
			}

			crediti = Utils.getRoundedFloatValue(crediti, 1);

		}else{
			crediti = 0.0f;
		}
	}

	@Override
	public boolean equals(Object obj) {
		RiepilogoRuoliFSC second = (RiepilogoRuoliFSC)obj;

		if(second == null)
			return false;

		if(second.getRuolo() == this.ruolo){
			return true;
		}else{
			return false;
		}
	}

	public void calcolaCreditiVersioneTre(EventoFSC evento, float maxValue, boolean isTematicaInteresseSpeciale){
		/*
		public enum RuoloFSCBaseEnum {
			PARTECIPANTE(1,"P"),
			TUTOR(2,"T"),
			ESPERTO(3,"D"),
			COORDINATORE(4,"D"),
			RESPONSABILE(5,"D"),

			//Sono stati aggiunti
			RESPONSABILE_SCIENTIFICO(6,"D"),
			COORDINATORE_X(7,"D");
		}

		public enum RuoloFSCEnum {
			...

			//Sono stati aggiunti
			RESPONSABILE_SCIENTIFICO_A(15,"Responsabile scientifico A", RuoloFSCBaseEnum.RESPONSABILE_SCIENTIFICO),
			RESPONSABILE_SCIENTIFICO_B(16,"Responsabile scientifico B", RuoloFSCBaseEnum.RESPONSABILE_SCIENTIFICO),
			RESPONSABILE_SCIENTIFICO_C(17,"Responsabile scientifico C", RuoloFSCBaseEnum.RESPONSABILE_SCIENTIFICO),

			COORDINATORE_A(18,"Coordinatore A", RuoloFSCBaseEnum.COORDINATORE_X),
			COORDINATORE_B(19,"Coordinatore B", RuoloFSCBaseEnum.COORDINATORE_X),
			COORDINATORE_C(20,"Coordinatore C", RuoloFSCBaseEnum.COORDINATORE_X);
		}
		 */
		TipologiaEventoFSCEnum tipologiaEvento = evento.getTipologiaEventoFSC();
		float moltiplicatore;

		float extraCreditiTematicheInteresse = 0.0f;
		if(isTematicaInteresseSpeciale)
			extraCreditiTematicheInteresse = 0.3f;

		float maxCrediti = 0.0f;

		if(tipologiaEvento != null && ruolo != null){
			switch(tipologiaEvento){
				case TRAINING_INDIVIDUALIZZATO:
					{
						/*
						 * PARTECIPANTI:				1.5 credito ogni ora (max 50) NON FRAZIONABILE
						 * TUTOR:						1 credito ogni ora (max 50)
						 Coordinatore, responsabile scientifico ed esperto:
						 * ESPERTO:						1 credito ogni mezz'ora (max 50)
						 * NO COORDINATORE					1 credito ogni mezz'ora (max 50)
						 * COORDINATORE_X				1 credito ogni mezz'ora (max 50)
						 * NO RESPONSABILE					1 credito ogni mezz'ora (max 50)
						 * RESPONSABILE_SCIENTIFICO		1 credito ogni mezz'ora (max 50)
						 *
						 * */
						switch(ruolo.getRuoloBase())
						{
							case PARTECIPANTE:
								crediti = (1.5f + extraCreditiTematicheInteresse) * (int) tempoDedicato;
								crediti = (crediti > TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_VERSIONE_DUE) ? TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_VERSIONE_DUE : crediti;
								break;
							case TUTOR:
								crediti = 1 * (int) tempoDedicato;

								maxCrediti = (maxValue > TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_TUTOR_VERSIONE_DUE) ? TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_TUTOR_VERSIONE_DUE : maxValue;
								crediti = (crediti > maxCrediti) ? maxCrediti : crediti;

								break;
							case ESPERTO:
							//case COORDINATORE:
							//case RESPONSABILE:
							case RESPONSABILE_SCIENTIFICO:
								crediti = 1 * (int) (tempoDedicato * 2);

								maxCrediti = (maxValue > TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_RESPONSABILESCIENTIFICI_ESPERTO_COORDINATORE_VERSIONE_DUE) ? TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_RESPONSABILESCIENTIFICI_ESPERTO_COORDINATORE_VERSIONE_DUE : maxValue;
								crediti = (crediti > maxCrediti) ? maxCrediti : crediti;

								break;
							case COORDINATORE_X:
								if(checkIsCoordinatoreEnabled(evento)) {
									crediti = 1 * (int) (tempoDedicato * 2);

									maxValue = maxValue * 2; //solo per esperto e coordinatore il limite è il doppio delle ore!
									maxCrediti = (maxValue > TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_RESPONSABILESCIENTIFICI_ESPERTO_COORDINATORE_VERSIONE_DUE) ? TRAINING_INDIVIDUALIZZATO_MAX_CREDITI_RESPONSABILESCIENTIFICI_ESPERTO_COORDINATORE_VERSIONE_DUE : maxValue;
									crediti = (crediti > maxCrediti) ? maxCrediti : crediti;

								} else {
									crediti = 0.0f;
								}
								break;
							default:
								crediti = 0.0f;
								break;
						}
					}
					break;

				case GRUPPI_DI_MIGLIORAMENTO:
					{
						/*
						 * PARTECIPANTI:				1 credito ogni ora (max 50) NON FRAZIONABILE
						 * 								se in "Tipologia gruppo" non è selezionato il valore "Comitati aziendali permanenti" allora
														incremento di 0,3 crediti per ora, cumulabili tra loro nel caso di:
															a. redazione di un documento conclusivo quale ad es. linee guida, procedure, protocolli, indicazioni operative: inserire in sezione 3 un nuovo campo (finalizzato all'incremento crediti per partecipanti) con la seguente domanda: "È prevista la redazione di un documento conclusivo quale ad es. linee guida, procedure, protocolli, indicazioni operative?" Scelta tra Sì o No. Se il provider sceglie Sì, attribuire l'incremento del credito di 0,3 crediti/ora. Se sceglie No, la piattaforma non deve attribuire l'incremento del credito.
															b. partecipazione di un docente/tutor esperto esterno al gruppo di miglioramento, che validi le attività del gruppo: inserire la domanda, in sezione 3 : "È presente un Tutor esperto esterno che validi le attività del gruppo? scelta tra Sì o No. Se il provider sceglie Sì, il sistema attribuisce l'aumento del credito di 0,3 ora. Se sceglie No, il sistema non deve attribuire l'incremento del credito. Al Docente/tutor esperto non vengono attribuiti crediti.

						 Responsabile scientifico/coordinatore
						 * NO RESPONSABILE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 * RESPONSABILE_SCIENTIFICO		1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 * NO COORDINATORE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 * COORDINATORE_X				1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 * ESPERTO:						1 credito ogni mezz'ora (max 50)
						 *
						 * */
						switch(ruolo.getRuoloBase())
						{
							case PARTECIPANTE:
								if(evento.getTipologiaGruppo() != null && evento.getTipologiaGruppo() != TipologiaGruppoFSCEnum.COMITATI_AZIENDALI_PERMANENTI) {
									moltiplicatore = 1f;
									if(evento.getPrevistaRedazioneDocumentoConclusivo() != null && evento.getPrevistaRedazioneDocumentoConclusivo().booleanValue()) {
										moltiplicatore += 0.3f;
									}
									if(evento.getPresenteTutorEspertoEsternoValidatoreAttivita() != null && evento.getPresenteTutorEspertoEsternoValidatoreAttivita().booleanValue()) {
										moltiplicatore += 0.3f;
									}
								} else {
									//evento.getTipologiaGruppo() == null || evento.getTipologiaGruppo() == TipologiaGruppoFSCEnum.COMITATI_AZIENDALI_PERMANENTI
									moltiplicatore = 1f;
								}

								crediti = (moltiplicatore + extraCreditiTematicheInteresse) * (int) tempoDedicato;
								crediti = (crediti > GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE) ? GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE : crediti;
								break;

							//case RESPONSABILE:
							case RESPONSABILE_SCIENTIFICO:
							//case COORDINATORE:
							case ESPERTO:
								crediti = 1 * (int) (tempoDedicato * 2);

								maxValue = maxValue * 2; //solo per esperto e coordinatore il limite è il doppio delle ore!
								maxCrediti = (maxValue > GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE) ? GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE : maxValue;
								crediti = (crediti > maxCrediti) ? maxCrediti : crediti;

								break;
							case COORDINATORE_X:
								if(checkIsCoordinatoreEnabled(evento)) {
									crediti = 1 * (int) (tempoDedicato * 2);

									maxValue = maxValue * 2; //solo per esperto e coordinatore il limite è il doppio delle ore!
									maxCrediti = (maxValue > GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE) ? GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE : maxValue;
									crediti = (crediti > maxCrediti) ? maxCrediti : crediti;

								} else {
									crediti = 0.0f;
								}
								break;
							default:
								crediti = 0.0f;
								break;
						}
					}
					break;

				case AUDIT_CLINICO_ASSISTENZIALE:
					{
						/*
						 * PARTECIPANTI:				1 credito ogni ora (max 50) NON FRAZIONABILE
														se in "Tipologia gruppo" non è selezionato il valore "Comitati aziendali permanenti" allora
														incremento di 0,3 crediti per ora, cumulabili tra loro nel caso di:
															a. Partecipazione di un docente/tutor esperto, esterno al gruppo di miglioramento, che validi le attività del gruppo: Inserire la domanda, in sezione 3: "È presente un Tutor esperto esterno che validi le attività del gruppo? scelta tra Sì o NO. Se il provider sceglie SI il sistema attribuisce l'aumento del credito di 0,3 ora. Se sceglie No il sistema non deve attribuire l'incremento del credito.
						responsabile scientifico e coordinatore
						 * NO RESPONSABILE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 * RESPONSABILE_SCIENTIFICO		1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 * NO COORDINATORE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 * COORDINATORE_X				1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
						 *
						 * */
						switch(ruolo.getRuoloBase())
						{
							case PARTECIPANTE:
								moltiplicatore = 1f + extraCreditiTematicheInteresse;
								if(evento.getPresenteTutorEspertoEsternoValidatoreAttivita() != null && evento.getPresenteTutorEspertoEsternoValidatoreAttivita().booleanValue()) {
									moltiplicatore += 0.3f;
								}

								crediti = moltiplicatore * (int) tempoDedicato;
								crediti = (crediti > AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_DUE) ? AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_DUE : crediti;
								break;
							//case RESPONSABILE:
							case RESPONSABILE_SCIENTIFICO:
							//case COORDINATORE:
								crediti = 1 * (int) (tempoDedicato * 2);

								maxCrediti = (maxValue > AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_DUE) ? AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_DUE : maxValue;
								crediti = (crediti > maxCrediti) ? maxCrediti : crediti;

								break;
							case COORDINATORE_X:
								if(checkIsCoordinatoreEnabled(evento)) {
									crediti = 1 * (int) (tempoDedicato * 2);

									maxValue = maxValue * 2; //solo per esperto e coordinatore il limite è il doppio delle ore!
									maxCrediti = (maxValue > AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_DUE) ? AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI_VERSIONE_DUE : maxValue;
									crediti = (crediti > maxCrediti) ? maxCrediti : crediti;

								} else {
									crediti = 0.0f;
								}
								break;
							default:
								crediti = 0.0f;
								break;
						}
					}
					break;
				case PROGETTI_DI_MIGLIORAMENTO:
				{
					/*
					 * PARTECIPANTI:				1 credito ogni ora (max 50) NON FRAZIONABILE
													se in "Tipologia gruppo" non è selezionato il valore "Comitati aziendali permanenti" allora
													incremento di 0,3 crediti per ora, cumulabili tra loro nel caso di:
														a. redazione di un documento conclusivo quale ad es. linee guida, procedure, protocolli, indicazioni operative: inserire in sezione 3 un nuovo campo (finalizzato all'incremento crediti per partecipanti) con la seguente domanda: "È prevista la redazione di un documento conclusivo quale ad es. linee guida, procedure, protocolli, indicazioni operative?" Scelta tra Sì o No. Se il provider sceglie Sì, attribuire l'incremento del credito di 0,3 crediti/ora. Se sceglie No, la piattaforma non deve attribuire l'incremento del credito.
														b. partecipazione di un docente/tutor esperto esterno al gruppo di miglioramento, che validi le attività del gruppo: inserire la domanda, in sezione 3 : "È presente un Tutor esperto esterno che validi le attività del gruppo? scelta tra Sì o No. Se il provider sceglie Sì, il sistema attribuisce l'aumento del credito di 0,3 ora. Se sceglie No, il sistema non deve attribuire l'incremento del credito. Al Docente/tutor esperto non vengono attribuiti crediti.

					 responsabile scientifico, coordinatore e esperto
					 * ESPERTO						1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * NO RESPONSABILE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * RESPONSABILE_SCIENTIFICO		1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * NO COORDINATORE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * COORDINATORE_X				1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 *
					 * */
					switch(ruolo.getRuoloBase())
					{
						case PARTECIPANTE:
							moltiplicatore = 1f + extraCreditiTematicheInteresse;
							if(evento.getPrevistaRedazioneDocumentoConclusivo() != null && evento.getPrevistaRedazioneDocumentoConclusivo().booleanValue()) {
								moltiplicatore += 0.3f;
							}
							if(evento.getPresenteTutorEspertoEsternoValidatoreAttivita() != null && evento.getPresenteTutorEspertoEsternoValidatoreAttivita().booleanValue()) {
								moltiplicatore += 0.3f;
							}

							crediti = moltiplicatore * (int) tempoDedicato;
							crediti = (crediti > PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE) ? PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE : crediti;
							break;
						case ESPERTO:
						//case RESPONSABILE:
						case RESPONSABILE_SCIENTIFICO:
						//case COORDINATORE:
							crediti = 1 * (int) (tempoDedicato * 2);

							maxCrediti = (maxValue > PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE) ? PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE : maxValue;
							crediti = (crediti > maxCrediti) ? maxCrediti : crediti;

							break;
						case COORDINATORE_X:
							if(checkIsCoordinatoreEnabled(evento)) {
								crediti = 1 * (int) (tempoDedicato * 2);

								maxValue = maxValue * 2; //solo per esperto e coordinatore il limite è il doppio delle ore!
								maxCrediti = (maxValue > PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE) ? PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE : maxValue;
								crediti = (crediti > maxCrediti) ? maxCrediti : crediti;

							} else {
								crediti = 0.0f;
							}
							break;
						default:
							crediti = 0.0f;
							break;
					}
				}
				break;




				case ATTIVITA_DI_RICERCA:
				{
					/*
					 * PARTECIPANTI:	sperimentazioni fino a sei mesi: 5 crediti
					 * 					sperimentazioni di durata superiore a sei mesi e fino a dodici mesi: 10 crediti
					 * 					sperimentazioni oltre i dodici mesi, non oltre i 24 mesi: 20 crediti
					 Coordinatore/responsabile scientifico:
					 * NO RESPONSABILE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * RESPONSABILE_SCIENTIFICO		1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * NO COORDINATORE					1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * COORDINATORE_X				1 credito ogni mezz'ora (max 50) solo se fanno docenza, solo quelli che fanno docenza si possono selezionare in eszione 2
					 * ESPERTO:						1 credito ogni mezz'ora (max 50)
					 * */
					switch(ruolo.getRuoloBase())
					{
						case PARTECIPANTE:
							//evento.getdu
							//if(evento.getDataInizio() != null && evento.getDataFine().isAfter(evento.getDataInizio().plusDays(ecmProperties.getGiorniMaxEventoFSC())))
							if(evento.getDataInizio() != null && evento.getDataFine() != null) {
								//long daysBetween = DAYS.between(evento.getDataInizio(), evento.getDataFine());
								long daysBetween = Duration.between(evento.getDataInizio().atStartOfDay(),evento.getDataFine().plusDays(1).atStartOfDay()).toDays();
								if(daysBetween <= GIORNI_DURATA_FSC_6_MESI) {
									crediti = 5f;
								} else if(daysBetween > GIORNI_DURATA_FSC_6_MESI && daysBetween <= GIORNI_DURATA_FSC_12_MESI) {
									crediti = 10f;
								} else if(daysBetween > GIORNI_DURATA_FSC_12_MESI) {
									crediti = 20f;
								}
							} else {
								crediti = 0.0f;
							}
							break;
						case ESPERTO:
						//case RESPONSABILE:
						case RESPONSABILE_SCIENTIFICO:
						//case COORDINATORE:
							crediti = 1 * (int) (tempoDedicato * 2);

							maxCrediti = (maxValue > ATTIVITA_DI_RICERCA_MAX_CREDITI_VERSIONE_DUE) ? PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE : maxValue;
							crediti = (crediti > maxCrediti) ? maxCrediti : crediti;

							break;
						case COORDINATORE_X:
							if(checkIsCoordinatoreEnabled(evento)) {
								crediti = 1 * (int) (tempoDedicato * 2);

								maxValue = maxValue * 2; //solo per esperto e coordinatore il limite è il doppio delle ore!
								maxCrediti = (maxValue > ATTIVITA_DI_RICERCA_MAX_CREDITI_VERSIONE_DUE) ? PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI_VERSIONE_DUE : maxValue;
								crediti = (crediti > maxCrediti) ? maxCrediti : crediti;

							} else {
								crediti = 0.0f;
							}
							break;
						default:
							crediti = 0.0f;
							break;
					}
				}
				break;

			default: break;
			}

			crediti = Utils.getRoundedFloatValue(crediti, 1);

		}else{
			crediti = 0.0f;
		}
	}




}
