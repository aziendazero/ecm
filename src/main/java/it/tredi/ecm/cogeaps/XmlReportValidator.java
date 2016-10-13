package it.tredi.ecm.cogeaps;

import java.io.StringReader;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.Validator;

public class XmlReportValidator {

	public static void validateXml(byte []xml, Schema schema) throws Exception {
	    Validator validator = schema.newValidator();
	    Source source = new StreamSource(new StringReader(new String(xml, Helper.XML_REPORT_ENCODING)));
		validator.validate(source);
	}

}
