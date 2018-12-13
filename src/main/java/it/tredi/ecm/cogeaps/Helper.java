package it.tredi.ecm.cogeaps;

import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.Professione;
import it.tredi.ecm.dao.enumlist.DestinatariEventoEnum;
import it.tredi.ecm.pdf.PdfPartecipanteInfo;
import it.tredi.ecm.pdf.PdfRiepilogoPartecipantiInfo;
import it.tredi.ecm.service.DisciplinaService;

public class Helper {

	public final static String XML_REPORT_ENCODING = "ISO-8859-1";
	public final static String XSD_1_1_16_FILENAME = "rapporto_evento_1.16.xsd";

	public final static String CODICE_ENTE_ACCREDITANTE = "050";
	public final static String []EVENTO_XML_ATTRIBUTES = {"cod_evento", "cod_edi", "cod_org", "cod_accr", "data_ini", "data_fine", "ore", "crediti", "tipo_form", "tipo_eve", "cod_obi", "num_part", "cod_tipologia_form"};
	public final static String []PARTECIPANTE_XML_ATTRIBUTES = {"cod_fisc", "cognome", "nome", "ruolo", "cred_acq", "data_acq"};//solo quelli che servono per salvare le info anagrafe regionali crediti
	public final static String PARTECIPANTE_DATA_FORMAT = "yyyy-MM-dd";

	public final static String PARTECIPANTE_NODE_NAME = "partecipante";
	public final static String PROFESSIONE_NODE_NAME = "professione";
	public final static String DISCIPLINE_NODE_NAME = "disciplina";

	@Autowired private DisciplinaService disciplinaService;

	//TODO - gestire eccezioni

	public static Set<String> createCodProfessioneSetFromEvento(Evento evento){
		Set<String> codProf = new HashSet<String>();
		Set<Professione> professioni = evento.getProfessioniSelezionate();

		for(Professione p : professioni) {
			if(p.getCodiceCogeaps() != null && !p.getCodiceCogeaps().isEmpty())
				codProf.add(p.getCodiceCogeaps());
		}

		return codProf;
	}

	public static Set<String> createCodDisciplinaSetFromEvento(Evento evento){
		Set<String> codDisc = new HashSet<String>();
		Set<Disciplina> discipline = evento.getDiscipline();

		for(Disciplina d : discipline) {
			if(d.getCodiceCogeaps() != null && !d.getCodiceCogeaps().isEmpty())
				codDisc.add(d.getCodiceCogeaps());
		}

		return codDisc;
	}

	public static Map<String, String> createEventoDataMapFromEvento(Evento evento) {
		Map<String, String> dbEventoDataMap = new HashMap<String, String>();
		dbEventoDataMap.put("cod_evento", evento.getPrefix());
		dbEventoDataMap.put("cod_edi", Integer.toString(evento.getEdizione()));
		dbEventoDataMap.put("cod_org", evento.getProvider().getCodiceCogeaps());
		dbEventoDataMap.put("cod_accr", CODICE_ENTE_ACCREDITANTE);
		dbEventoDataMap.put("data_ini", evento.getDataInizio().format(DateTimeFormatter.ISO_LOCAL_DATE));
		dbEventoDataMap.put("data_fine", evento.getDataFine().format(DateTimeFormatter.ISO_LOCAL_DATE));
		//tiommi 22/05/2017 | dpranteda 12/12/2018
		BigDecimal durataRounded = new BigDecimal(Float.toString(evento.getDurata())).setScale(0, RoundingMode.HALF_UP);
		dbEventoDataMap.put("ore", durataRounded.toString());
		//end
		dbEventoDataMap.put("crediti", Float.toString(evento.getCrediti()));
		dbEventoDataMap.put("tipo_form", Integer.toString(evento.getProceduraFormativa().getId()));
		//tiommi 22/05/2017
		Set<DestinatariEventoEnum> destinatariEvento = evento.getDestinatariEvento();
		if((destinatariEvento.contains(DestinatariEventoEnum.PERSONALE_CONVENZIONATO) ||
				destinatariEvento.contains(DestinatariEventoEnum.PERSONALE_DIPENDENTE)) &&
				!destinatariEvento.contains(DestinatariEventoEnum.ALTRO_PERSONALE)) {
			dbEventoDataMap.put("tipo_eve", "P");
		}
		else
			dbEventoDataMap.put("tipo_eve", "E");
		//end
		dbEventoDataMap.put("cod_obi", evento.getObiettivoNazionale().getCodiceCogeaps());
		dbEventoDataMap.put("num_part", Integer.toString(evento.getNumeroPartecipanti()));
		String cod_tipologia_form = "-1";
		if (evento instanceof EventoFSC) { //FSC
			//tiommi 15/06/2017
			//old -> cod_tipologia_form = Integer.toString(((EventoFSC)evento).getTipologiaEventoFSC().getId());
			//new dpranteda 21/08/2017: ripristinato il meccanismo in quanto sono stati modificati direttamente gli id nel enum
			cod_tipologia_form = Integer.toString(((EventoFSC)evento).getTipologiaEventoFSC().getId());
		}
		else if (evento instanceof EventoRES) { //RES
			//tiommi 22/05/2017
			//old -> cod_tipologia_form = Integer.toString(((EventoRES)evento).getTipologiaEventoRES().getId());
			//new dpranteda 21/08/2017: ripristinato il meccanismo in quanto sono stati modificati direttamente gli id nel enum
			cod_tipologia_form = Integer.toString(((EventoRES)evento).getTipologiaEventoRES().getId());
			//end
		}
		else if (evento instanceof EventoFAD) { //FAD
			cod_tipologia_form = ((EventoFAD)evento).getSupportoSvoltoDaEsperto()? "11" : "10";
		}
		dbEventoDataMap.put("cod_tipologia_form", cod_tipologia_form);
		return dbEventoDataMap;
	}

