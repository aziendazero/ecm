package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.Set;

import it.tredi.ecm.dao.entity.AnagrafeRegionaleCrediti;

public interface PdfAnagrafeRegionaleService {

	ByteArrayOutputStream creaOutputStreamPdfAnagrafeRegionale(String codiceFiscale, Integer annoRiferimento) throws Exception;

}
