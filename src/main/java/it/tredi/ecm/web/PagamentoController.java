package it.tredi.ecm.web;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Pagamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.service.EngineeringServiceImpl;
import it.tredi.ecm.service.PagamentoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.QuotaAnnualeService;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;

@Controller
public class PagamentoController {
	public static final Logger LOGGER = LoggerFactory.getLogger(PagamentoController.class);

	@Autowired private ProviderService providerService;
	@Autowired private PagamentoService pagamentoService;
	@Autowired private QuotaAnnualeService quotaAnnualeService;
	@Autowired private EngineeringServiceImpl engineeringService;

	private final String LIST = "provider/pagamentoList";

	@RequestMapping("/provider/pagamento/list")
	public String getListPagamentiForCurrentProvider(Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/pagamento/list"));
		try {
			Provider provider = providerService.getProvider();
			if(provider != null)
				return "redirect:/provider/"+ provider.getId() + "/pagamento/list";
			else
				return "redirect:/home";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET " + LIST),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#id)")
	@RequestMapping("/provider/{id}/pagamento/list")
	public String getListPagamenti(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /evento/list"));
		try {
			//model.addAttribute("pagamentoList", pagamentoService.getAllPagamentiByProviderId(id));
			model.addAttribute("quoteAnnualiList", 	quotaAnnualeService.getAllQuotaAnnualeByProviderId(id));
			model.addAttribute("providerId",id);
			LOGGER.info(Utils.getLogMessage("VIEW: " + LIST));
			return LIST;
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET " + LIST),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect: /provider/{id}/pagamento/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditProvider(principal,#id)")
	@RequestMapping("/provider/{id}/pagamento/inserisci")
	public String inserisciPagamento(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /evento/list"));
		try {
			//pagamentoService.createPagamentoProviderPerQuotaAnnua(id, 2017, true);
			quotaAnnualeService.createPagamentoProviderPerQuotaAnnuale(id, 2017, false);
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET " + LIST),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/provider/{id}/pagamento/list";
		}
		return "redirect:/provider/"+ id + "/pagamento/list";
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditProvider(principal,#id)")
	@RequestMapping("/provider/{id}/pagamento/{quotaAnnualeId}/paga")
	public String pagaQuotaAnnuale(@PathVariable Long id, @PathVariable Long quotaAnnualeId, HttpServletRequest request, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /evento/list"));
		try {
			String rootUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
			//String url = pagamentoService.pagaQuotaAnnualeForProvider(pagamentoId, rootUrl + request.getContextPath() + "/provider/" + id + "/pagamento/list");
			String url = quotaAnnualeService.pagaQuotaAnnualeForProvider(quotaAnnualeId, rootUrl + request.getContextPath() + "/provider/" + id + "/pagamento/list");
			if (StringUtils.hasText(url)) {
				return "redirect:" + url;
			}

			return "redirect:/provider/"+ id + "/pagamento/list";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET " + LIST),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/provider/{id}/pagamento/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditProvider(principal,#id)")
	@RequestMapping("/provider/{id}/pagamento/verifica")
	public String verificaPagamentoProvider(@PathVariable Long id, HttpServletRequest request, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /evento/list"));
		try {
			engineeringService.esitoPagamentiQuoteAnnuali();
			return "redirect:/provider/"+ id + "/pagamento/list";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET " + LIST),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/provider/{id}/pagamento/list";
		}
	}
}
