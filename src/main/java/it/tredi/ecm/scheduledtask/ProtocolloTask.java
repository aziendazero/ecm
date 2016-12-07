package it.tredi.ecm.scheduledtask;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import it.tredi.ecm.service.ProtocolloService;

@Component
public class ProtocolloTask {
	private static Logger LOGGER = LoggerFactory.getLogger(ProtocolloTask.class);

	@Autowired ProtocolloService protocolloService;

	@Async
	@Transactional
	public void controllaStatoProtocollazione() throws Exception{
		LOGGER.info(Thread.currentThread().getName());
		LOGGER.info("controllaStatoProtocollazione - entering");

		protocolloService.protoBatchLog();
		protocolloService.getStatoSpedizione();

		LOGGER.info("controllaStatoProtocollazione - exiting");
	}
}
