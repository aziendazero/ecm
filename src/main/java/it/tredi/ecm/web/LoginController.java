package it.tredi.ecm.web;

import java.util.Iterator;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Profile;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.EventoStatoEnum;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.ComunicazioneService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.PianoFormativoService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.QuotaAnnualeService;
import it.tredi.ecm.service.RelazioneAnnualeService;
import it.tredi.ecm.service.SedutaService;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.HomeWrapper;
import it.tredi.ecm.web.bean.Message;

@Controller
public class LoginController {
	public static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);

	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private ProviderService providerService;
	@Autowired private AccountService accountService;
	@Autowired private SedutaService sedutaService;
	@Autowired private QuotaAnnualeService quotaAnnualeService;
	@Autowired private EventoService eventoService;
	@Autowired private PianoFormativoService pianoFormativoService;
	@Autowired private RelazioneAnnualeService relazioneAnnualeService;
	@Autowired private ComunicazioneService comunicazioneService;

	@RequestMapping("/")
	public String root(Locale locale) {
		return "redirect:/home";
	}

	/** Home page. */
	@RequestMapping("/home")
	public String home(Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /home"));

		try{
			//Check del profilo del utente loggato
			CurrentUser currentUser = Utils.getAuthenticatedUser();
			return goToShow(model, prepareHomeWrapper(currentUser), redirectAttrs);
		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /home"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /login"));
			return "redirect:/login";
		}
	}

	private HomeWrapper prepareHomeWrapper(CurrentUser currentUser) {
		HomeWrapper wrapper = new HomeWrapper();
		wrapper.setUser(currentUser.getAccount());
		Iterator<Profile> iterator = currentUser.getAccount().getProfiles().iterator();
		while(iterator.hasNext()) {
			switch(iterator.next().getProfileEnum()) {
				case ADMIN:
					wrapper.setIsAdmin(true);
					wrapper.setUtentiInAttesaDiAttivazione(1);
					break;
				case PROVIDER:
				case PROVIDERUSERADMIN:
				case PROVIDER_VISUALIZZATORE:
					wrapper.setIsProvider(true);
					wrapper.setProviderId(providerService.getProviderIdByAccountId(currentUser.getAccount().getId()));
					wrapper.setEventiDaPagare(eventoService.countEventiForProviderIdInScadenzaDiPagamento(currentUser.getAccount().getProvider().getId()));
					wrapper.setEventiPagamentoScaduto(eventoService.countEventiForProviderIdPagamentoScaduti(currentUser.getAccount().getProvider().getId()));
					wrapper.setMessaggi(9);
					wrapper.setAccreditamentiDaIntegrare(accreditamentoService.countAllAccreditamentiByStatoAndProviderId(AccreditamentoStatoEnum.INTEGRAZIONE, wrapper.getProviderId()));
					wrapper.setAccreditamentiInPreavvisoRigetto(accreditamentoService.countAllAccreditamentiByStatoAndProviderId(AccreditamentoStatoEnum.PREAVVISO_RIGETTO, wrapper.getProviderId()));
					wrapper.setNomeProvider(providerService.getProvider(providerService.getProviderIdByAccountId(currentUser.getAccount().getId())).getDenominazioneLegale()); //TODO fare con query
					wrapper.setEventiBozza(eventoService.countAllEventiByProviderIdAndStato(currentUser.getAccount().getProvider().getId(), EventoStatoEnum.BOZZA));
					wrapper.setNuoviMessaggi(comunicazioneService.countAllMessaggiNonLetti(currentUser.getAccount().getId()));
					break;
				case RESPONSABILE_SEGRETERIA_ECM:
					wrapper.setIsResponsabileSegreteriaEcm(true);
				case SEGRETERIA:
					wrapper.setIsSegreteria(true);
					wrapper.setDomandeNotTaken(accreditamentoService.countAllAccreditamentiByStatoAndTipoDomanda(AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA_ASSEGNAMENTO, null, true));
					wrapper.setDomandeAssegnamento(accreditamentoService.countAllAccreditamentiByStatoAndTipoDomanda(AccreditamentoStatoEnum.ASSEGNAMENTO, null, null));
					wrapper.setDomandeSbloccoCampiIntegrazione(accreditamentoService.countAllAccreditamentiByGruppoAndTipoDomanda("richiestaIntegrazione", null, null));
					wrapper.setDomandeValutazioneIntegrazione(accreditamentoService.countAllAccreditamentiByStatoAndTipoDomanda(AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA, null, null));
					wrapper.setDomandeInScadenza(accreditamentoService.countAllAccreditamentiInScadenza());
					wrapper.setDomandeInFirma(accreditamentoService.countAllAccreditamentiByGruppoAndTipoDomanda("inFirma", null, null));
					wrapper.setBadReferee(accountService.countAllRefereeWithValutazioniNonDate());
					wrapper.setDomandeDaValutareAll(accreditamentoService.countAllAccreditamentiByStatoAndTipoDomanda(AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA_ASSEGNAMENTO, null, null));
					wrapper.setDomandeInInsODG(accreditamentoService.countAllAccreditamentiInseribiliInODG());
					wrapper.setProviderPagamentoNonEffettuatoAllaScadenza(quotaAnnualeService.countProviderNotPagamentoEffettuatoAllaScadenza());
					wrapper.setProviderPianoFormativoNonInserito(pianoFormativoService.countProviderNotPianoFormativoInseritoPerAnno());
					wrapper.setEventiCreditiNonConfermati(eventoService.countAllEventiCreditiNonConfermati());
					wrapper.setProviderInadempienti(providerService.countAllProviderInadempienti());
					wrapper.setProviderNotRelazioneAnnualeRegistrata(relazioneAnnualeService.countProviderNotRelazioneAnnualeRegistrataAllaScadenza());
					wrapper.setEventiAlimentazionePrimaInfanzia(eventoService.countAllEventiAlimentazionePrimaInfanzia());
					wrapper.setEventiMedicineNonConvenzionali(eventoService.countAllEventiMedicineNonConvenzionali());
					wrapper.setDomandeTipoStandart(accreditamentoService.countAllTipoStandart(currentUser));
					break;
				case REFEREE:
					wrapper.setIsReferee(true);
					wrapper.setDomandeDaValutareNotDone(accreditamentoService.countAllAccreditamentiByStatoAndTipoDomandaForValutatoreId(AccreditamentoStatoEnum.VALUTAZIONE_CRECM, null, Utils.getAuthenticatedUser().getAccount().getId(), true)
							+ accreditamentoService.countAllAccreditamentiByStatoAndTipoDomandaForValutatoreId(AccreditamentoStatoEnum.VALUTAZIONE_TEAM_LEADER, null, Utils.getAuthenticatedUser().getAccount().getId(), true));
					wrapper.setDomandeNonValutateConsecutivamente(accountService.getUserById(currentUser.getAccount().getId()).getValutazioniNonDate());
					wrapper.setDomandeTipoStandart(accreditamentoService.countAllTipoStandart(currentUser));
					break;
				case COMMISSIONE:
					wrapper.setIsCommissione(true);
					wrapper.setProssimaSeduta(sedutaService.getNextSeduta());
					break;
				case COMPONENTE_OSSERVATORIO:
					//TODO
					break;
				case ENGINEERING:
					//TODO rimuovere caso
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

	/** Login form. */
	@RequestMapping(value = "/cas/login", method = RequestMethod.GET)
	public String casLogin() {
		return "redirect:/home";
	}
}
