package it.tredi.ecm.service;

import java.util.HashMap;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.enumlist.FileEnum;

public interface FileService {
	public File getFile(Long id);
	public void save(File file);
	public HashMap<FileEnum,Long> getModelFileIds();
	public File copyFile(File file) throws CloneNotSupportedException;
	public void deleteById(Long id);
}