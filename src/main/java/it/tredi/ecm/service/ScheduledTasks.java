package it.tredi.ecm.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import it.tredi.ecm.scheduledtask.AlertScadenzeTask;
import it.tredi.ecm.scheduledtask.InvioCogeapsTask;
import it.tredi.ecm.scheduledtask.PagamentoTask;
import it.tredi.ecm.scheduledtask.ProtocolloTask;
import it.tredi.ecm.scheduledtask.SedutaTask;
import it.tredi.ecm.scheduledtask.UpdateDateProviderTask;
import it.tredi.ecm.service.bean.EcmProperties;
/*
 * Scheduler che esegue i task automatici con periodicità fissata (fixedDelay)
 *
 * 1) Bisogna implementare un XXXTask per ogni funzionalità e dichiarare i metodi del task @Transactional e @Async
 * 2) Fare l'injection di xxxTask e aggiungere la chiamata nel metodo taskExecutor
 *
 * NOTE:
 *
 * @Async: crea un thread per ogni metodo
 * @Transactional: e' indispensabile perche' essendo il metodo in @Async in un thread diverso dallo springContext
 * la sessione di hibernate risulta sganciata e quindi si avranno una serie di lazy initialization error a RUN-TIME
 * Non si possono annotare @Transactional direttamente i metodi perche' si spaccano alcuni meccanismi dell'autowired nei bootloader
 * */
@Component
public class ScheduledTasks {

	@Autowired private PagamentoTask pagamentoTask;
	@Autowired private InvioCogeapsTask invioCogeapsTask;
	@Autowired private ProtocolloTask protocolloTask;
	@Autowired private SedutaTask sedutaTask;
	@Autowired private AlertScadenzeTask alertScadenzeTask;
	@Autowired private EcmProperties ecmProperties;
	@Autowired private UpdateDateProviderTask updateDateProviderTask;

	@Scheduled(fixedDelay=60000)
	public void taskExecutor() throws Exception{
		pagamentoTask.controllaEsitoPagamenti();
		invioCogeapsTask.checkStatoElaborazioneCogeaps();
		protocolloTask.controllaStatoProtocollazione();
		sedutaTask.bloccaSedute();
		updateDateProviderTask.updateDateScadenza();
		if(ecmProperties.isTaskSendAlertEmail())
			alertScadenzeTask.inviaAlert();
	}
}
