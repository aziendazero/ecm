package it.tredi.ecm.service;

import java.util.Set;

import it.tredi.ecm.dao.entity.File;

public interface FileService {
	public File getFile(Long id);
	public Set<File> getFileFromPersona(Long personaId);
	public Set<File> getFileFromProvider(Long providerId);
	public File getFileFromPersonaByTipo(Long personaId, String tipo);
	public Set<File> getAll();
	public void save(File file);
}
