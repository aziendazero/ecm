package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.PianoFormativo;

public interface PianoFormativoService {

	public boolean exist(Long providerId, Integer annoPianoFormativo);
	public PianoFormativo create(Long providerId, Integer annoPianoFormativo);
	public void save(PianoFormativo pianoFormativo);

	public PianoFormativo getPianoFormativo(Long pianoFormativoId);
	public Set<PianoFormativo> getAllPianiFormativiForProvider(Long providerId);
	public PianoFormativo getPianoFormativoAnnualeForProvider(Long providerId, Integer annoPianoFormativo);
	public boolean isPianoModificabile(Long pianoFormativoId);
	public Set<Long> getAllPianiFormativiIdInAccreditamentoForProvider(Long providerId);
	public void importaEventiDaCSV(Long pianoFormativoId, File importEventiDaCsvFile) throws Exception;
}
