package it.tredi.ecm.service;

import java.time.LocalDate;

import it.tredi.ecm.dao.enumlist.MotivazioneProrogaEnum;

public interface ReportRitardiService {

	void createReport(MotivazioneProrogaEnum motivazione, Long idObjRef, LocalDate dataOriginale, LocalDate dataRinnovata,
			LocalDate dataProroga, boolean possibileRitardo, Long providerId);

}
