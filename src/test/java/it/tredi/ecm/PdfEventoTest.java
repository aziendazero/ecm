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
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.PdfEventoService;
import it.tredi.ecm.service.WorkflowServiceImpl;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.EventoWrapper;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@ActiveProfiles("abarducci")
@WithUserDetails("segreteria")
//@Rollback(false)
@Ignore
public class PdfEventoTest {
	private static Logger LOGGER = LoggerFactory.getLogger(WorkflowServiceImpl.class);

	@Autowired private PdfEventoService pdfEventoService;
	@Autowired private EventoService eventoService;

	@Test
	@Ignore
	@Transactional
	public void creazionePdfPerAccreditamento() throws Exception {
		//Long eventoId = 1354L;//FAD
		//Long eventoId = 1438L;//FAD
		//Long eventoId = 1395L;//FSC
		//Long eventoId = 1440L;//FSC
		//Long eventoId = 1352L;//RES
		Long eventoId = 1442L;//RES
		EventoWrapper wrapper = prepareEventoWrapperShow(eventoService.getEvento(eventoId));
		ByteArrayOutputStream pdfOutputStream = pdfEventoService.creaOutputStreamPdfEvento(wrapper);
		String fileName = "evento_" + wrapper.getEvento().getCodiceIdentificativo() + ".pdf";
		saveFile(fileName, pdfOutputStream);
		System.out.println("FILE CREATO");
	}

	private void saveFile(String fileName, ByteArrayOutputStream pdfOutputStream) throws Exception {
    	String pathFileEvento = "C:\\__Progetti\\ECM\\zzFileDownload\\" + fileName;
        OutputStream outputStreamEvento = new FileOutputStream(new File(pathFileEvento));
        pdfOutputStream.writeTo(outputStreamEvento);
        outputStreamEvento.close();
	}

	private EventoWrapper prepareEventoWrapperShow(Evento evento) throws Exception {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperShow(" + evento.getId() + ") - entering"));
		EventoWrapper eventoWrapper = new EventoWrapper();
		eventoWrapper.setProceduraFormativa(evento.getProceduraFormativa());
		eventoWrapper.setProviderId(evento.getProvider().getId());
		eventoWrapper.setEvento(evento);
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperShow(" + evento.getId() + ") - exiting"));
		return eventoWrapper;
	}

}
