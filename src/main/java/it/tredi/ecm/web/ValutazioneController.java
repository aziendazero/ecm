package it.tredi.ecm.web;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.ValutazioneTipoEnum;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.ValutazioneService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;

@Controller
public class ValutazioneController {
	private static Logger LOGGER = LoggerFactory.getLogger(ValutazioneController.class);

	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private ValutazioneService valutazioneService;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@RequestMapping("/valutazioneDemo")
	public String showValutazioneDemo(RedirectAttributes redirectAttrs) throws Exception{
		LOGGER.info(Utils.getLogMessage("GET /valutazioneDemo"));
		return "segreteria/valutazione";
	}

	//prendi in carica segreteria ECM
//	@PreAuthorize("@securityAccessServiceImpl.canValidateAccreditamento(principal,#accreditamentoId) TODO
	@RequestMapping("/accreditamento/{accreditamentoId}/takeCharge")
	public String prendiInCaricoAccreditamento(@PathVariable Long accreditamentoId, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/takeCharge"));
		try {
			Valutazione valutazione = new Valutazione();

			//utente corrente che prende in carico
			Account segretarioEcm = Utils.getAuthenticatedUser().getAccount();
			valutazione.setAccount(segretarioEcm);

			//accreditamento
			valutazione.setAccreditamento(accreditamentoService.getAccreditamento(accreditamentoId));

			//tipo di valutatore
			valutazione.setTipoValutazione(ValutazioneTipoEnum.SEGRETERIA_ECM);

			valutazioneService.save(valutazione);
			return "redirect:/accreditamento/{accreditamentoId}/validate";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/takeCharge"),ex);
			redirectAttrs.addAttribute("accreditamentoId",accreditamentoId);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/show"));
			return "redirect:/accreditamento/{accreditamentoId}/show";
		}
	}


}
