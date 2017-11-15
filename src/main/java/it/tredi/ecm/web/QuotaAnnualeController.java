package it.tredi.ecm.web;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.QuotaAnnuale;
import it.tredi.ecm.service.QuotaAnnualeService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.ScadenzaPagamentoProviderWrapper;
import it.tredi.ecm.web.validator.ScadenzaPagamentoProviderValidator;

@Controller
public class QuotaAnnualeController {
	private static final Logger LOGGER = LoggerFactory.getLogger(QuotaAnnualeController.class);
	private final String LIST = "quotaAnnuale/quotaAnnualeList";
	
	@Autowired private QuotaAnnualeService quotaAnnualeService;
	@Autowired private ScadenzaPagamentoProviderValidator dateValidator;
	
	@RequestMapping(value = "/quotaAnnuale/scaduteENonPagate")
	public String showAllQuotaAnnuale(@ModelAttribute("scadenzaPagamentoProviderWrapper") ScadenzaPagamentoProviderWrapper wrapper, Model model, BindingResult result, RedirectAttributes redirectAttrs) {
		
		try{
			LOGGER.info(Utils.getLogMessage("GET /quotaAnnuale/scaduteENonPagate"));
			Set<QuotaAnnuale> quotaAnnualeList = quotaAnnualeService.getAllPagamentiScaduti(LocalDate.now());
			model.addAttribute("quotaAnnualeList", quotaAnnualeList);
			model.addAttribute("scadenzaPagamentoProviderWrapper", wrapper);
			return LIST;
			
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /quotaAnnuale/scaduteENonPagate"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}
	
	@RequestMapping(value = "/quotaAnnuale/scaduteENonPagate/confermaSposta", method = RequestMethod.POST)
	public String archiviaSelezionate(@RequestParam("qa_Id") String ids, @ModelAttribute("scadenzaPagamentoProviderWrapper") ScadenzaPagamentoProviderWrapper wrapper, Model model, BindingResult result, RedirectAttributes redirectAttrs, HttpServletRequest request) {
		LOGGER.info(Utils.getLogMessage("POST /quotaAnnuale/scaduteENonPagate/confermaSposta"));
		try{
			dateValidator.validate(wrapper, result);
			wrapper.setIds(ids);
			if(result.hasErrors()) {
				wrapper.setSubmitScadenzePagamentoProviderError(true);
				Set<QuotaAnnuale> quotaAnnualeList = quotaAnnualeService.getAllPagamentiScaduti(LocalDate.now());
				model.addAttribute("quotaAnnualeList", quotaAnnualeList);
				model.addAttribute("scadenzaPagamentoProviderWrapper", wrapper);
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required_data", "error"));
				return LIST;
			}else {
				HashMap<Long, LocalDate> quoteAnnualiSpostate = fromStringToHashMap(wrapper.getIds(), wrapper.getDataScadenzaPagamento());
				quotaAnnualeService.spostaDataScadenzaPagamenti(quoteAnnualiSpostate);
				LOGGER.info(Utils.getLogMessage("REDIRECT success: /quotaAnnuale/scaduteENonPagate"));
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.quotaAnnuale_salvata", "success"));
				return "redirect:/quotaAnnuale/scaduteENonPagate";
			}
		}catch (Exception ex){
			LOGGER.info(Utils.getLogMessage("Errore: /quotaAnnuale/scaduteENonPagate/confermaSposta"));
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}
	
	private HashMap<Long, LocalDate> fromStringToHashMap(String ids, LocalDate newDate){
		HashMap<Long, LocalDate> quoteAnnualiSpostate = new HashMap<>();
		
		List<String> numbers = Arrays.asList(ids.split(","));
		List<Long> longIds = new ArrayList<>();
		for (String number : numbers) {
			if(!number.isEmpty())
				longIds.add(Long.valueOf(number));
		}
		
		for(Long id : longIds) {
			quoteAnnualiSpostate.put(id, newDate);
		}
		
		return quoteAnnualiSpostate;
	}
}
