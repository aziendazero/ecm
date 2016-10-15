package it.tredi.ecm.web;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import it.tredi.ecm.dao.entity.Anagrafica;
import it.tredi.ecm.dao.entity.AnagraficaEvento;
import it.tredi.ecm.dao.entity.AnagraficaFullEvento;
import it.tredi.ecm.service.AnagraficaEventoService;
import it.tredi.ecm.service.AnagraficaFullEventoService;
import it.tredi.ecm.service.ProviderService;

@Controller
public class AnagraficaEventoController {

	@Autowired private AnagraficaEventoService anagraficaEventoService;
	@Autowired private AnagraficaFullEventoService anagraficaFullEventoService;
	@Autowired private ProviderService providerService;
	
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/anagraficaEventoList")
	@ResponseBody
	public Set<AnagraficaEvento>getAnagraficheEventoDelProvider(@PathVariable Long providerId){
		Set<AnagraficaEvento> lista = anagraficaEventoService.getAllAnagaficheByProvider(providerId);
		return lista;
	}
	
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/anagraficaFullEventoList")
	@ResponseBody
	public Set<AnagraficaFullEvento>getAnagraficheFullEventoDelProvider(@PathVariable Long providerId){
		Set<AnagraficaFullEvento> lista = anagraficaFullEventoService.getAllAnagraficheFullEventoByProvider(providerId);
		return lista;
	}
	
	@RequestMapping(value="/provider/{providerId}/createAnagraficaEvento", method=RequestMethod.POST)
	@ResponseBody
	public String saveAnagraficaEvento(@PathVariable("providerId") Long providerId, AnagraficaEvento anagrafica){
		anagrafica.setProvider(providerService.getProvider(providerId));
		anagraficaEventoService.save(anagrafica);
		return "OK";
	}
	
	
	
	
}
