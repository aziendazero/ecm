package it.tredi.ecm.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.annotation.JsonView;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.AnagraficaEvento;
import it.tredi.ecm.dao.entity.AnagraficaEventoBase;
import it.tredi.ecm.dao.entity.AnagraficaFullEvento;
import it.tredi.ecm.dao.entity.AnagraficaFullEventoBase;
import it.tredi.ecm.dao.entity.AzioneRuoliEventoFSC;
import it.tredi.ecm.dao.entity.DatiAccreditamento;
import it.tredi.ecm.dao.entity.DettaglioAttivitaFAD;
import it.tredi.ecm.dao.entity.DettaglioAttivitaRES;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoListDataModel;
import it.tredi.ecm.dao.entity.EventoListDataTableModel;
import it.tredi.ecm.dao.entity.EventoPianoFormativo;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.FaseAzioniRuoliEventoFSCTypeA;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.JsonViewModel;
import it.tredi.ecm.dao.entity.Partner;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.entity.PersonaFullEvento;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.entity.RuoloOreFSC;
import it.tredi.ecm.dao.entity.Sponsor;
import it.tredi.ecm.dao.enumlist.EventoSearchEnum;
import it.tredi.ecm.dao.enumlist.EventoStatoEnum;
import it.tredi.ecm.dao.enumlist.EventoWrapperModeEnum;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaFADEnum;
import it.tredi.ecm.dao.enumlist.MetodologiaDidatticaRESEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiFADEnum;
import it.tredi.ecm.dao.enumlist.ObiettiviFormativiRESEnum;
import it.tredi.ecm.dao.enumlist.ProceduraFormativa;
import it.tredi.ecm.dao.enumlist.RuoloFSCEnum;
import it.tredi.ecm.dao.enumlist.TipologiaEventoFSCEnum;
import it.tredi.ecm.dao.repository.PersonaEventoRepository;
import it.tredi.ecm.exception.AccreditamentoNotFoundException;
import it.tredi.ecm.exception.EcmException;
import it.tredi.ecm.exception.PagInCorsoException;
import it.tredi.ecm.service.AccreditamentoService;
import it.tredi.ecm.service.AlertEmailService;
import it.tredi.ecm.service.AnagraficaEventoService;
import it.tredi.ecm.service.AnagraficaFullEventoService;
import it.tredi.ecm.service.DatiAccreditamentoService;
import it.tredi.ecm.service.DisciplinaService;
import it.tredi.ecm.service.EngineeringService;
import it.tredi.ecm.service.EventoPianoFormativoService;
import it.tredi.ecm.service.EventoService;
import it.tredi.ecm.service.FileService;
import it.tredi.ecm.service.ObiettivoService;
import it.tredi.ecm.service.PdfEventoService;
import it.tredi.ecm.service.PersonaEventoService;
import it.tredi.ecm.service.ProfessioneService;
import it.tredi.ecm.service.ProviderService;
import it.tredi.ecm.service.bean.CurrentUser;
import it.tredi.ecm.service.controller.EventoServiceController;
import it.tredi.ecm.utils.Utils;
import it.tredi.ecm.web.bean.ErrorsAjaxWrapper;
import it.tredi.ecm.web.bean.EventoWrapper;
import it.tredi.ecm.web.bean.Message;
import it.tredi.ecm.web.bean.ModificaOrarioAttivitaWrapper;
import it.tredi.ecm.web.bean.QuietanzaWrapper;
import it.tredi.ecm.web.bean.RicercaEventoWrapper;
import it.tredi.ecm.web.bean.ScadenzeEventoWrapper;
import it.tredi.ecm.web.bean.SponsorWrapper;
import it.tredi.ecm.web.validator.AnagraficaValidator;
import it.tredi.ecm.web.validator.EventoValidator;
import it.tredi.ecm.web.validator.PersonaEventoValidator;
import it.tredi.ecm.web.validator.RuoloOreFSCValidator;
import it.tredi.ecm.web.validator.ScadenzeEventoValidator;

@Controller
@SessionAttributes({"eventoWrapper","eventoWrapperRendiconto", "searchList", "providerId", "returnLink"})
public class EventoController {
	public static final Logger LOGGER = LoggerFactory.getLogger(EventoController.class);

	@Autowired private EventoService eventoService;
	@Autowired private EventoPianoFormativoService eventoPianoFormativoService;
	@Autowired private ProviderService providerService;
	@Autowired private ObiettivoService obiettivoService;
	@Autowired private AccreditamentoService accreditamentoService;
	@Autowired private FileService fileService;
	@Autowired private AnagraficaValidator anagraficaValidator;

	@Autowired private AnagraficaEventoService anagraficaEventoService;
	@Autowired private AnagraficaFullEventoService anagraficaFullEventoService;
	@Autowired private PersonaEventoRepository personaEventoRepository;
	@Autowired private PersonaEventoService personaEventoService;

	@Autowired private RuoloOreFSCValidator ruoloOreFSCValidator;
	@Autowired private EventoValidator eventoValidator;

	@Autowired private EngineeringService engineeringService;

	@Autowired private ProfessioneService professioneService;
	@Autowired private DisciplinaService disciplinaService;

	@Autowired private PdfEventoService pdfEventoService;
	@Autowired private AlertEmailService alertEmailService;

	@Autowired private ScadenzeEventoValidator scadenzeEventoValidator;
	@Autowired private PersonaEventoValidator personaEventoValidator;

	@Autowired private DatiAccreditamentoService datiAccreditamentoService;
	@Autowired private EventoServiceController eventoServiceController;

	@Autowired
	private MessageSource messageSource;

	private final String LIST = "evento/eventoList";
	private final String EDIT = "evento/eventoEdit";
	private final String SHOW = "evento/eventoShow";
	private final String RENDICONTO = "evento/eventoRendiconto";
	private final String EDITRES = "evento/eventoRESEdit";
	private final String EDITFSC = "evento/eventoFSCEdit";
	private final String EDITFAD = "evento/eventoFADEdit";
	private final String RICERCA = "ricerca/ricercaEvento";
	private final String ERROR = "fragments/errorsAjax";
	private final String SPONSOR = "evento/allegaContrattiSponsor";
	private final String PAGAMENTOQUIETANZA = "evento/pagamentoEventoList";

	@InitBinder
    public void setAllowedFields(WebDataBinder dataBinder) {
        dataBinder.setDisallowedFields("id");
    }

