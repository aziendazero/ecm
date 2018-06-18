package it.tredi.ecm.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.tredi.ecm.cogeaps.Helper;
import it.tredi.ecm.cogeaps.XmlReportValidator;
import it.tredi.ecm.dao.entity.AnagrafeRegionaleCrediti;
import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.repository.AnagrafeRegionaleCreditiRepository;
import it.tredi.ecm.dao.repository.DisciplinaRepository;
import it.tredi.ecm.utils.Utils;

@Service
public class AnagrafeRegionaleCreditiServiceImpl implements AnagrafeRegionaleCreditiService {

	private static Logger LOGGER = LoggerFactory.getLogger(AnagrafeRegionaleCreditiServiceImpl.class);

	@Autowired private AnagrafeRegionaleCreditiRepository anagrafeRegionaleCreditiRepository;
	@Autowired private DisciplinaRepository disciplinaRepository;

	@Override
	public Set<Integer> getAnnoListForAnagrafeRegionaleCrediti() {
		LOGGER.debug(Utils.getLogMessage("Recupero lista anni disponibili"));
		Set<Integer> annoList = new HashSet<Integer>();

		//data minima presente
		LocalDate minDate = anagrafeRegionaleCreditiRepository.getMinData();

		//creo un elenco dall'anno minimo fino all'anno corrente
		if(minDate != null)
			for(int a = minDate.getYear(); a <= LocalDate.now().getYear(); a++)
				annoList.add(a);

		return annoList;
	}

	@Override
	public Set<AnagrafeRegionaleCrediti> getAll(Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage("Recupero tutte le anagrafe regionali crediti ECM per l'anno: " + annoRiferimento));
		Set<AnagrafeRegionaleCrediti> result = new HashSet<AnagrafeRegionaleCrediti>();

		List<Object[]> items = anagrafeRegionaleCreditiRepository.findDistinctAll(LocalDate.of(annoRiferimento, 1, 1), LocalDate.of(annoRiferimento, 12, 31));
		for(Object[] obj : items)
			result.add(new AnagrafeRegionaleCrediti((String) obj[0], (String) obj[1], (String) obj[2], null, null, null, null));

		return result;
	}

	@Override
	public Set<AnagrafeRegionaleCrediti> getAllByCodiceFiscale(String codiceFiscale, Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage("Recupero anagrafe regionali crediti ECM: " + codiceFiscale + " per l'anno " + annoRiferimento));
		return anagrafeRegionaleCreditiRepository.findDistinctAllByCodiceFiscale(codiceFiscale, LocalDate.of(annoRiferimento, 1, 1), LocalDate.of(annoRiferimento, 12, 31));
	}

	@Override
	public BigDecimal getSumCreditiByCodiceFiscale(String codiceFiscale, Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage("Recupero somma crediti anagrafe regionali: " + codiceFiscale + " per l'anno " + annoRiferimento));
		BigDecimal somma = new BigDecimal(0);
		somma = anagrafeRegionaleCreditiRepository.getSumCreditiByCodiceFiscale(codiceFiscale, LocalDate.of(annoRiferimento, 1, 1), LocalDate.of(annoRiferimento, 12, 31));
		return somma;
	}

	/*
	 *Faccio una query per individuare tutti i ruoli che hanno ricevuto crediti contando il numero di persone distinte.
	 *Ottengo per ciascun ruolo quante persone hanno avuto crediti
	 *
	 * dpranteda 18/06/2018: rimossa clausula distinct
	 **/
	@Override
	public Map<String,Integer> getRuoliAventeCreditiPerAnno(Long providerId, Integer annoRiferimento) {
		LOGGER.debug(Utils.getLogMessage("Recupero ruoli con conteggio di persone distinte che hanno avuto crediti nell'anno: " + annoRiferimento + " per il provider " + providerId));
		Map<String,Integer> ruoli = new HashMap<String, Integer>();

		List<Object[]> result = anagrafeRegionaleCreditiRepository.getRuoliAventeCreditiPerAnno(providerId, LocalDate.of(annoRiferimento, 1, 1), LocalDate.of(annoRiferimento, 12, 31));
		for(Object[] obj : result)
			ruoli.put((String) obj[0], Integer.valueOf((int)(long)obj[1]));

		return ruoli;
	};

	@Override
	public int getProfessioniAnagrafeAventeCrediti(Long providerId, Integer annoRiferimento) {
		return anagrafeRegionaleCreditiRepository.getProfessioniAnagrafeAventeCrediti(providerId, LocalDate.of(annoRiferimento, 1, 1), LocalDate.of(annoRiferimento, 12, 31));
	}

//	@Override
//	public Set<Evento> getEventiAnagrafeAventeCrediti(Long providerId, Integer annoRiferimento){
//		LOGGER.debug(Utils.getLogMessage("Recupero eventi che hanno avuto crediti nell'anno: " + annoRiferimento + " per il provide " + providerId));
//		return anagrafeRegionaleCreditiRepository.getEventiAnagrafeAventeCrediti(providerId, LocalDate.of(annoRiferimento, 1, 1), LocalDate.of(annoRiferimento, 12, 31));
//	}

	@Override
	public Set<AnagrafeRegionaleCrediti> extractAnagrafeRegionaleCreditiPartecipantiFromXml(String fileName, byte []reportEventoXml) throws Exception {
		//estrazione xml
    	Set<AnagrafeRegionaleCrediti> items = new HashSet<AnagrafeRegionaleCrediti>();
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Helper.PARTECIPANTE_DATA_FORMAT);

		reportEventoXml = XmlReportValidator.extractXml(fileName, reportEventoXml);

		//ora siamo sicuri di avere un XML
		Document xmlDoc = DocumentHelper.parseText(new String(reportEventoXml, Helper.XML_REPORT_ENCODING));
		Element eventoEl = xmlDoc.getRootElement().element("evento");

		List<Element> partecipanti = eventoEl.elements("partecipante");
		Set<Disciplina> discipline;
		Disciplina disciplina;
		for (Element p : partecipanti) {
			discipline = new HashSet<Disciplina>();
			List<Element> professioni = p.elements("professione");
			for (Element prof : professioni) {
				List<Element> disciplineElem = prof.elements("disciplina");
				for (Element disc : disciplineElem) {
					String codiceCogeaps = disc.getStringValue();
					//disciplina = new Disciplina();
					//disciplina.setId(Long.decode(disciplinaId));
					disciplina = disciplinaRepository.findOneByCodiceCogeaps(codiceCogeaps);
					discipline.add(disciplina);
				}

			}

			items.add(new AnagrafeRegionaleCrediti(p.attributeValue(Helper.PARTECIPANTE_XML_ATTRIBUTES[0]),//cod_fisc
													p.attributeValue(Helper.PARTECIPANTE_XML_ATTRIBUTES[1]),//cognome
													p.attributeValue(Helper.PARTECIPANTE_XML_ATTRIBUTES[2]),//nome
													p.attributeValue(Helper.PARTECIPANTE_XML_ATTRIBUTES[3]),//ruolo
													new BigDecimal(p.attributeValue(Helper.PARTECIPANTE_XML_ATTRIBUTES[4])),//cred_acq
													LocalDate.parse(p.attributeValue(Helper.PARTECIPANTE_XML_ATTRIBUTES[5]), formatter), discipline));//data_acq
		}

		return items;
	}
}
