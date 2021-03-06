package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.dao.entity.Provider;

public interface PianoFormativoService {

	public boolean exist(Long providerId, Integer annoPianoFormativo);
	public PianoFormativo create(Long providerId, Integer annoPianoFormativo);
	public void save(PianoFormativo pianoFormativo);

	public PianoFormativo getPianoFormativo(Long pianoFormativoId);
	public Set<PianoFormativo> getAllPianiFormativiForProvider(Long providerId);
	public PianoFormativo getPianoFormativoAnnualeForProvider(Long providerId, Integer annoPianoFormativo);
	public boolean isPianoModificabile(Long pianoFormativoId);
	public Set<Long> getAllPianiFormativiIdInAccreditamentoForProvider(Long providerId);

	public void importaEventiDaCSV(Long pianoFormativoId, File importEventiDaCsvFile, Long accreditamentoId) throws Exception;

	public void removeEventoFrom(Long eventoPianoFormatvioId, Long pianoFormativoId) throws Exception;

	public void addEventoTo(Long providerId, int annoPianoFormativo, Long eventoPianoFormativoId) throws Exception;
	public void removeEventoFromIfNotNativo(Long providerId, int annoPianoFormativo, Long eventoPianoFormativoId) throws Exception;


	public Set<Provider> getAllProviderNotPianoFormativoInseritoPerAnno();
	public int countProviderNotPianoFormativoInseritoPerAnno();
}
