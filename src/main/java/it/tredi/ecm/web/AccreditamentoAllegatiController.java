package it.tredi.ecm.web;

import java.util.Arrays;
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

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.AccreditamentoAllegatiWrapper;
import it.tredi.ecm.web.bean.DatiAccreditamentoWrapper;
import it.tredi.ecm.web.validator.AccreditamentoAllegatiValidator;

@Controller
public class AccreditamentoAllegatiController {
	private final String EDIT = "accreditamento/allegatiEdit";

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

	@RequestMapping("/accreditamento/{accreditamentoId}/allegati/edit")
	public String editAllegati(@PathVariable Long accreditamentoId, Model model){
		try{
			return goToEdit(model, prepareAccreditamentoAllegatiWrapper(accreditamentoId));
		}catch (Exception ex){
			//TODO gestione eccezione
			return "redirect:/accreditamento/" + accreditamentoId;
		}
	}

	@RequestMapping(value = "/accreditamento/{accreditamentoId}/allegati/save", method = RequestMethod.POST)
	public String saveDatiAccreditamento(@ModelAttribute("accreditamentoAllegatiWrapper") AccreditamentoAllegatiWrapper wrapper, BindingResult result,
			@PathVariable Long accreditamentoId, Model model,
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
	
				return EDIT;
			}else{
				saveFiles(wrapper, attoCostitutivo_multipart, esperienzaFormazione_multipart, utilizzo_multipart, sistemaInformatico_multipart, pianoQualita_multipart, dichiarazioneLegale_multipart);
				//NOTIFY
				return "redirect:/accreditamento/" + accreditamentoId;
			}
		}catch (Exception ex){
			//TODO gestione eccezione
			return EDIT;
		}
	}
	
	public void saveFiles(AccreditamentoAllegatiWrapper wrapper, MultipartFile attoCostitutivo_multipart, MultipartFile esperienzaFormazione_multipart, MultipartFile utilizzo_multipart, MultipartFile sistemaInformatico_multipart, MultipartFile pianoQualita_multipart, MultipartFile dichiarazioneLegale_multipart){
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

		wrapper.setOffsetAndIds();
		wrapper.setIdEditabili(Arrays.asList(87,88,89,90,91,92));

		return wrapper;
	}

}