	public static Schema getSchemaEvento_1_16_XSD() throws Exception {
		InputStream is = Helper.class.getResourceAsStream(XSD_1_1_16_FILENAME);
		Source source = new StreamSource(is);
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = factory.newSchema(source);
		return schema;
	}

	public static String createReportXmlFileName() {
		return "report-" + new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()) +  ".xml";
	}

	//crea la lista di oggetti utilizzati per estrapolare i dati relativi al partecipante
	public static PdfRiepilogoPartecipantiInfo extractRiepilogoPartecipantiFromXML(Document xmlDoc, boolean withDocenti, Map<String,String> professioniMap, Map<String,String> disciplineMap) throws Exception {
		PdfRiepilogoPartecipantiInfo pdfInfo = new PdfRiepilogoPartecipantiInfo();
		Element eventoEl = xmlDoc.getRootElement().element("evento");
		List<Element> partecipantiEl = eventoEl.elements(PARTECIPANTE_NODE_NAME);

		if(withDocenti){
			//prendo tutti
			for(Element partecipanteEl : partecipantiEl) {
				PdfPartecipanteInfo pdfPartecipanteInfo = extractPartecipanteFromXML(partecipanteEl, professioniMap, disciplineMap);
				pdfInfo.getPartecipanti().add(pdfPartecipanteInfo);
			}
		}else{
			//prendo solo i partecipanti e non i docenti
			for(Element partecipanteEl : partecipantiEl) {
				if(partecipanteEl.attributeValue("ruolo", "").equalsIgnoreCase("P")){
					PdfPartecipanteInfo pdfPartecipanteInfo = extractPartecipanteFromXML(partecipanteEl, professioniMap, disciplineMap);
					pdfInfo.getPartecipanti().add(pdfPartecipanteInfo);
				}
			}
		}

		return pdfInfo;
	}

	//crea l'oggetto utilizzato per stampare i dati del partecipante nel pdf
	private static PdfPartecipanteInfo extractPartecipanteFromXML(Element partecipanteEl, Map<String,String> professioniMap, Map<String,String> disciplineMap) throws Exception {
		PdfPartecipanteInfo partecipante = new PdfPartecipanteInfo();
		partecipante.setNome(partecipanteEl.attributeValue("nome"));
		partecipante.setCognome(partecipanteEl.attributeValue("cognome"));
		partecipante.setCodiceFiscale(partecipanteEl.attributeValue("cod_fisc").toUpperCase());
		if(Integer.parseInt(partecipanteEl.attributeValue("part_reclutato")) == 1)
			partecipante.setReclutato("SI");
		else partecipante.setReclutato("NO");
		partecipante.setSponsor(partecipanteEl.attributeValue("sponsor"));
		switch(partecipanteEl.attributeValue("ruolo")) {
			case "P": partecipante.setTipologiaPartecipante("PARTECIPANTE"); break;
			case "D": partecipante.setTipologiaPartecipante("DOCENTE"); break;
			case "T": partecipante.setTipologiaPartecipante("TUTOR"); break;
			case "R": partecipante.setTipologiaPartecipante("RELATORE"); break;
			default: partecipante.setTipologiaPartecipante("N/D"); break;
		}
		partecipante.setNumeroCrediti(partecipanteEl.attributeValue("cred_acq"));
		partecipante.setDataCreditiAcquisiti(partecipanteEl.attributeValue("data_acq"));
		List<Element> professioniEl = partecipanteEl.elements(PROFESSIONE_NODE_NAME);

		/*
		//prende il nome della professione dal file professioni.xml come l'XSLT resolver e lo salvo in una mappa
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	    String xmlFileUri = classLoader.getResource("xsltResolver/professioni.xml").getPath();
	    Path xmlFilePath = Paths.get(xmlFileUri);
		byte[] xmlFileData = Files.readAllBytes(xmlFilePath);
		Document docProfessioni = DocumentHelper.parseText(new String(xmlFileData, XML_REPORT_ENCODING));
		Element allProfessioniEl = docProfessioni.getRootElement();
		List<Element> allProfessioniListEl = allProfessioniEl.elements("p");
		Map<String, String> mappaConversione = new HashMap<String, String>();
		for(Element p : allProfessioniListEl) {
			mappaConversione.put(p.attributeValue("id"), p.getText());
		}
		for(Element professioneEl : professioniEl) {
			String nomeProfessione = mappaConversione.get(professioneEl.attributeValue("cod_prof"));
			if(nomeProfessione != null)
				partecipante.getProfessioni().add(nomeProfessione.toUpperCase());
		}
		*/

		/* nuovo metodo che tiene conto anche delle discipline */
		for(Element professioneEl : professioniEl) {
			String nomeProfessione = professioniMap.get(professioneEl.attributeValue("cod_prof"));
			if(nomeProfessione != null) {
				List<Element> disciplineEl = professioneEl.elements(DISCIPLINE_NODE_NAME);
				Set<String> disciplineList = new HashSet();
				for(Element disciplinaEl : disciplineEl) {
					disciplineList.add(disciplineMap.get(disciplinaEl.getText()));
				}

				partecipante.getProfessioni_discipline().put(nomeProfessione, disciplineList);
			}else {
				throw new Exception("Professione non trovata!");
			}
		}

		return partecipante;
	}

}
