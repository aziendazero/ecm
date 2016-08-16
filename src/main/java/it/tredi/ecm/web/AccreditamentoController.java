package it.tredi.ecm.web;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.ProfileEnum;
import it.tredi.ecm.dao.enumlist.ValutazioneTipoEnum;
import it.tredi.ecm.service.AccountService;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.FieldValutazioneAccreditamentoService;
import it.tredi.ecm.service.PersonaService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.ValutazioneService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AccreditamentoWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.ValutazioneValidator;

@Controller
public class AccreditamentoController {
	private static Logger LOGGER = LoggerFactory.getLogger(AccreditamentoController.class);

	@Autowired private PersonaService personaService;
	@Autowired private ProviderService providerService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private FieldValutazioneAccreditamentoService fieldValutazioneAccreditamentoService;
	@Autowired private ValutazioneService valutazioneService;
	@Autowired private AccountService accountService;
	@Autowired private ValutazioneValidator valutazioneValidator;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@RequestMapping("/accreditamento/{accreditamentoId}/stato/{stato}")
	public String SetStatoFromBonita(@PathVariable("accreditamentoId") Long accreditamentoId, @PathVariable("stato") AccreditamentoStatoEnum stato) throws Exception{
		//TODO modifica stato della domanda da parte del flusso
		//lo facciamo cosi in modo tale da non dover disabilitare la cache di hibernate
		//accreditamentoService.setStato(accreditamentoId, stato);
		return "";
	}

	/***	Get Lista Accreditamenti per provider CORRENTE	***/
	@RequestMapping("/provider/accreditamento/list")
	public String getAllAccreditamentiForCurrentProvider(RedirectAttributes redirectAttrs) throws Exception{
		LOGGER.info(Utils.getLogMessage("GET /provider/accreditamento/list"));
		try {
			Provider currentProvider = providerService.getProvider();
			if(currentProvider.isNew()){
				throw new Exception("Provider non registrato");
			}else{
				redirectAttrs.addAttribute("providerId",currentProvider.getId());
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + currentProvider.getId() + "/accreditamento/list"));
				return "redirect:/provider/{providerId}/accreditamento/list";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/accreditamento/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	/***	Get Lista Accreditamenti per {providerID}	***/
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/accreditamento/list")
	public String getAllAccreditamentiForProvider(@PathVariable("providerId") Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/accreditamento/list"));
		try {
			Set<Accreditamento> listaAccreditamenti = accreditamentoService.getAllAccreditamentiForProvider(providerId);
			model.addAttribute("accreditamentoList", listaAccreditamenti);
			model.addAttribute("canProviderCreateAccreditamentoProvvisorio", accreditamentoService.canProviderCreateAccreditamento(providerId,AccreditamentoTipoEnum.PROVVISORIO));
			model.addAttribute("canProviderCreateAccreditamentoStandard", accreditamentoService.canProviderCreateAccreditamento(providerId,AccreditamentoTipoEnum.STANDARD));
			model.addAttribute("providerId", providerId);
			LOGGER.info(Utils.getLogMessage("VIEW: accreditamento/accreditamentoList"));
			return "accreditamento/accreditamentoList";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/accreditamento/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	/***	Get show Accreditamento {ID}	***/
	@PreAuthorize("@securityAccessServiceImpl.canShowAccreditamento(principal,#id)")
	@RequestMapping("/accreditamento/{id}/show")
	public String showAccreditamento(@PathVariable Long id, Model model, @RequestParam(required = false) String tab, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + id + "/show"));
		try {
			if (tab != null) {
				model.addAttribute("currentTab", tab);
				LOGGER.info(Utils.getLogMessage("TAB:" + tab));
			}
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(id);
			return goToAccreditamentoShow(model, accreditamento);
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + id + "/show"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: /accreditamento/accreditamentoList"));
			return "accreditamento/accreditamentoList";
		}
	}

	/***	Get edit Accreditamento {ID}	***/
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#id)")
	@RequestMapping("/accreditamento/{id}/edit")
	public String editAccreditamento(@PathVariable Long id, Model model, @RequestParam(required = false) String tab, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + id + "/edit"));
		try {
			if (tab != null) {
				LOGGER.info(Utils.getLogMessage("TAB:" + tab));
			}
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(id);
			return goToAccreditamentoEdit(model, accreditamento, tab);
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + id + "/edit"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: /accreditamento/accreditamentoList"));
			return "accreditamento/accreditamentoList";
		}
	}

	/*** Get validate Accreditamento {ID} ***/
