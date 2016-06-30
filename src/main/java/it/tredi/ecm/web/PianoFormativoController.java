package it.tredi.ecm.web;

import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.web.bean.Message;

@Controller
public class PianoFormativoController {

	private static final Logger LOGGER = Logger.getLogger(PianoFormativoController.class); 
	
	@Autowired
	private EventoService eventoService;
		
	
}
