package it.tredi.ecm.web;

import java.math.BigDecimal;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.AnagrafeRegionaleCrediti;
import it.tredi.ecm.service.AnagrafeRegionaleCreditiService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;

@Controller
public class AnagrafeRegionaleCreditiController {
	private static final Logger LOGGER = LoggerFactory.getLogger(AnagrafeRegionaleCreditiController.class);

	private static String SHOW = "anagrafeRegionaleCrediti/anagrafeRegionaleCreditiShow";
	private static String LIST = "anagrafeRegionaleCrediti/anagrafeRegionaleCreditiList";
	private static String ANNO_LIST = "anagrafeRegionaleCrediti/anagrafeRegionaleCreditiAnnoList";

	@Autowired private AnagrafeRegionaleCreditiService anagrafeRegionaleCreditiService;

	@PreAuthorize("@securityAccessServiceImpl.canShowAnagrafeRegionale(principal)")
	@RequestMapping("/anagrafeRegionaleCrediti/list")
	public String listAnnoAnagrafeRegionaleCrediti(Model model, RedirectAttributes redirectAttrs){
		try {
			LOGGER.info(Utils.getLogMessage("GET /anagrafeRegionaleCrediti/list"));
			model.addAttribute("annoList", anagrafeRegionaleCreditiService.getAnnoListForAnagrafeRegionaleCrediti());
			return ANNO_LIST;
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /anagrafeRegionaleCrediti/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowAnagrafeRegionale(principal)")
	@RequestMapping("/anagrafeRegionaleCrediti/{annoRiferimento}/list")
	public String listAnagrafeRegionaleCrediti(@PathVariable Integer annoRiferimento, Model model, RedirectAttributes redirectAttrs){
		try {
			LOGGER.info(Utils.getLogMessage("GET /anagrafeRegionaleCrediti/" + annoRiferimento + "/list"));
			model.addAttribute("anagrafeRegionaleCreditiList",anagrafeRegionaleCreditiService.getAll(annoRiferimento));
			model.addAttribute("annoRiferimento", annoRiferimento);
			return LIST;
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /anagrafeRegionaleCrediti/" + annoRiferimento + "/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowAnagrafeRegionale(principal)")
	@RequestMapping("/anagrafeRegionaleCrediti/{codiceFiscale}/{annoRiferimento}/show")
	public String showAnagrafeRegionaleCrediti(@PathVariable String codiceFiscale, @PathVariable Integer annoRiferimento, Model model, RedirectAttributes redirectAttrs){
		try {
			LOGGER.info(Utils.getLogMessage("GET /anagrafeRegionaleCrediti/" + codiceFiscale + "/" + annoRiferimento + "/show"));

			//recupero tutte le registrazioni riguardanti il professionista selezionato
			Set<AnagrafeRegionaleCrediti> lista = anagrafeRegionaleCreditiService.getAllByCodiceFiscale(codiceFiscale, annoRiferimento);
			model.addAttribute("anagrafeRegionaleCreditiList", lista);


			//recupero crediti totali maturati nell'anno di riferimento
			model.addAttribute("totaleCrediti", anagrafeRegionaleCreditiService.getSumCreditiByCodiceFiscale(codiceFiscale, annoRiferimento));
			model.addAttribute("annoRiferimento", annoRiferimento);

			//registro i dati della persona per l'intestazione della scheda
			AnagrafeRegionaleCrediti unoqualsiasi = lista.iterator().next();
			model.addAttribute("codiceFiscale", codiceFiscale);
			model.addAttribute("cognome", unoqualsiasi.getCognome());
			model.addAttribute("nome", unoqualsiasi.getNome());

			return SHOW;
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /anagrafeRegionaleCrediti/" + codiceFiscale + "/" + annoRiferimento + "/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

}
