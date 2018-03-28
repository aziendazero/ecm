package it.tredi.ecm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.transaction.Transactional;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.PianoFormativo;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.PdfEventoService;
import it.tredi.ecm.service.ExportPianoFormativoService;
import it.tredi.ecm.service.PianoFormativoService;
import it.tredi.ecm.service.WorkflowServiceImpl;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoWrapper;
import it.tredi.ecm.web.bean.PianoFormativoWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("demo")
@WithUserDetails("test1")
//@Rollback(false)
@Ignore
public class PdfExportPianoFormativoTest {
	private static Logger LOGGER = LoggerFactory.getLogger(PdfExportPianoFormativoTest.class);

	@Autowired private ExportPianoFormativoService exportPfService;
	@Autowired private PianoFormativoService pfService;

	@Test
	//@Ignore
	@Transactional
	public void creazionePdf() throws Exception {
		//Long pfId = 23886L;
		Long pfId = 23889L;
		//Long pfId = 32923L;
		
		
		PianoFormativo pf = pfService.getPianoFormativo(pfId);
		
		ByteArrayOutputStream pdfOutputStream = exportPfService.creaOutputSteramPdfExportPianoFormativo(pf);
		String fileName = "pf_" + pf.getId() + ".pdf";
		saveFile(fileName, pdfOutputStream);
		System.out.println("FILE CREATO");
	}
	
	@Test
	//@Ignore
	@Transactional
	public void creazioneCsv() throws Exception {
		//Long pfId = 23886L;
		Long pfId = 23889L;
		//Long pfId = 32923L;
		
		PianoFormativo pf = pfService.getPianoFormativo(pfId);
		
		ByteArrayOutputStream pdfOutputStream = exportPfService.creaOutputSteramCsvExportPianoFormativo(pf);
		String fileName = "pf_" + pf.getId() + ".csv";
		saveFile(fileName, pdfOutputStream);
		System.out.println("FILE CREATO");
	}

	private void saveFile(String fileName, ByteArrayOutputStream pdfOutputStream) throws Exception {
    	String pathFileEvento = "C:\\tmp\\ECM\\" + fileName;
        OutputStream outputStreamEvento = new FileOutputStream(new File(pathFileEvento));
        pdfOutputStream.writeTo(outputStreamEvento);
        outputStreamEvento.close();
	}

}
