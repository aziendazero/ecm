package it.tredi.ecm.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Valutazione;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import it.tredi.ecm.dao.enumlist.AccreditamentoWrapperModeEnum;
import it.tredi.ecm.dao.enumlist.IdFieldEnum;
import it.tredi.ecm.dao.enumlist.SubSetFieldEnum;
import it.tredi.ecm.dao.enumlist.TipoIntegrazioneEnum;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.DatiAccreditamentoService;
import it.tredi.ecm.service.FieldEditabileAccreditamentoService;
import it.tredi.ecm.service.FieldIntegrazioneAccreditamentoService;
import it.tredi.ecm.service.FieldValutazioneAccreditamentoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.IntegrazioneService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.ValutazioneService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AccreditamentoAllegatiWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.RichiestaIntegrazioneWrapper;
import it.tredi.ecm.web.validator.AccreditamentoAllegatiValidator;
import it.tredi.ecm.web.validator.ValutazioneValidator;

@Controller
public class AccreditamentoAllegatiController {
	private static Logger LOGGER = LoggerFactory.getLogger(AccreditamentoAllegatiController.class);

	private final String EDIT = "accreditamento/accreditamentoAllegatiEdit";
	private final String SHOW = "accreditamento/accreditamentoAllegatiShow";
	private final String VALIDATE = "accreditamento/accreditamentoAllegatiValidate";
	private final String ENABLEFIELD = "accreditamento/accreditamentoAllegatiEnableField";

	@Autowired private AccreditamentoAllegatiValidator accreditamentoAllegatiValidator;
	@Autowired private DatiAccreditamentoService datiAccreditamentoService;
	@Autowired private FileService fileService;

	@Autowired private ValutazioneService valutazioneService;
	@Autowired private ValutazioneValidator valutazioneValidator;
	@Autowired private FieldEditabileAccreditamentoService fieldEditabileService;
	@Autowired private FieldValutazioneAccreditamentoService fieldValutazioneAccreditamentoService;

	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private IntegrazioneService integrazioneService;
	@Autowired private FieldIntegrazioneAccreditamentoService fieldIntegrazioneAccreditamentoService;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("accreditamentoAllegatiWrapper")
	public AccreditamentoAllegatiWrapper getAccreditamentoAllegatiWrapper(@RequestParam(value="editId",required = false) Long id,
			@RequestParam(value="statoAccreditamento",required = false) AccreditamentoStatoEnum statoAccreditamento,
			@RequestParam(value="wrapperMode",required = false) AccreditamentoWrapperModeEnum wrapperMode) throws Exception{
		if(id != null){
			//return prepareAccreditamentoAllegatiWrapperEdit(id, statoAccreditamento, true);
			return prepareWrapperForReloadByEditId(id, statoAccreditamento, wrapperMode);
		}
		return new AccreditamentoAllegatiWrapper();
	}

	private AccreditamentoAllegatiWrapper  prepareWrapperForReloadByEditId(Long accreditamentoId, AccreditamentoStatoEnum statoAccreditamento,
				AccreditamentoWrapperModeEnum wrapperMode) throws Exception{
		if(wrapperMode == AccreditamentoWrapperModeEnum.EDIT)
			return prepareAccreditamentoAllegatiWrapperEdit(accreditamentoId, statoAccreditamento, true);
		if(wrapperMode == AccreditamentoWrapperModeEnum.VALIDATE)
			return prepareAccreditamentoAllegatiWrapperValidate(accreditamentoId, statoAccreditamento, false);

		return new AccreditamentoAllegatiWrapper();
	}

	/***	EDIT	***/
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/allegati/edit")
	public String editAllegati(@PathVariable Long accreditamentoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/"+ accreditamentoId +"/allegati/edit"));
		try{
			return goToEdit(model, prepareAccreditamentoAllegatiWrapperEdit(accreditamentoId, accreditamentoService.getStatoAccreditamento(accreditamentoId), false));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/"+ accreditamentoId +"/allegati/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addFlashAttribute("currentTab","tab3");
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/"+ accreditamentoId + "/edit"));
			return "redirect:/accreditamento/{accreditamentoId}/edit";
		}
	}

