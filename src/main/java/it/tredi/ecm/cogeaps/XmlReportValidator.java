package it.tredi.ecm.cogeaps;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipInputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import it.tredi.ecm.dao.entity.Disciplina;
import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.Professione;


public class XmlReportValidator {

	public static void validateXmlWithXsd(String fileName, byte []reportEventoXml, Schema schema, Evento evento) throws Exception {
		//estrazione xml
		reportEventoXml = extractXml(fileName, reportEventoXml);

	    Validator validator = schema.newValidator();
	    Source source = new StreamSource(new StringReader(new String(reportEventoXml, Helper.XML_REPORT_ENCODING)));
		validator.validate(source);

		//ora siamo sicuri di avere un XML
		Document xmlDoc = DocumentHelper.parseText(new String(reportEventoXml, Helper.XML_REPORT_ENCODING));
		Element eventoEl = xmlDoc.getRootElement().element("evento");

		validateProfessioniAndDiscipline(eventoEl, Helper.createCodProfessioneSetFromEvento(evento), Helper.createCodDisciplinaSetFromEvento(evento));
	}

	public static void validateEventoXmlWithDb(String fileName, byte []reportEventoXml, Evento evento) throws Exception {
		validateEventoXmlWithDb(fileName, reportEventoXml, Helper.createEventoDataMapFromEvento(evento), Helper.createCodProfessioneSetFromEvento(evento), Helper.createCodDisciplinaSetFromEvento(evento));
	}

	private static void validateEventoXmlWithDb(String fileName, byte []reportEventoXml, Map<String, String> dbEventoDataMap, Set<String> codProfessioneSetFromEvento, Set<String> codDisciplinaSetFromEvento) throws Exception {
		//estrazione xml
		reportEventoXml = extractXml(fileName, reportEventoXml);

		//ora siamo sicuri di avere un XML
		Document xmlDoc = DocumentHelper.parseText(new String(reportEventoXml, Helper.XML_REPORT_ENCODING));
		Element eventoEl = xmlDoc.getRootElement().element("evento");
		//FIXME workaround controllo caso RES - CORSO DI AGGIORNAMENTO che può accettare valori 1 o 5 in base a parametri non gestiti dall'applicazione!
		//controllo da DB che sono nel caso RES - CORSO DI AGGIORNAMENTO
		boolean isCorsoAggiornamentoRESFromDb = dbEventoDataMap.get("tipo_form").equals("3") && dbEventoDataMap.get("cod_tipologia_form").equals("1");
		for (String evento_field:Helper.EVENTO_XML_ATTRIBUTES) {
			String xmlValue = eventoEl.attributeValue(evento_field);
			String dbValue = dbEventoDataMap.get(evento_field);
			//controllo che i valori corrispondano a meno che non sono nel caso ad hoc descritto nel commento "FIXME workaround"
			if(!evento_field.equalsIgnoreCase("num_part") && (!evento_field.equalsIgnoreCase("cod_tipologia_form") || !isCorsoAggiornamentoRESFromDb)){
				if (!xmlValue.equals(dbValue))
					throw new Exception("I dati dell'evento non corrispondono a quelli memorizzati nel database: [" + evento_field + "]: '" + xmlValue + "' - '" + dbValue + "'");
			}else{
				//vedi commento sopra "FIXME workaround", valori hardcodati!
				//cod_tipologia_form per eventi RES - CORSO DI AGGIORNAMENTO
				if(evento_field.equalsIgnoreCase("cod_tipologia_form")) {
					if(!(xmlValue.equals("1") || xmlValue.equals("5")))
						throw new Exception("I dati dell'evento non corrispondono a quelli memorizzati nel database: [" + evento_field + "]: '" + xmlValue + "' - '1 o 5'");
				}
				//num_part
				else {
					if(Integer.valueOf(xmlValue) > Integer.valueOf(dbValue))
						throw new Exception("I dati dell'evento non corrispondono a quelli memorizzati nel database: [" + evento_field + "]: '" + xmlValue + "' - '" + dbValue + "'");
				}
			}
		}
		validateProfessioniAndDiscipline(eventoEl, codProfessioneSetFromEvento, codDisciplinaSetFromEvento);
	}

	public static void validateProfessioniAndDiscipline(Element eventoEl, Set<String> prof, Set<String> disc) throws Exception {
		List<Element> partecipanti = eventoEl.elements();
		if(partecipanti != null) {
			if(partecipanti.size() > 0) {
				if(prof != null && disc != null) {
					if(prof.size() > 0 && disc.size() > 0) {
						for(Element partecipante : partecipanti) {
							//CONTROLLO SU PROFESSIONI E DISCIPLINE SOLO SU PARTECIPANTI (ruolo=P)
							if(partecipante.attributeValue("ruolo", "").equalsIgnoreCase("P")) {
								List<Element> professioni = partecipante.elements();
								for(Element professione : professioni) {
									if(!prof.contains(professione.attributeValue("cod_prof"))) {
										throw new Exception("Le professioni dell'evento non corrispondono a quelle memorizzate nel database!");
									}
									List<Element> discpline = professione.elements();
									for(Element disciplina : discpline) {
										if(!disc.contains(disciplina.getTextTrim())) {
											throw new Exception("Le discipline dell'evento non corrispondono a quelle memorizzate nel database!");
										}
									}
								}
							}
						}
					}
					else {
						throw new Exception("Non è stata restituita nessuna professione o disciplina dal database");
					}
				}
				else {
					throw new Exception("Errore durante la lettura delle professioni e/o discipline dal database");
				}
			}
			else {
				throw new Exception("Non si trova nessun partecipante nel XML");
			}

		}
		else {
			throw new Exception("Errore durante la lettura dei dati dall'xml");
		}


	}

	public static byte []extractXml(String fileName, byte []data) throws Exception {
		//se P7M -> sbusto
		if (fileName.trim().toUpperCase().endsWith(".P7M"))
			data = extraxtFromP7m(data);

		//se ZIP -> unzip
		if (fileName.trim().toUpperCase().endsWith(".ZIP.P7M"))
			data = extractFromZip(data);

		return data;
	}

	private static byte []extraxtFromP7m(byte []signed_b) throws Exception {
		CMSSignedData csd = new CMSSignedData(signed_b);
		CMSProcessableByteArray cpb = (CMSProcessableByteArray)csd.getSignedContent();
		return (byte[])cpb.getContent();
	}

    private static byte []extractFromZip(byte []zip_b) throws Exception {
		ZipInputStream zin = new ZipInputStream(new ByteArrayInputStream(zip_b));
		zin.getNextEntry(); //si suppone che ci sia un solo file dentro lo zip
		//TODO - mettere controlli di errore? controllare se file e controllare estensione dentro?
		int count;
		final int BUFFER = 2048;
        byte data[] = new byte[BUFFER];
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((count = zin.read(data, 0, BUFFER)) != -1) {
			bos.write(data, 0, count);
		}
		bos.flush();
	    bos.close();
		return bos.toByteArray();
    }

}
