package it.tredi.ecm.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.repository.FileRepository;

@Service
public class FileServiceImpl implements FileService{
	private static Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);
	
	@Autowired
	private FileRepository fileRepository;
	
	@Override
	public File getFile(Long id) {
		LOGGER.debug("Getting file with id: " + id);
		return fileRepository.findOne(id);
	}

	@Override
	public Set<File> getFileFromProvider(Long providerId) {
		LOGGER.debug("Getting files for provider: " + providerId);
		return fileRepository.findByProviderId(providerId);
	}

	@Override
	public Set<File> getAll() {
		LOGGER.debug("Getting all files");
		return fileRepository.findAll();
	}
	
	@Override
	public HashMap<String, Long> getModelIds() {
		LOGGER.debug("Getting Model ids");
		Set<File> files = fileRepository.findModelFiles("model_");
		HashMap<String, Long> fileIds = new HashMap<String, Long>();
		
		for(File file : files){
			fileIds.put(file.getTipo(), file.getId());
		}
		
		return fileIds;
	}
	
	@Override
	public Set<String> checkFileExists(Long providerId, List<String> tipoFile) {
		Set<File> files = fileRepository.findByProviderId(providerId);
		Set<String> existsFile = new HashSet<String>();
		for (File file : files){
			if(tipoFile.contains(file.getTipo()))
				existsFile.add(file.getTipo());
		}
		return existsFile;
	}

	@Override
	//@Transactional
	public void save(File file) {
		LOGGER.debug("Saving file: " + file.getNomeFile());
		LOGGER.debug("Saving file: " + file.getData().length + " bytes");
		try{
			fileRepository.save(file);
		}catch (Exception ex){
			LOGGER.error("Errore durante salvataggio file", ex);
			throw ex;
		}
	}
	
	@Override
	@Transactional
	public void deleteByPersonaId(Long personaId) {
		LOGGER.debug("Eliminazione degli allegati della persona " + personaId);
		//fileRepository.deleteByPersonaId(personaId);
	}
}