//	@PreAuthorize("@securityAccessServiceImpl.canValidateAccreditamento(principal,#id)") TODO
	@RequestMapping("/accreditamento/{id}/validate")
	public String validateAccreditamento(@PathVariable Long id, Model model, @RequestParam(required = false) String tab, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + id + "/validate"));
		try {
			if (tab != null) {
				model.addAttribute("currentTab", tab);
				LOGGER.info(Utils.getLogMessage("TAB:" + tab));
			}
			Accreditamento accreditamento = accreditamentoService.getAccreditamento(id);
			return goToAccreditamentoValidate(model, accreditamento);
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + id + "/validate"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: /accreditamento/accreditamentoList"));
			return "accreditamento/accreditamentoList";
		}
	}

	private String goToAccreditamentoShow(Model model, Accreditamento accreditamento){
		AccreditamentoWrapper accreditamentoWrapper = prepareAccreditamentoWrapperShow(accreditamento);
		model.addAttribute("accreditamentoWrapper", accreditamentoWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: /accreditamento/accreditamentoShow"));
		return "accreditamento/accreditamentoShow";
	}

	private String goToAccreditamentoEdit(Model model, Accreditamento accreditamento, String tab){
		AccreditamentoWrapper accreditamentoWrapper = prepareAccreditamentoWrapperEdit(accreditamento);
		model.addAttribute("accreditamentoWrapper", accreditamentoWrapper);
		selectCorrectTab(tab, accreditamentoWrapper, model);
		LOGGER.info(Utils.getLogMessage("VIEW: /accreditamento/accreditamentoEdit"));
		return "accreditamento/accreditamentoEdit";
	}

	private String goToAccreditamentoValidate(Model model, Accreditamento accreditamento) {
		return goToAccreditamentoValidate(model, accreditamento, null);
	}

	private String goToAccreditamentoValidate(Model model, Accreditamento accreditamento, AccreditamentoWrapper wrapper){
		//carico la valutazione dell'utente corrente
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamento.getId(), Utils.getAuthenticatedUser().getAccount().getId());
		AccreditamentoWrapper accreditamentoWrapper = prepareAccreditamentoWrapperValidate(accreditamento, valutazione, wrapper);
		model.addAttribute("accreditamentoWrapper", accreditamentoWrapper);
		model.addAttribute("refereeList", accountService.getUserByProfileEnum(ProfileEnum.REFEREE));
 		LOGGER.info(Utils.getLogMessage("VIEW: /accreditamento/accreditamentoValidate"));
		return "accreditamento/accreditamentoValidate";
	}

	//Check per capire in che tab ritornare e con quale messaggio
	private void selectCorrectTab(String tab, AccreditamentoWrapper accreditamentoWrapper, Model model){
		if(tab != null) {
			switch(tab) {

			case "tab1":	model.addAttribute("currentTab", "tab1");
			break;

			case "tab2":	if(accreditamentoWrapper.isSezione1Stato()) {
				model.addAttribute("currentTab", "tab2");
				if (accreditamentoWrapper.getResponsabileSegreteria() == null &&
						accreditamentoWrapper.getResponsabileAmministrativo() == null &&
						accreditamentoWrapper.getResponsabileSistemaInformatico() == null &&
						accreditamentoWrapper.getResponsabileQualita() == null) {
					model.addAttribute("message", new Message("message.warning", "message.legale_non_piu_modificabile", "warning"));
				}
			}
			else {
				model.addAttribute("currentTab", "tab1");
				model.addAttribute("message", new Message("message.warning", "message.compilare_tab1", "warning"));
			}
			break;

			case "tab3":	if(accreditamentoWrapper.isSezione2Stato())
				model.addAttribute("currentTab", "tab3");
			else {
				if(accreditamentoWrapper.isSezione1Stato()) {
					model.addAttribute("currentTab", "tab2");
					model.addAttribute("message", new Message("message.warning", "message.compilare_tab2", "warning"));
				}
				else {
					model.addAttribute("currentTab", "tab1");
					model.addAttribute("message", new Message("message.warning", "message.compilare_tab1", "warning"));
				}
			}
			break;

			case "tab4":  	if(accreditamentoWrapper.isCompleta())
				model.addAttribute("currentTab", "tab4");
			else {
				if(accreditamentoWrapper.isSezione2Stato()) {
					model.addAttribute("currentTab", "tab3");
					model.addAttribute("message", new Message("message.warning", "message.compilare_altre_tab", "warning"));
				}
				else {
					if(accreditamentoWrapper.isSezione1Stato()) {
						model.addAttribute("currentTab", "tab2");
						model.addAttribute("message", new Message("message.warning", "message.compilare_tab2", "warning"));
					}
					else {
						model.addAttribute("currentTab", "tab1");
						model.addAttribute("message", new Message("message.warning", "message.compilare_tab1", "warning"));
					}
				}
			}
			break;

			default:		break;
			}
		}
	}

	/*** NEW 	Nuova domanda accreditamento per provider corrente	***/
	@RequestMapping("/provider/accreditamento/{tipoDomanda}/new")
	public String getNewAccreditamentoForCurrentProvider(@PathVariable AccreditamentoTipoEnum tipoDomanda, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /provider/accreditamento/" + tipoDomanda + "/new"));
		try{
			Long accreditamentoId = accreditamentoService.getNewAccreditamentoForCurrentProvider(tipoDomanda).getId();
			redirectAttrs.addAttribute("id", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
			return "redirect:/accreditamento/{id}/edit";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/accreditamento/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/accreditamento/list"));
			return "redirect:/provider/accreditamento/list";
		}
	}

	/*** NEW 	Nuova domanda accreditamento per provider	***/
	@PreAuthorize("@securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/accreditamento/{tipoDomanda}/new")
	public String getNewAccreditamentoForProvider(@PathVariable Long providerId, @PathVariable AccreditamentoTipoEnum tipoDomanda, Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/accreditamento/" + tipoDomanda + "/new"));
		try{
			Long accreditamentoId = accreditamentoService.getNewAccreditamentoForProvider(providerId,tipoDomanda).getId();
			redirectAttrs.addAttribute("id", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
			return "redirect:/accreditamento/{id}/edit";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId +"/accreditamento/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addAttribute("providerId",providerId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/{providerId}/accreditamento/list"));
			return "redirect:/provider/{providerId}/accreditamento/list";
		}
	}

	/***	INVIA DOMANDA ALLA SEGRETERIA	***/
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/send")
	public String inviaDomandaAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long providerId, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/" + providerId + "/send"));
		try{
			accreditamentoService.inviaDomandaAccreditamento(accreditamentoId);
			redirectAttrs.addAttribute("providerId",providerId);
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.domanda_inviata", "success"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/accreditamento/list"));
			return "redirect:/provider/{providerId}/accreditamento/list";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/" + providerId + "/send"),ex);
			redirectAttrs.addAttribute("id",accreditamentoId);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId));;
			return "redirect:/accreditamento/{id}";
		}
	}

	/***	INSERISCI PIANO FORMATIVO	***/
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#providerId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{providerId}/insertPianoFormativo")
	public String inserisciPianoFormativo(@PathVariable Long accreditamentoId, @PathVariable Long providerId, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/" + providerId + "/insertPianoFormativo"));
		try{
			accreditamentoService.inserisciPianoFormativo(accreditamentoId);
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			redirectAttrs.addAttribute("providerId", providerId);
			redirectAttrs.addAttribute("pianoFormativo", LocalDate.now().getYear());
			redirectAttrs.addFlashAttribute("currentTab", "tab4");
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
			return "redirect:/accreditamento/{accreditamentoId}/edit";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId +"/provider/" + providerId + "/insertPianoFormativo"),ex);
			redirectAttrs.addAttribute("accreditamentoId",accreditamentoId);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
			return "redirect:/accreditamento/{accreditamentoId}/edit";
		}
	}

	/*** METODI PRIVATI PER IL SUPPORTO ***/
	private AccreditamentoWrapper prepareAccreditamentoWrapperEdit(Accreditamento accreditamento){
		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoWrapper(" + accreditamento.getId() + ") - entering"));

		AccreditamentoWrapper accreditamentoWrapper = new AccreditamentoWrapper(accreditamento);
		commonPrepareAccreditamentoWrapper(accreditamentoWrapper, "edit");

		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoWrapper(" + accreditamento.getId() + ") - exiting"));
		return accreditamentoWrapper;
	}

	private AccreditamentoWrapper prepareAccreditamentoWrapperShow(Accreditamento accreditamento){
		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoWrapperShow(" + accreditamento.getId() + ") - entering"));

		AccreditamentoWrapper accreditamentoWrapper = new AccreditamentoWrapper(accreditamento);
		commonPrepareAccreditamentoWrapper(accreditamentoWrapper, "show");

		//logica per mostrare i pulsanti relativi alla valutazione della segreteria o i referee
		accreditamentoWrapper.setCanPrendiInCarica(accreditamentoService.canUserPrendiInCarica(accreditamento.getId(), Utils.getAuthenticatedUser()));
		accreditamentoWrapper.setCanValutaDomanda(accreditamentoService.canUserValutaDomanda(accreditamento.getId(), Utils.getAuthenticatedUser()));

		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoWrapperShow(" + accreditamento.getId() + ") - exiting"));
		return accreditamentoWrapper;
	}

	private AccreditamentoWrapper prepareAccreditamentoWrapperValidate(Accreditamento accreditamento, Valutazione valutazione, AccreditamentoWrapper accreditamentoWrapper){
		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoWrapperValidate(" + accreditamento.getId() + ") - entering"));

		if(accreditamentoWrapper == null)
			accreditamentoWrapper = new AccreditamentoWrapper(accreditamento);
		else
			accreditamentoWrapper.setAllAccreditamento(accreditamento);

		//inserisco i suoi fieldValutazione nella mappa per il wrapper
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		if(valutazione != null) {
			mappa = fieldValutazioneAccreditamentoService.putSetFieldValutazioneInMap(valutazione.getValutazioni());
		}
		accreditamentoWrapper.setMappa(mappa);

		//init delle strutture dati che servono per la verifica degli stati di valutazione dei multistanza
		Map<Long, Map<IdFieldEnum, FieldValutazioneAccreditamento>> mappaComponenti = new HashMap<Long, Map<IdFieldEnum, FieldValutazioneAccreditamento>>();
		Map<Long, Boolean> componentiComitatoScientificoStati = new HashMap<Long, Boolean>();
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappaCoordinatore = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		Map<Long, Map<IdFieldEnum, FieldValutazioneAccreditamento>> mappaEventi = new HashMap<Long, Map<IdFieldEnum, FieldValutazioneAccreditamento>>();
		Map<Long, Boolean> eventiStati = new HashMap<Long, Boolean>();

		//aggiungo i componenti del comitato scientifico
		for(Persona p : accreditamentoWrapper.getComponentiComitatoScientifico()) {
			mappaComponenti.put(p.getId(), fieldValutazioneAccreditamentoService.filterFieldValutazioneByObjectAsMap(valutazione.getValutazioni(), p.getId()));
			componentiComitatoScientificoStati.put(p.getId(), false);
		}
		//aggiungo anche il coordinatore
		Long coordinatoreId = accreditamentoWrapper.getCoordinatoreComitatoScientifico().getId();
		mappaCoordinatore = fieldValutazioneAccreditamentoService.filterFieldValutazioneByObjectAsMap(valutazione.getValutazioni(), coordinatoreId);

		//aggiungo gli eventi
		for(Evento e : accreditamentoWrapper.getAccreditamento().getPianoFormativo().getEventi()) {
			mappaEventi.put(e.getId(), fieldValutazioneAccreditamentoService.filterFieldValutazioneByObjectAsMap(valutazione.getValutazioni(), e.getId()));
			eventiStati.put(e.getId(), false);
		}

		accreditamentoWrapper.setMappaCoordinatore(mappaCoordinatore);
		accreditamentoWrapper.setMappaComponenti(mappaComponenti);
		accreditamentoWrapper.setComponentiComitatoScientificoStati(componentiComitatoScientificoStati);
		accreditamentoWrapper.setMappaEventi(mappaEventi);
		accreditamentoWrapper.setEventiStati(eventiStati);

		commonPrepareAccreditamentoWrapper(accreditamentoWrapper, "validate");

		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoWrapperValidate(" + accreditamento.getId() + ") - exiting"));
		return accreditamentoWrapper;
	}

	private void commonPrepareAccreditamentoWrapper(AccreditamentoWrapper accreditamentoWrapper, String mode){
		Long providerId = accreditamentoWrapper.getProvider().getId();
		//ALLEGATI
		Set<String> filesDelProvider = providerService.getFileTypeUploadedByProviderId(providerId);

		Set<Professione> professioniSelezionate = (accreditamentoWrapper.getAccreditamento().getDatiAccreditamento() != null && !accreditamentoWrapper.getDatiAccreditamento().isNew()) ? accreditamentoWrapper.getDatiAccreditamento().getProfessioniSelezionate() : new HashSet<Professione>();

		int numeroComponentiComitatoScientifico = personaService.numeroComponentiComitatoScientifico(providerId);
		int numeroProfessionistiSanitarie 		= personaService.numeroComponentiComitatoScientificoConProfessioneSanitaria(providerId);
		//int professioniDeiComponenti 			= personaService.numeroProfessioniDistinteDeiComponentiComitatoScientifico(providerId);
		int professioniDeiComponentiAnaloghe 	= (professioniSelezionate.size() > 0) ? personaService.numeroProfessioniDistinteAnalogheAProfessioniSelezionateDeiComponentiComitatoScientifico(providerId, professioniSelezionate) : 0;
		Set<Professione> elencoProfessioniDeiComponenti = personaService.elencoProfessioniDistinteDeiComponentiComitatoScientifico(providerId);

		LOGGER.debug(Utils.getLogMessage("<*>NUMERO COMPONENTI: " + numeroComponentiComitatoScientifico));
		LOGGER.debug(Utils.getLogMessage("<*>NUMERO PROFESSIONISTI SANITARI: " + numeroProfessionistiSanitarie));
		LOGGER.debug(Utils.getLogMessage("<*>NUMERO PROFESSIONI DISTINTE: " + elencoProfessioniDeiComponenti.size()));
		LOGGER.debug(Utils.getLogMessage("<*>NUMERO PROFESSIONI ANALOGHE: " + professioniDeiComponentiAnaloghe));

		accreditamentoWrapper.checkStati(numeroComponentiComitatoScientifico, numeroProfessionistiSanitarie, elencoProfessioniDeiComponenti, professioniDeiComponentiAnaloghe, filesDelProvider, mode);
	}

	//PARTE RELATIVA ALLA VALUTAZIONE

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

			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.presa_in_carico", "success"));
			return "redirect:/accreditamento/{accreditamentoId}/validate";
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/takeCharge"),ex);
			redirectAttrs.addAttribute("accreditamentoId",accreditamentoId);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/show"));
			return "redirect:/accreditamento/{accreditamentoId}/show";
		}
	}

	// salva valutazione complessiva (inserisce data e valutazione complessiva)
	//	@PreAuthorize("@securityAccessServiceImpl.canValidateAccreditamento(principal,#accreditamentoId) TODO
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/confirmEvaluation", method = RequestMethod.POST)
	public String confermaValutazioneAccreditamento(@ModelAttribute("accreditamentoWrapper") AccreditamentoWrapper wrapper, BindingResult result,
			@PathVariable Long accreditamentoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/confirmEvaluation"));
		try {
			//validazione della valutazioneComplessiva
			valutazioneValidator.validateValutazioneComplessiva(wrapper.getRefereeGroup(), result);

			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				model.addAttribute("confirmErrors", true);
				Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
				return goToAccreditamentoValidate(model, accreditamento, wrapper);
			}else {
				Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountId(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());

				//setta la data
				valutazione.setDataValutazione(LocalDate.now());

				//inserisce il commento complessivo
				valutazione.setValutazioneComplessiva(wrapper.getValutazioneComplessiva());

				//crea le valutazioni per i referee TODO
				for (Account a : wrapper.getRefereeGroup()) {
					Valutazione valutazioneReferee = new Valutazione();
					valutazioneReferee.setAccount(a);
					valutazioneReferee.setAccreditamento(accreditamentoService.getAccreditamento(accreditamentoId));
					valutazioneReferee.setTipoValutazione(ValutazioneTipoEnum.REFEREE);
					valutazioneService.save(valutazioneReferee);
				}

				valutazioneService.save(valutazione);
				LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/show"));
				redirectAttrs.addAttribute("accreditamentoId",accreditamentoId);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.valutazione_complessiva_salvata", "success"));
				return "redirect:/accreditamento/{accreditamentoId}/show";
			}

		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/confirmEvaluation"),ex);
			redirectAttrs.addAttribute("accreditamentoId",accreditamentoId);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/validate"));
			return "redirect:/accreditamento/{accreditamentoId}/validate";
		}
	}
}
