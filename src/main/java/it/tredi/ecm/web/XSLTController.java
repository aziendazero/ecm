package it.tredi.ecm.web;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.service.FileService;
import it.tredi.ecm.web.bean.Message;


@Controller
public class XSLTController {

	@Autowired private FileService fileService;

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
}
