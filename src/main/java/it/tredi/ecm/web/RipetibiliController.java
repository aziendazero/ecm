package it.tredi.ecm.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.RipetibiliWrapper;

@Controller
@SessionAttributes("wrapper")
public class RipetibiliController {
	public static final Logger LOGGER = LoggerFactory.getLogger(EventoPianoFormativoController.class);
	private final String EDIT = "ripetibili/edit";
	private final String SHOW = "ripetibili/show";

	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}
	
	private RipetibiliWrapper prepareWrapper(){
		RipetibiliWrapper wrapper = new RipetibiliWrapper();
		wrapper.getElements().add(new String());
		return wrapper;
	}
	
	@RequestMapping("/ripetibili/edit")
	public String editPage(Model model, RedirectAttributes redirectAttrs){
		try{
			model.addAttribute("wrapper", prepareWrapper());
			return EDIT;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@RequestMapping(value="/ripetibili/save", method=RequestMethod.POST, params={"action"})
	public String savePage(@ModelAttribute("wrapper") RipetibiliWrapper wrapper,Model model, RedirectAttributes redirectAttrs){
		try{
			return SHOW;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@RequestMapping(value = "/ripetibili/save", method=RequestMethod.POST, params={"addElement"})
	public String addElement(@ModelAttribute("wrapper") RipetibiliWrapper wrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			wrapper.getElements().add(new String());
			return EDIT;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
	
	@RequestMapping(value = "/ripetibili/save", method=RequestMethod.POST, params={"removeElement"})
	public String removeElement(@RequestParam("removeElement") String index, @ModelAttribute("wrapper") RipetibiliWrapper wrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(wrapper.getElements().size() == 1){
				wrapper.getElements().clear();
				wrapper.getElements().add(new String());
			}else{
				wrapper.getElements().remove(Integer.valueOf(index).intValue());
			}
			return EDIT;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/home";
		}
	}
}
