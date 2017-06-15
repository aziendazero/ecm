package it.tredi.ecm.service;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.ReportRitardi;
import it.tredi.ecm.dao.enumlist.MotivazioneProrogaEnum;
import it.tredi.ecm.dao.repository.ReportRitardiRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class ReportRitardiServiceImpl implements ReportRitardiService {
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportRitardiServiceImpl.class);
	@Autowired private ReportRitardiRepository reportRitardiRepository;

	@Override
	public void createReport(MotivazioneProrogaEnum motivazione, Long idObjRef, LocalDate dataOriginale,
			LocalDate dataRinnovata, LocalDate dataProroga, boolean possibileRitardo, Long providerId) {
		LOGGER.debug(Utils.getLogMessage("Creazione Report Ritardo: " + motivazione + " per il provider: " + providerId));
		ReportRitardi report = new ReportRitardi(motivazione, idObjRef, dataOriginale, dataRinnovata, dataProroga, possibileRitardo, providerId);
		reportRitardiRepository.save(report);
	}

}
