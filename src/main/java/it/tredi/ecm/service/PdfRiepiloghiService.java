package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;

public interface PdfRiepiloghiService {

	public ByteArrayOutputStream creaOutputStreamPdfRiepilogoDomanda(Long accreditamentoId, String argument, Long valutazioneId) throws Exception;

}
