package it.tredi.ecm.web;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.HomeWrapper;
import it.tredi.ecm.web.bean.Message;

@Controller
public class LoginController {
	public static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	@RequestMapping("/")
	public String root(Locale locale) {
		return "redirect:/home";
	}

	/** Home page. */
	@RequestMapping("/home")
	public String home(Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /home"));

		try{
			//Init della lista di profili dell'utente
			Set<String> profili = new HashSet<String>();

			//Check del profilo del utente loggato + riempimento lista profili
			CurrentUser currentUser = Utils.getAuthenticatedUser();
			if(currentUser.hasProfile(Costanti.PROFILO_ADMIN))
				profili.add(Costanti.PROFILO_ADMIN);
			if(currentUser.hasProfile(Costanti.PROFILO_PROVIDER))
				profili.add(Costanti.PROFILO_PROVIDER);
			if(currentUser.hasProfile(Costanti.PROFILO_SEGRETERIA))
				profili.add(Costanti.PROFILO_SEGRETERIA);

			return goToShow(model, prepareHomeWrapper(profili), redirectAttrs);

		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /home"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /login"));
			return "redirect:/login";
		}
	}

	private HomeWrapper prepareHomeWrapper(Set<String> profili) {
		HomeWrapper wrapper = new HomeWrapper();
		Iterator<String> iterator = profili.iterator();
		while(iterator.hasNext()) {
			switch(iterator.next()) {
				case Costanti.PROFILO_ADMIN:
					//TODO riempe i dati relativi ad admin
					wrapper.setIsAdmin(true);
					wrapper.setUtentiInAttesaDiAttivazione(1);
					break;
				case Costanti.PROFILO_PROVIDER:
					//TODO riempe i dati relativi al provider
					wrapper.setIsProvider(true);
					wrapper.setEventiDaPagare(3);
					wrapper.setMessaggi(9);
					wrapper.setAccreditamentiDaIntegrare(2);
					break;
				case Costanti.PROFILO_SEGRETERIA:
					//TODO riempe i dati relativi alla segreteria
					wrapper.setIsSegreteria(true);
					wrapper.setRichiesteInviateDaiProvider(5);
					wrapper.setProviderQuotaAnnuale(9);
					wrapper.setProviderQuotaEventi(23);
					break;
			}
		}
		return wrapper;
	}

	private String goToShow(Model model, HomeWrapper wrapper, RedirectAttributes redirectAttrs) {
		try {
			model.addAttribute("homeWrapper", wrapper);
			LOGGER.info(Utils.getLogMessage("VIEW: /home"));
			return "home";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("goToShow"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /login"));
			return "redirect:/login";
		}
	}

	/** Login form. */
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public String login() {
		return "login";
	}

	/** Main form. */
	@RequestMapping(value = "/main", method = RequestMethod.GET)
	public String main() {
		return "main";
	}
}
