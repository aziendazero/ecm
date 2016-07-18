package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.PianoFormativo;

public interface PianoFormativoService {

	public boolean exist(Long providerId, Integer annoPianoFormativo);
	public void create(Long providerId, Integer annoPianoFormativo);
	public void save(PianoFormativo pianoFormativo);
	
	public PianoFormativo getPianoFormativo(Long pianoFormativoId);
	public Set<PianoFormativo> getAllPianiFormativiForProvider(Long providerId);
	public PianoFormativo getPianoFormativoAnnualeForProvider(Long providerId, Integer annoPianoFormativo);
	public boolean isEditabile(Long pianoFormativoId);
}
