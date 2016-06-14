package it.tredi.ecm.web;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AccreditamentoAllegatiWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.AccreditamentoAllegatiValidator;

@Controller
public class AccreditamentoAllegatiController {
	private final String EDIT = "accreditamento/accreditamentoAllegatiEdit";

	@Autowired
	private AccreditamentoAllegatiValidator accreditamentoAllegatiValidator;
	@Autowired
	private FileService fileService;
	@Autowired
	private AccreditamentoService accreditamentoService;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}
	
	@ModelAttribute("accreditamentoAllegatiWrapper")
	public AccreditamentoAllegatiWrapper getAccreditamentoAllegatiWrapper(@RequestParam(value="editId",required = false) Long id){
		if(id != null){
			return prepareAccreditamentoAllegatiWrapper(id);
		}
		return new AccreditamentoAllegatiWrapper();
	}

	/***	EDIT	***/
	@RequestMapping("/accreditamento/{accreditamentoId}/allegati/edit")
	public String editAllegati(@PathVariable Long accreditamentoId, Model model, RedirectAttributes redirectAttrs){
		try{
			return goToEdit(model, prepareAccreditamentoAllegatiWrapper(accreditamentoId));
		}catch (Exception ex){
			//TODO gestione eccezione
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			redirectAttrs.addFlashAttribute("currentTab","tab3");
			redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
			return "redirect:/accreditamento/{accreditamentoId}";
		}
	}

	/***	SAVE	***/
	@RequestMapping(value = "/accreditamento/{accreditamentoId}/allegati/save", method = RequestMethod.POST)
	public String saveDatiAccreditamento(@ModelAttribute("accreditamentoAllegatiWrapper") AccreditamentoAllegatiWrapper wrapper, BindingResult result,
			@PathVariable Long accreditamentoId, RedirectAttributes redirectAttrs, Model model,
			@RequestParam(value = "attoCostitutivo_multipart", required = false) MultipartFile attoCostitutivo_multipart,
			@RequestParam(value = "esperienzaFormazione_multipart", required = false) MultipartFile esperienzaFormazione_multipart,
			@RequestParam(value = "utilizzo_multipart", required = false) MultipartFile utilizzo_multipart,
			@RequestParam(value = "sistemaInformatico_multipart", required = false) MultipartFile sistemaInformatico_multipart,
			@RequestParam(value = "pianoQualita_multipart", required = false) MultipartFile pianoQualita_multipart,
			@RequestParam(value = "dichiarazioneLegale_multipart", required = false) MultipartFile dichiarazioneLegale_multipart){

		try {
			if(attoCostitutivo_multipart != null && !attoCostitutivo_multipart.isEmpty())
				wrapper.setAttoCostitutivo(Utils.convertFromMultiPart(attoCostitutivo_multipart));
			if(esperienzaFormazione_multipart != null && !esperienzaFormazione_multipart.isEmpty())
				wrapper.setEsperienzaFormazione(Utils.convertFromMultiPart(esperienzaFormazione_multipart));
			if(utilizzo_multipart != null && !utilizzo_multipart.isEmpty())
				wrapper.setUtilizzo(Utils.convertFromMultiPart(utilizzo_multipart));
			if(sistemaInformatico_multipart != null && !sistemaInformatico_multipart.isEmpty())
				wrapper.setSistemaInformatico(Utils.convertFromMultiPart(sistemaInformatico_multipart));
			if(pianoQualita_multipart != null && !pianoQualita_multipart.isEmpty())
				wrapper.setPianoQualita(Utils.convertFromMultiPart(pianoQualita_multipart));
			if(dichiarazioneLegale_multipart != null && !dichiarazioneLegale_multipart.isEmpty())
				wrapper.setDichiarazioneLegale(Utils.convertFromMultiPart(dichiarazioneLegale_multipart));
	
			accreditamentoAllegatiValidator.validate(wrapper, result, "", wrapper.getFiles());
			
			if(result.hasErrors()){
	
				if(!result.hasFieldErrors("attoCostitutivo*") && attoCostitutivo_multipart != null && !attoCostitutivo_multipart.isEmpty()){
					fileService.save(wrapper.getAttoCostitutivo());
				}
				if(!result.hasFieldErrors("esperienzaFormazione*") && esperienzaFormazione_multipart != null && !esperienzaFormazione_multipart.isEmpty()){
					fileService.save(wrapper.getEsperienzaFormazione());
				}
				if(!result.hasFieldErrors("utilizzo*") && utilizzo_multipart != null && !utilizzo_multipart.isEmpty()){
					fileService.save(wrapper.getUtilizzo());
				}
				if(!result.hasFieldErrors("sistemaInformatico*") && sistemaInformatico_multipart != null && !sistemaInformatico_multipart.isEmpty()){
					fileService.save(wrapper.getSistemaInformatico());
				}
				if(!result.hasFieldErrors("pianoQualita*") && pianoQualita_multipart != null && !pianoQualita_multipart.isEmpty()){
					fileService.save(wrapper.getPianoQualita());
				}
				if(!result.hasFieldErrors("dichiarazioneLegale*") && dichiarazioneLegale_multipart != null && !dichiarazioneLegale_multipart.isEmpty()){
					fileService.save(wrapper.getDichiarazioneLegale());
				}
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				return EDIT;
			}else{
				saveFiles(wrapper, attoCostitutivo_multipart, esperienzaFormazione_multipart, utilizzo_multipart, sistemaInformatico_multipart, pianoQualita_multipart, dichiarazioneLegale_multipart);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.allegati_inseriti", "success"));
				redirectAttrs.addFlashAttribute("currentTab","tab3");
				redirectAttrs.addAttribute("accreditamentoId", accreditamentoId);
				return "redirect:/accreditamento/{accreditamentoId}";
			}
		}catch (Exception ex){
			//TODO gestione eccezione
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return EDIT;
		}
	}
	
	
	/***	METODI PRIVATI PER SUPPORTO	***/
	private void saveFiles(AccreditamentoAllegatiWrapper wrapper, MultipartFile attoCostitutivo_multipart, MultipartFile esperienzaFormazione_multipart, MultipartFile utilizzo_multipart, MultipartFile sistemaInformatico_multipart, MultipartFile pianoQualita_multipart, MultipartFile dichiarazioneLegale_multipart){
		if(attoCostitutivo_multipart != null && !attoCostitutivo_multipart.isEmpty()){
			fileService.save(wrapper.getAttoCostitutivo());
		}
		if(esperienzaFormazione_multipart != null && !esperienzaFormazione_multipart.isEmpty()){
			fileService.save(wrapper.getEsperienzaFormazione());
		}
		if(utilizzo_multipart != null && !utilizzo_multipart.isEmpty()){
			fileService.save(wrapper.getUtilizzo());
		}
		if(sistemaInformatico_multipart != null && !sistemaInformatico_multipart.isEmpty()){
			fileService.save(wrapper.getSistemaInformatico());
		}
		if(pianoQualita_multipart != null && !pianoQualita_multipart.isEmpty()){
			fileService.save(wrapper.getPianoQualita());
		}
		if(dichiarazioneLegale_multipart != null && !dichiarazioneLegale_multipart.isEmpty()){
			fileService.save(wrapper.getDichiarazioneLegale());
		}
	}
	
	private String goToEdit(Model model, AccreditamentoAllegatiWrapper wrapper){
		model.addAttribute("accreditamentoAllegatiWrapper", wrapper);
		return EDIT;
	}

	private AccreditamentoAllegatiWrapper prepareAccreditamentoAllegatiWrapper(Long accreditamentoId){
		AccreditamentoAllegatiWrapper wrapper = new AccreditamentoAllegatiWrapper();
		wrapper.setAccreditamentoId(accreditamentoId);

		Accreditamento accreditamento = accreditamentoService.getAccreditamento(accreditamentoId);
		wrapper.setProvider(accreditamento.getProvider());

		Set<File> files = fileService.getFileFromProvider(accreditamento.getProvider().getId());
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

		HashMap<String, Long> modelIds = fileService.getModelIds();
		wrapper.setModelIds(modelIds);
		wrapper.setOffsetAndIds(new LinkedList<Integer>(Costanti.IDS_ALLEGATI), accreditamento.getIdEditabili());

		return wrapper;
	}

}
