package it.tredi.ecm.cogeaps;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import org.apache.commons.csv.CSVFormat;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Document;
import org.dom4j.io.XMLWriter;
import it.tredi.ecm.dao.entity.Evento;
import org.dom4j.io.OutputFormat;
import org.apache.commons.csv.CSVRecord;
import java.util.HashMap;
import java.util.Map;

/**
 * Questa classe gestisce la produzione dell'XML per il Cogeaps a partire dal CSV fornito dal provider e dall'entity evento caricata dal db
 * @author sstagni
 *
 */
public class XmlReportBuilder {

//TODO - fare gestione errori più evoluta
	
	public final static String CSV_REPORT_ENCODING = "CP1252";
	private final static String []CSV_FIELDS = {"cod_fisc", "nome", "cognome", "ruolo", "lib_dip", "cred_acq", "data_acq", "part_reclutato", "sponsor", "cod_prof.disciplina"};
	private final static String CSV_COD_PROF_DISCIPLINA_FIELD = "cod_prof.disciplina";
	
	public static byte []buildXMLReportForCogeaps(byte []csv, Evento evento) throws Exception {
		Map<String, String> dbEventoDataMap = Helper.createEventoDataMapFromEvento(evento);
		Document document = buildXMLReportForCogeaps(csv, dbEventoDataMap);
		//pretty print
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
	    OutputFormat format = OutputFormat.createPrettyPrint();
	    format.setEncoding(Helper.XML_REPORT_ENCODING);
	    XMLWriter writer = new XMLWriter(bos, format);
        writer.write(document);
        return bos.toByteArray();
	}	
	
	private static Document buildXMLReportForCogeaps(byte []csv, Map<String, String> dbEventoDataMap) throws Exception {
		//dati evento
		Element datarootEl = DocumentHelper.createElement("dataroot");
		Document xmlDoc = DocumentHelper.createDocument(datarootEl);
		xmlDoc.setXMLEncoding(Helper.XML_REPORT_ENCODING);
		Element eventoEl = DocumentHelper.createElement("evento");
		datarootEl.add(eventoEl);
		handleEventoEl(eventoEl, dbEventoDataMap, Helper.EVENTO_XML_ATTRIBUTES);
		
		//parsing CSV e dati partecipanti
		Iterable<CSVRecord> records = CSVFormat.EXCEL.withDelimiter(';').withFirstRecordAsHeader().parse(new StringReader(new String(csv, CSV_REPORT_ENCODING)));
		for (CSVRecord record : records) {
		    Map<String, String> map = buildRowMapFromCSVRow(record, CSV_FIELDS);
		    Element partecipanteEl = DocumentHelper.createElement("partecipante");
		    eventoEl.add(partecipanteEl);
		    handlePartecipanteEl(partecipanteEl, map, CSV_FIELDS);
		}
		return xmlDoc;
	}	
	
	private static Map<String, String> buildRowMapFromCSVRow(CSVRecord record, String []fields_arr) {
		Map<String, String> map = new HashMap<>();
		for (int i=0; i<fields_arr.length; i++) {
			String key =  fields_arr[i];
			String value = record.get(key);
			if (value == null)
				value = record.get(i);
			map.put(key, value);
		}
		return map;
	}

	private static void handlePartecipanteEl(Element partecipanteEl, Map<String, String> map, String []fields_arr) {
		for (String key:fields_arr) {
			String value = map.get(key).trim();
			if (value.length() > 0) {
				if (key.equals(CSV_COD_PROF_DISCIPLINA_FIELD)) { //campo multistanza con professioni e discipline
					String []professioni = value.split(":");
					for (String professione_disciplina:professioni) {
					    Element professioneEl = DocumentHelper.createElement("professione");
					    partecipanteEl.add(professioneEl);
					    String []professione_disciplina_arr = professione_disciplina.split("\\.");
					    professioneEl.addAttribute("cod_prof", professione_disciplina_arr[0]);					    
					    for (int i=1; i<professione_disciplina_arr.length; i++) {
						    Element disciplinaEl = DocumentHelper.createElement("disciplina");
						    professioneEl.add(disciplinaEl);
						    disciplinaEl.setText(professione_disciplina_arr[i]);
					    }
					}
				}
				else
					partecipanteEl.addAttribute(key,  value);
			}
		}
	}
	
	private static void handleEventoEl(Element eventoEl, Map<String, String> map, String []fields_arr) {
		for (String key:fields_arr) {
			String value = map.get(key).trim();
			if (value.length() > 0) {
				eventoEl.addAttribute(key,  value);
			}
		}
	}

}
