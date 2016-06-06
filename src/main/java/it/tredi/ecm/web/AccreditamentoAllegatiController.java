package it.tredi.ecm.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.FileService;

@Controller
public class AccreditamentoAllegatiController {
	private final String EDIT = "accreditamento/allegatiEdit";
	
	@Autowired
	private FileService fileService;
	@Autowired
	private AccreditamentoService accreditamentoService;
	
	@InitBinder
	public void setAllowedFields(WebDataBinder dataBinder) {
		dataBinder.setDisallowedFields("id");
	}
	
	
}