	//@ModelAttribute("eventoWrapper")
	public EventoWrapper getEvento(@RequestParam(name = "editId", required = false) Long id,
			@RequestParam(value="providerId",required = false) Long providerId,
			@RequestParam(value="proceduraFormativa",required = false) ProceduraFormativa proceduraFormativa,
			@RequestParam(value="wrapperMode",required = false) EventoWrapperModeEnum wrapperMode) throws Exception{
		if(id != null){
			if (wrapperMode == EventoWrapperModeEnum.RENDICONTO)
				return prepareEventoWrapperRendiconto(eventoService.getEvento(id), providerId);
			else
				return prepareEventoWrapperEdit(eventoService.getEvento(id), false);
		}
		if(providerId != null && proceduraFormativa != null)
			return prepareEventoWrapperNew(proceduraFormativa, providerId);
		return new EventoWrapper();
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowAllEventi(principal)")
	@RequestMapping("/evento/list")
	public String getListEventi(Model model, RedirectAttributes redirectAttrs, HttpServletRequest request){
		LOGGER.info(Utils.getLogMessage("GET /evento/list"));
		try {

			if(model.asMap().get("eventoList") == null)
				model.addAttribute("eventoList", eventoService.getAllEventi());

			LOGGER.info(Utils.getLogMessage("VIEW: evento/eventoList"));

			if(Utils.getAuthenticatedUser().isSegreteria())
				model.addAttribute("scadenzeEventoWrapper", new ScadenzeEventoWrapper());

			return goToEventoList(request, model);
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /evento/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowAllEventi(principal)")
	@RequestMapping("/evento/list/all")
	public String getListAllEventi(Model model, RedirectAttributes redirectAttrs, HttpServletRequest request){
		LOGGER.info(Utils.getLogMessage("GET /evento/list"));
		try {

			//model.addAttribute("eventoList", eventoService.getAllEventi());

			LOGGER.info(Utils.getLogMessage("VIEW: evento/eventoList"));

			if(Utils.getAuthenticatedUser().isSegreteria())
				model.addAttribute("scadenzeEventoWrapper", new ScadenzeEventoWrapper());
			//model attribute to tell if template should display full list of events using ajax
			model.addAttribute("showAllList", true);
			//Remove eventoList, thymeleafs checks if eventoList is present to display the list
			model.asMap().remove("eventoList");
			//Remove providerId, js in template checks if providerId is present to do a call for provider events
			//This controller is for segretaria to list ALL eventi
			model.asMap().remove("providerId");
			return goToEventoList(request, model);
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /evento/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "";
		}
	}


	//Builds an EventoListDataModel from an event required by DataTable for displaying the information
	//Throws Exception from event.getAuditEntityType()
	private EventoListDataModel buildEventiDataModel(Evento event) throws NoSuchMessageException, Exception {
		EventoListDataModel dataModel = new EventoListDataModel();
		//DateFormatter
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		dataModel.setCodiceIdent("<a class=\"linkButton\" href=\"provider/" + event.getProvider().getId() + "/evento/" + event.getId() + "/show\">" + event.getCodiceIdentificativo() + "</a>");
		if(Utils.getAuthenticatedUser().isSegreteria())
			dataModel.setDenominazioneLeg(event.getProvider().getDenominazioneLegale());
		dataModel.setEdizione(event.getEdizione());
		dataModel.setTipo(event.getProceduraFormativa().toString());

		if (event.getProceduraFormativa() == ProceduraFormativa.FSC) {
			EventoFSC eventoFsc = (EventoFSC) event;
			if(eventoFsc.getSedeEvento() != null)
				dataModel.setSede(eventoFsc.getSedeEvento().getLuogo());
		}
		else if (event.getProceduraFormativa() == ProceduraFormativa.RES) {
			EventoRES eventoRes = (EventoRES) event;
			if(eventoRes.getSedeEvento() != null)
				dataModel.setSede(eventoRes.getSedeEvento().getLuogo());
		}

		dataModel.setTitolo(event.getTitolo());
		if(event.getDataInizio() != null)
			dataModel.setDataInizio(event.getDataInizio().format(formatter));
		if(event.getDataFine() != null)
			dataModel.setDataFine(event.getDataFine().format(formatter));

		String statoBuild = "<div>" + event.getStato().getNome() + "</div>";

		if(event.getPagato() != null && event.getPagato() && !event.isCancellato())
			statoBuild += "<div ><span class=\"label-pagato\">" + messageSource.getMessage("label.pagato", null, LocaleContextHolder.getLocale()) + "</span></div>";
		else if (event.isCancellato())
			statoBuild += "<div><span class=\"label-non-pagato\">" + messageSource.getMessage("label.cancellato", null, LocaleContextHolder.getLocale()) + "</span></div>";
		else if (event.getPagato() == null || (!event.getPagato() && !event.isCancellato())) {
			if(event.getPagInCorso() != null && event.getPagInCorso())
				statoBuild += "<div><span class=\"label-non-pagato\">" + messageSource.getMessage("label.pagInCorso", null, LocaleContextHolder.getLocale()) + "</span></div>";
			else
				statoBuild += "<div><span class=\"label-non-pagato\">" + messageSource.getMessage("label.da_pagare", null, LocaleContextHolder.getLocale()) + "</span></div>";
		}
		dataModel.setStato(statoBuild);
		dataModel.setNumPart(event.getNumeroPartecipanti() != null ? event.getNumeroPartecipanti().toString() : "");
		dataModel.setDurata(Utils.formatOrario(event.getDurata() != null ? event.getDurata() : 0));
		if(event.getCrediti() != null)
			dataModel.setCrediti(event.getCrediti());
		else
			dataModel.setCrediti(0);
		if(event.getDataScadenzaInvioRendicontazione() != null)
			dataModel.setDataScadenzaRediconto(event.getDataScadenzaInvioRendicontazione().format(formatter));

		if(event.getConfermatiCrediti() != null) {
			if(event.getConfermatiCrediti())
				dataModel.setCreditiConfermati("<div><i class=\"fa table-icon fa-check green\" title=\"" + messageSource.getMessage("label.sì", null, LocaleContextHolder.getLocale()) + "\"></i></div>");
			else
				dataModel.setCreditiConfermati("<div><i class=\"fa table-icon fa-remove red\" title=\"" + messageSource.getMessage("label.no", null, LocaleContextHolder.getLocale()) + "\"></i></div>");
		}
		else {
			dataModel.setCreditiConfermati("<div><i class=\"fa table-icon fa-question grey\" title=\"" + messageSource.getMessage("label.non_specificato", null, LocaleContextHolder.getLocale()) + "\"></i></div>");
		}

		if(event.getVersione() != null)
			dataModel.setVersione(event.getVersione().getNumeroVersione());
		else
			dataModel.setVersione(null);

		//Build the Azioni Buttons
		String buttons = "";
		if(Utils.getAuthenticatedUser().isSegreteria() || Utils.getAuthenticatedUser().isProvider()) {
			if(event.canEdit() || Utils.getAuthenticatedUser().isSegreteria()) {
				buttons += "	<a class=\"btn btn-primary min-icon-width linkButton\" href=\"provider/" + event.getProvider().getId() + "/evento/" + event.getId() + "/edit\" title=\""
								+ messageSource.getMessage("label.modifica", null, LocaleContextHolder.getLocale()) + "\"><i class=\"fa fa-pencil\"></i></a>";
			}

			if(Utils.getAuthenticatedUser().isSegreteria() && event.canSegreteriaShiftData()) {
				buttons+= "<button type=\"button\" class=\"btn btn-primary min-icon-width \" onclick=\"openModalScadenze(" + event.getId() +
						", '" + event.getDataScadenzaPagamento() + "', '" + event.getDataScadenzaInvioRendicontazione() + "')\" title=\"" + messageSource.getMessage("label.abilita", null, LocaleContextHolder.getLocale()) + "\"><i class=\"fa fa-unlock-alt\"> </i></button>";
			}

			if(event.canDoPagamento()) {
				if((event.getProvider().getMyPay() != null && event.getProvider().getMyPay()) || (event.getProvider().getMyPay() == null && event.getProvider().isGruppoB())) {
					buttons += "<a class=\"btn btn-success btn-min-icon-width linkButton\" href=\"provider/" + event.getProvider().getId() + "/evento/" +
									event.getId() + "/paga\" title=\"" + messageSource.getMessage("label.paga", null, LocaleContextHolder.getLocale()) + "\"><i class=\"fa fa-euro\"></i></a>";
				}

				if (event.getProvider().getMyPay() != null && !event.getProvider().getMyPay()) {
					buttons += "<a class=\"btn btn-success btn-min-icon-width linkButton\" href=\"provider/" + event.getProvider().getId() + "/evento/" +
															event.getId() + "/quietanzaPage\" title=\"" + messageSource.getMessage("label.allega_quietanza", null, LocaleContextHolder.getLocale()) + "\"><i class=\"fa fa-euro\"></i></a>";
				}
			}

			if (event.getPagato() && event.getPagatoQuietanza()) {
				buttons += "<a class=\"btn btn-success btn-min-icon-width linkButton\" href=\"provider/" + event.getProvider().getId() + "/evento/" +
														event.getId() + "/quietanzaPagamento/show\"  title=\"" + messageSource.getMessage("label.visualizza_quietanza", null, LocaleContextHolder.getLocale()) + "\"><i class=\"fa fa-euro\"> </i></a>";
			}

			if (Utils.getAuthenticatedUser().isSegreteria()) {
				buttons += "<a class=\"btn btn-primary min-icon-width linkButton\" href=\"audit/entity/" + event.getAuditEntityType() + "/entityId/" + event.getId() +
										"\" th:title=\"" + messageSource.getMessage("label.registro_operazioni", null, LocaleContextHolder.getLocale()) + "\"><i class=\"fa fa-book\"></i></a>";
			}

			if (event.canDoRendicontazione()) {
				buttons += "<a class=\"btn btn-warning min-icon-width linkButton\" href=\"provider/" + event.getProvider().getId() + "/evento/" + event.getId() + "/rendiconto\" title=\"" +
																messageSource.getMessage("label.rendiconto", null, LocaleContextHolder.getLocale()) + "\"><i class=\"fa fa-file-text\"></i></a>";
			}

			if (event.canDoUploadSponsor()) {
				buttons += "<a class=\"btn btn-primary min-icon-width linkButton\" href=\"provider/" + event.getProvider().getId() + "/evento/" + event.getId() + "/allegaContrattiSponsor\" title=\"" +
														messageSource.getMessage("label.allega_contratti_sponsor", null, LocaleContextHolder.getLocale()) + "\"><i class=\"fa fa-file\"></i></a>";
			}

			if (event.canEdit()) {
				buttons += "<button class=\"btn btn-danger min-icon-width\" onclick=\"confirmDeleteEventoModal('" + event.getProvider().getId() + "','" +
														event.getId() + "','" + event.getProceduraFormativa() + "','" + event.getCodiceIdentificativo() + "','" + event.getStato() + "')\" title=\"" +
														(event.getStato() == EventoStatoEnum.BOZZA ? messageSource.getMessage("label.elimina", null, LocaleContextHolder.getLocale()) : messageSource.getMessage("label.annulla", null, LocaleContextHolder.getLocale())) + "\"><i class=\"fa fa-trash\"></i></button>";
			}
		}
		dataModel.setLinks(buttons);

		return dataModel;
	}

	@JsonView(EventoListDataTableModel.View.class)
	@PreAuthorize("@securityAccessServiceImpl.canShowAllEventi(principal)")
	@RequestMapping(value = "/evento/eventoListPaginated", method = RequestMethod.GET)
    public @ResponseBody EventoListDataTableModel eventoListPaginated(HttpServletRequest  request, RedirectAttributes redirectAttrs) throws Exception {
		EventoListDataTableModel dataTable = new EventoListDataTableModel();
	try {


		dataTable.setData(new ArrayList<EventoListDataModel>());
			//Fetch the page number from client
			Integer pageNumber = 0;
			//Fetch number of rows from client
			Integer numOfRows = 10;

			if (null != request.getParameter("length"))
				numOfRows = Integer.valueOf(request.getParameter("length"));
			else
				throw new Exception("Cannot get length parameter!");

			if (null != request.getParameter("start"))
				pageNumber = (Integer.valueOf(request.getParameter("start"))/numOfRows);

			Integer columnNumber = 0;
			if (null != request.getParameter("order[0][column]"))
				columnNumber = Integer.valueOf(request.getParameter("order[0][column]"));
			else
				throw new Exception("Cannot get order[0][column] parameter!");

			String order = "";
			if (null != request.getParameter("order[0][dir]") && !request.getParameter("order[0][dir]").isEmpty())
				order = request.getParameter("order[0][dir]");
			 else
				throw new Exception("Cannot get order[0][dir] parameter!");

			Page<Evento> eventi =  eventoService.getAllEventi(pageNumber, columnNumber, order, numOfRows);

			for(Evento event : eventi) {
				dataTable.getData().add(buildEventiDataModel(event));
			}


			dataTable.setRecordsTotal(eventi.getTotalElements());
			dataTable.setRecordsFiltered(eventi.getTotalElements());
		return dataTable;
	} catch (Exception e) {
			LOGGER.error(Utils.getLogMessage("GET /evento/list"),e);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			dataTable.setError("Session expired or an Error occured! Please refresh the page. \n" + e.getMessage());
			return dataTable;
		}
    }

	@JsonView(EventoListDataTableModel.View.class)
	@PreAuthorize("@securityAccessServiceImpl.canShowAllEventiProvider(principal, #providerId)")
	@RequestMapping(value = "/provider/{providerId}/evento/eventoListPaginated", method = RequestMethod.GET)
    public @ResponseBody EventoListDataTableModel eventoListPaginatedById(@PathVariable Long providerId, HttpServletRequest  request, RedirectAttributes redirectAttrs) throws Exception {
		EventoListDataTableModel dataTable = new EventoListDataTableModel();
	try {


		dataTable.setData(new ArrayList<EventoListDataModel>());
			//Fetch the page number from client
			Integer pageNumber = 0;
			//Fetch number of rows from client
			Integer numOfRows = 10;

			if (null != request.getParameter("length"))
				numOfRows = Integer.valueOf(request.getParameter("length"));
			else
				throw new Exception("Cannot get length parameter!");

			if (null != request.getParameter("start"))
				pageNumber = (Integer.valueOf(request.getParameter("start"))/numOfRows);

			Integer columnNumber = 0;
			if (null != request.getParameter("order[0][column]"))
				columnNumber = Integer.valueOf(request.getParameter("order[0][column]"));
			else
				throw new Exception("Cannot get order[0][column] parameter!");

			String order = "";
			if (null != request.getParameter("order[0][dir]") && !request.getParameter("order[0][dir]").isEmpty())
				order = request.getParameter("order[0][dir]");
			 else
				throw new Exception("Cannot get order[0][dir] parameter!");

			Page<Evento> eventi = eventoService.getAllEventiForProviderId(providerId ,pageNumber, columnNumber, order, numOfRows);

			for(Evento event : eventi) {
				dataTable.getData().add(buildEventiDataModel(event));
			}


			dataTable.setRecordsTotal(eventi.getTotalElements());
			dataTable.setRecordsFiltered(eventi.getTotalElements());
		return dataTable;
	} catch (Exception e) {
			LOGGER.error(Utils.getLogMessage("GET /evento/list"),e);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			dataTable.setError("Session expired or an Error occured! Please refresh the page. \n" + e.getMessage());
			return dataTable;
		}
    }

	@RequestMapping("/provider/evento/list")
	public String getListEventiCurrentUserProvider(Model model, RedirectAttributes redirectAttrs, SessionStatus sessionStatus){
		LOGGER.info(Utils.getLogMessage("GET /provider/evento/list"));
		try {
			Provider currentProvider = providerService.getProvider();
			if(currentProvider.isNew()){
				throw new Exception("Provider non registrato");
			}else{
				//svuota sessione eventoList per ricaricare tutto
				//Removed model Attribute eventoList
				redirectAttrs.addFlashAttribute("eventoList", null);
				Long providerId = currentProvider.getId();
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/evento/list"));
				return "redirect:/provider/"+providerId+"/evento/list";
			}
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/evento/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /redirect:/home"));
			return "redirect:/home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowAllEventiProvider(principal, #providerId)")
	@RequestMapping("/provider/{providerId}/evento/list")
	public String getListEventiProvider(@PathVariable Long providerId, Model model,
			HttpServletRequest request, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/list"));
		try {
			//Remove old database call for loading eventi, loading is done using lazy loading
			if(model.asMap().get("eventoList") == null || !Objects.equals(providerId, model.asMap().get("providerId"))) {
				//model attribute to tell if template should display full list of events using ajax
				model.asMap().remove("eventoList");
				model.addAttribute("showAllList", true);
			}else {
				model.addAttribute("showAllList", false);
			}
			return goToList(model, providerId, request);
		}
		catch (AccreditamentoNotFoundException accreditamentoNotFoundEx) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/list"),accreditamentoNotFoundEx);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.non_risulta_attivo_nessun_accreditamento", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /redirect:/home"));
			return "redirect:/home";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /redirect:/home"));
			return "redirect:/home";
		}
	}

	//Handle search results differently. searchList should be stored, eventoList should be flush everytime it is displayed
	@PreAuthorize("@securityAccessServiceImpl.canShowAllEventi(principal)")
	@RequestMapping("/evento/ricercaList")
	public String getRicercaListEventi(Model model, RedirectAttributes redirectAttrs, HttpServletRequest request){
		LOGGER.info(Utils.getLogMessage("GET /evento/list"));
		try {

			if(model.asMap().get("searchList") != null) {
				//Populate eventoList with search results
				model.addAttribute("eventoList", model.asMap().get("searchList"));
				//model attribute to tell if template should display full list of events using ajax
				model.addAttribute("showAllList", false);
			}

			LOGGER.info(Utils.getLogMessage("VIEW: evento/ricercaList"));

			if(Utils.getAuthenticatedUser().isSegreteria())
				model.addAttribute("scadenzeEventoWrapper", new ScadenzeEventoWrapper());

			return goToEventoList(request, model);
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /evento/ricercaList"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "";
		}
	}

	//Handle search results for provider
	@PreAuthorize("@securityAccessServiceImpl.canShowAllEventiProvider(principal, #providerId)")
	@RequestMapping("/provider/{providerId}/evento/ricercaList")
	public String getRicercaListEventiProvider(@PathVariable Long providerId, Model model,
			HttpServletRequest request, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/ricercaList"));
		try {
			//Remove old database call for loading eventi, loading is done using lazy loading
			if(model.asMap().get("searchList") != null) {
				//Populate eventoList with search results
				model.addAttribute("eventoList", model.asMap().get("searchList"));
				//model attribute to tell if template should display full list of events using ajax
				model.addAttribute("showAllList", false);
			}
			return goToList(model, providerId, request);
		}
		catch (AccreditamentoNotFoundException accreditamentoNotFoundEx) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/ricercaList"),accreditamentoNotFoundEx);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.non_risulta_attivo_nessun_accreditamento", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /redirect:/home"));
			return "redirect:/home";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/ricercaList"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /redirect:/home"));
			return "redirect:/home";
		}
	}

	private String goToList(Model model, Long providerId, HttpServletRequest request) throws AccreditamentoNotFoundException {
		String denominazioneProvider = providerService.getProvider(providerId).getDenominazioneLegale();
		model.addAttribute("eventoAttuazioneList", eventoPianoFormativoService.getAllEventiAttuabiliForProviderId(providerId));
		model.addAttribute("eventoRiedizioneList", eventoService.getAllEventiRieditabiliForProviderId(providerId));
		model.addAttribute("denominazioneProvider", denominazioneProvider);
		model.addAttribute("providerId", providerId);
		try {
			Accreditamento accreditamento =  accreditamentoService.getAccreditamentoAttivoForProvider(providerId);
			model.addAttribute("proceduraFormativaList", accreditamento.getDatiAccreditamento().getProcedureFormative());
			model.addAttribute("canCreateEvento", eventoService.canCreateEvento(Utils.getAuthenticatedUser().getAccount()));
			model.addAttribute("canRieditEvento", eventoService.canRieditEvento(Utils.getAuthenticatedUser().getAccount()));
		}
		catch (Exception ex) {
			model.addAttribute("proceduraFormativaList", null);
			model.addAttribute("canCreateEvento", false);
			model.addAttribute("canRieditEvento", false);
		}
		if(Utils.getAuthenticatedUser().isSegreteria())
			model.addAttribute("scadenzeEventoWrapper", new ScadenzeEventoWrapper());
		return goToEventoList(request, model);
	}

	@PreAuthorize("@securityAccessServiceImpl.canCreateEvento(principal, #providerId)")
	@RequestMapping(value= "/provider/{providerId}/evento/new", method = RequestMethod.POST)
	public String createNewEvento(@RequestParam(name = "proceduraFormativa", required = false) ProceduraFormativa proceduraFormativa,
			@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/evento/new, proceduraFormativa = " + proceduraFormativa));
		try {
			if(proceduraFormativa == null) {
				redirectAttrs.addFlashAttribute("error", true);
				redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.inserire_campi_required", "error"));
				if(model.asMap().containsKey("returnLink")) {
					String returnLink = (String) model.asMap().get("returnLink");
					return "redirect:" + returnLink;
				}
				else
					return "redirect:/provider/"+providerId+"/evento/list";
			}
			else {
				EventoWrapper wrapper = prepareEventoWrapperNew(proceduraFormativa, providerId);
				return goToNew(model, wrapper);
			}
		}
		catch (AccreditamentoNotFoundException ex){
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.accreditamento_not_trovato", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
			return "redirect:/provider/"+providerId+"/evento/list";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/new"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
			return "redirect:/provider/"+providerId+"/evento/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canCreateEvento(principal, #providerId)")
	@RequestMapping("/provider/{providerId}/eventoPianoFormativo/{eventoPianoFormativoId}/fulfill")
	public String attuaEvento(@PathVariable Long providerId, @PathVariable Long eventoPianoFormativoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/eventoPianoFormativo/" + eventoPianoFormativoId +"/fulfill"));
		try {
			EventoPianoFormativo eventoPianoFormativo = eventoPianoFormativoService.getEvento(eventoPianoFormativoId);
			EventoWrapper wrapper = prepareEventoWrapperAttuazione(eventoPianoFormativo, providerId);
			return goToEdit(model, wrapper);
		}
		catch (AccreditamentoNotFoundException ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/eventoPianoFormativo/" + eventoPianoFormativoId +"/fulfill"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.accreditamento_not_trovato", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
			return "redirect:/provider/"+providerId+"/evento/list";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/eventoPianoFormativo/" + eventoPianoFormativoId +"/fulfill"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
			return "redirect:/provider/"+providerId+"/evento/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canCreateEvento(principal, #providerId)")
	@RequestMapping("/provider/{providerId}/evento/{eventoId}/re-edit")
	public String rieditaEvento(@PathVariable Long providerId, @PathVariable Long eventoId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId +"/re-edit"));
		try {
			Evento evento = eventoService.getEvento(eventoId);
			EventoWrapper wrapper = prepareEventoWrapperRiedizione(evento, providerId);
			return goToEdit(model, wrapper);
		}
		catch (AccreditamentoNotFoundException ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId +"/re-edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.accreditamento_not_trovato", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
			return "redirect:/provider/"+providerId+"/evento/list";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId +"/re-edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
			return "redirect:/provider/"+providerId+"/evento/list";
		}
	}


