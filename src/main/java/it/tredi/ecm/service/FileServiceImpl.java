package it.tredi.ecm.service;

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
	public Set<File> getFileFromPersona(Long personaId) {
		LOGGER.debug("Getting files for user: " + personaId);
		return fileRepository.findByPersonaId(personaId);
	}
	
	@Override
	public Set<File> getFileFromProvider(Long providerId) {
		LOGGER.debug("Getting files for provider: " + providerId);
		return fileRepository.findByProviderId(providerId);
	}
	
	@Override
	public File getFileFromPersonaByTipo(Long personaId, String tipo) {
		LOGGER.debug("Getting files " + tipo + " for user: " + personaId);
		return fileRepository.findOneByPersonaIdAndTipo(personaId, tipo);
	}

	@Override
	public Set<File> getAll() {
		LOGGER.debug("Getting all files");
		return fileRepository.findAll();
	}

	@Override
	@Transactional
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
}
