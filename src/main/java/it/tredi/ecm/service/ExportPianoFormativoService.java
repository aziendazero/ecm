package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;

import it.tredi.ecm.dao.entity.PianoFormativo;

public interface ExportPianoFormativoService {

	public ByteArrayOutputStream creaOutputSteramPdfExportPianoFormativo(PianoFormativo pf) throws Exception;
	public ByteArrayOutputStream creaOutputSteramCsvExportPianoFormativo(PianoFormativo pf) throws Exception;

}