	@PreAuthorize("@securityAccessServiceImpl.canCreateEvento(principal, #providerId)")
	@RequestMapping(value= "/provider/{providerId}/evento/save", method = RequestMethod.POST)
	public String saveEvento(@ModelAttribute EventoWrapper eventoWrapper,
			@PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs,
			HttpSession session){
		LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/evento/save"));
		try {
			//salvataggio temporaneo senza validatore (in stato di bozza)
			//gestione dei campi ripetibili
			Evento evento = eventoService.handleRipetibiliAndAllegati(eventoWrapper);
			eventoService.save(evento);
			updateEventoList(evento.getId(), session, false, true);
			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.evento_salvato_in_bozza_success", "success"));
			if(model.asMap().containsKey("returnLink")) {
				String returnLink = (String) model.asMap().get("returnLink");
				return "redirect:" + returnLink;
			}
			else
				return "redirect:/provider/"+providerId+"/evento/list";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/save"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/{providerId}/evento/list"));
			return "redirect:/provider/"+providerId+"/evento/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canCreateEvento(principal, #providerId)")
	@RequestMapping(value= "/provider/{providerId}/evento/validate", method = RequestMethod.POST)
	public String validaEvento(@ModelAttribute EventoWrapper eventoWrapper,
			HttpSession session, BindingResult result, @PathVariable Long providerId, Model model,
			RedirectAttributes redirectAttrs, @RequestParam("eventoWrapper_cId") String cIdWrapper){
		LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/evento/validate"));
		try {
			//gestione dei campi ripetibili
			Evento evento = eventoService.handleRipetibiliAndAllegati(eventoWrapper);

			//riaggiungo il cId nel caso abbia un passaggio view -> controller -> view senza reinit del bean
			//ottenuto come requestParam da un input hidden
			model.addAttribute("eventoWrapper_cId", cIdWrapper);

			//validator
			eventoValidator.validate(evento, eventoWrapper, result, "evento.");

			if(result.hasErrors()){
				model.addAttribute("message", new Message("message.errore", "message.inserire_campi_required", "error"));
				//gestione adHoc degli errori per evitare di perdere i dati dopo i refresh delle tab eventi
				eventoWrapper.setMappaErroriValidazione(prepareMappaErroriValutazione(result));
				LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
				return EDIT;
			}
			else {
				if(Utils.getAuthenticatedUser().isSegreteria() &&
						evento.isValidatorCheck() &&
						(eventoWrapper.getCreditiOld() !=  evento.getCrediti() || evento.getConfermatiCrediti() == false)) {
					//la segreteria potrebbe aver modificato i crediti dell'evento (già accreditato) e va notificato
					model.addAttribute("creditiModificati", true);
					model.addAttribute("oldValueCrediti", eventoWrapper.getCreditiOld());
					model.addAttribute("newValueCrediti", evento.getCrediti());
					model.addAttribute("creditiProposti", eventoWrapper.getCreditiProposti());
					LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
					return EDIT;
				}
//				else if (eventoService.checkIfRESAndWorkshopOrCorsoAggiornamentoAndInterettivoSelected(evento)) {
//					//va notificato che si stanno inserendo metodologie didattiche interattive (workshop)
//					//e che il rapporto tutor docenti dovrebbe essere 1 a 25 massimo
//					model.addAttribute("RESWorkshopInterattivo", true);
//					LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
//					return EDIT;
//				}
				else if (eventoService.checkIfFSCAndTrainingAndTutorPartecipanteRatioAlert(evento)) {
					//va notificato che si stanno inserendo un numero di partecipanti superiore a quello seguibile dai tutor
					//il rapporto tutor partecipanti dovrebbe essere 1 a 5 massimo
					model.addAttribute("FSCTutorPartecipanti", true);
					LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
					return EDIT;
				}
				else {
					evento.setStato(EventoStatoEnum.VALIDATO);
					if(evento.getVersione() == null) {
						//imposto la versione in modo che non venga piu' modificata
						evento.setVersione(eventoService.versioneEvento(evento));
					}
					evento.setValidatorCheck(true);
					eventoService.save(evento);
					updateEventoList(evento.getId(), session, false, true);
					alertEmailService.creaAlertForEvento(evento);
					LOGGER.info(Utils.getLogMessage("Evento validato e salvato!"));
				}
			}

			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.evento_validato_e_salvato_success", "success"));
			if(model.asMap().containsKey("returnLink")) {
				String returnLink = (String) model.asMap().get("returnLink");
				return "redirect:" + returnLink;
			}
			else
				return "redirect:/provider/"+providerId+"/evento/list";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/validate"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/{providerId}/evento/list"));
			return "redirect:/provider/"+providerId+"/evento/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canCreateEvento(principal, #providerId)")
	@RequestMapping(value= "/provider/{providerId}/evento/validate/confirm", method = RequestMethod.POST)
	public String validaEventoConfirm(@ModelAttribute EventoWrapper eventoWrapper, BindingResult result,
			HttpSession session, @PathVariable Long providerId, Model model, RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/evento/validate/confirm"));
		try {
			//gestione dei campi ripetibili
			Evento evento = eventoService.handleRipetibiliAndAllegati(eventoWrapper);

			evento.setStato(EventoStatoEnum.VALIDATO);
			evento.setValidatorCheck(true);
			eventoService.save(evento);
			updateEventoList(evento.getId(), session, false, true);
			alertEmailService.creaAlertForEvento(evento);
			LOGGER.info(Utils.getLogMessage("Evento validato e salvato!"));

			redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.evento_validato_e_salvato_success", "success"));
			if(model.asMap().containsKey("returnLink")) {
				String returnLink = (String) model.asMap().get("returnLink");
				return "redirect:" + returnLink;
			}
			else
				return "redirect:/provider/"+providerId+"/evento/list";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/validate"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/{providerId}/evento/list"));
			return "redirect:/provider/"+providerId+"/evento/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canEditEvento(principal, #providerId)")
	@RequestMapping("/provider/{providerId}/evento/{eventoId}/edit")
	public String editEvento(@PathVariable Long providerId, @PathVariable Long eventoId,
			Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/"+ eventoId + "/edit"));
		try {
			//edit dell'evento
			Evento evento = eventoService.getEvento(eventoId);
			if(evento instanceof EventoFSC)
				for(FaseAzioniRuoliEventoFSCTypeA fase : ((EventoFSC) evento).getFasiAzioniRuoli())
					for(AzioneRuoliEventoFSC azione : fase.getAzioniRuoli()) {
						azione.getMetodiDiLavoro().size(); //tiommi 2017-06-15 fix al workaround (prevent lazy initialization ex)
						azione.getRuoli().size();
					}
			if(evento instanceof EventoFAD)
				((EventoFAD) evento).getProgrammaFAD().size(); //workarounda pure Barduz
			EventoWrapper wrapper = prepareEventoWrapperEdit(evento, true);
			return goToEdit(model, wrapper);
		}
		catch (AccreditamentoNotFoundException ex){
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/"+ eventoId + "/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.accreditamento_not_trovato", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
			return "redirect:/provider/"+providerId+"/evento/list";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/"+ eventoId + "/edit"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
			return "redirect:/provider/"+providerId+"/evento/list";
		}
	}

//TODO	@PreAuthorize("@securityAccessServiceImpl.canShowEvento(principal, #providerId")
	@RequestMapping("/provider/{providerId}/evento/{eventoId}/show")
	public String showEvento(@PathVariable Long providerId, @PathVariable Long eventoId,
			Model model, RedirectAttributes redirectAttrs) {
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/"+ eventoId + "/show"));
		try {
			//show dell'evento
			EventoWrapper wrapper = prepareEventoWrapperShow(eventoService.getEvento(eventoId));
			return goToShow(model, wrapper);
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/"+ eventoId + "/show"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
			return "redirect:/provider/"+providerId+"/evento/list";
		}
	}

//TODO	@PreAuthorize("@securityAccessServiceImpl.canShowEvento(principal, #providerId")
	@RequestMapping(value = "/provider/{providerId}/evento/{eventoId}/pdf", method = RequestMethod.GET)
	public void pdfEvento(@PathVariable Long providerId, @PathVariable Long eventoId
			, HttpServletResponse response, Model model) throws IOException {
		LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/"+ eventoId + "/pdf"));
		try {
			if(eventoId == null){
				model.addAttribute("message",new Message("A","B","C"));
			}
			else{
				//pdf dell'evento
				EventoWrapper wrapper = prepareEventoWrapperShow(eventoService.getEvento(eventoId));

				//response.setContentType(mimeType);

				/* "Content-Disposition : inline" will show viewable types [like images/text/pdf/anything viewable by browser] right on browser
		            while others(zip e.g) will be directly downloaded [may provide save as popup, based on your browser setting.]*/
				response.setHeader("Content-Disposition", String.format("attachment; filename=\"Evento " + wrapper.getEvento().getProceduraFormativa() + " " + wrapper.getEvento().getCodiceIdentificativo() +".pdf\""));
				/* "Content-Disposition : attachment" will be directly download, may provide save as popup, based on your browser setting*/
				//response.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\"", file.getName()));

				/*
				//1) Con file
				File file = pdfEventoService.creaPdfEvento(wrapper);
				if(file == null){
					throw new FileNotFoundException();
				}
				response.setContentLength((int)file.getData().length);
				InputStream inputStream = new BufferedInputStream(new ByteArrayInputStream(file.getData()));
				//Copy bytes from source to destination(outputstream in this example), closes both streams.
				FileCopyUtils.copy(inputStream, response.getOutputStream());
				*/

				//2) scrivendo direttamente nel response.getOutputStream()
				//pdfEventoService.creaPdfEvento(wrapper, response.getOutputStream());

				//3) con ByteArrayOutputStream
				ByteArrayOutputStream pdfOutputStream = pdfEventoService.creaOutputStreamPdfEvento(wrapper);
				response.setContentLength(pdfOutputStream.size());
				response.getOutputStream().write(pdfOutputStream.toByteArray());

			}

		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/"+ eventoId + "/pdf"),ex);
			//redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			//LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
			//return "redirect:/provider/"+providerId+"/evento/list";

			model.addAttribute("message",new Message("Errore","Impossibile creare il pdf","Errore creazione pdf evento " + eventoId));
		}

	}


		@PreAuthorize("@securityAccessServiceImpl.canDeleteEvento(principal, #providerId)")
		@RequestMapping("/provider/{providerId}/evento/{eventoId}/delete")
		public String deleteEvento(@PathVariable Long providerId, @PathVariable Long eventoId,
				HttpSession session, Model model, RedirectAttributes redirectAttrs) {
			LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/"+ eventoId + "/delete"));
			try {
				//delete dell'evento
				Evento evento = eventoService.getEvento(eventoId);
				if(evento.getStato() == EventoStatoEnum.BOZZA){
					eventoService.delete(eventoId);
					updateEventoList(evento.getId(), session, true, false);
				}else{
					evento.setStato(EventoStatoEnum.CANCELLATO);
					eventoService.save(evento);
					updateEventoList(evento.getId(), session);
				}
				if(model.asMap().containsKey("returnLink")) {
					String returnLink = (String) model.asMap().get("returnLink");
					return "redirect:" + returnLink;
				}
				else
					return "redirect:/provider/"+providerId+"/evento/list";
			}
			catch (Exception ex) {
				LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/"+ eventoId + "/delete"),ex);
				redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/"+providerId+"/evento/list"));
				return "redirect:/provider/"+providerId+"/evento/list";
			}
		}

	//TODO	@PreAuthorize("@securityAccessServiceImpl.canSendRendiconto(principal)")
	@RequestMapping("/provider/{providerId}/evento/{eventoId}/rendiconto")
	public String rendicontoEvento(@PathVariable Long providerId,
			@PathVariable Long eventoId, Model model, RedirectAttributes redirectAttrs) {
		try{
			LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/rendiconto"));
			return goToRendiconto(model, prepareEventoWrapperRendiconto(eventoService.getEvento(eventoId), providerId));
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/rendiconto"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/evento/list"));
			return "redirect:/provider/" + providerId + "/evento/list";
		}
	}


		@RequestMapping("/evento/{eventoId}")
		public String visualizzaEvento(@PathVariable Long eventoId, Model model, RedirectAttributes redirectAttrs) {
			try{
				LOGGER.info(Utils.getLogMessage("GET /evento/"+eventoId));
				String returnRedirect = "";

				Account user = Utils.getAuthenticatedUser().getAccount();
				if(user.isSegreteria()){
					returnRedirect = "redirect:/evento/list";
				}else{
					Long providerId = user.getProvider().getId();
					returnRedirect = "redirect:/provider/" + providerId + "/evento/list";
				}

				List<Evento> listaEventi = new ArrayList<Evento>();
				listaEventi.add(eventoService.getEvento(eventoId));

				redirectAttrs.addFlashAttribute("eventoList", listaEventi);

				return returnRedirect;
			}
			catch (Exception ex) {
				LOGGER.error(Utils.getLogMessage("GET /evento/"+eventoId),ex);
				redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /comunicazione/dashboard"));
				return "redirect:/comunicazione/dashboard";
			}
		}

