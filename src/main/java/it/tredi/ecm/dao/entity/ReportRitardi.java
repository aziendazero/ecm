package it.tredi.ecm.dao.entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import it.tredi.ecm.dao.enumlist.MotivazioneProrogaEnum;
import lombok.Getter;
import lombok.Setter;

/*
 * Classe per la reportistica dei ritardi e dei slittamenti delle scadenze dei Provider
 * */

@Getter
@Setter
@Entity
public class ReportRitardi extends BaseEntityDefaultId {
	@Enumerated(EnumType.STRING)
	private MotivazioneProrogaEnum motivazioneProroga;
	private Long objectRefId;
	private LocalDate dataOriginale;
	private LocalDate dataRinnovata;
	private LocalDate dataProroga;
	private	boolean ritardo;
	private long providerId;

	public ReportRitardi(MotivazioneProrogaEnum motivazione, Long objectRefId, LocalDate dataOriginale, LocalDate dataRinnovata, LocalDate dataProroga, boolean possibileRitardo, long providerId) {
		this.motivazioneProroga = motivazione;
		this.objectRefId = objectRefId;
		this.dataOriginale = dataOriginale;
		this.dataRinnovata = dataRinnovata;
		this.dataProroga = dataProroga;
		this.providerId = providerId;
		//double check per il ritardo dopo aver controllato che la situazione permetta un ritardo
		//(es. evento già pagato -> possibileRitardo = false)
		//e ritardo solo se slittamento ad azione scaduta
		this.ritardo = dataOriginale != null && dataProroga.isAfter(dataOriginale) && possibileRitardo;
	}
}
