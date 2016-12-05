package it.tredi.ecm.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;

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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoWrapperModeEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.FieldEditabileAccreditamentoService;
import it.tredi.ecm.service.FieldIntegrazioneAccreditamentoService;
import it.tredi.ecm.service.FieldValutazioneAccreditamentoService;
import it.tredi.ecm.service.IntegrazioneService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.TokenService;
import it.tredi.ecm.service.ValutazioneService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.ProviderWrapper;
import it.tredi.ecm.web.bean.ResponseState;
import it.tredi.ecm.web.bean.ResponseUsername;
import it.tredi.ecm.web.bean.RicercaProviderWrapper;
import it.tredi.ecm.web.bean.RichiestaIntegrazioneWrapper;
import it.tredi.ecm.web.validator.ProviderValidator;
import it.tredi.ecm.web.validator.ValutazioneValidator;

@Controller
public class ProviderController {
	private static final Logger LOGGER = LoggerFactory.getLogger(Provider.class);

	private final String EDIT = "provider/providerEdit";
	private final String SHOW = "provider/providerShow";
	private final String VALIDATE = "provider/providerValidate";
	private final String ENABLEFIELD = "provider/providerEnableField";
	private final String RICERCA = "ricerca/ricercaProvider";

	@Autowired private ProviderService providerService;
	@Autowired private ProviderValidator providerValidator;

	@Autowired private ValutazioneService valutazioneService;
	@Autowired private ValutazioneValidator valutazioneValidator;
	@Autowired private FieldEditabileAccreditamentoService fieldEditabileAccreditamenoService;
	@Autowired private FieldValutazioneAccreditamentoService fieldValutazioneAccreditamentoService;

	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private IntegrazioneService integrazioneService;
	@Autowired private FieldIntegrazioneAccreditamentoService fieldIntegrazioneAccreditamentoService;

	@Autowired private TokenService tokenService;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	/*** GLOBAL MODEL ATTRIBUTES***/

	@ModelAttribute("providerWrapper")
	public ProviderWrapper getProvider(@RequestParam(name = "editId", required = false) Long id,
			@RequestParam(value="statoAccreditamento",required = false) AccreditamentoStatoEnum statoAccreditamento,
			@RequestParam(value="wrapperMode",required = false) AccreditamentoWrapperModeEnum wrapperMode,
			@RequestParam(value="accreditamentoId",required = false) Long accreditamentoId) throws Exception{
		if(id != null){
			//return prepareProviderWrapperEdit(providerService.getProvider(id), statoAccreditamento);
			return prepareWrapperForReloadByEditId(providerService.getProvider(id), accreditamentoId, statoAccreditamento, wrapperMode);
		}
		return new ProviderWrapper();
	}

	//Distinguo il prepareWrapper dalla verisione chiamata in caricamento della View da quella chiamata per il reload e merge con il form
	//nel secondo caso non riapplico le eventuali integrazioni altrimenti mi ritroverei delle entity attached me mi danno errore.
	//Distinguo inoltre il prepareWrapper in funzione dello stato della domanda
	private ProviderWrapper prepareWrapperForReloadByEditId(Provider provider, Long accreditamentoId, AccreditamentoStatoEnum statoAccreditamento, AccreditamentoWrapperModeEnum wrapperMode) throws Exception{
		if(wrapperMode == AccreditamentoWrapperModeEnum.EDIT)
			return prepareProviderWrapperEdit(provider, statoAccreditamento, true);
		if(wrapperMode == AccreditamentoWrapperModeEnum.VALIDATE)
			return prepareProviderWrapperValidate(provider, accreditamentoId, statoAccreditamento, false);

		return new ProviderWrapper();
	}

	/*** GLOBAL MODEL ATTRIBUTES***/

	/***	SHOW	***/
	@RequestMapping("/provider/show/all")
	public String showProviderFromCurrentUser(Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET: provider/show/all"));
		try {
			return goToShowProvider(model, providerService.getProvider());
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: provider/show/all"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect: /home";
		}
	}