//TODO	@PreAuthorize("@securityAccessServiceImpl.canSendRendiconto(principal)")
		@RequestMapping(value = "/provider/{providerId}/evento/{eventoId}/rendiconto/validate", method = RequestMethod.POST)
		public String rendicontoEventoValidate(@PathVariable Long providerId,
				@PathVariable Long eventoId, @ModelAttribute("eventoWrapperRendiconto") EventoWrapper wrapper, BindingResult result,
				Model model, RedirectAttributes redirectAttrs) {
			try{
				LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/validate"));
				if(wrapper.getReportPartecipanti().getId() == null)
					redirectAttrs.addFlashAttribute("message", new Message("message.warning", "message.inserire_il_rendiconto", "alert"));
				else {
					LOGGER.info(Utils.getLogMessage("Ricevuto File id: " + wrapper.getReportPartecipanti().getId() + " da validare"));
					File file = wrapper.getReportPartecipanti();
					if(file != null && !file.isNew()){
						if(file.isREPORTPARTECIPANTI()) {
							String fileName = wrapper.getReportPartecipanti().getNomeFile().trim().toUpperCase();
							if (fileName.endsWith(".XML") || fileName.endsWith(".XML.P7M") || fileName.endsWith(".XML.ZIP.P7M") || fileName.endsWith(".CSV")) {
								wrapper.setReportPartecipanti(fileService.getFile(file.getId()));
								eventoService.validaRendiconto(eventoId, wrapper.getReportPartecipanti());
								redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.xml_evento_validation_ok", "success"));
							}
							else {
								redirectAttrs.addFlashAttribute("message", new Message("message.warning", "error.formatNonAcceptedXML", "alert"));
							}
						}
					}
			}
			return "redirect:/provider/{providerId}/evento/{eventoId}/rendiconto";
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/validate"),ex);
				if (ex instanceof EcmException) //errore gestito
					//TODO - l'idea era quella di utilizzare error._free_msg={0} ma non funziona!!!!
					redirectAttrs.addFlashAttribute("message", new Message(((EcmException) ex).getMessageTitle(), ((EcmException) ex).getMessageDetail(), "alert"));
				else
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/validate"));
				return "redirect:/provider/{providerId}/evento/{eventoId}/rendiconto";
		}
	}

	//TODO	@PreAuthorize("@securityAccessServiceImpl.canSendRendiconto(principal)")
		@RequestMapping(value = "/provider/{providerId}/evento/{eventoId}/rendiconto/inviaACogeaps", method = RequestMethod.GET)
		public String rendicontoEventoIviaACogeaps(@PathVariable Long providerId,
				@PathVariable Long eventoId, @ModelAttribute("eventoWrapperRendiconto") EventoWrapper wrapper, BindingResult result,
				Model model, RedirectAttributes redirectAttrs) {
			try{
				LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/inviaACogeaps"));
				eventoService.inviaRendicontoACogeaps(eventoId);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.invio_cogeaps_ok", "success"));
				return "redirect:/provider/{providerId}/evento/{eventoId}/rendiconto";
			}
			catch (Exception ex) {
				LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/inviaACogeaps"),ex);
				if (ex instanceof EcmException) //errore gestito
	//TODO - l'idea era quella di utilizzare error._free_msg={0} ma non funziona!!!!
					redirectAttrs.addFlashAttribute("message", new Message(((EcmException) ex).getMessageTitle(), ((EcmException) ex).getMessageDetail(), "alert"));
				else
					redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/inviaACogeaps"));
				return "redirect:/provider/{providerId}/evento/{eventoId}/rendiconto";
			}
		}

		//TODO	@PreAuthorize("@securityAccessServiceImpl.canSendRendiconto(principal)")
		@RequestMapping(value = "/provider/{providerId}/evento/{eventoId}/rendiconto/statoElaborazioneCogeaps", method = RequestMethod.GET)
		public String rendicontoEventoStatoElaborazioneCogeaps(@PathVariable Long providerId,
				@PathVariable Long eventoId, @ModelAttribute("eventoWrapperRendiconto") EventoWrapper wrapper, BindingResult result,
				Model model, RedirectAttributes redirectAttrs) {
			try{
				LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/statoElaborazioneCogeaps"));
				eventoService.statoElaborazioneCogeaps(eventoId);
				return "redirect:/provider/{providerId}/evento/{eventoId}/rendiconto";
			}
			catch (Exception ex) {
				LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/statoElaborazioneCogeaps"),ex);
				if (ex instanceof EcmException) //errore gestito
					//TODO - l'idea era quella di utilizzare error._free_msg={0} ma non funziona!!!!
					redirectAttrs.addFlashAttribute("message", new Message(((EcmException) ex).getMessageTitle(), ((EcmException) ex).getMessageDetail(), "alert"));
				else
					redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/evento/" + eventoId + "/rendiconto/statoElaborazioneCogeaps"));
				return "redirect:/provider/{providerId}/evento/{eventoId}/rendiconto";
			}
		}

		//goto inserimento allegati contratti sponsor
		@PreAuthorize("@securityAccessServiceImpl.canAllegaSponsorEvento(principal, #eventoId)")
		@RequestMapping("/provider/{providerId}/evento/{eventoId}/allegaContrattiSponsor")
		public String allegaContrattiSponsor(@PathVariable Long providerId, @PathVariable Long eventoId, Model model, RedirectAttributes redirectAttrs) {
			try{
				LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/allegaContrattiSponsor"));
				return goToAllegaSponsor(model, prepareSponsorWrapper(providerId, eventoService.getEvento(eventoId)));
			}
			catch (Exception ex) {
				LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/allegaContrattiSponsor"),ex);
				redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
				LOGGER.info(Utils.getLogMessage("REDIRECT: /provider/" + providerId + "/evento/list"));
				return "redirect:/provider/" + providerId + "/evento/list";
			}
		}

		//salva allegato contratto sponsor e ritorna info da aggiornare sulla tabella o errori da far visualizzare
		@RequestMapping(value = "/provider/{providerId}/evento/{eventoId}/sponsor/{sponsorId}/saveContratto", method = RequestMethod.POST)
		public String salvaContrattoSponsor(@PathVariable Long providerId, @PathVariable Long eventoId, @PathVariable Long sponsorId,
				@RequestParam(name = "idModalSponsor", required=false) Long idModalSponsor,
				@RequestParam(name = "modeModalSponsor") String modeModalSponsor,
				@ModelAttribute ("sponsorWrapper") SponsorWrapper wrapper, BindingResult result, Model model, RedirectAttributes redirectAttrs) {
			try{
				LOGGER.info(Utils.getLogMessage("POST /provider/" + providerId + "/evento/" + eventoId + "/sponsor/" + sponsorId + "/contratto/save"));

				File f = wrapper.getSponsorFile();
				if(f!= null && !f.isNew())
					wrapper.setSponsorFile(fileService.getFile(f.getId()));

				Map<String, String> errMap = eventoValidator.validateContrattoSponsor(wrapper.getSponsorFile(), providerId, "sponsorFile");
				if(!errMap.isEmpty()) {
					ErrorsAjaxWrapper errWrapper = new ErrorsAjaxWrapper();
					errWrapper.setMappaErrori(errMap);
					model.addAttribute("errorsAjaxWrapper", errWrapper);
					return ERROR + " :: fragmentError";
				}
				else {
					Sponsor sponsor = eventoService.getSponsorById(sponsorId);
					eventoService.saveAndCheckContrattoSponsorEvento(wrapper.getSponsorFile(), sponsor, eventoId, modeModalSponsor);
					model.addAttribute("sponsor", sponsor);
					return SPONSOR + ":: allegatoContrattoSponsorTable";
				}
			}
			catch (Exception ex) {
				LOGGER.error(Utils.getLogMessage("POST /provider/" + providerId + "/evento/" + eventoId + "/sponsor/" + sponsorId + "/contratto/save"),ex);
				redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
				return "redirect:/provider/"+ providerId + "/evento/" + eventoId + "/allegaContrattiSponsor";
			}
		}

		@RequestMapping("/provider/{providerId}/evento/{eventoId}/sponsor/{sponsorId}/loadModaleSponsor")
		public String caricaModaleSponsor(@PathVariable Long providerId, @PathVariable Long eventoId, @PathVariable Long sponsorId,
				@ModelAttribute ("sponsorWrapper") SponsorWrapper wrapper, Model model, RedirectAttributes redirectAttrs) {
			try{
				LOGGER.info(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/sponsor/" + sponsorId + "/loadModaleSponsor"));
				Sponsor sponsor = eventoService.getSponsorById(sponsorId);
				wrapper.setSponsorFile(sponsor.getSponsorFile());
				model.addAttribute("sponsorWrapper", wrapper);
				return SPONSOR + ":: allegatoContrattoSponsorModal";
			}
			catch (Exception ex) {
				LOGGER.error(Utils.getLogMessage("GET /provider/" + providerId + "/evento/" + eventoId + "/sponsor/" + sponsorId + "/loadModaleSponsor"),ex);
				redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
				return "redirect:/provider/"+ providerId + "/evento/" + eventoId + "/allegaContrattiSponsor";
			}
		}


//	//metodo per chiamate AJAX sulle date ripetibili
//	@RequestMapping("/add/dataIntermedia")
//	public String addDataIntermedia(@RequestParam (name="dataIntermedia", required = false) LocalDate dataIntermedia, Model model) {
//		try{
//			LOGGER.info(Utils.getLogMessage("AJAX /add/dataIntermedia"));
//			EventoWrapper wrapper = (EventoWrapper) model.asMap().get("eventoWrapper");
//			EventoRES evento = (EventoRES) wrapper.getEvento();
//			Set<LocalDate> dateIntermedie = evento.getDateIntermedie();
//			if(dataIntermedia != null) {
//				dateIntermedie.add(dataIntermedia);
//				wrapper.setEvento(evento);
//				model.addAttribute("eventoWrapper", wrapper);
//			}
//			else model.addAttribute("message", new Message("message.errore", "message.non_possibile_salvare_data", "error"));
//			return EDIT;
//		}
//		catch (Exception ex) {
//			LOGGER.error(Utils.getLogMessage("POST /add/dataIntermedia"),ex);
//			model.addAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
//			return EDIT;
//		}
//	}

	//metodi privati di supporto

	private EventoWrapper prepareEventoWrapperNew(ProceduraFormativa proceduraFormativa, Long providerId) throws AccreditamentoNotFoundException, Exception {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperNew(" + proceduraFormativa + ") - entering"));
		EventoWrapper eventoWrapper = prepareCommonEditWrapper(proceduraFormativa, providerId);
		Evento evento;
		switch(proceduraFormativa){
			case FAD: evento = new EventoFAD(); break;
			case RES: evento = new EventoRES(); break;
			case FSC: evento = new EventoFSC(); break;
			default: evento = new Evento(); break;
		}
		evento.setProvider(providerService.getProvider(providerId));
		evento.setAccreditamento(accreditamentoService.getAccreditamentoAttivoForProvider(providerId));
		evento.setProceduraFormativa(proceduraFormativa);
		evento.setStato(EventoStatoEnum.BOZZA);
		eventoWrapper.setEvento(evento);
		eventoWrapper.initProgrammi();
//		eventoWrapper = eventoService.prepareRipetibiliAndAllegati(eventoWrapper);
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperNew(" + proceduraFormativa + ") - exiting"));
		return eventoWrapper;
	}

	private EventoWrapper prepareEventoWrapperEdit(Evento evento, boolean reloadWrapperFromDB) throws Exception {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperEdit(" + evento.getId() + ") - entering"));
		EventoWrapper eventoWrapper = prepareCommonEditWrapper(evento.getProceduraFormativa(), evento.getProvider().getId());
		eventoWrapper.setEvento(evento);
		eventoWrapper.initProgrammi();
		if(reloadWrapperFromDB)
			eventoWrapper = eventoService.prepareRipetibiliAndAllegati(eventoWrapper);
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperEdit(" + evento.getId() + ") - exiting"));
		//editabilità
		eventoWrapper.setEditSemiBloccato(eventoService.isEditSemiBloccato(evento));
		eventoWrapper.setEventoIniziato(eventoService.isEventoIniziato(evento));
		eventoWrapper.setHasRiedizioni(eventoService.existRiedizioniOfEventoId(evento.getId()));
//		eventoWrapper.setHasDataInizioRestrictions(eventoService.hasDataInizioRestrictions(evento));
		//flag per capire se la segreteria fa modifiche che toccano il numero dei crediti
		if(evento.getCrediti() != null)
			eventoWrapper.setCreditiOld(evento.getCrediti());
		return eventoWrapper;
	}

	private EventoWrapper prepareEventoWrapperShow(Evento evento) throws Exception {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperShow(" + evento.getId() + ") - entering"));
		EventoWrapper eventoWrapper = new EventoWrapper();
		eventoWrapper.setProceduraFormativa(evento.getProceduraFormativa());
		eventoWrapper.setProviderId(evento.getProvider().getId());
		eventoWrapper.setEvento(evento);
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperShow(" + evento.getId() + ") - exiting"));
		return eventoWrapper;
	}

	private EventoWrapper prepareCommonEditWrapper(ProceduraFormativa proceduraFormativa, Long providerId) throws AccreditamentoNotFoundException, Exception {
		EventoWrapper eventoWrapper = new EventoWrapper();
		eventoWrapper.setProceduraFormativa(proceduraFormativa);
		eventoWrapper.setProviderId(providerId);
		eventoWrapper.setObiettiviNazionali(obiettivoService.getObiettiviNazionali());
		eventoWrapper.setObiettiviRegionali(obiettivoService.getObiettiviRegionali());
		DatiAccreditamento datiAccreditamento = accreditamentoService.getDatiAccreditamentoForAccreditamentoId(accreditamentoService.getAccreditamentoAttivoForProvider(providerId).getId());
		List<Professione> professioneList = new ArrayList<Professione>();
		professioneList.addAll(datiAccreditamento.getProfessioniSelezionate());
		professioneList.sort(new Comparator<Professione>() {
			 public int compare(Professione p1, Professione p2) {
				 return (p1.getNome().compareTo(p2.getNome()));
			 }
		});
		eventoWrapper.setProfessioneList(professioneList);
		List<Disciplina> disciplinaList = new ArrayList<Disciplina>();
		disciplinaList.addAll(datiAccreditamento.getDiscipline());
		disciplinaList.sort(new Comparator<Disciplina>() {
			 public int compare(Disciplina d1, Disciplina d2) {
				 return (d1.getNome().compareTo(d2.getNome()));
			 }
		});
		eventoWrapper.setDisciplinaList(disciplinaList);
		eventoWrapper.setWrapperMode(EventoWrapperModeEnum.EDIT);
		eventoWrapper.setMappaErroriValidazione(new HashMap<String, String>());
		return eventoWrapper;
	}

	private EventoWrapper prepareEventoWrapperRendiconto(Evento evento, long providerId) {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperRendiconto(" + evento.getId() + "," + providerId + ") - entering"));
		EventoWrapper eventoWrapper = new EventoWrapper();
		eventoWrapper.setEvento(evento);
		eventoWrapper.setProviderId(providerId);
		eventoWrapper.setProceduraFormativa(evento.getProceduraFormativa());
		eventoWrapper.setReportPartecipanti(new File(FileEnum.FILE_REPORT_PARTECIPANTI));
		eventoWrapper.setWrapperMode(EventoWrapperModeEnum.RENDICONTO);
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperRendiconto(" + evento.getId() + "," + providerId + ") - exiting"));
		return eventoWrapper;
	}

	private EventoWrapper prepareEventoWrapperAttuazione(EventoPianoFormativo eventoPianoFormativo, long providerId) throws AccreditamentoNotFoundException, Exception {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperAttuazione(" + eventoPianoFormativo.getId() + ") - entering"));
		EventoWrapper eventoWrapper = prepareCommonEditWrapper(eventoPianoFormativo.getProceduraFormativa(), providerId);
		Evento evento;
		switch(eventoPianoFormativo.getProceduraFormativa()){
			case FAD: evento = new EventoFAD(); break;
			case RES: evento = new EventoRES(); break;
			case FSC: evento = new EventoFSC(); break;
			default: evento = new Evento(); break;
		}
		evento.setFromEventoPianoFormativo(eventoPianoFormativo);
		evento.setStato(EventoStatoEnum.BOZZA);
		eventoWrapper.setEvento(evento);
		eventoWrapper.initProgrammi();
//		eventoWrapper = eventoService.prepareRipetibiliAndAllegati(eventoWrapper);

		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperAttuazione(" + eventoPianoFormativo.getId() + ") - exiting"));
		return eventoWrapper;
	}

	private EventoWrapper prepareEventoWrapperRiedizione(Evento evento, long providerId) throws AccreditamentoNotFoundException, Exception {
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperRiedizione(" + evento.getId() + ") - entering"));
		EventoWrapper eventoWrapper = prepareCommonEditWrapper(evento.getProceduraFormativa(), providerId);
		Evento riedizioneEvento = eventoService.prepareRiedizioneEvento(evento);

		if(evento instanceof EventoFSC) {
			for(PersonaEvento p : ((EventoFSC)evento).getResponsabili())
				eventoWrapper.getPersoneEventoInserite().add(p);
			for(PersonaEvento p : ((EventoFSC)evento).getEsperti())
				eventoWrapper.getPersoneEventoInserite().add(p);
			for(PersonaEvento p : ((EventoFSC)evento).getCoordinatori())
				eventoWrapper.getPersoneEventoInserite().add(p);
			for(PersonaEvento p : ((EventoFSC)evento).getInvestigatori())
				eventoWrapper.getPersoneEventoInserite().add(p);
		} else if(evento instanceof EventoRES) {
			for(PersonaEvento p : ((EventoRES)evento).getResponsabili())
				eventoWrapper.getPersoneEventoInserite().add(p);
			for(PersonaEvento p : ((EventoRES)evento).getDocenti())
				eventoWrapper.getPersoneEventoInserite().add(p);
		} else if(evento instanceof EventoFAD) {
			for(PersonaEvento p : ((EventoFAD)evento).getResponsabili())
				eventoWrapper.getPersoneEventoInserite().add(p);
			for(PersonaEvento p : ((EventoFAD)evento).getDocenti())
				eventoWrapper.getPersoneEventoInserite().add(p);
		}

		eventoWrapper.setEvento(riedizioneEvento);
		eventoWrapper.initProgrammi();
		eventoWrapper = eventoService.prepareRipetibiliAndAllegati(eventoWrapper);
		//editabilità
		eventoWrapper.setEditSemiBloccato(eventoService.isEditSemiBloccato(evento));
		eventoWrapper.setEventoIniziato(eventoService.isEventoIniziato(evento));
//		eventoWrapper.setHasDataInizioRestrictions(eventoService.hasDataInizioRestrictions(evento));
		LOGGER.info(Utils.getLogMessage("prepareEventoWrapperRiedizione(" + evento.getId() + ") - exiting"));
		return eventoWrapper;
	}

	private SponsorWrapper prepareSponsorWrapper(Long providerId, Evento evento) {
		SponsorWrapper wrapper = new SponsorWrapper();
		wrapper.setSponsorList(evento.getSponsors());
		wrapper.setProviderId(providerId);
		wrapper.setDenominazioneProvider(providerService.getProvider(providerId).getDenominazioneLegale());
		wrapper.setEventoId(evento.getId());
		wrapper.setCodiceIdentificativoEvento(evento.getCodiceIdentificativo());
		return wrapper;
	}

	private String goToNew(Model model, EventoWrapper eventoWrapper) {
		model.addAttribute("eventoWrapper", eventoWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	private String goToEdit(Model model, EventoWrapper eventoWrapper) {
		model.addAttribute("eventoWrapper", eventoWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return EDIT;
	}

	private String goToShow(Model model, EventoWrapper eventoWrapper) {
		model.addAttribute("eventoWrapperShow", eventoWrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + EDIT));
		return SHOW;
	}

	private String goToRendiconto(Model model, EventoWrapper wrapper) {
		model.addAttribute("eventoWrapperRendiconto", wrapper);
		model.addAttribute("eventoWrapperShow", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + RENDICONTO));
		return RENDICONTO;
	}

	private String goToAllegaSponsor(Model model, SponsorWrapper wrapper) {
		model.addAttribute("sponsorWrapper", wrapper);
		LOGGER.info(Utils.getLogMessage("VIEW: " + SPONSOR));
		return SPONSOR;
	}

	private Map<String, String> prepareMappaErroriValutazione(BindingResult result){
		Map<String, String> mappa = new HashMap<String, String>();
		List<FieldError> errori = result.getFieldErrors();
		for (FieldError e : errori) {
			mappa.put(e.getField(), e.getCode());
		}
		return mappa;
	}

	@RequestMapping("/listaMetodologieRES")
	@ResponseBody
	public List<MetodologiaDidatticaRESEnum>getListaMetodologieRES(@RequestParam ObiettiviFormativiRESEnum obiettivo){
		return obiettivo.getMetodologieDidattiche();
	}

	@RequestMapping("/listaMetodologieFAD")
	@ResponseBody
	public List<MetodologiaDidatticaFADEnum>getListaMetodologieFAD(@RequestParam ObiettiviFormativiFADEnum obiettivo){
		return obiettivo.getMetodologieDidattiche();
	}

	@RequestMapping(value="/provider/{providerId}/createAnagraficaFullEvento", method=RequestMethod.POST)
	@ResponseBody
	public String saveAnagraficaFullEvento(@PathVariable("providerId") Long providerId, AnagraficaEvento anagrafica){
		//TODO
		anagraficaEventoService.save(anagrafica);
		return "OK";
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addPersonaTo", method=RequestMethod.POST, params={"addPersonaTo"})
	public String addPersonaTo(@RequestParam("addPersonaTo") String target,
								@RequestParam("fromLookUp") String fromLookUp,
								@RequestParam(name = "modificaElemento",required=false) String modificaElemento,
								@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			//se non siamo in modifica fromLookUp = true significa che la AnagraficaEvento è stata selezionata da lookup, quindi è già esistente
			//in questo caso che devo controllare che quanto si sta salvando non esista già nella lista ed emettere errore senza salvare
			//anche in modifica devo controllare che quanto si sta salvando non esista già nella lista, esludendo se stesso ed emettere errore senza salvare
			if(modificaElemento == null || modificaElemento.isEmpty()){
				//INSERIMENTO NUOVA PERSONA
				AnagraficaEventoBase anagraficaBase = eventoWrapper.getTempPersonaEvento().getAnagrafica();
				Long providerId = eventoWrapper.getEvento().getProvider().getId();

				//inserimento file in anagrafica
				if(eventoWrapper.getCv() != null && !eventoWrapper.getCv().isNew()) {
					File cv = fileService.getFile(eventoWrapper.getCv().getId());
					cv.getData();
					if(fromLookUp != null && Boolean.valueOf(fromLookUp)){
						File f = (File) cv.clone();
						fileService.save(f);
						anagraficaBase.setCv(f);
						if(f.getId() == cv.getId())
							throw new Exception("Errore durante la clonazione dei file!");
					}else{
						anagraficaBase.setCv(cv);
					}
				}

				//check se non esiste -> si registra l'anagrafica per il provider
				if(anagraficaBase != null) {
					//init gestione errori
					ErrorsAjaxWrapper errWrapper = new ErrorsAjaxWrapper();
					Map<String, String> errMap = new HashMap<String, String>();
					if(!Boolean.valueOf(fromLookUp)) {
						//validator
						if(target.equalsIgnoreCase("esperti") || target.equalsIgnoreCase("coordinatori") || target.equalsIgnoreCase("investigatori"))
							errMap = anagraficaValidator.validateAnagraficaBaseEvento(anagraficaBase, providerId, "anagraficaBase_", false);
						else
							errMap = anagraficaValidator.validateAnagraficaBaseEvento(anagraficaBase, providerId, "anagraficaBase_");
					} else {
						if(target.equalsIgnoreCase("docenti")){
							errMap = personaEventoValidator.validateAnagraficaBaseEvento(eventoWrapper.getTempPersonaEvento(), eventoWrapper.getDocenti(), false, "anagraficaBase_");
						}						
					}
					if(!errMap.isEmpty()) {
						errWrapper.setMappaErrori(errMap);
						model.addAttribute("errorsAjaxWrapper", errWrapper);
						return ERROR + " :: fragmentError";
					}
					else {
						//salva solo se non duplicato
						if(anagraficaEventoService.getAnagraficaEventoByCodiceFiscaleForProvider(anagraficaBase.getCodiceFiscale(), providerId) == null) {
							AnagraficaEvento anagraficaEventoToSave = new AnagraficaEvento();
							anagraficaEventoToSave.setAnagrafica(anagraficaBase);
							anagraficaEventoToSave.setProvider(eventoWrapper.getEvento().getProvider());
							anagraficaEventoService.save(anagraficaEventoToSave);
						}
					}
				}

				PersonaEvento p = SerializationUtils.clone(eventoWrapper.getTempPersonaEvento());
				//TODO sono obbligato a salvare i docenti perchè altrimenti non riesco a fare il binding in AddAttivitaRES (select si basa su id della entity)
				//questo comporta anche che prima di salvare l'evento devo fare il reload della persona altrimenti hibernate mi da detached object e non mi fa salvare
				//applico la regola a tutte le liste di PersoneEvento per avere la stessa gestione anche se non sarebbe neccessario

				eventoWrapper.getPersoneEventoInserite().add(p);
				if(target.equalsIgnoreCase("responsabiliScientifici")){
					if(eventoWrapper.getProceduraFormativa() == ProceduraFormativa.FSC && p.getIdentificativoPersonaRuoloEvento() == null) {
						p.setIdentificativoPersonaRuoloEvento(personaEventoService.prossimoIdentificativoPersonaRuoloEventoNonUtilizzato(eventoWrapper.getResponsabiliScientifici()));
					}
					File cvAnagrafica = p.getAnagrafica().getCv();
					if(cvAnagrafica != null) {
						cvAnagrafica.getData();
						File f = (File) cvAnagrafica.clone();
						fileService.save(f);
						p.getAnagrafica().setCv(f);
					}
					personaEventoRepository.save(p);
					eventoWrapper.getResponsabiliScientifici().add(p);
				}else if(target.equalsIgnoreCase("esperti")){
					if(eventoWrapper.getProceduraFormativa() == ProceduraFormativa.FSC && p.getIdentificativoPersonaRuoloEvento() == null) {
						p.setIdentificativoPersonaRuoloEvento(personaEventoService.prossimoIdentificativoPersonaRuoloEventoNonUtilizzato(eventoWrapper.getEsperti()));
					}
					personaEventoRepository.save(p);
					eventoWrapper.getEsperti().add(p);
				}else if(target.equalsIgnoreCase("coordinatori")){
					if(eventoWrapper.getProceduraFormativa() == ProceduraFormativa.FSC && p.getIdentificativoPersonaRuoloEvento() == null) {
						p.setIdentificativoPersonaRuoloEvento(personaEventoService.prossimoIdentificativoPersonaRuoloEventoNonUtilizzato(eventoWrapper.getCoordinatori()));
					}
					personaEventoRepository.save(p);
					eventoWrapper.getCoordinatori().add(p);
				}else if(target.equalsIgnoreCase("investigatori")){
					personaEventoRepository.save(p);
					eventoWrapper.getInvestigatori().add(p);
				}else if(target.equalsIgnoreCase("docenti")){
					File cvAnagrafica = p.getAnagrafica().getCv();
					if(cvAnagrafica != null) {
						cvAnagrafica.getData();
						File f = (File) cvAnagrafica.clone();
						fileService.save(f);
						p.getAnagrafica().setCv(f);
					}
					personaEventoRepository.save(p);
					eventoWrapper.getDocenti().add(p);
				}
			}else{
				//MODIFICA
				//15/12/2017 in modifica salvare il record crea un problema di validazione in quanto l'utente potrebbe
				//uscire dalla modifca dell'evento senza salvare l'evento stesso ma ritrovandosi le modifiche
				//alle personeEvento delle liste docenti, responsabili scientifici, esperti, etc. gia' salvate
				//non occorre neppure risettare l'oggetto nella relativa lista in quanto si sta modificando proprio quello
				int index = Integer.parseInt(modificaElemento);
				//se la modifica avviene su una PersonEvento inserita durante la modifica all'evento salviamo l'entity su db
				//in quanto non risultera' presente nell'evento a meno che lo stesso non venga salvato
				
				ErrorsAjaxWrapper errWrapper = new ErrorsAjaxWrapper();
				Map<String, String> errMap = new HashMap<String, String>();
				if(target.equalsIgnoreCase("docenti")){
					errMap = personaEventoValidator.validateAnagraficaBaseEvento(eventoWrapper.getTempPersonaEvento(), eventoWrapper.getDocenti(), true, "anagraficaBase_");
				}						
				if(!errMap.isEmpty()) {
					errWrapper.setMappaErrori(errMap);
					model.addAttribute("errorsAjaxWrapper", errWrapper);
					return ERROR + " :: fragmentError";
				}


				if(eventoWrapper.getCv() != null && !eventoWrapper.getCv().isNew()) {
					File cv = fileService.getFile(eventoWrapper.getCv().getId());
					cv.getData();
					//Una modifica ad una PersonaEvento non sara' mai da lookup
//					if(fromLookUp != null && Boolean.valueOf(fromLookUp)){
//						File f = (File) cv.clone();
//						fileService.save(f);
//						if(f.getId() == cv.getId())
//							throw new Exception("Errore durante la clonazione dei file!");
//					}else{
//						anagraficaBase.setCv(cv);
//					}
					if(target.equalsIgnoreCase("responsabiliScientifici")){
						eventoWrapper.getTempPersonaEvento().getAnagrafica().setCv(cv);
					}else if(target.equalsIgnoreCase("docenti")){
						eventoWrapper.getTempPersonaEvento().getAnagrafica().setCv(cv);
					}
				}
				
				//l'oggetto in modifica è un clone di quello presente nella lista, questo perchè le modifiche effettuate all'oggetto se non valide non devono essere applicate
				//se siamo qui l'oggetto è valido occore riportare i dati nell'oggetto in modifica
				if(target.equalsIgnoreCase("responsabiliScientifici")){
					eventoWrapper.getResponsabiliScientifici().set(index, eventoWrapper.getTempPersonaEvento());
				}else if(target.equalsIgnoreCase("docenti")){
					eventoWrapper.getDocenti().set(index, eventoWrapper.getTempPersonaEvento());
				}else if(target.equalsIgnoreCase("esperti")){
					eventoWrapper.getEsperti().set(index, eventoWrapper.getTempPersonaEvento());
				}else if(target.equalsIgnoreCase("coordinatori")){
					eventoWrapper.getCoordinatori().set(index, eventoWrapper.getTempPersonaEvento());
				}else if(target.equalsIgnoreCase("investigatori")){
					eventoWrapper.getInvestigatori().set(index, eventoWrapper.getTempPersonaEvento());
				}

				if(eventoWrapper.getPersoneEventoInserite().contains(eventoWrapper.getTempPersonaEvento())) {
					//modifica di una personaEvento inserita
					personaEventoRepository.save(eventoWrapper.getTempPersonaEvento());
				}
			}
			eventoWrapper.setTempPersonaEvento(new PersonaEvento());
			return EDIT + " :: " + target;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: " + target;
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addPersonaFullTo", method=RequestMethod.POST, params={"addPersonaFullTo"})
	public String addPersonaFullTo(@RequestParam("addPersonaFullTo") String target,
									@RequestParam("fromLookUpFull") String fromLookUpFull,
									@RequestParam(name = "modificaElementoFull",required=false) String modificaElemento,
									@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(modificaElemento == null || modificaElemento.isEmpty()){
				//INSERIMENTO NUOVA PERSONA
				//validator
				AnagraficaFullEventoBase anagraficaFull = eventoWrapper.getTempPersonaFullEvento().getAnagrafica();
				Long providerId = eventoWrapper.getEvento().getProvider().getId();
				if(anagraficaFull != null) {
					//init gestione errori
					ErrorsAjaxWrapper errWrapper = new ErrorsAjaxWrapper();
					Map<String, String> errMap = new HashMap<String, String>();
					if(!Boolean.valueOf(fromLookUpFull)) {
						//validator
						errMap = anagraficaValidator.validateAnagraficaFullEvento(anagraficaFull, providerId, "anagraficaFull_");
					}
					if(!errMap.isEmpty()) {
						errWrapper.setMappaErrori(errMap);
						model.addAttribute("errorsAjaxWrapper", errWrapper);
						return ERROR + " :: fragmentError";
					}
					else {
						//salva solo se non duplicato
						if(anagraficaFullEventoService.getAnagraficaFullEventoByCodiceFiscaleForProvider(anagraficaFull.getCodiceFiscale(), providerId) == null) {
							AnagraficaFullEvento anagraficaFullEventoToSave = new AnagraficaFullEvento();
							anagraficaFullEventoToSave.setAnagrafica(anagraficaFull);
							anagraficaFullEventoToSave.setProvider(eventoWrapper.getEvento().getProvider());
							anagraficaFullEventoService.save(anagraficaFullEventoToSave);
						}
					}
				}
				//PersonaFullEvento p = (PersonaFullEvento) Utils.copy(eventoWrapper.getTempPersonaFullEvento());
				PersonaFullEvento p = SerializationUtils.clone(eventoWrapper.getTempPersonaFullEvento());
				if(target.equalsIgnoreCase("responsabileSegreteria")){
					eventoWrapper.getEvento().setResponsabileSegreteria(p);
				}
			}else{
				//MODIFICA
				eventoWrapper.getEvento().setResponsabileSegreteria(eventoWrapper.getTempPersonaFullEvento());
			}
			eventoWrapper.setTempPersonaFullEvento(new PersonaFullEvento());
			return EDIT + " :: " + target;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: " + target;
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addSponsorTo", method=RequestMethod.POST)
	public String addSponsorTo(@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			Sponsor s = SerializationUtils.clone(eventoWrapper.getTempSponsorEvento());
			eventoWrapper.getSponsors().add(s);
			eventoWrapper.setTempSponsorEvento(new Sponsor());
			if(eventoWrapper.getSponsorFile() != null && !eventoWrapper.getSponsorFile().isNew())
				s.setSponsorFile(fileService.getFile(eventoWrapper.getSponsorFile().getId()));
			return EDIT + " :: sponsors";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: sponsors";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addPartnerTo", method=RequestMethod.POST)
	public String addPartnerTo(@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			Partner p = SerializationUtils.clone(eventoWrapper.getTempPartnerEvento());
			eventoWrapper.getPartners().add(p);
			eventoWrapper.setTempPartnerEvento(new Partner());
			if(eventoWrapper.getPartnerFile() != null && !eventoWrapper.getPartnerFile().isNew())
				p.setPartnerFile(fileService.getFile(eventoWrapper.getPartnerFile().getId()));
			return EDIT + " :: partners";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: partners";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/removePersonaFrom/{removePersonaFrom}/{rowIndex}", method=RequestMethod.GET)
	public String removePersonaFrom(@PathVariable("removePersonaFrom") String target, @PathVariable("rowIndex") String rowIndex,
												@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			int index;
			if(target.equalsIgnoreCase("responsabiliScientifici")){
				index = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getResponsabiliScientifici().remove(index);
				personaEventoService.setIdentificativoPersonaRuoloEvento(eventoWrapper.getResponsabiliScientifici());
				//è possibile che sia stato modificato il campo identificativoPersonaRuoloEvento
				//per le PersoneEvento inserite durante la corrente modifica del evento su salvataggio al ricaricamento della PersonaEvento
				//per evitare l'errore di detach la modifica andrebbe perduta occorre effettuare il salvataggio della PersonaEvento
				for(PersonaEvento p : eventoWrapper.getResponsabiliScientifici()) {
					if(eventoWrapper.getPersoneEventoInserite().contains(p))
						personaEventoRepository.save(p);
				}
			}else if(target.equalsIgnoreCase("docenti")){
				index = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getDocenti().remove(index);
			}else if(target.equalsIgnoreCase("responsabileSegreteria")){
				eventoWrapper.getEvento().setResponsabileSegreteria(new PersonaFullEvento());
			}else if(target.equalsIgnoreCase("esperti")){
				index = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getEsperti().remove(index);
				personaEventoService.setIdentificativoPersonaRuoloEvento(eventoWrapper.getEsperti());
				//è possibile che sia stato modificato il campo identificativoPersonaRuoloEvento
				//per le PersoneEvento inserite durante la corrente modifica del evento su salvataggio al ricaricamento della PersonaEvento
				//per evitare l'errore di detach la modifica andrebbe perduta occorre effettuare il salvataggio della PersonaEvento
				for(PersonaEvento p : eventoWrapper.getEsperti()) {
					if(eventoWrapper.getPersoneEventoInserite().contains(p))
						personaEventoRepository.save(p);
				}
			}else if(target.equalsIgnoreCase("coordinatori")){
				index = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getCoordinatori().remove(index);
				personaEventoService.setIdentificativoPersonaRuoloEvento(eventoWrapper.getCoordinatori());
				//è possibile che sia stato modificato il campo identificativoPersonaRuoloEvento
				//per le PersoneEvento inserite durante la corrente modifica del evento su salvataggio al ricaricamento della PersonaEvento
				//per evitare l'errore di detach la modifica andrebbe perduta occorre effettuare il salvataggio della PersonaEvento
				for(PersonaEvento p : eventoWrapper.getCoordinatori()) {
					if(eventoWrapper.getPersoneEventoInserite().contains(p))
						personaEventoRepository.save(p);
				}
			}else if(target.equalsIgnoreCase("investigatori")){
				index = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getInvestigatori().remove(index);
			}
			return EDIT + " :: " + target;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: " + target;
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/removeSponsor/{rowIndex}", method=RequestMethod.GET)
	public String removeSponsorFrom(@PathVariable("rowIndex") String rowIndex,
			@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			int sponsorIndex = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getSponsors().remove(sponsorIndex);
			return EDIT + " :: sponsors";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: sponsors";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/removePartner/{rowIndex}", method=RequestMethod.GET)
	public String removePartnerFrom(@PathVariable("rowIndex") String rowIndex,
			@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			int partnerIndex = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getPartners().remove(partnerIndex);
			return EDIT + " :: partners";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: partners";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/setLookupAnagraficaEvento/{type}/{angraficaEventoId}", method=RequestMethod.GET)
	public String lookupPersona(@PathVariable("type") String type,
									@PathVariable("angraficaEventoId") Long angraficaEventoId,
												@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(type.equalsIgnoreCase("Full")){
				eventoWrapper.setTempPersonaFullEvento(new PersonaFullEvento(anagraficaFullEventoService.getAnagraficaFullEvento(angraficaEventoId)));
				return EDIT + " :: #addPersonaFullTo";
			}else{
				PersonaEvento p = new PersonaEvento(anagraficaEventoService.getAnagraficaEvento(angraficaEventoId));
				eventoWrapper.setTempPersonaEvento(p);
				eventoWrapper.setCv(p.getAnagrafica().getCv());
				return EDIT + " :: #addPersonaTo";
			}
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/setNewAnagraficaEvento/{type}", method=RequestMethod.GET)
	public String newPersona(@PathVariable("type") String type,
			@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(type.equalsIgnoreCase("Full")){
				eventoWrapper.setTempPersonaFullEvento(new PersonaFullEvento());
				return EDIT + " :: #addPersonaFullTo";
			}else{
				PersonaEvento p = new PersonaEvento();
				eventoWrapper.setTempPersonaEvento(p);
				eventoWrapper.setCv(new File());
				return EDIT + " :: #addPersonaTo";
			}
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addAttivitaTo", method=RequestMethod.POST)
	public String addAttivitaTo(@RequestParam("target") String target,
								@RequestParam("addAttivitaTo") String addAttivitaTo,
								@RequestParam("modificaElemento") Integer modificaElemento,
								@RequestParam(name = "extraType",required=false) String extraType,
								@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(modificaElemento == null){
				//INSERIMENTO
				int programmaIndex = Integer.valueOf(addAttivitaTo).intValue();
				if(target.equalsIgnoreCase("attivitaRES")){
					DettaglioAttivitaRES attivitaRES =  SerializationUtils.clone(eventoWrapper.getTempAttivitaRES());
					attivitaRES.calcolaOreAttivita();
					Long programmaIndexLong = Long.valueOf(programmaIndex);
					LOGGER.debug("EventoRES - evento/addAttivitaTo programmaIndexLong: " + programmaIndexLong);
					eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().get(programmaIndexLong).getProgramma().getProgramma().add(attivitaRES);

					if(extraType != null){
						attivitaRES.setExtraType(extraType);
					}

					eventoWrapper.setTempAttivitaRES(new DettaglioAttivitaRES());
				}else if(target.equalsIgnoreCase("attivitaFSC")){
					AzioneRuoliEventoFSC azioniRuoli = SerializationUtils.clone(eventoWrapper.getTempAttivitaFSC());
					eventoWrapper.getProgrammaEventoFSC().get(programmaIndex).getAzioniRuoli().add(azioniRuoli);
					eventoWrapper.setTempAttivitaFSC(new AzioneRuoliEventoFSC());
				}else if(target.equalsIgnoreCase("attivitaFAD")){
					DettaglioAttivitaFAD attivitaFAD =  SerializationUtils.clone(eventoWrapper.getTempAttivitaFAD());
					eventoWrapper.getProgrammaEventoFAD().add(attivitaFAD);
					eventoWrapper.setTempAttivitaFAD(new DettaglioAttivitaFAD());
				}
			}else{
				//MODIFICA
				int programmaIndex = Integer.valueOf(addAttivitaTo).intValue();
				int elementoIndex = Integer.valueOf(modificaElemento).intValue();
				if(target.equalsIgnoreCase("attivitaRES")){
					DettaglioAttivitaRES attivitaRES =  eventoWrapper.getTempAttivitaRES();
					attivitaRES.calcolaOreAttivita();
					Long programmaIndexLong = Long.valueOf(programmaIndex);
					eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().get(programmaIndexLong).getProgramma().getProgramma().set(elementoIndex, attivitaRES);
					eventoWrapper.setTempAttivitaRES(new DettaglioAttivitaRES());
				}else if(target.equalsIgnoreCase("attivitaFSC")){
					AzioneRuoliEventoFSC azioniRuoli = eventoWrapper.getTempAttivitaFSC();
					eventoWrapper.getProgrammaEventoFSC().get(programmaIndex).getAzioniRuoli().set(elementoIndex, azioniRuoli);
					eventoWrapper.setTempAttivitaFSC(new AzioneRuoliEventoFSC());
				}else if(target.equalsIgnoreCase("attivitaFAD")){
					DettaglioAttivitaFAD attivitaFAD =  eventoWrapper.getTempAttivitaFAD();
					eventoWrapper.getProgrammaEventoFAD().set(elementoIndex, attivitaFAD);
					eventoWrapper.setTempAttivitaFAD(new DettaglioAttivitaFAD());
				}
			}
			return EDIT + " :: " + target;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: " + target;
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/removeAttivitaFrom/{target}/{removeAttivitaFrom}/{rowIndex}", method=RequestMethod.GET)
	public String removeAttivitaFrom(@PathVariable("target") String target,
										@PathVariable("removeAttivitaFrom") String removeAttivitaFrom,
											@PathVariable("rowIndex") String rowIndex,
												@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			int programmaIndex;
			int attivitaRow;
			if(target.equalsIgnoreCase("attivitaRES")){
				programmaIndex = Integer.valueOf(removeAttivitaFrom).intValue();
				attivitaRow = Integer.valueOf(rowIndex).intValue();
				Long programmaIndexLong = Long.valueOf(programmaIndex);
				LOGGER.debug("EventoRES - evento/removeAttivitaFrom programmaIndexLong: " + programmaIndexLong + "; attivitaRow: " + attivitaRow);
				eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().get(programmaIndexLong).getProgramma().getProgramma().remove(attivitaRow);
			}else if(target.equalsIgnoreCase("attivitaFSC")){
				programmaIndex = Integer.valueOf(removeAttivitaFrom).intValue();
				attivitaRow = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getProgrammaEventoFSC().get(programmaIndex).getAzioniRuoli().remove(attivitaRow);
			}else if(target.equalsIgnoreCase("attivitaFAD")){
				programmaIndex = Integer.valueOf(removeAttivitaFrom).intValue();
				attivitaRow = Integer.valueOf(rowIndex).intValue();
				eventoWrapper.getProgrammaEventoFAD().remove(attivitaRow);
			}
			return EDIT + " :: " + target;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: " + target;
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/showSection/{sectionIndex}", method=RequestMethod.POST)
	public String showSection(@PathVariable("sectionIndex") String sIndex, @ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper,
			Model model, RedirectAttributes redirectAttrs){
		int sectionIndex = 1;
		try{
			sectionIndex = Integer.valueOf(sIndex).intValue();
			if(sectionIndex == 2){
				//sezione programma evento
				//Eseguo questo aggiornamento perche' le date dataInizioe dataFine potrebbero essere state modificate
				eventoService.aggiornaDati(eventoWrapper);
			}else if(sectionIndex == 3){
				//sezione finale - ricalcolo durata e crediti
				eventoService.calculateAutoCompilingData(eventoWrapper);
			}
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
		}

		if(eventoWrapper.getEvento() instanceof EventoRES){
			return EDITRES + " :: " + "section-" + sectionIndex;
		}else if(eventoWrapper.getEvento() instanceof EventoFSC){
			return EDITFSC + " :: " + "section-" + sectionIndex;
		}else{
			return EDITFAD + " :: " + "section-" + sectionIndex;
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addDataIntermedia/{sectionToRefresh}", method=RequestMethod.POST)
	public String addDataIntermedia(@PathVariable("sectionToRefresh") String sectionToRefresh,
								@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(eventoWrapper.getEvento() instanceof EventoRES){
				eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().addProgrammaGiornalieroIntermedio(null);
				//return EDITRES + " :: " + sectionToRefresh;
				return EDITRES + " :: eventoRESEdit";
			} else {
				throw new Exception("Metodo chiamato dalla pagina errata aspettatto EventoRES.");
			}
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/removeDataIntermedia/{key}/{sectionToRefresh}", method=RequestMethod.POST)
	public String removeDataIntermedia(@PathVariable("key") String key, @PathVariable("sectionToRefresh") String sectionToRefresh,
								@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			Long k = Long.valueOf(key);
			if(eventoWrapper.getEvento() instanceof EventoRES){
				eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().removeProgrammaGiornalieroIntermedio(k);
				//return EDITRES + " :: " + sectionToRefresh;
				return EDITRES + " :: eventoRESEdit";
			} else {
				throw new Exception("Metodo chiamato dalla pagina errata aspettatto EventoRES.");
			}
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addRisultatoAtteso/{sectionToRefresh}", method=RequestMethod.POST)
	public String addRisultatoAtteso(@PathVariable("sectionToRefresh") String sectionToRefresh,
			@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(eventoWrapper.getEvento() instanceof EventoRES || eventoWrapper.getEvento() instanceof EventoFAD){
				if(eventoWrapper.getRisultatiAttesiMapTemp() == null) {
					Map<Long, String> val = new LinkedHashMap<Long, String>();
					val.put(1L, null);
					eventoWrapper.setRisultatiAttesiMapTemp(val);
				} else {
					Long max = 1L;
					if(eventoWrapper.getRisultatiAttesiMapTemp().size() != 0)
						max = Collections.max(eventoWrapper.getRisultatiAttesiMapTemp().keySet()) + 1;
					eventoWrapper.getRisultatiAttesiMapTemp().put(max, null);
				}
				String evType = eventoWrapper.getEvento() instanceof EventoRES ? EDITRES : EDITFAD;
				return evType + " :: " + sectionToRefresh;
			} else {
				throw new Exception("Metodo chiamato dalla pagina errata aspettatto EventoRES o EventoFAD.");
			}
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/removeRisultatoAtteso/{key}/{sectionToRefresh}", method=RequestMethod.POST)
	public String removeRisultatoAtteso(@PathVariable("key") String key, @PathVariable("sectionToRefresh") String sectionToRefresh,
			@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			Long k = Long.valueOf(key);
			if(eventoWrapper.getEvento() instanceof EventoRES || eventoWrapper.getEvento() instanceof EventoFAD){
				eventoWrapper.getRisultatiAttesiMapTemp().remove(k);
				String evType = eventoWrapper.getEvento() instanceof EventoRES ? EDITRES : EDITFAD;
				return evType + " :: " + sectionToRefresh;
			} else {
				throw new Exception("Metodo chiamato dalla pagina errata aspettatto EventoRES o EventoFAD.");
			}
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/evento/listaDocentiAttivita")
	@ResponseBody
	public List<PersonaEvento>getListaDocentiAttivitaRES(@PathVariable Long providerId, @ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		List<PersonaEvento> lista = new ArrayList<PersonaEvento>();
		if(eventoWrapper.getEvento() instanceof EventoRES || eventoWrapper.getEvento() instanceof EventoFAD){
			lista = eventoWrapper.getDocenti();
		}
		return lista;
	}

	@RequestMapping("/listaRuoliCoinvolti")
	@ResponseBody
	public List<RuoloFSCEnum>getListaRuoliCoinvolti(@RequestParam TipologiaEventoFSCEnum tipologiaEvento){
		if(tipologiaEvento != null)
			return tipologiaEvento.getRuoliCoinvolti();
		else
			return null;
	}

	@RequestMapping(value = "/provider/{providerId}/evento/addRuoloOreToTemp", method=RequestMethod.POST)
	public String addRuoloOreToTemp(@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, BindingResult result, Model model, RedirectAttributes redirectAttrs){
		try{
			eventoWrapper.getTempAttivitaFSC().getRuoli().add(new RuoloOreFSC(eventoWrapper.getTempRuoloOreFSC().getRuolo(), eventoWrapper.getTempRuoloOreFSC().getTempoDedicato()));
			return EDITFSC + " :: ruoloOreFSC";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDITFSC + " :: ruoloOreFSC";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/removeRuoloOreToTemp/{rowIndex}", method=RequestMethod.GET)
	public String removeRuoloOreToTemp(@PathVariable("rowIndex") String rowIndex,
										@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			int index = Integer.valueOf(rowIndex).intValue();
			eventoWrapper.getTempAttivitaFSC().getRuoli().remove(index);
			return EDITFSC + " :: ruoloOreFSC";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDITFSC + " :: ruoloOreFSC";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/modifica/{target}/{modificaElemento}", method=RequestMethod.GET)
	public String modificaPersona(@PathVariable("target") String target,
									@PathVariable("modificaElemento") Long modificaElemento,
												@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(target.equalsIgnoreCase("responsabiliScientifici")){
				//PersonaEvento p = eventoWrapper.getResponsabiliScientifici().get(modificaElemento.intValue());
				PersonaEvento p = SerializationUtils.clone(eventoWrapper.getResponsabiliScientifici().get(modificaElemento.intValue()));
				eventoWrapper.setTempPersonaEvento(p);
				eventoWrapper.setCv(p.getAnagrafica().getCv());
				return EDIT + " :: #addPersonaTo";
			}else if(target.equalsIgnoreCase("docenti")){
				//PersonaEvento p = eventoWrapper.getDocenti().get(modificaElemento.intValue());
				PersonaEvento p = SerializationUtils.clone(eventoWrapper.getDocenti().get(modificaElemento.intValue()));
				eventoWrapper.setTempPersonaEvento(p);
				eventoWrapper.setCv(p.getAnagrafica().getCv());
				return EDIT + " :: #addPersonaTo";
			}else if(target.equalsIgnoreCase("responsabileSegreteria")){
				//PersonaFullEvento p = eventoWrapper.getEvento().getResponsabileSegreteria();
				PersonaFullEvento p = SerializationUtils.clone(eventoWrapper.getEvento().getResponsabileSegreteria());
				eventoWrapper.setTempPersonaFullEvento(p);
				return EDIT + " :: #addPersonaFullTo";
			}else if(target.equalsIgnoreCase("esperti")){
				//PersonaEvento p = eventoWrapper.getEsperti().get(modificaElemento.intValue());
				PersonaEvento p = SerializationUtils.clone(eventoWrapper.getEsperti().get(modificaElemento.intValue()));
				eventoWrapper.setTempPersonaEvento(p);
				eventoWrapper.setCv(null);
				return EDIT + " :: #addPersonaTo";
			}else if(target.equalsIgnoreCase("coordinatori")){
				//PersonaEvento p = eventoWrapper.getCoordinatori().get(modificaElemento.intValue());
				PersonaEvento p = SerializationUtils.clone(eventoWrapper.getCoordinatori().get(modificaElemento.intValue()));
				eventoWrapper.setTempPersonaEvento(p);
				eventoWrapper.setCv(null);
				return EDIT + " :: #addPersonaTo";
			}else if(target.equalsIgnoreCase("investigatori")){
				//PersonaEvento p = eventoWrapper.getInvestigatori().get(modificaElemento.intValue());
				PersonaEvento p = SerializationUtils.clone(eventoWrapper.getInvestigatori().get(modificaElemento.intValue()));
				eventoWrapper.setTempPersonaEvento(p);
				eventoWrapper.setCv(null);
				return EDIT + " :: #addPersonaTo";
			}

			return "redirect:/home";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/modificaAttivita/{target}/{addAttivitaTo}/{modificaElemento}", method=RequestMethod.GET)
	public String modificaAttivita(@PathVariable("target") String target,
									@PathVariable("addAttivitaTo") String addAttivitaTo,
									@PathVariable("modificaElemento") Integer modificaElemento,
									@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			int programmaIndex = Integer.valueOf(addAttivitaTo).intValue();
			int elementoIndex = Integer.valueOf(modificaElemento).intValue();
			if(target.equalsIgnoreCase("attivitaRES")){
				Long programmaIndexLong = Long.valueOf(programmaIndex);
				DettaglioAttivitaRES attivita = eventoWrapper.getEventoRESDateProgrammiGiornalieriWrapper().getSortedProgrammiGiornalieriMap().get(programmaIndexLong).getProgramma().getProgramma().get(elementoIndex);
				eventoWrapper.setTempAttivitaRES(attivita);
				return EDITRES + " :: #addAttivitaRES";
			}else if(target.equalsIgnoreCase("attivitaFSC")){
				AzioneRuoliEventoFSC azione = eventoWrapper.getProgrammaEventoFSC().get(programmaIndex).getAzioniRuoli().get(elementoIndex);
				eventoWrapper.setTempAttivitaFSC(azione);
				return EDITFSC + " :: #addAttivitaFSC";
			}else if(target.equalsIgnoreCase("attivitaFAD")){
				DettaglioAttivitaFAD attivitaFAD = eventoWrapper.getProgrammaEventoFAD().get(elementoIndex);
				eventoWrapper.setTempAttivitaFAD(attivitaFAD);
				return EDITFAD + " :: #addAttivitaFAD";
			}
			return "redirect:/home";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/resettaAttivita/{target}", method=RequestMethod.GET)
	public String resettaAttivita(@PathVariable("target") String target,
			@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper, Model model, RedirectAttributes redirectAttrs){
		try{
			if(target.equalsIgnoreCase("attivitaRES")){
				DettaglioAttivitaRES attivita = new DettaglioAttivitaRES();
				eventoWrapper.setTempAttivitaRES(attivita);
				return EDITRES + " :: #addAttivitaRES";
			}else if(target.equalsIgnoreCase("attivitaFSC")){
				AzioneRuoliEventoFSC azione = new AzioneRuoliEventoFSC();
				eventoWrapper.setTempAttivitaFSC(azione);
				return EDITFSC + " :: #addAttivitaFSC";
			}else if(target.equalsIgnoreCase("attivitaFAD")){
				DettaglioAttivitaFAD attivitaFAD = new DettaglioAttivitaFAD();
				eventoWrapper.setTempAttivitaFAD(attivitaFAD);
				return EDITFAD + " :: #addAttivitaFAD";
			}
			return "redirect:/home";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/provider/{providerId}/evento/{eventoId}/paga", method=RequestMethod.GET)
	public String pagaEvento(@PathVariable("providerId") Long providerId, @PathVariable("eventoId") Long eventoId,
			 					HttpServletRequest request, Model model, RedirectAttributes redirectAttrs){
		try{
			String rootUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
			try {
				String url = engineeringService.pagaEvento(eventoId, rootUrl + request.getContextPath() + "/provider/" + providerId + "/evento/list");
				if (StringUtils.hasText(url)) {
					return "redirect:" + url;
				}
			}
			catch (PagInCorsoException pagInCorsoEx) {
				redirectAttrs.addFlashAttribute("message", new Message("message.attenzione", "message.pagamento_gia_in_corso", "warning"));
				LOGGER.error(Utils.getLogMessage(pagInCorsoEx.getMessage()),pagInCorsoEx);
				return "redirect:/provider/"+providerId+"/evento/list";
			}
			return "redirect:/provider/"+providerId+"/evento/list";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/provider/"+providerId+"/evento/list";
		}
	}

	@RequestMapping("/provider/eventi/{search}/list")
	public String getAllEventiByProviderIdForGruppo(@PathVariable("search") EventoSearchEnum search, Model model,
			RedirectAttributes redirectAttrs, HttpServletRequest request) throws Exception {
		LOGGER.info(Utils.getLogMessage("GET /provider/eventi/" + search + "/list"));
		try {

			Set<Evento> listaEventi = new HashSet<Evento>();
			CurrentUser currentUser = Utils.getAuthenticatedUser();

			switch(search){
				case SCADENZA_PAGAMENTO : 	listaEventi = eventoService.getEventiForProviderIdInScadenzaDiPagamento(currentUser.getAccount().getProvider().getId());
											break;

				case NON_PAGATI : listaEventi = eventoService.getEventiForProviderIdScadutiENonPagati(currentUser.getAccount().getProvider().getId());
										break;
				case NON_RENDICONTATI : listaEventi = eventoService.getEventiForProviderIdScadutiENonRendicontati(currentUser.getAccount().getProvider().getId());
										break;
				case BOZZA : listaEventi = eventoService.getEventiByProviderIdAndStato(currentUser.getAccount().getProvider().getId(), EventoStatoEnum.BOZZA);
								break;
				case SCADENZA_RENDICONTAZIONE : listaEventi = eventoService.getEventiForProviderIdInScadenzaDiRendicontazione(currentUser.getAccount().getProvider().getId());
												break;

				default: break;
			}

			model.addAttribute("label", search.getNome());
			model.addAttribute("eventoList", listaEventi);
			model.addAttribute("canCreateEvento", false);
			LOGGER.info(Utils.getLogMessage("VIEW: accreditamento/accreditamentoList"));
			if(Utils.getAuthenticatedUser().isSegreteria())
				model.addAttribute("scadenzeEventoWrapper", new ScadenzeEventoWrapper());
			return goToEventoList(request, model);
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /provider/eventi/" + search + "/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	@RequestMapping("/eventi/{search}/list")
	public String getAllEventiByForGruppo(@PathVariable("search") EventoSearchEnum search, Model model,
			RedirectAttributes redirectAttrs, HttpServletRequest request) throws Exception {
		LOGGER.info(Utils.getLogMessage("GET /eventi/" + search + "/list"));
		try {

			Set<Evento> listaEventi = new HashSet<Evento>();

			switch(search){
				case CREDITI_NON_CONFERMATI : listaEventi = eventoService.getEventiCreditiNonConfermati();
								break;
				case ALIMENTAZIONE_PRIMA_INFANZIA : listaEventi = eventoService.getEventiAlimentazionePrimaInfanzia();
													model.addAttribute("archiviaEvento",true);
													break;
				case MEDICINE_NON_CONVENZIONALI : listaEventi = eventoService.getEventiMedicineNonConvenzionali();
													model.addAttribute("archiviaEvento",true);
								break;

				default: break;
			}

			model.addAttribute("label", search.getNome());
			model.addAttribute("search", search);
			model.addAttribute("eventoList", listaEventi);
			model.addAttribute("canCreateEvento", false);
			if(Utils.getAuthenticatedUser().isSegreteria())
				model.addAttribute("scadenzeEventoWrapper", new ScadenzeEventoWrapper());
			return goToEventoList(request, model);
		}catch (Exception ex){
			LOGGER.error(Utils.getLogMessage("GET /eventi/" + search + "/list"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	@RequestMapping("/evento/ricerca")
	public String ricercaEventoGlobale(Model model,RedirectAttributes redirectAttrs){
		LOGGER.info(Utils.getLogMessage("POST /evento/ricerca"));
		try {
			RicercaEventoWrapper wrapper = prepareRicercaEventoWrapper();

			CurrentUser currentUser = Utils.getAuthenticatedUser();
			if(currentUser.isProviderVisualizzatore()){
				wrapper.setProviderId(currentUser.getAccount().getProvider().getId());
			}else{
				wrapper.setProviderId(null);
			}

			model.addAttribute("ricercaEventoWrapper", wrapper);
			LOGGER.info(Utils.getLogMessage("VIEW: " + RICERCA));
			return RICERCA;
		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /evento/ricerca"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.info(Utils.getLogMessage("REDIRECT: /home"));
			return "redirect:/home";
		}
	}

	@RequestMapping(value = "/evento/ricerca", method = RequestMethod.POST)
	public String executeRicercaEvento(@ModelAttribute("ricercaEventoWrapper") RicercaEventoWrapper wrapper,
									BindingResult result, RedirectAttributes redirectAttrs, Model model, HttpServletRequest request){
		LOGGER.info(Utils.getLogMessage("POST /ricerca/evento"));
		try {

			String returnRedirect = "";

			if(wrapper.getProviderId() != null){
				wrapper.setCampoIdProvider(wrapper.getProviderId());
				//assicura di non fare il refresh della lista per gli utenti Provider
				redirectAttrs.addFlashAttribute("providerId", wrapper.getProviderId());
				returnRedirect = "redirect:/provider/" + wrapper.getProviderId() + "/evento/ricercaList";
			}else{
				returnRedirect = "redirect:/evento/ricercaList";
			}

			List<Evento> listaEventi = new ArrayList<Evento>();
			listaEventi = eventoService.cerca(wrapper);

			redirectAttrs.addFlashAttribute("searchList", listaEventi);

			return returnRedirect;
		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /evento/ricerca"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/evento/ricerca";
		}
	}

	@RequestMapping(value = "/evento/{search}/archivia", method = RequestMethod.POST)
	public String archiveEvent(@RequestParam("event_Id") List<Long> ids, @PathVariable("search") EventoSearchEnum search, Model model, RedirectAttributes redirectAttrs, HttpServletRequest request) {
		LOGGER.info(Utils.getLogMessage("POST /evento/archivia"));
		try{
				List<Long> idsLong = ids;
				eventoService.archiveEventoInPrimaInfanziaOrMedNonConv(idsLong);
				LOGGER.info(Utils.getLogMessage("REDIRECT success: /eventi/ALIMENTAZIONE_PRIMA_INFANZIA/list"));
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "label.archivia_success", "success"));
				return "redirect:/eventi/" + search + "/list";
		}catch (Exception ex){
			LOGGER.info(Utils.getLogMessage("Errore: /evento/archiva"));
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return "redirect:/home";
		}
	}

	private RicercaEventoWrapper prepareRicercaEventoWrapper(){
		RicercaEventoWrapper wrapper = new RicercaEventoWrapper();
		wrapper.setProfessioniList(professioneService.getAllProfessioni());
		wrapper.setDisciplineList(disciplinaService.getAllDiscipline());
		wrapper.setObiettiviNazionaliList(obiettivoService.getObiettiviNazionali());
		wrapper.setObiettiviRegionaliList(obiettivoService.getObiettiviRegionali());
		return wrapper;
	}


	@RequestMapping(value = "/evento/{eventoId}/proroga/scadenze", method = RequestMethod.POST)
	public String prorogaScadenzeEvento(@PathVariable("eventoId") Long eventoId,
			@ModelAttribute("scadenzeEventoWrapper") ScadenzeEventoWrapper wrapper,
			BindingResult result, RedirectAttributes redirectAttrs, Model model, HttpServletRequest request,
			HttpSession session){
		LOGGER.info(Utils.getLogMessage("POST /evento/"+eventoId+"/proroga/scadenze"));
		try {
			scadenzeEventoValidator.validate(wrapper, result, "");
			if(result.hasErrors()) {
				wrapper.setSubmitScadenzeError(true);
				model.addAttribute("scadenzeEventoWrapper", wrapper);
				model.addAttribute("message", new Message("message.errore", "message.inserire_campi_required", "error"));
				return goToEventoList(request, model);
			}
			else {
				eventoService.updateScadenze(eventoId, wrapper);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.scadenze_evento_aggiornate", "success"));
				updateEventoList(eventoId, session);
				return "redirect:"+wrapper.getReturnLink();
			}
		}catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("POST /evento/"+eventoId+"/proroga/scadenze"),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			try {
				return "redirect:"+wrapper.getReturnLink();
			}
			catch (Exception ex2) {
				return "redirect:/home";
			}
		}
	}

	@JsonView(JsonViewModel.EventoLookup.class)
	@PreAuthorize("@securityAccessServiceImpl.canShowProvider(principal,#providerId)")
	@RequestMapping("/provider/{providerId}/evento/listaEventiLookup")
	@ResponseBody
	public Set<Evento> getListaEventiForLookup(@PathVariable Long providerId){
		Set<Evento> eventi = eventoService.getAllEventiForProviderId(providerId);
		return eventi;
	}

	private String goToEventoList(HttpServletRequest request, Model model) {
		LOGGER.info(Utils.getLogMessage("VIEW: "+LIST));
		//tasto indietro
		String returnLink = request.getRequestURI().substring(request.getContextPath().length());
	    if(request.getQueryString() != null)
	    	returnLink+="?"+request.getQueryString();
	    model.addAttribute("returnLink", returnLink);
		return LIST;
	}

	//update dell'evento modificato nella lista in sessione
	private void updateEventoList(Long eventoId, HttpSession session, boolean rimozione, boolean updateRiedizioni) {
		Collection<Evento> eventoList = (Collection<Evento>) session.getAttribute("eventoList");

		Evento eventoToUpdate = eventoService.getEvento(eventoId);
		if(eventoList != null && eventoToUpdate != null) {
			//se il provider è nella lista in sessione la aggiorna
			eventoList.remove(eventoToUpdate);
			if(rimozione == false)
				eventoList.add(eventoToUpdate);
			if(updateRiedizioni) {
				Set<Evento> riedizioni = eventoService.getRiedizioniOfEventoId(eventoId);
				for(Evento ev : riedizioni)
					updateEventoList(ev.getId(), session, false, true);
			}
			session.setAttribute("eventoList", eventoList);
		}
	}

	private void updateEventoList(Long eventoId, HttpSession session) {
		updateEventoList(eventoId, session, false, false);
	}

	@RequestMapping(value="/provider/{providerId}/evento/{eventoId}/updateOrari", method=RequestMethod.POST)
	   public String updateOrari(@ModelAttribute("eventoWrapper") EventoWrapper eventoWrapper,
			   Model model, RedirectAttributes redirectAttrs,@RequestParam("eventoWrapper_cId") String cIdWrapper,
			   @RequestBody ModificaOrarioAttivitaWrapper jsonObj) {
		try{
			boolean ok = eventoService.updateOrariAttivita(jsonObj, eventoWrapper);
			//non posso spostare le date perchè scavalcano la fine della giornata
			if(!ok)
				model.addAttribute("erroreSpostamentoOrario", true);
			return EDIT + " :: attivitaRES";
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return EDIT + " :: attivitaRES";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowAllEventiProvider(principal, #providerId)")
	@RequestMapping(value="/provider/{providerId}/evento/{eventoId}/quietanzaPage", method=RequestMethod.GET)
	public String quietanzaPage(@PathVariable("providerId") Long providerId, @PathVariable("eventoId") Long eventoId,
								Model model, RedirectAttributes redirectAttrs, HttpServletRequest request) {
		try{
			Evento evento = eventoService.getEvento(eventoId);
			model.addAttribute("quietanzaWrapper", new QuietanzaWrapper());
			model.addAttribute("evento", evento);
			model.addAttribute(eventoService.getPagamentoForQuietanza(evento));
			return PAGAMENTOQUIETANZA;
		}catch (Exception ex){
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			LOGGER.error(Utils.getLogMessage(ex.getMessage()),ex);
			return goToEventoList(request, model);
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowAllEventiProvider(principal, #providerId)")
	@RequestMapping(value= "/provider/{providerId}/evento/{eventoId}/quietanzaPagamento/save", method = RequestMethod.POST)
	public String allegaQuietanzaPagamentoEvento(@PathVariable Long providerId, @PathVariable Long eventoId,
													@ModelAttribute QuietanzaWrapper quietanzaWrapper, Model model, RedirectAttributes redirectAttrs,
													HttpServletRequest request, HttpSession session){
		LOGGER.info(Utils.getLogMessage("POST /provider/"+providerId+"/evento/"+eventoId+"/quietanzaPagamento/save"));
		try {
			File quietanzaPagamento = quietanzaWrapper.getQuietanzaPagamento();
			if(quietanzaPagamento == null || quietanzaPagamento.isNew()) {
				//errore validazione personalizzato
				quietanzaWrapper.setSubmitError(true);
				model.addAttribute("quietanzaWrapper", quietanzaWrapper);
				Evento evento = eventoService.getEvento(eventoId);
				model.addAttribute("evento", evento);
				model.addAttribute(eventoService.getPagamentoForQuietanza(evento));
				return PAGAMENTOQUIETANZA;
			}
			else {
				eventoService.salvaQuietanzaPagamento(quietanzaPagamento, eventoId);
				redirectAttrs.addFlashAttribute("message", new Message("message.completato", "message.quietanza_pagamento_salvata", "success"));
				updateEventoList(eventoId, session);
				return "redirect:"+quietanzaWrapper.getReturnLink();
			}
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET " + LIST),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/provider/"+providerId+"/evento/list";
		}
	}

	@PreAuthorize("@securityAccessServiceImpl.canShowAllEventiProvider(principal, #providerId)")
	@RequestMapping(value= "/provider/{providerId}/evento/{eventoId}/quietanzaPagamento/show", method = RequestMethod.GET)
	public String showQuietanzaPagamentoEvento(@PathVariable Long providerId, @PathVariable Long eventoId,
												Model model, RedirectAttributes redirectAttrs, HttpServletRequest request){
		LOGGER.info(Utils.getLogMessage("GET /provider/"+providerId+"/evento/"+eventoId+"/quietanzaPagamento/show"));
		try {
			Long fileId = eventoService.getFileQuietanzaId(eventoId);
			if(fileId == null) {
				model.addAttribute("message", new Message("message.errore", "message.quietanza_non_presente", "error"));
				return goToEventoList(request, model);
			}
			else {
				return "redirect:/file/"+fileId;
			}
		}
		catch (Exception ex) {
			LOGGER.error(Utils.getLogMessage("GET " + LIST),ex);
			redirectAttrs.addFlashAttribute("message", new Message("message.errore", "message.errore_eccezione", "error"));
			return "redirect:/provider/"+providerId+"/evento/list";
		}
	}

}
