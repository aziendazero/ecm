package it.tredi.ecm.scheduledtask;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.tredi.ecm.service.ProviderService;


@Component
public class UpdateDateProviderTask {
	private static Logger LOGGER = LoggerFactory.getLogger(UpdateDateProviderTask.class);

	@Autowired private ProviderService providerService;

	@Async
	@Transactional
	public void updateDateScadenza() {
		LOGGER.info(Thread.currentThread().getName());
		LOGGER.info("updateDataPianoFormativo - entering");

		providerService.eseguiUpdateDataPianoFormativo();
		providerService.eseguiUpdateDataDomandaStandard();
		providerService.eseguiUpdateDataRelazioneAnnuale();

		LOGGER.info("updateDataPianoFormativo - exiting");
	}

}