	/*** WORKFLOW ***/
	@RequestMapping("/workflow/token/{token}/provider/{providerId}")
	@ResponseBody
	public ResponseState GetProviderUsers(@PathVariable("token") String token, @PathVariable("providerId") Long providerId) throws Exception{
		LOGGER.info(Utils.getLogMessage("GET /workflow/token/{token}/provider/{providerId} token: " + token + "; providerId: " + providerId));

		if(!tokenService.checkTokenAndDelete(token)) {
			String msg = "Impossibile trovare il token passato token: " + token;
			LOGGER.error(msg);
			return new ResponseState(true, msg);
		}
		//recupero la lista degli usernamebonita degli utenti del provider
		Provider provider = providerService.getProvider(providerId);

		if(provider == null) {
			String msg = "Impossibile trovare il provider passato providerId: " + providerId;
			LOGGER.error(msg);
			return new ResponseState(true, msg);
		}

		ResponseUsername responseUsername = new ResponseUsername();
		Set<String> usernames = new HashSet<>();
		//if(provider.getAccount() != null && provider.getAccount().getUsernameWorkflow() != null && !provider.getAccount().getUsernameWorkflow().isEmpty())
		//	usernames.add(provider.getAccount().getUsernameWorkflow());
		if(provider.getAccounts() != null && !provider.getAccounts().isEmpty()) {
			for(Account account : provider.getAccounts()) {
				usernames.add(account.getUsernameWorkflow());
			}
		}

		responseUsername.setUserNames(usernames);
		ResponseState responseState = new ResponseState(false, "Elenco usernames");
		ObjectMapper objMapper = new ObjectMapper();
	    String responseUsernameJson = objMapper.writeValueAsString(responseUsername);
	    responseState.setJsonObject(responseUsernameJson);

		return responseState;

/*
		Account account = accountRepository.findOneByUsername("provider").orElse(null);
		if(account != null) {
			workflowService.saveOrUpdateBonitaUserByAccount(account);
		}
 */
		//TODO modifica stato della domanda da parte del flusso
		//lo facciamo cosi in modo tale da non dover disabilitare la cache di hibernate
		//accreditamentoService.setStato(accreditamentoId, stato);
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#id)")
	@RequestMapping("/provider/{id}/show/all")
	public String showProvider(@PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET: provider/" + id + "/show/all"));
		try {
			return goToShowProvider(model, providerService.getProvider(id));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: provider/" + id + "/show/all"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: provider/list"));
			return "redirect: /provider/list";
		}
	}

	private String goToShowProvider(Model model, Provider provider){
		model.addAttribute("provider",provider);
		LOGGER.info(Utils.getLogMessage("VIEW: provider/providerShowAll"));
		return "provider/providerShowAll";
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canEditProvider(principal,#id)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{id}/edit")
	public String editProviderFromAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long id, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/edit"));
		try {
			return goToEdit(model, prepareProviderWrapperEdit(providerService.getProvider(id), accreditamentoId, accreditamentoService.getStatoAccreditamento(accreditamentoId), false));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
			return "redirect:/accreditamento/" + accreditamentoId + "/edit";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowAccreditamento(principal,#accreditamentoId) and @securityAccessServiceImpl.canShowProvider(principal,#id)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{id}/show")
	public String showProviderFromAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long id,
			@RequestParam(required = false) String from, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/show"));
		try {
			if (from != null) {
				redirectAttrs.addFlashAttribute("mode", from);
				return "redirect:/accreditamento/" + accreditamentoId + "/provider/" + id + "/show";
			}
			return goToShow(model, prepareProviderWrapperShow(providerService.getProvider(id), accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/show"));
			return "redirect:/accreditamento/" + accreditamentoId + "/show";
		}
	}

	/*** VALUTAZIONE ***/
	@PreAuthorize("@securityAccessServiceImpl.canValidateAccreditamento(principal,#accreditamentoId,#showRiepilogo)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{id}/validate")
	public String validateProviderFromAccreditamento(@RequestParam(name = "showRiepilogo", required = false) Boolean showRiepilogo,
			@PathVariable Long accreditamentoId, @PathVariable Long id,
			Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/validate"));
		try {
			//controllo se è possibile modificare la valutazione o meno
			model.addAttribute("canValutaDomanda", accreditamentoService.canUserValutaDomanda(accreditamentoId, Utils.getAuthenticatedUser()));
			return goToValidate(model, prepareProviderWrapperValidate(providerService.getProvider(id), accreditamentoId, accreditamentoService.getStatoAccreditamento(accreditamentoId), false));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/validate"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/validate"));
			return "redirect:/accreditamento/" + accreditamentoId + "/validate";
		}
	}

	/*** ENABLE_FIELD ***/
	@PreAuthorize("@securityAccessServiceImpl.canEnableField(principal,#accreditamentoId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/provider/{id}/enableField")
	public String enableFieldProviderFromAccreditamento(@PathVariable Long accreditamentoId, @PathVariable Long id,
			Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/enableField"));
		try {
			return goToEnableField(model, prepareProviderWrapperEnableField(providerService.getProvider(id), accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/" + id + "/enableField"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/enableField"));
			return "redirect:/accreditamento/" + accreditamentoId + "/enableField";
		}
	}

	/***	SAVE	***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/save", method = RequestMethod.POST)
	public String salvaProvider(@ModelAttribute("providerWrapper") ProviderWrapper providerWrapper, BindingResult result,
			Model model, RedirectAttributes redirectAttrs, @PathVariable Long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/save"));
		try{
			//validazione del provider
			providerValidator.validateForAccreditamento(providerWrapper.getProvider(), result, "provider.");

			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{

				if(providerWrapper.getStatoAccreditamento() == AccreditamentoStatoEnum.INTEGRAZIONE || providerWrapper.getStatoAccreditamento() == AccreditamentoStatoEnum.PREAVVISO_RIGETTO){
					integra(providerWrapper);
				}else{
					salva(providerWrapper);
				}

				redirectAttrs.addAttribute("accreditamentoId", providerWrapper.getAccreditamentoId());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.provider_salvato", "success"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/edit"));
				return "redirect:/accreditamento/{accreditamentoId}/edit";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/save"),ex);
			model.addAttribute("accreditamentoId",providerWrapper.getAccreditamentoId());
			model.addAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
			return EDIT;
		}
	}

	/***	SALVA VALUTAZIONE
	 * @throws Exception ***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/validate", method = RequestMethod.POST)
	public String valutaProvider(@ModelAttribute("providerWrapper") ProviderWrapper providerWrapper, BindingResult result,
			Model model, RedirectAttributes redirectAttrs, @PathVariable Long accreditamentoId) throws Exception{
		LOGGER.info(Utils.getLogMessage("POST: /accreditamento/" + accreditamentoId + "/provider/validate"));
		try{
			//validazione del provider
			valutazioneValidator.validateValutazione(providerWrapper.getMappa(), result);

			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				model.addAttribute("canValutaDomanda", accreditamentoService.canUserValutaDomanda(accreditamentoId, Utils.getAuthenticatedUser()));
				LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
				return VALIDATE;
			}else{
				Accreditamento accreditamento = new Accreditamento();
				accreditamento.setId(providerWrapper.getAccreditamentoId());
				providerWrapper.getMappa().forEach((k, v) -> {
					v.setIdField(k);
					v.setAccreditamento(accreditamento);
				});
				Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamento.getId(), Utils.getAuthenticatedUser().getAccount().getId());
				Set<FieldValutazioneAccreditamento> values = new HashSet<FieldValutazioneAccreditamento>(fieldValutazioneAccreditamentoService.saveMapList(providerWrapper.getMappa()));
				valutazione.getValutazioni().addAll(values);
				valutazioneService.save(valutazione);

				redirectAttrs.addAttribute("accreditamentoId", providerWrapper.getAccreditamentoId());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.valutazione_salvata", "success"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/validate"));
				return "redirect:/accreditamento/{accreditamentoId}/validate";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/validate"),ex);
			model.addAttribute("accreditamentoId",providerWrapper.getAccreditamentoId());
			model.addAttribute("canValutaDomanda", accreditamentoService.canUserValutaDomanda(accreditamentoId, Utils.getAuthenticatedUser()));
			model.addAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
			return VALIDATE;
		}
	}

	/***	SALVA ENABLEFIELD	***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/provider/enableField", method = RequestMethod.POST)
	public String enableFieldProvider(@ModelAttribute("richiestaIntegrazioneWrapper") RichiestaIntegrazioneWrapper richiestaIntegrazioneWrapper, Model model,
			RedirectAttributes redirectAttrs, @PathVariable Long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("POST: /accreditamento/" + accreditamentoId + "/provider/enableField"));
		try{
			integrazioneService.saveEnableField(richiestaIntegrazioneWrapper);
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.campi_salvati", "success"));
		}
		catch(Exception ex){
			LOGGER.error(Utils.getLogMessage("GET: /accreditamento/" + accreditamentoId + "/provider/enableField"),ex);
			model.addAttribute("message",new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/enablField"));
		}
		return "redirect:/accreditamento/{accreditamentoId}/enableField";
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowAllProvider(principal)")
	@RequestMapping("/provider/list")
	public String showAll(Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET: /provider/list"));
		try {

			if(model.asMap().get("providerList") == null)
				model.addAttribute("providerList", providerService.getAll());

			LOGGER.info(Utils.getLogMessage("VIEW: /provider/providerList"));
			return "provider/providerList";
		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET: /provider/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	private String goToEdit(Model model, ProviderWrapper providerWrapper){
		model.addAttribute("providerWrapper",providerWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	private String goToShow(Model model, ProviderWrapper providerWrapper){
		model.addAttribute("providerWrapper", providerWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
		return SHOW;
	}

	private String goToValidate(Model model, ProviderWrapper providerWrapper){
		model.addAttribute("providerWrapper", providerWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
		return VALIDATE;
	}

	private String goToEnableField(Model model, ProviderWrapper providerWrapper){
		model.addAttribute("providerWrapper", providerWrapper);
		model.addAttribute("richiestaIntegrazioneWrapper", integrazioneService.prepareRichiestaIntegrazioneWrapper(providerWrapper.getAccreditamentoId(), SubSetFieldEnum.PROVIDER, null));
		LOGGER.info(Utils.getLogMessage("VIEW: " + ENABLEFIELD));
		return ENABLEFIELD;
	}

	private ProviderWrapper prepareProviderWrapperEdit(Provider provider, AccreditamentoStatoEnum statoAccreditamento, boolean reloadByEditId) throws Exception{
		return prepareProviderWrapperEdit(provider, 0L, statoAccreditamento, reloadByEditId);
	}

	/*
	 * Se INTEGRAZIONE:
	 * caso 1: MODIFICA SINGOLO CAMPO
	 * 		(+) Saranno sbloccati SOLO gli IdFieldEnum eslpicitamente abilitati dalla segreteria (creazione di FieldEditabileAccreditamento)
	 * 		(+) Vengono applicati eventuali fieldIntegrazioneAccreditamento già salvati per visualizzare correttamente lo stato attuale delle modifiche
	 */
	private ProviderWrapper prepareProviderWrapperEdit(Provider provider, Long accreditamentoId, AccreditamentoStatoEnum statoAccreditamento, boolean reloadByEditId) throws Exception{
		LOGGER.info(Utils.getLogMessage("prepareProviderWrapperEdit("+ provider.getId() + "," + accreditamentoId +") - entering"));

		ProviderWrapper providerWrapper = new ProviderWrapper(provider, accreditamentoId);
		//la Segreteria se non è in uno stato di integrazione/preavviso rigetto può sempre modificare
		if (Utils.getAuthenticatedUser().getAccount().isSegreteria() && statoAccreditamento != AccreditamentoStatoEnum.INTEGRAZIONE && statoAccreditamento != AccreditamentoStatoEnum.PREAVVISO_RIGETTO)
			providerWrapper.setIdEditabili(IdFieldEnum.getAllForSubset(SubSetFieldEnum.PROVIDER));
		else
			providerWrapper.setIdEditabili(Utils.getSubsetOfIdFieldEnum(fieldEditabileAccreditamenoService.getAllFieldEditabileForAccreditamento(accreditamentoId), SubSetFieldEnum.PROVIDER));
		providerWrapper.setStatoAccreditamento(statoAccreditamento);
		providerWrapper.setWrapperMode(AccreditamentoWrapperModeEnum.EDIT);

		if(statoAccreditamento == AccreditamentoStatoEnum.INTEGRAZIONE || statoAccreditamento == AccreditamentoStatoEnum.PREAVVISO_RIGETTO){
			prepareApplyIntegrazione(providerWrapper, SubSetFieldEnum.PROVIDER, reloadByEditId);
		}

		LOGGER.info(Utils.getLogMessage("prepareProviderWrapperEdit("+ provider.getId() + "," + accreditamentoId +") - exiting"));
		return providerWrapper;
	}

	private void prepareApplyIntegrazione(ProviderWrapper providerWrapper, SubSetFieldEnum subset, boolean reloadByEditId) throws Exception{
		//providerWrapper.getProvider().getFiles().size();
		providerWrapper.getProvider().getComponentiComitatoScientifico().size();
		integrazioneService.detach(providerWrapper.getProvider());
		providerWrapper.setFieldIntegrazione(Utils.getSubset(fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazioneForAccreditamento(providerWrapper.getAccreditamentoId()), SubSetFieldEnum.PROVIDER));
		if(!reloadByEditId)
			integrazioneService.applyIntegrazioneObject(providerWrapper.getProvider(), providerWrapper.getFieldIntegrazione());
	}

	/*
	 * Se INTEGRAZIONE:
	 *
	 * caso 1: MODIFICA SINGOLO CAMPO
	 * 		(+) Viene salvato un fieldIntegrazione per ogni fieldEditabile abilitato
	 * 		(+) Ogni fieldIntegrazione contiene il nuovo valore serializzato in funzione del setField/getField di IntegrazioneServiceImpl
	 */
	@Transactional
	private void integra(ProviderWrapper wrapper) throws Exception{
		LOGGER.info(Utils.getLogMessage("Integrazione provider"));
		Accreditamento accreditamento = new Accreditamento();
		accreditamento.setId(wrapper.getAccreditamentoId());

		List<FieldIntegrazioneAccreditamento> fieldIntegrazioneList = new ArrayList<FieldIntegrazioneAccreditamento>();
		for(IdFieldEnum idField : wrapper.getIdEditabili()){
			fieldIntegrazioneList.add(new FieldIntegrazioneAccreditamento(idField, accreditamento, integrazioneService.getField(wrapper.getProvider(), idField.getNameRef()), TipoIntegrazioneEnum.MODIFICA));
		}

		fieldIntegrazioneAccreditamentoService.update(wrapper.getFieldIntegrazione(), fieldIntegrazioneList);
	}

	@Transactional
	private void salva(ProviderWrapper wrapper){
		LOGGER.info(Utils.getLogMessage("Salvataggio provider"));
		providerService.save(wrapper.getProvider());
	}

	private ProviderWrapper prepareProviderWrapperShow(Provider provider, Long accreditamentoId) {
		LOGGER.info(Utils.getLogMessage("prepareProviderWrapperShow("+ provider.getId() + "," + accreditamentoId +") - entering"));

		ProviderWrapper providerWrapper = new ProviderWrapper(provider, accreditamentoId);

		LOGGER.info(Utils.getLogMessage("prepareProviderWrapperShow("+ provider.getId() + "," + accreditamentoId +") - exiting"));
		return providerWrapper;
	}

	private ProviderWrapper prepareProviderWrapperValidate(Provider provider, Long accreditamentoId, AccreditamentoStatoEnum statoAccreditamento, boolean reloadByEditId) throws Exception{
		LOGGER.info(Utils.getLogMessage("prepareProviderWrapperValidate("+ provider.getId() + "," + accreditamentoId +") - entering"));

		SubSetFieldEnum subset = SubSetFieldEnum.PROVIDER;

		ProviderWrapper providerWrapper = new ProviderWrapper(provider, accreditamentoId);
		providerWrapper.setStatoAccreditamento(statoAccreditamento);
		providerWrapper.setWrapperMode(AccreditamentoWrapperModeEnum.VALIDATE);

		//carico la valutazione per l'utente corrente
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		if(valutazione != null) {
			mappa = fieldValutazioneAccreditamentoService.filterFieldValutazioneBySubSetAsMap(valutazione.getValutazioni(), subset);
		}
		providerWrapper.setMappa(mappa);

		//cerco tutte le valutazioni del subset provider per ciascun valutatore dell'accreditamento
		Map<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>> mappaValutatoreValutazioni = valutazioneService.getMapValutatoreValutazioniByAccreditamentoIdAndSubSet(accreditamentoId, subset);
		providerWrapper.setMappaValutatoreValutazioni(mappaValutatoreValutazioni);

		//prendo tutti gli id del subset
		Set<IdFieldEnum> idEditabili = IdFieldEnum.getAllForSubset(subset);
		providerWrapper.setIdEditabili(idEditabili);

		//solo se la valutazione è della segretaeria dopo l'INTEGRAZIONE
		if(statoAccreditamento == AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA){
			prepareApplyIntegrazione(providerWrapper, subset, reloadByEditId);
		}

		LOGGER.info(Utils.getLogMessage("prepareProviderWrapperValidate("+ provider.getId() + "," + accreditamentoId +") - exiting"));
		return providerWrapper;
	}

	private ProviderWrapper prepareProviderWrapperEnableField(Provider provider, Long accreditamentoId) {
		LOGGER.info(Utils.getLogMessage("prepareProviderWrapperEnableField("+ provider.getId() + "," + accreditamentoId +") - entering"));
		ProviderWrapper providerWrapper = prepareProviderWrapperShow(provider, accreditamentoId);
		LOGGER.info(Utils.getLogMessage("prepareProviderWrapperEnableField("+ provider.getId() + "," + accreditamentoId +") - exiting"));
		return providerWrapper;
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowAllProvider(principal)")
	@RequestMapping("/provider/ricerca")
	public String ricercaProviderGlobale(Model model,RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /provider/ricerca"));
		try {
			RicercaProviderWrapper wrapper = prepareRicercaProviderWrapper();
			model.addAttribute("ricercaProviderWrapper", wrapper);
			LOGGER.info(Utils.getLogMessage("VIEW: " + RICERCA));
			return RICERCA;
		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/ricerca"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/provider/ricerca", method = RequestMethod.POST)
	public String executeRicercaProvider(@ModelAttribute("ricercaProviderWrapper") RicercaProviderWrapper wrapper,
									BindingResult result, RedirectAttributes redirectAttrs, Model model, HttpServletRequest request){
		LOGGER.info(Utils.getLogMessage("POST /provider/ricerca"));
		try {

			String returnRedirect = "redirect:/provider/list";

			Set<Provider> listaProvider = new HashSet<Provider>();
			listaProvider.addAll(providerService.cerca(wrapper));

			redirectAttrs.addFlashAttribute("providerList", listaProvider);

			return returnRedirect;
		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/ricerca"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/provider/ricerca";
		}
	}

	private RicercaProviderWrapper prepareRicercaProviderWrapper(){
		RicercaProviderWrapper wrapper = new RicercaProviderWrapper();
		return wrapper;
	}

}
