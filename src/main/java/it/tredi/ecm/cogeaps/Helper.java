package it.tredi.ecm.cogeaps;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoRES;

public class Helper {
	
	public final static String XML_REPORT_ENCODING = "ISO-8859-1";
	
	public final static String []EVENTO_XML_ATTRIBUTES = {"cod_evento", "cod_edi", "cod_org", "cod_accr", "data_ini", "data_fine", "ore", "crediti", "tipo_form", "tipo_eve", "cod_obi", "num_part", "cod_tipologia_form"};
	
	//TODO - gestire eccezioni
	
	public static Map<String, String> createEventoDataMapFromEvento(Evento evento) {
		Map<String, String> dbEventoDataMap = new HashMap<String, String>();
		dbEventoDataMap.put("cod_evento", Long.toString(evento.getId()));
		dbEventoDataMap.put("cod_edi", Integer.toString(evento.getEdizione()));
		dbEventoDataMap.put("cod_org", Long.toString(evento.getProvider().getCodiceIdentificativoUnivoco()));
		dbEventoDataMap.put("cod_accr", "050");
		dbEventoDataMap.put("data_ini", evento.getDataInizio().format(DateTimeFormatter.ISO_LOCAL_DATE));
		dbEventoDataMap.put("data_fine", evento.getDataFine().format(DateTimeFormatter.ISO_LOCAL_DATE));
		dbEventoDataMap.put("ore", Integer.toString((int)evento.getDurata()));
		dbEventoDataMap.put("crediti", Float.toString(evento.getCrediti()));
		dbEventoDataMap.put("tipo_form", Integer.toString(evento.getProceduraFormativa().getId()));
		dbEventoDataMap.put("tipo_eve", "E");
		dbEventoDataMap.put("cod_obi", evento.getObiettivoNazionale().getCodiceCogeaps());
		dbEventoDataMap.put("num_part", Integer.toString(evento.getNumeroPartecipanti()));
		String cod_tipologia_form = "-1";
		if (evento instanceof EventoFSC) { //FSC
			cod_tipologia_form = Integer.toString(((EventoFSC)evento).getTipologiaEvento().getId());
		}
		else if (evento instanceof EventoRES) { //RES
			cod_tipologia_form = Integer.toString(((EventoRES)evento).getTipologiaEvento().getId());
		}
		else if (evento instanceof EventoFAD) { //FAD
			cod_tipologia_form = ((EventoFAD)evento).isSupportoSvoltoDaEsperto()? "11" : "10";
		}
		dbEventoDataMap.put("cod_tipologia_form", cod_tipologia_form);				
		return dbEventoDataMap;
	}
	
	public static Schema getSchemaEvento_1_16_XSD() throws Exception {
		InputStream is = Helper.class.getResourceAsStream("rapporto_evento_1.16.xsd");
		Source source = new StreamSource(is);
		SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = factory.newSchema(source);
		return schema;
	}
	
	public static String createReportXmlFileName() {
		return "report-" + new SimpleDateFormat("yyyyMMdd-HHmm").format(new Date()) +  ".xml";
	}

}