	/***	VALIDATE	***/
	@PreAuthorize("@securityAccessServiceImpl.canValidateAccreditamento(principal,#accreditamentoId,#showRiepilogo)")
	@RequestMapping("/accreditamento/{accreditamentoId}/allegati/validate")
	public String validateAllegati(@RequestParam(name = "showRiepilogo", required = false) Boolean showRiepilogo,
			@PathVariable Long accreditamentoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/"+ accreditamentoId +"/allegati/validate"));
		try{
			//controllo se è possibile modificare la valutazione o meno
			model.addAttribute("canValutaDomanda", accreditamentoService.canUserValutaDomanda(accreditamentoId, Utils.getAuthenticatedUser()));
			return goToValidate(model, prepareAccreditamentoAllegatiWrapperValidate(accreditamentoId, accreditamentoService.getStatoAccreditamento(accreditamentoId),false));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/"+ accreditamentoId +"/allegati/validate"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addFlashAttribute("currentTab","tab3");
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/"+ accreditamentoId + "/validate"));
			return "redirect:/accreditamento/{accreditamentoId}/validate";
		}
	}

	/***	ENABLEFIELD	***/
	@PreAuthorize("@securityAccessServiceImpl.canEnableField(principal,#accreditamentoId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/allegati/enableField")
	public String enableFieldAllegati(@PathVariable Long accreditamentoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/"+ accreditamentoId +"/allegati/enableField"));
		try{
			return goToEnableField(model, prepareAccreditamentoAllegatiWrapperEnableField(accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/"+ accreditamentoId +"/allegati/enableField"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addFlashAttribute("currentTab","tab3");
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/"+ accreditamentoId + "/enableField"));
			return "redirect:/accreditamento/{accreditamentoId}/enableField";
		}
	}

	/***	SHOW	***/
	@PreAuthorize("@securityAccessServiceImpl.canShowAccreditamento(principal,#accreditamentoId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/allegati/show")
	public String showAllegati(@PathVariable Long accreditamentoId, @RequestParam(required = false) String from, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/"+ accreditamentoId +"/allegati/show"));
		try{
			if (from != null) {
				redirectAttrs.addFlashAttribute("mode", from);
				return "redirect:/accreditamento/" + accreditamentoId + "/allegati/show";
			}
			return goToShow(model, prepareAccreditamentoAllegatiWrapperShow(accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/"+ accreditamentoId +"/allegati/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addFlashAttribute("currentTab","tab3");
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/"+ accreditamentoId));
			return "redirect:/accreditamento/{accreditamentoId}";
		}
	}

	/***	SAVE	***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/allegati/save", method = RequestMethod.POST)
	public String saveAllegatiAccreditamento(@ModelAttribute("accreditamentoAllegatiWrapper") AccreditamentoAllegatiWrapper wrapper, BindingResult result,
			@PathVariable Long accreditamentoId, RedirectAttributes redirectAttrs, Model model){
		LOGGER.info(Utils.getLogMessage("POST /accreditamento/"+ accreditamentoId +"/allegati/save"));
		try {
			//TODO getFile da testare se funziona anche senza reload
			//reload degli allegati perchè se è stato fatto un upload ajax...il wrapper non ha i byte[] aggiornati e nemmeno il ref a providerId
			for(File file : wrapper.getFiles()){
				if(file != null && !file.isNew()){
					if(file.isATTOCOSTITUTIVO())
						wrapper.setAttoCostitutivo(fileService.getFile(file.getId()));
					else if(file.isESPERIENZAFORMAZIONE())
						wrapper.setEsperienzaFormazione(fileService.getFile(file.getId()));
					else if(file.isDICHIARAZIONELEGALE())
						wrapper.setDichiarazioneLegale(fileService.getFile(file.getId()));
					else if(file.isPIANOQUALITA())
						wrapper.setPianoQualita(fileService.getFile(file.getId()));
					else if(file.isUTILIZZO())
						wrapper.setUtilizzo(fileService.getFile(file.getId()));
					else if(file.isSISTEMAINFORMATICO())
						wrapper.setSistemaInformatico(fileService.getFile(file.getId()));
					else if(file.isDICHIARAZIONEESCLUSIONE())
						wrapper.setDichiarazioneEsclusione(fileService.getFile(file.getId()));
				}
			}

			//Ri-effettuo il detach..non sarebbe indispensabile...ma e' una precauzione a eventuali modifiche future
			//ci assicuriamo che effettivamente qualsiasi modifica alla entity in INTEGRAZIONE non venga flushata su DB
			AccreditamentoStatoEnum statoAccreditamento = accreditamentoService.getStatoAccreditamento(wrapper.getAccreditamentoId());
			if(statoAccreditamento == AccreditamentoStatoEnum.INTEGRAZIONE || statoAccreditamento == AccreditamentoStatoEnum.PREAVVISO_RIGETTO) {
				/*
				 * 20161216 abarducci
				 * correzzione ERROR - it.tredi.ecm.web.AccreditamentoAllegatiController - [provider4] - POST /accreditamento/3445/allegati/save
				 * org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: it.tredi.ecm.dao.entity.File.fileData, could not initialize proxy - no Session
				 * at org.hibernate.collection.internal.AbstractPersistentCollection.throwLazyInitializationException(AbstractPersistentCollection.java:576)
				 * at org.hibernate.collection.internal.AbstractPersistentCollection.withTemporarySessionIfNeeded(AbstractPersistentCollection.java:215)
				 * at org.hibernate.collection.internal.AbstractPersistentCollection.readSize(AbstractPersistentCollection.java:156)
				 * at org.hibernate.collection.internal.PersistentBag.isEmpty(PersistentBag.java:283)
				 * at it.tredi.ecm.dao.entity.File.getData(File.java:81)
				 * at it.tredi.ecm.web.validator.FileValidator.validateData(FileValidator.java:40)
				 * at it.tredi.ecm.web.validator.FileValidator.validate(FileValidator.java:30)
				 * at it.tredi.ecm.web.validator.AccreditamentoAllegatiValidator.validateFiles(AccreditamentoAllegatiValidator.java:69)
				 * at it.tredi.ecm.web.validator.AccreditamentoAllegatiValidator.validate(AccreditamentoAllegatiValidator.java:28)
				 * at it.tredi.ecm.web.AccreditamentoAllegatiController.saveAllegatiAccreditamento(AccreditamentoAllegatiController.java:214)
				 * at it.tredi.ecm.web.AccreditamentoAllegatiController$$FastClassBySpringCGLIB$$816a8258.invoke(<generated>)
				 * che si verifica in INTEGRAZIONE e PREAVVISO_RIGETTO a causa del successivo detach (vedi sotto)
				 * integrazioneService.detach(wrapper.getDatiAccreditamento());
				 * che detach anche i file restituiti da wrapper.getFiles() causando l'eccezione in validazione sul campo file.getFileData() che risulta lazy
				 */
				for(File file: wrapper.getFiles()) {
					if(file != null && !file.isNew())
						file.getFileData().size();
				}
				integrazioneService.detach(wrapper.getDatiAccreditamento());
			}

			LOGGER.debug(Utils.getLogMessage("MANAGED ENTITY: AccreditamentoAllegatiSave:__AFTER SET__"));
			integrazioneService.isManaged(wrapper.getDatiAccreditamento());

			accreditamentoAllegatiValidator.validate(wrapper, result, "", wrapper.getFiles(), wrapper.getDatiAccreditamento().getAccreditamento().getProvider().getId());

			if(result.hasErrors()){
				LOGGER.debug(Utils.getLogMessage("Validazione fallita"));
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				if(wrapper.getStatoAccreditamento() == AccreditamentoStatoEnum.INTEGRAZIONE || statoAccreditamento == AccreditamentoStatoEnum.PREAVVISO_RIGETTO){
					integra(wrapper);
				}else{
					LOGGER.debug(Utils.getLogMessage("Salvataggio allegati al provider"));
					datiAccreditamentoService.save(wrapper.getDatiAccreditamento(),wrapper.getAccreditamentoId());
				}
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.allegati_inseriti", "success"));
				redirectAttrs.addFlashAttribute("currentTab","tab3");
				redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
				LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/"+ accreditamentoId + "/edit"));
				return "redirect:/accreditamento/{accreditamentoId}/edit";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /accreditamento/"+ accreditamentoId +"/allegati/save"),ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
			return EDIT;
		}
	}

	@Transactional
	private void integra(AccreditamentoAllegatiWrapper wrapper) throws Exception{
		LOGGER.info(Utils.getLogMessage("Integrazione allegati accreditamento"));
		Accreditamento accreditamento = new Accreditamento();
		accreditamento.setId(wrapper.getAccreditamentoId());

		List<FieldIntegrazioneAccreditamento> fieldIntegrazioneList = new ArrayList<FieldIntegrazioneAccreditamento>();
		for(IdFieldEnum idField : wrapper.getIdEditabili()){
			fieldIntegrazioneList.add(new FieldIntegrazioneAccreditamento(idField, accreditamento, integrazioneService.getField(wrapper.getDatiAccreditamento(), idField.getNameRef()), TipoIntegrazioneEnum.MODIFICA));
		}

		fieldIntegrazioneAccreditamentoService.update(wrapper.getFieldIntegrazione(), fieldIntegrazioneList);
	}

	/***	 SAVE VALUTAZIONE ALLEGATI ACCREDITAMENTO
	 * @throws Exception ***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/allegati/validate", method = RequestMethod.POST)
	public String valutaAllegatiAccreditamento(@ModelAttribute("accreditamentoAllegatiWrapper") AccreditamentoAllegatiWrapper wrapper, BindingResult result,
			@PathVariable Long accreditamentoId, RedirectAttributes redirectAttrs, Model model) throws Exception{
		LOGGER.info(Utils.getLogMessage("POST /accreditamento/"+ accreditamentoId +"/allegati/validate"));
		try {
			//validazione dei file allegati accreditamento
			valutazioneValidator.validateValutazione(wrapper.getMappa(), result);

			if(result.hasErrors()){
				LOGGER.debug(Utils.getLogMessage("Validazione fallita"));
				model.addAttribute("canValutaDomanda", accreditamentoService.canUserValutaDomanda(accreditamentoId, Utils.getAuthenticatedUser()));
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
				return VALIDATE;
			}else{
				Accreditamento accreditamento = new Accreditamento();
				accreditamento.setId(wrapper.getAccreditamentoId());
				wrapper.getMappa().forEach((k, v) -> {
					v.setIdField(k);
					v.setAccreditamento(accreditamento);
				});

				Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamento.getId(), Utils.getAuthenticatedUser().getAccount().getId());
				Set<FieldValutazioneAccreditamento> values = new HashSet<FieldValutazioneAccreditamento>(fieldValutazioneAccreditamentoService.saveMapList(wrapper.getMappa()));
				valutazione.getValutazioni().addAll(values);
				valutazioneService.save(valutazione);

				redirectAttrs.addAttribute("accreditamentoId", wrapper.getAccreditamentoId());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.valutazione_salvata", "success"));
				redirectAttrs.addFlashAttribute("currentTab","tab3");
				LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/validate"));
				return "redirect:/accreditamento/{accreditamentoId}/validate";
			}
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("POST /accreditamento/"+ accreditamentoId +"/allegati/validate"),ex);
			model.addAttribute("canValutaDomanda", accreditamentoService.canUserValutaDomanda(accreditamentoId, Utils.getAuthenticatedUser()));
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
			return VALIDATE;
		}
	}

	/*** 	SAVE  ENABLEFIELD   ***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/allegati/enableField", method = RequestMethod.POST)
	public String enableFieldAllegatiAccreditamento(@ModelAttribute("richiestaIntegrazioneWrapper") RichiestaIntegrazioneWrapper richiestaIntegrazioneWrapper, @PathVariable Long accreditamentoId,
			Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /accreditamento/" + accreditamentoId + "/allegati/enableField"));
		try{
			integrazioneService.saveEnableField(richiestaIntegrazioneWrapper);
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.campi_salvati", "success"));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/" + accreditamentoId + "/allegati/enableField"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/" + accreditamentoId + "/enableField"));
		}
		redirectAttrs.addFlashAttribute("currentTab","tab3");
		return "redirect:/accreditamento/{accreditamentoId}/enableField";
	};

	private String goToEdit(Model model, AccreditamentoAllegatiWrapper wrapper){
		model.addAttribute("accreditamentoAllegatiWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	private String goToShow(Model model, AccreditamentoAllegatiWrapper wrapper){
		model.addAttribute("accreditamentoAllegatiWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + SHOW));
		return SHOW;
	}

	private String goToValidate(Model model, AccreditamentoAllegatiWrapper wrapper){
		model.addAttribute("accreditamentoAllegatiWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + VALIDATE));
		return VALIDATE;
	}

	private String goToEnableField(Model model, AccreditamentoAllegatiWrapper wrapper){
		model.addAttribute("accreditamentoAllegatiWrapper", wrapper);
		model.addAttribute("richiestaIntegrazioneWrapper", integrazioneService.prepareRichiestaIntegrazioneWrapper(wrapper.getAccreditamentoId(), SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO, null));
		LOGGER.info(Utils.getLogMessage("VIEW: " + ENABLEFIELD));
		return ENABLEFIELD;
	}

	private AccreditamentoAllegatiWrapper prepareAccreditamentoAllegatiWrapperEdit(Long accreditamentoId, AccreditamentoStatoEnum statoAccreditamento, boolean reloadByEditId) throws Exception{
		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoAllegatiWrapperEdit(" + accreditamentoId + ") - entering"));
		Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);

		SubSetFieldEnum subset = SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO;

		AccreditamentoAllegatiWrapper wrapper = new AccreditamentoAllegatiWrapper();
		wrapper.setAccreditamentoId(accreditamentoId);
		//wrapper.setProvider(accreditamento.getProvider());
		wrapper.setDatiAccreditamento(accreditamento.getDatiAccreditamento());
		wrapper.setModelIds(fileService.getModelFileIds());
		//la Segreteria se non è in uno stato di integrazione/preavviso rigetto può sempre modificare
		if (Utils.getAuthenticatedUser().getAccount().isSegreteria() && statoAccreditamento != AccreditamentoStatoEnum.INTEGRAZIONE && statoAccreditamento != AccreditamentoStatoEnum.PREAVVISO_RIGETTO)
			wrapper.setIdEditabili(IdFieldEnum.getAllForSubset(subset));
		else
			wrapper.setIdEditabili(Utils.getSubsetOfIdFieldEnum(fieldEditabileService.getAllFieldEditabileForAccreditamento(accreditamentoId), subset));
		wrapper.setStatoAccreditamento(statoAccreditamento);
		wrapper.setWrapperMode(AccreditamentoWrapperModeEnum.EDIT);

		if(statoAccreditamento == AccreditamentoStatoEnum.INTEGRAZIONE || statoAccreditamento == AccreditamentoStatoEnum.PREAVVISO_RIGETTO){
			prepareApplyIntegrazione(wrapper, subset, reloadByEditId);
		}

		//set dei files sul wrapper, per allinearmi nel caso ci fossero dei fieldIntegrazione relativi a files
		if(!reloadByEditId)
			wrapper.setFiles(wrapper.getDatiAccreditamento().getFiles());

		LOGGER.debug(Utils.getLogMessage("__EXITING PREPAREWRAPPER__"));
		integrazioneService.isManaged(wrapper.getDatiAccreditamento());

		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoAllegatiWrapperEdit(" + accreditamentoId + ") - exiting"));
		return wrapper;
	}

	private void prepareApplyIntegrazione(AccreditamentoAllegatiWrapper wrapper, SubSetFieldEnum subset, boolean reloadByEditId) throws Exception{
		wrapper.setFieldIntegrazione(Utils.getSubset(fieldIntegrazioneAccreditamentoService.getAllFieldIntegrazioneForAccreditamento(wrapper.getAccreditamentoId()), subset));
		wrapper.getDatiAccreditamento().getFiles().size();
		integrazioneService.detach(wrapper.getDatiAccreditamento());
		//se stiamo ricaricando il wrapper per andare in save...non ha senso riapplicare le integrazioni
		//il wrapper del form arriva gia' aggiornato (vedi accreditamentoId = 0 in PersonaController)
		if(!reloadByEditId){
			integrazioneService.applyIntegrazioneObject(wrapper.getDatiAccreditamento(), wrapper.getFieldIntegrazione());
		}
	}

	private AccreditamentoAllegatiWrapper prepareAccreditamentoAllegatiWrapperValidate(Long accreditamentoId, AccreditamentoStatoEnum statoAccreditamento, boolean reloadByEditId) throws Exception{
		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoAllegatiWrapperValidate(" + accreditamentoId + ") - entering"));

		SubSetFieldEnum subset = SubSetFieldEnum.ALLEGATI_ACCREDITAMENTO;

		AccreditamentoAllegatiWrapper wrapper = new AccreditamentoAllegatiWrapper();
		wrapper.setAccreditamentoId(accreditamentoId);
		wrapper.setStatoAccreditamento(statoAccreditamento);
		wrapper.setWrapperMode(AccreditamentoWrapperModeEnum.VALIDATE);

		Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
		wrapper.setDatiAccreditamento(accreditamento.getDatiAccreditamento());

		//carico la valutazione per l'utente
		Valutazione valutazione = valutazioneService.getValutazioneByAccreditamentoIdAndAccountIdAndNotStoricizzato(accreditamentoId, Utils.getAuthenticatedUser().getAccount().getId());
		Map<IdFieldEnum, FieldValutazioneAccreditamento> mappa = new HashMap<IdFieldEnum, FieldValutazioneAccreditamento>();
		if(valutazione != null) {
			mappa = fieldValutazioneAccreditamentoService.filterFieldValutazioneBySubSetAsMap(valutazione.getValutazioni(), subset);
		}

		//cerco tutte le valutazioni del subset allegati per ciascun valutatore dell'accreditamento
		Map<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>> mappaValutatoreValutazioni = new HashMap<Account, Map<IdFieldEnum, FieldValutazioneAccreditamento>>();
		mappaValutatoreValutazioni = valutazioneService.getMapValutatoreValutazioniByAccreditamentoIdAndSubSet(accreditamentoId, subset);

		//prendo tutti gli id del subset
		Set<IdFieldEnum> idEditabili = new HashSet<IdFieldEnum>();
		idEditabili = IdFieldEnum.getAllForSubset(subset);

		wrapper.setMappaValutatoreValutazioni(mappaValutatoreValutazioni);
		wrapper.setIdEditabili(idEditabili);
		wrapper.setMappa(mappa);

		if(statoAccreditamento == AccreditamentoStatoEnum.VALUTAZIONE_SEGRETERIA){
			prepareApplyIntegrazione(wrapper, subset, reloadByEditId);
		}

		wrapper.setFiles(wrapper.getDatiAccreditamento().getFiles());

		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoAllegatiWrapperValidate(" + accreditamentoId + ") - exiting"));
		return wrapper;
	}

	private AccreditamentoAllegatiWrapper prepareAccreditamentoAllegatiWrapperShow(Long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoAllegatiWrapperShow(" + accreditamentoId + ") - entering"));
		AccreditamentoAllegatiWrapper wrapper = new AccreditamentoAllegatiWrapper();
		wrapper.setAccreditamentoId(accreditamentoId);

		Set<File> files = accreditamentoService.getAccreditamento(accreditamentoId).getDatiAccreditamento().getFiles();
		for(File file : files){
			if(file.isATTOCOSTITUTIVO())
				wrapper.setAttoCostitutivo(file);
			else if(file.isESPERIENZAFORMAZIONE())
				wrapper.setEsperienzaFormazione(file);
			else if(file.isDICHIARAZIONELEGALE())
				wrapper.setDichiarazioneLegale(file);
			else if(file.isPIANOQUALITA())
				wrapper.setPianoQualita(file);
			else if(file.isUTILIZZO())
				wrapper.setUtilizzo(file);
			else if(file.isSISTEMAINFORMATICO())
				wrapper.setSistemaInformatico(file);
			else if(file.isDICHIARAZIONEESCLUSIONE())
				wrapper.setDichiarazioneEsclusione(file);
		}

		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoAllegatiWrapperShow(" + accreditamentoId + ") - exiting"));
		return wrapper;
	}

	private AccreditamentoAllegatiWrapper prepareAccreditamentoAllegatiWrapperEnableField(Long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoAllegatiWrapperEnableField(" + accreditamentoId + ") - entering"));
		AccreditamentoAllegatiWrapper wrapper = prepareAccreditamentoAllegatiWrapperShow(accreditamentoId);
		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoAllegatiWrapperEnableField(" + accreditamentoId + ") - exiting"));
		return wrapper;
	}
}
