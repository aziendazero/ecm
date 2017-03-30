package it.tredi.ecm.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.cogeaps.XmlReportValidator;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.FileData;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.repository.FileRepository;
import it.tredi.ecm.service.bean.EcmProperties;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;

@Service
public class FileServiceImpl implements FileService{
	private static Logger LOGGER = LoggerFactory.getLogger(FileServiceImpl.class);

	@Autowired private FileRepository fileRepository;
	@Autowired private EcmProperties ecmProperties;

	@Override
	public File getFile(Long id) {
		LOGGER.debug("Recupero file id: " + id);
		return fileRepository.findOne(id);
	}

	@Override
	public HashMap<FileEnum, Long> getModelFileIds() {
		LOGGER.debug("Caricamento degli id dei modelli dei file");
		List<Object[]> resultSet = fileRepository.findModelFilesIds(new HashSet<FileEnum>(Arrays.asList(FileEnum.FILE_MODELLO_ATTO_COSTITUTIVO,
																										FileEnum.FILE_MODELLO_ATTO_COSTITUTIVO,
																										FileEnum.FILE_MODELLO_DICHIARAZIONE_LEGALE,
																										FileEnum.FILE_MODELLO_ESPERIENZA_FORMAZIONE,
																										FileEnum.FILE_MODELLO_PIANO_QUALITA,
																										FileEnum.FILE_MODELLO_SISTEMA_INFORMATICO,
																										FileEnum.FILE_MODELLO_UTILIZZO)));
		 HashMap<FileEnum, Long> fileIds = new HashMap<FileEnum, Long>();
		 for(Object[] obj : resultSet){
			 fileIds.put((FileEnum)obj[0], (Long)obj[1]);
		 }

		 return fileIds;
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

	public void saveFileSuDisco(File file){

	}

	@Override
	public File copyFile(File file) throws CloneNotSupportedException {
		if(file != null) {
			LOGGER.debug(Utils.getLogMessage("Clonazione File id: " + file.getId()));
			File fileCopiato = (File) file.clone();
			save(fileCopiato);
			LOGGER.debug(Utils.getLogMessage("File Clonato id: " + fileCopiato.getId()));
			return fileCopiato;
		}
		else return null;
	}

	@Override
	public byte[] sbustaP7mById(Long fileId) throws Exception {
		File file = getFile(fileId);

		if(file == null)
			throw new Exception("File: " + fileId + " is null");

		String fileName = file.getNomeFile().trim().toUpperCase();

		byte[] xmlSbustato = null;
		if (fileName.endsWith(".XML") || fileName.endsWith(".XML.P7M") || fileName.endsWith(".XML.ZIP.P7M")) {
			xmlSbustato = XmlReportValidator.extractXml(fileName, file.getData());
		}
		return xmlSbustato;
	}

//	@Override
//	public void deleteById(Long id) {
//		fileRepository.delete(id);
//	}
}
