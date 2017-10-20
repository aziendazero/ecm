package it.tredi.ecm.web;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Comunicazione;
import it.tredi.ecm.dao.entity.ComunicazioneResponse;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.QuotaAnnuale;
import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.service.ComunicazioneService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.QuotaAnnualeService;
import it.tredi.ecm.service.SecurityAccessService;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.ComunicazioneWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.RicercaComunicazioneWrapper;
import it.tredi.ecm.web.bean.SedutaWrapper;
import it.tredi.ecm.web.validator.ComunicazioneValidator;

@Controller
public class QuotaAnnualeController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(QuotaAnnuale.class);
	
	@Autowired private QuotaAnnualeService quotaAnnualeService;
	private final String CONFERMA = "/quotaAnnuale/quotaAnnualeconferma";
	
	@RequestMapping("/quotaAnnuale/list")
	public String listaQuotaAnnuale (Model model){
		try{
			Set<QuotaAnnuale> allQuotaAnnuale = quotaAnnualeService.getAllQuotaAnnuale();
			model.addAttribute("quotaAnnualeList", allQuotaAnnuale);
			return "quotaAnnuale/quotaAnnualeList";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("Errore recupero lista quote annuale"));
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@RequestMapping("/quotaAnnuale/sposta")
	public String spostaDataScadenzaPagamento(@RequestParam(name = "date", required = false) String date, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /quotaAnnuale/sposta"));
		try {
			if(date != null) {
				LocalDate localDate = LocalDate.parse(date);
				return "quotaAnnuale/quotaAnnualeSposta";
			}
			else return "quotaAnnuale/quotaAnnualeSposta";	
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /quotaAnnuale/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /quotaAnnuale/list"));
			return "redirect:/quotaAnnuale/list";
		}
	}

	@RequestMapping(value = "/quotaAnnuale/conferma", method = RequestMethod.POST)
	public String confermaDataScadenza(String date, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("POST /quotaAnnuale/conferma"));
		try{
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.seduta_salvata", "success"));
			model.addAttribute("date", date);
//			model.addAttribute("returnLink", "/quotaAnnuale/conferma");
			LOGGER.info(Utils.getLogMessage("REDIRECT: /quotaAnnuale/quotaAnnualeConferma"));
			return "quotaAnnuale/quotaAnnualeConferma";
		}catch(Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /quotaAnnuale/conferma"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + CONFERMA));
			return CONFERMA;
		}
		
	}
}
