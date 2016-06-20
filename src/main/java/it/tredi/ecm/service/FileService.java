package it.tredi.ecm.service;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import it.tredi.ecm.dao.entity.File;

public interface FileService {
	public File getFile(Long id);
	public Set<File> getFileFromProvider(Long providerId);
	public Set<File> getAll();
	public void save(File file);
	public void deleteByPersonaId(Long personaId);
	
	public HashMap<String,Long> getModelIds();
	public Set<String> checkFileExists(Long providerId, List<String> tipoFile);
}
