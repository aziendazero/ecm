package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Protocollo;
import it.tredi.ecm.dao.enumlist.MotivazioneDecadenzaEnum;

public interface ProtocolloService {

	public Protocollo getProtollo(Long id);
	public Set<Protocollo> getAllProtocolli();
	public Set<Protocollo> getAllProtocolliInUscitaErrati();

	public void protocollaDomandaInArrivo(Long accreditamentoId, Long fileId) throws Exception;
	public void protocollaAllegatoFlussoDomandaInUscita(Long accreditamentoId, Long fileId)  throws Exception;

	//metodi da richiamare nei TASK SCHEDULER
	public void protoBatchLog() throws Exception;
	public void getStatoSpedizione() throws Exception;
	public void protocollaBloccoProviderInUscita(Long providerId, File fileDaProtocollare, MotivazioneDecadenzaEnum motivazione) throws Exception;
	public void protocollaAllegatoFlussoDomandaInUscita(Long accreditamentoId, Long fileId, Set<Long> fileAllegatiIds)	throws Exception;
	public void protocollaDomandaInArrivo(Long accreditamentoId, Long fileId, Set<Long> fileAllegatiIds) throws Exception;

	public void annullaProtocollo(Long oldProtocolloId) throws Exception;
	public void rieseguiProtocollo(Long oldProtocolloId)  throws Exception;
}
