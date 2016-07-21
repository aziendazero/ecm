package it.tredi.ecm.web;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import javax.persistence.OneToMany;

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

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AccreditamentoAllegatiWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.AccreditamentoAllegatiValidator;

@Controller
public class AccreditamentoAllegatiController {
	private static Logger LOGGER = LoggerFactory.getLogger(AccreditamentoAllegatiController.class);

	private final String EDIT = "accreditamento/accreditamentoAllegatiEdit";
	private final String SHOW = "accreditamento/accreditamentoAllegatiShow";

	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private ProviderService providerService;
	@Autowired private FileService fileService;
	@Autowired private AccreditamentoAllegatiValidator accreditamentoAllegatiValidator;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("accreditamentoAllegatiWrapper")
	public AccreditamentoAllegatiWrapper getAccreditamentoAllegatiWrapper(@RequestParam(value="editId",required = false) Long id){
		if(id != null){
			return prepareAccreditamentoAllegatiWrapperEdit(id);
		}
		return new AccreditamentoAllegatiWrapper();
	}

	/***	EDIT	***/
	@PreAuthorize("@securityAccessServiceImpl.canEditAccreditamento(principal,#accreditamentoId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/allegati/edit")
	public String editAllegati(@PathVariable Long accreditamentoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/"+ accreditamentoId +"/allegati/edit"));
		try{
			return goToEdit(model, prepareAccreditamentoAllegatiWrapperEdit(accreditamentoId));
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /accreditamento/"+ accreditamentoId +"/allegati/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addFlashAttribute("currentTab","tab3");
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			LOGGER.info(Utils.getLogMessage("REDIRECT: /accreditamento/"+ accreditamentoId));
			return "redirect:/accreditamento/{accreditamentoId}";
		}
	}

	/***	SHOW	***/
	@PreAuthorize("@securityAccessServiceImpl.canShowAccreditamento(principal,#accreditamentoId)")
	@RequestMapping("/accreditamento/{accreditamentoId}/allegati/show")
	public String showAllegati(@PathVariable Long accreditamentoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /accreditamento/"+ accreditamentoId +"/allegati/show"));
		try{
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
	public String saveDatiAccreditamento(@ModelAttribute("accreditamentoAllegatiWrapper") AccreditamentoAllegatiWrapper wrapper, BindingResult result,
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
					}
				}

			accreditamentoAllegatiValidator.validate(wrapper, result, "", wrapper.getFiles());

			if(result.hasErrors()){
				LOGGER.debug(Utils.getLogMessage("Validazione fallita"));
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}else{
				LOGGER.debug(Utils.getLogMessage("Salvataggio allegati al provider"));
				providerService.save(wrapper.getProvider());
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

	@RequestMapping(value = "/allegatiRedirect/{target}/{targetId}/{mode}")
	public String allegatiRedirect (@PathVariable String target, @PathVariable Long targetId, @PathVariable String mode,  RedirectAttributes redirectAttrs) {
		redirectAttrs.addFlashAttribute("currentTab", "tab3");
		return "redirect:/" + target + "/" + targetId + "/" + mode;
	}

	private AccreditamentoAllegatiWrapper prepareAccreditamentoAllegatiWrapperEdit(Long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoAllegatiWrapperEdit(" + accreditamentoId + ") - entering"));
		AccreditamentoAllegatiWrapper wrapper = new AccreditamentoAllegatiWrapper();
		wrapper.setAccreditamentoId(accreditamentoId);

		Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
		wrapper.setProvider(accreditamento.getProvider());

		Set<File> files = wrapper.getProvider().getFiles();
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
		}

		HashMap<FileEnum, Long> modelIds = fileService.getModelFileIds();
		wrapper.setModelIds(modelIds);
		wrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_ALLEGATI), accreditamento.getIdEditabili());

		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoAllegatiWrapperEdit(" + accreditamentoId + ") - exiting"));
		return wrapper;
	}

	private AccreditamentoAllegatiWrapper prepareAccreditamentoAllegatiWrapperShow(Long accreditamentoId){
		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoAllegatiWrapperShow(" + accreditamentoId + ") - entering"));
		AccreditamentoAllegatiWrapper wrapper = new AccreditamentoAllegatiWrapper();
		wrapper.setAccreditamentoId(accreditamentoId);

		Set<File> files = accreditamentoService.getAccreditamento(accreditamentoId).getProvider().getFiles();
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
		}

		LOGGER.info(Utils.getLogMessage("prepareAccreditamentoAllegatiWrapperShow(" + accreditamentoId + ") - exiting"));
		return wrapper;
	}

}
