package it.tredi.ecm.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.cogeaps.Helper;
import it.tredi.ecm.cogeaps.XmlReportBuilder;
import it.tredi.ecm.cogeaps.XmlReportValidator;
import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.enumlist.FileEnum;
import it.tredi.ecm.dao.repository.EventoRepository;
import it.tredi.ecm.web.bean.EventoWrapper;
import it.tredi.ecm.exception.EcmException;

@Service
public class EventoServiceImpl implements EventoService {
	public static final Logger LOGGER = Logger.getLogger(Evento.class);

	@Autowired
	private EventoRepository eventoRepository;

	@Autowired
	private FileService fileService;
	
	@Override
	public Evento getEvento(Long id) {
		LOGGER.debug("Recupero evento: " + id);
		return eventoRepository.findOne(id);
	}

	@Override
	public Set<Evento> getAllEventiFromProvider(Long providerId) {
		LOGGER.debug("Recupero eventi del provider: " + providerId);
		return eventoRepository.findAllByProviderId(providerId);
	}

	@Override
	@Transactional
	public void save(Evento evento) {
		LOGGER.debug("Salvataggio evento");
		if(evento.isNew()) {
			eventoRepository.saveAndFlush(evento);
			evento.buildPrefix();
		}
		eventoRepository.save(evento);
	}

	@Override
	@Transactional
	public void delete(Long id) {
		LOGGER.debug("Eliminazione evento:" + id);
		eventoRepository.delete(id);
	}

	@Override
	public void validaRendiconto(Long id, File rendiconto) throws Exception {
		Evento evento = getEvento(id);
		
		String fileName = rendiconto.getNomeFile();		
		if (fileName.trim().toUpperCase().endsWith(".CSV")) { //CSV -> produzione XML
			rendiconto.setTipo(FileEnum.FILE_REPORT_PARTECIPANTI_CSV);
			evento.setReportPartecipantiCSV(rendiconto);

			//produzione xml da csv
			byte []xml_b = null;
			try {
				xml_b = XmlReportBuilder.buildXMLReportForCogeaps(rendiconto.getData(), evento);
			}
			catch (Exception e) {
				throw new EcmException("error.csv_to_xml_report_error", e.getMessage(), e);
			}
			
			//xsd validation
			try {
				XmlReportValidator.validateXml(xml_b, Helper.getSchemaEvento_1_16_XSD());	
			}
			catch (Exception e) {
				throw new EcmException("error.xml_validation", e.getMessage(), e);
			}

			//salvo file xml
			File rendicontoXml = new File(FileEnum.FILE_REPORT_PARTECIPANTI_XML);
			rendicontoXml.setNomeFile(Helper.createReportXmlFileName());
			rendicontoXml.setData(xml_b);
			evento.setReportPartecipantiXML(rendicontoXml);
			fileService.save(rendicontoXml);			
		}
		else { //XML, XML.P7M, XML.ZIP.P7M
			evento.setReportPartecipantiCSV(null);
			rendiconto.setTipo(FileEnum.FILE_REPORT_PARTECIPANTI_XML);
			evento.setReportPartecipantiXML(rendiconto);
			
			//evento validation (rispetto al db)
			
			//xsd validation
			try {			
				XmlReportValidator.validateXml(rendiconto.getData(), Helper.getSchemaEvento_1_16_XSD());
			}
			catch (Exception e) {
				throw new EcmException("error.xml_validation", e.getMessage(), e);
			}
		}
		save(evento);
	}

	@Override
	public List<Evento> getAllEventi() {
		LOGGER.debug("Recupero tutti gli eventi");
		return eventoRepository.findAll();
	}

	@Override
	public Set<Evento> getAllEventiForProviderId(Long providerId) {
		LOGGER.debug("Recupero tutti gli eventi del provider: " + providerId);
		return eventoRepository.findAllByProviderId(providerId);
	}

	@Override
	public boolean canCreateEvento(Account account) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Evento handleRipetibili(EventoWrapper eventoWrapper) {
		Evento evento = eventoWrapper.getEvento();

		//date intermedie
		if (eventoWrapper.getDateIntermedieTemp() != null && !eventoWrapper.getDateIntermedieTemp().isEmpty()) {
			Set<LocalDate> dateIntermedie = ((EventoRES) evento).getDateIntermedie();
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
			for (String s : eventoWrapper.getDateIntermedieTemp()) {
				if(s != null && !s.isEmpty()) {
					LocalDate data = LocalDate.parse(s, dtf);
					dateIntermedie.add(data);
				}
			}
			((EventoRES) evento).setDateIntermedie(dateIntermedie);
		}

		return evento;
	}

}
