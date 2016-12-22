package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;

import it.tredi.ecm.dao.entity.Accreditamento;

public interface PdfVerbaleService {
	ByteArrayOutputStream creaOutputSteramPdfVerbale(Accreditamento accreditamento) throws Exception;
}
