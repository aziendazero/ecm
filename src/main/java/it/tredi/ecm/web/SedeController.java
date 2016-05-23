package it.tredi.ecm.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.enumlist.Costanti;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.SedeService;
import it.tredi.ecm.web.validator.SedeValidator;

@Controller
public class SedeController {
	
	private final String EDIT = "sede/sedeEdit";
	
	
	@Autowired
	private SedeValidator sedeValidator;
	@Autowired
	private SedeService sedeService;
	@Autowired
	private ProviderService providerService;
	
	/***	GLOBAL MODEL ATTRIBUTES	***/
	@ModelAttribute("elencoProvince")
	public List<String> getElencoProvince(){
		List<String> elencoProvince = new ArrayList<String>();
		
		elencoProvince.add("Belluno");
		elencoProvince.add("Padova");
		elencoProvince.add("Rovigo");
		elencoProvince.add("Treviso");
		elencoProvince.add("Venezia");
		elencoProvince.add("Verona");
		elencoProvince.add("Vicenza");
		
		return elencoProvince;
	}

	@RequestMapping("/sede/comuni")
	@ResponseBody
	public List<String>getElencoComuni(@RequestParam String provincia){
		HashMap<String, List<String>> elencoComuni = new HashMap<String, List<String>>();
		
		List<String> provinciaA = new ArrayList<String>();
		provinciaA.add("comuneA - 1");
		provinciaA.add("comuneA - 2");
		provinciaA.add("comuneA - 3");
		
		List<String> provinciaB = new ArrayList<String>();
		provinciaB.add("comuneB - 1");
		provinciaB.add("comuneB - 2");
		provinciaB.add("comuneB - 3");
		
		List<String> provinciaC = new ArrayList<String>();
		provinciaC.add("comuneC - 1");
		provinciaC.add("comuneC - 2");
		provinciaC.add("comuneC - 3");
		
		elencoComuni.put("Venezia", provinciaA);
		elencoComuni.put("Padova", provinciaB);
		elencoComuni.put("Trieste", provinciaC);
		
		return elencoComuni.get(provincia);
	}
	
	@ModelAttribute("sede")
	public Sede getSede(@RequestParam(name = "editId", required = false) Long id){
		if(id != null)
			return sedeService.getSede(id);
		return new Sede();
	}
	
	@RequestMapping("/sede/copia")
	public String getCopiaSedeLegale(Model model){
		Sede sedeLegale = new Sede();
		Provider currentProvider = providerService.getProvider();
		if(currentProvider != null){
			sedeLegale = currentProvider.getSedeLegale();
		}
		
		model.addAttribute("tipologiaSede", Costanti.SEDE_OPERATIVA);
		return goToEditWhitFragment(model, sedeLegale, "content");
	}
	
	/***	NEW / EDIT 	***/
	@RequestMapping("/sede/new")
	public String getNewSedeCurrentProvider(@RequestParam("tipologiaSede") String tipologiaSede, Model model) throws Exception{
		model.addAttribute("tipologiaSede", tipologiaSede);
		return goToEdit(model, new Sede());
	}
	
	@RequestMapping("/sede/{id}/edit")
	public String editSede(@PathVariable("id") Long id, 
							@RequestParam("tipologiaSede") String tipologiaSede, Model model){
		model.addAttribute("tipologiaSede", tipologiaSede);
		return goToEdit(model, sedeService.getSede(id));
	}
	
	/***	SAVE 	***/
	@RequestMapping(value = "/sede/save", method = RequestMethod.POST)
	public String saveSede(@ModelAttribute("sede") Sede sede, BindingResult result,  Model model, 
								@RequestParam(name = Costanti.SEDE_OPERATIVA, required = false) String SedeOperativa, 
								@RequestParam(name = Costanti.SEDE_LEGALE, required = false) String SedeLegale){

		sedeValidator.validate(sede, result, "");
		try{
			if(result.hasErrors()){
				model.addAttribute("tipologiaSede", SedeOperativa != null ? Costanti.SEDE_OPERATIVA : Costanti.SEDE_LEGALE);
				return EDIT;
			}else{
					sedeService.save(sede, providerService.getProvider(), SedeOperativa != null ? Costanti.SEDE_OPERATIVA : Costanti.SEDE_LEGALE);
					return "redirect:/provider/accreditamento/list";
			}
		}catch(Exception ex){
			//TODO Exception
		}
		
		return EDIT;
	}
	
	private String goToEdit(Model model, Sede sede){
		
		model.addAttribute("idOffset",0);
		model.addAttribute("idEditabili", new int[]{0,1,2,5,7,8});
		
		model.addAttribute("sede", sede);
		return EDIT;
	}
	
	private String goToEditWhitFragment(Model model, Sede sede, String fragment){
		model.addAttribute("sede", sede);
		return EDIT + " :: " + fragment;
	}
}
