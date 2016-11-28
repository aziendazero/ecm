package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;

import it.tredi.ecm.web.bean.EventoWrapper;

public interface PdfEventoService {
	public ByteArrayOutputStream creaOutputStreamPdfEvento(EventoWrapper wrapper) throws Exception;
}
