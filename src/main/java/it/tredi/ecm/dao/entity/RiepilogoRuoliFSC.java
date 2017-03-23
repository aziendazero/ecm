package it.tredi.ecm.dao.entity;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.javers.core.metamodel.annotation.TypeName;

import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.utils.Utils;
import lombok.Getter;
import lombok.Setter;

@TypeName("RiepilogoRuoliFSC")
@Getter
@Setter
@Embeddable
public class RiepilogoRuoliFSC {

	public static final float TRAINING_INDIVIDUALIZZATO_MAX_CREDITI = 30f;
	public static final float GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI = 50f;
	public static final float PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI = 50f;
	public static final float ATTIVITA_DI_RICERCA_MAX_CREDITI = 3f;
	public static final float AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI = 50f;

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

	public void calcolaCrediti(TipologiaEventoFSCEnum tipologiaEvento, float f){
		if(tipologiaEvento != null && ruolo != null){
			switch(tipologiaEvento){
				case TRAINING_INDIVIDUALIZZATO:
					{
						/*
						 * PARTECIPANTI:	1 credito ogni ora (max 30) NON FRAZIONABILE
						 * TUTOR:			1 credito ogni 6 ore
						 * ESPERTO:			1 credito ogni ora (max 'crediti evento')
						 * COORDINATORE		1 credito ogni ora (max 'crediti evento')
						 *
						 * */
						switch(ruolo.getRuoloBase())
						{
							case PARTECIPANTE:  crediti = 1 * (int) tempoDedicato;
												//crediti = (int) crediti;
												crediti = (crediti > TRAINING_INDIVIDUALIZZATO_MAX_CREDITI) ? TRAINING_INDIVIDUALIZZATO_MAX_CREDITI : crediti;
								break;

							case TUTOR: crediti = 1 * (int) (tempoDedicato/6) ;
								break;

							case ESPERTO: 	crediti = 1 * (int) tempoDedicato;
											crediti = (crediti > f) ? f : crediti;
								break;

							case COORDINATORE: 	crediti = 1 * (int) tempoDedicato;
												crediti = (crediti > f) ? f : crediti;
								break;

							default:	crediti = 0.0f;
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
											crediti = (crediti > GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI) ? GRUPPI_DI_MIGLIORAMENTO_MAX_CREDITI : crediti;
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
					 * ESPERTO:			1 credito ongi ora (max 'crediti evento')
					 * COORDINATORE:	1 credito ongi ora (max 'crediti evento')
					 * RESPONSABILE:	1 credito ongi ora (max 'crediti evento')
					 *
					 * */
					switch(ruolo.getRuoloBase())
					{
						case PARTECIPANTE:  crediti = 0.5f * (int) tempoDedicato;
											//crediti = (int) crediti;
											crediti = (crediti > PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI) ? PROGETTI_DI_MIGLIORAMENTO_MAX_CREDITI : crediti;

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
					 * COORDINATORE:	1 credito ongi ora (max 'crediti evento')
					 *
					 * */
					switch(ruolo.getRuoloBase())
					{
						case PARTECIPANTE: crediti = ATTIVITA_DI_RICERCA_MAX_CREDITI;
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
											crediti = (crediti > AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI) ? AUDIT_CLINICO_ASSISTENZIALE_MAX_CREDITI : crediti;
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
}
