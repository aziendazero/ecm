package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.pdf.PdfRiepilogoPartecipantiInfo;

public interface PdfRiepilogoPartecipantiService {

	public ByteArrayOutputStream creaOutputSteramPdfRiepilogoPartecipanti(PdfRiepilogoPartecipantiInfo pdfRiepilogoPartecipantiInfo, String codiceEvento) throws Exception;

	public ByteArrayOutputStream creaOutputSteramPdfAttestatiPartecipanti(PdfRiepilogoPartecipantiInfo pdfRiepilogoPartecipantiInfo, Evento evento) throws Exception;

}
