package it.tredi.ecm.web;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.web.bean.EngineeringWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.validator.EngineeringTestValidator;

@Controller
public class EngineeringController {

	private static Logger LOGGER = LoggerFactory.getLogger(EngineeringController.class);

	@Autowired
	private ProviderService providerService;
	@Autowired
	private FileService fileService;
	@Autowired
	private EventoService eventoService;
	@Autowired
	private EngineeringTestValidator engineeringTestValidator;

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}

	@ModelAttribute("engineeringWrapper")
	public EngineeringWrapper getEngineeringWrapper(@RequestParam(value="editId",required = false) Long id){
		if(id != null){
			return prepareEngineeringWrapper(providerService.getProvider(id));
		}
		return new EngineeringWrapper();
	}

	/*** TEST FIRMA ***/
	@RequestMapping("/engineering/test/firma")
	public String engineeringTestFirma(Model model, RedirectAttributes redirectAttrs){
		try {
			model.addAttribute("engineeringWrapper", prepareEngineeringWrapper(providerService.getProvider()));
			return "engineering/firmaTest";
		}catch (Exception ex){
			//TODO gestione eccezione
			LOGGER.error("Errore redirect firma", ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	/*** TEST MYPAY ***/
	@RequestMapping("/engineering/test/mypay")
	public String engineeringTestMypay(Model model, RedirectAttributes redirectAttrs){
		try {
			model.addAttribute("eventoList", eventoService.getAllEventiFromProvider(providerService.getProvider().getId()));
			return "engineering/mypayTest";
		}catch (Exception ex){
			//TODO gestione eccezione
			LOGGER.error("Errore redirect mypay", ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}

	/*** SAVE ***/
	@RequestMapping(value = "/engineering/test/firma/save", method = RequestMethod.POST)
	public String saveEngineeringTestSave(@ModelAttribute("engineeringWrapper") EngineeringWrapper wrapper, BindingResult result,
											RedirectAttributes redirectAttrs, Model model){
		try {

			//TODO getFile da testare se funziona anche senza reload
			//reload degli allegati perchè se è stato fatto un upload ajax...il wrapper non ha i byte[] aggiornati e nemmeno il ref a providerId
			File file = wrapper.getFileDaFirmare();
			if(file != null && !file.isNew()){
				if(file.isFILEDAFIRMARE())
					wrapper.setFileDaFirmare(fileService.getFile(file.getId()));
			}

			engineeringTestValidator.validate(wrapper.getProvider(), result, "testFirma.", wrapper.getFileDaFirmare());

			if(result.hasErrors()){
				model.addAttribute("message",new Message("message.errore", "message.inserire_campi_required", "error"));
				return "engineering/firmaTest";
			}else{
				providerService.save(wrapper.getProvider());
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "engineering.dati_inseriti", "success"));
				return "redirect:/engineering/test/firma";
			}
		}catch (Exception ex){
			//TODO gestione eccezione
			LOGGER.error("Errore Salvataggio", ex);
			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "engineering/firmaTest";
		}
	}

	private EngineeringWrapper prepareEngineeringWrapper(Provider provider) {
		EngineeringWrapper wrapper = new EngineeringWrapper();
		wrapper.setProvider(provider);

		Set<File> files = provider.getFiles();
		for(File file : files){
			if(file.isFILEDAFIRMARE())
				wrapper.setFileDaFirmare(file);
		}

		return wrapper;
	}

}