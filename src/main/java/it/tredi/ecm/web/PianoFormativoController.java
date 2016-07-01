package it.tredi.ecm.web;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.PianoFormativoWrapper;

@Controller
public class PianoFormativoController {

	private static final Logger LOGGER = Logger.getLogger(PianoFormativoController.class); 
	
	@Autowired
	private EventoService eventoService;
		
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/pianoFormativo/{pianoFormativo}/edit")
	public String editPianoFormativo(@PathVariable Long accreditamentoId, @PathVariable Long providerId, @PathVariable Integer pianoFormativo, 
			Model model, RedirectAttributes redirectAttrs){
		try{
			model.addAttribute("pianoFormativoWrapper", preparePianoFormativoWrapper(providerId, pianoFormativo, accreditamentoId));
			return "evento/pianoFormativoEdit";
		}catch (Exception ex){
			LOGGER.error(ex.getMessage(), ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@RequestMapping("/provider/{providerId}/pianoFormativo/{pianoFormativo}")
	public String showPianoFormativo(@PathVariable Long providerId, @PathVariable Integer pianoFormativo, 
			Model model, RedirectAttributes redirectAttrs){
		try{
			model.addAttribute("pianoFormativoWrapper", preparePianoFormativoWrapper(providerId, pianoFormativo, 0L));
			return "evento/pianoFormativoShow";
		}catch (Exception ex){
			LOGGER.error("showPianoFormativo: " + pianoFormativo + " del provider: " + providerId, ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	private PianoFormativoWrapper preparePianoFormativoWrapper(Long providerId, Integer pianoFormativo, Long accreditamentoId){
		PianoFormativoWrapper wrapper = new PianoFormativoWrapper();
		
		wrapper.setAccreditamentoId(accreditamentoId);
		wrapper.setProviderId(providerId);
		wrapper.setListaEventi(eventoService.getAllEventiFromProviderInPianoFormativo(providerId, pianoFormativo));
		wrapper.setPianoFormativo(String.valueOf(pianoFormativo));
		
		return wrapper;
	}
}
