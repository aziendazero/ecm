package it.tredi.ecm.web;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.cogeaps.Helper;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.PdfRiepilogoPartecipantiService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;


@Controller
public class XSLTController {
	private static Logger LOGGER = LoggerFactory.getLogger(XSLTController.class);

	@Autowired private FileService fileService;
	@Autowired private EventoService eventoService;
	@Autowired private PdfRiepilogoPartecipantiService pdfRiepilogoPartecipantiService;

	@RequestMapping(value="/provider/{providerId}/evento/{eventoId}/viewXSLT/{fileId}")
    public ModelAndView viewXSLT(RedirectAttributes redirectAttrs, HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Long fileId, @PathVariable Long eventoId, @PathVariable Long providerId) {
		try {
			byte[] fileSbustato = fileService.sbustaP7mById(fileId);
			InputStream inputStreamXML = new ByteArrayInputStream(fileSbustato);

	        Source source = new StreamSource(inputStreamXML);

	        // adds the XML source file to the model so the XsltView can detect
	        ModelAndView modelView = new ModelAndView("XSLTView");
	        modelView.addObject("xmlSource", source);

	        return modelView;
		}
		catch (Exception ex) {
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return new ModelAndView("redirect:/provider/"+providerId+"/evento/"+eventoId+"/rendiconto");
		}
    }

	@RequestMapping(value="/provider/{providerId}/evento/{eventoId}/esportaPDFfromXSLT/{fileId}")
    public void esportaPDFfromXSLT(RedirectAttributes redirectAttrs, Model model, HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Long fileId, @PathVariable Long eventoId, @PathVariable Long providerId) {
		try {
			byte[] fileSbustatoXml = fileService.sbustaP7mById(fileId);
			Document xmlDoc = DocumentHelper.parseText(new String(fileSbustatoXml, Helper.XML_REPORT_ENCODING));

	        Evento evento = eventoService.getEvento(eventoId);
	        response.setHeader("Content-Disposition", String.format("attachment; filename=\"Riepilogo Partecipanti inviato al CO.Ge.A.P.S. Evento: " + evento.getCodiceIdentificativo() + ".pdf\""));

			ByteArrayOutputStream pdfOutputStream = pdfRiepilogoPartecipantiService.creaOutputSteramPdfRiepilogoPartecipanti(Helper.extractRiepilogoPartecipantiFromXML(xmlDoc), evento.getCodiceIdentificativo());
			response.setContentLength(pdfOutputStream.size());
			response.getOutputStream().write(pdfOutputStream.toByteArray());
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/"+providerId+"/evento/"+eventoId+"/esportaPDFfromXSLT/"+fileId),ex);
			model.addAttribute("message",new Message("message.errore", "message.impossibile_creare_pdf_from_xslt", "error"));
		}
    }

	@RequestMapping(value="/provider/{providerId}/evento/{eventoId}/attestatiPDFfromXSLT/{fileId}")
    public void generaAttestatiPDFfromXSLT(RedirectAttributes redirectAttrs, Model model, HttpServletRequest request, HttpServletResponse response,
    		@PathVariable Long fileId, @PathVariable Long eventoId, @PathVariable Long providerId) {
		try {
			byte[] fileSbustatoXml = fileService.sbustaP7mById(fileId);
			Document xmlDoc = DocumentHelper.parseText(new String(fileSbustatoXml, Helper.XML_REPORT_ENCODING));

	        Evento evento = eventoService.getEvento(eventoId);
	        response.setHeader("Content-Disposition", String.format("attachment; filename=\"Attestati per i Partecipanti " + evento.getCodiceIdentificativo() + ".pdf\""));

			ByteArrayOutputStream pdfOutputStream = pdfRiepilogoPartecipantiService.creaOutputSteramPdfAttestatiPartecipanti(Helper.extractRiepilogoPartecipantiFromXML(xmlDoc), evento);
			response.setContentLength(pdfOutputStream.size());
			response.getOutputStream().write(pdfOutputStream.toByteArray());
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/"+providerId+"/evento/"+eventoId+"/attestatiPDFfromXSLT/"+fileId),ex);
			model.addAttribute("message",new Message("message.errore", "message.impossibile_creare_pdf_from_xslt", "error"));
		}
    }
}
