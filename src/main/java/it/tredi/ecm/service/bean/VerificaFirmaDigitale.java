package it.tredi.ecm.service.bean;

import java.security.MessageDigest;
import java.security.Principal;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERUTCTime;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.AttributeTable;
import org.bouncycastle.asn1.cms.CMSAttributes;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.util.Store;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.security.PdfPKCS7;

import it.tredi.ecm.dao.entity.File;

/**
 * Classe di utility per la verifica della firma digitale su un file.
 * All'interno della classe e' stato incluso anche il codice di verifica tramite
 * servizio ActalisVOL di Aruba (mai utilizzato in produzione, andrebbe
 * testato).
 * 
 * @author mbernardini
 */
public class VerificaFirmaDigitale {

	private static final Logger log = Logger.getLogger(VerificaFirmaDigitale.class);

	String fileName = null;
	byte[] content = null;

	SimpleDateFormat sdf = null;

	private static final String COUNTRY_KEY = "C";
	private static final String ORGANIZATION_KEY = "O";
	private static final String SURNAME_KEY = "SURNAME";
	private static final String GIVENNAME_KEY = "GIVENNAME";
	private static final String CN_KEY = "CN";
	private static final String SERIALNUMBER_KEY = "SERIALNUMBER";

	public VerificaFirmaDigitale(String fileName, byte[] fileContent) {
		this.fileName = fileName;
		this.content = fileContent;

		sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z"); // TODO potrebbe essere caricato da file di properties
	}
	
	public VerificaFirmaDigitale(File file) {
		if(file != null){
			this.fileName = file.getNomeFile();
			this.content = file.getData();
	
			sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss z"); // TODO potrebbe essere caricato da file di properties
		}
	}

	/**
	 * Estrazione delle informazioni di firma digitale da file P7M e PDF. Non viene utilizzato alcun servizio esterno per la verifica dei certificati
	 * @return Elemento contenente i dettagli della firma
	 * @throws Exception
	 */
	public Element check() throws Exception {
		// registrazione di BouncyCastle come provider (chiamata necessaria per non ottenere errori in fase di verifica)
		Security.addProvider(new BouncyCastleProvider());
		
		Element verificaFirmaElement = DocumentHelper.createElement("verificaFirma");
		verificaFirmaElement.addAttribute("fileName", fileName);
		
		if (this.fileName.toLowerCase().endsWith(".pdf"))
			verificaFirmaElement = extractSignatureInfoFromPDF(verificaFirmaElement);
		else
			verificaFirmaElement = extractSignatureInfoFromP7M(verificaFirmaElement); // estrazione da P7M
		
		return verificaFirmaElement;
	}
	
	public String getLastSignerCF() throws Exception{
		Element verificaFirmaElement = check();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date start = sdf.parse("1/1/1900");
		String resultCF = "";
		
		for(Object e : verificaFirmaElement.elements()){
			Element cf = ((Element)e).element("cf");
			Element data = ((Element)e).element("signatureDate");
			if(data != null && cf != null){
				Date d = sdf.parse(data.getText());
				if(d.after(start)){
					resultCF = getCodiceFiscaleFromCfElementText(cf.getText());
				}
			}
		}			
		
		return resultCF;
	}
	
	private String getCodiceFiscaleFromCfElementText(String cf) {
		int pos = cf.indexOf("-");
		if(pos == -1)
			pos = cf.indexOf(":");
		if(pos != -1)
			return cf.substring(pos+1);
		return cf;
	}

	/**
	 * Estrazione dei dettagli di firma da file P7M
	 */
	private Element extractSignatureInfoFromP7M(Element verificaFirmaElement) {
		try {
			// se base64, decodifica del file
			if (Base64.isBase64(this.content))
				this.content = Base64.decodeBase64(this.content);
			
			CMSSignedData signature = new CMSSignedData(this.content);
			Store cs = signature.getCertificates();
			SignerInformationStore signers = signature.getSignerInfos();
			Collection<?> c = signers.getSigners();
			Iterator<?> it = c.iterator();

			while (it.hasNext()) {
				try {
					SignerInformation signer = (SignerInformation) it.next();
					Collection<?> certCollection = cs.getMatches(signer.getSID());
					Iterator<?> certIt = certCollection.iterator();
					X509CertificateHolder cert = (X509CertificateHolder) certIt.next();

					Date signatureTime = getSignatureTimeP7M(signer);
					String signTime = "";
					if (signatureTime != null)
						signTime = sdf.format(signatureTime);
					log.debug("VerificaFirmaDigitale.extractSignatureInfoFromP7M(): signTime = " + signTime);

					String certFrom = null;
					if (cert.getNotBefore() != null)
						certFrom = sdf.format(cert.getNotBefore());
					String certTo = null;
					if (cert.getNotAfter() != null)
						certTo = sdf.format(cert.getNotAfter());
					log.debug("VerificaFirmaDigitale.extractSignatureInfoFromP7M(): cert.getNotBefore = " + certFrom + ", cert.getNotAfter = " + certTo);

					X500Name subject = cert.getSubject();
					log.debug("VerificaFirmaDigitale.extractSignatureInfoFromP7M(): subject = " + subject);
					Map<String, String> subjectMap = signInfoToMap(subject.toString());
					X500Name issuer = cert.getIssuer();
					log.debug("VerificaFirmaDigitale.extractSignatureInfoFromP7M(): issuer = " + issuer);
					Map<String, String> issuerMap = signInfoToMap(issuer.toString());

					String signerName = "";
					if (subjectMap.containsKey(CN_KEY))
						signerName = subjectMap.get(CN_KEY);
					else if (subjectMap.containsKey(SURNAME_KEY) && subjectMap.containsKey(GIVENNAME_KEY))
						signerName = subjectMap.get(SURNAME_KEY) + " " + subjectMap.get(GIVENNAME_KEY);
					
					String digestAlg = "";
					try {
						digestAlg = getDigestAlgorithm(signer.getDigestAlgorithmID().getAlgorithm().getId());
					}
					catch (Exception e) {
						log.error("VerificaFirmaDigitale.extractSignatureInfoFromP7M(): can't get digest algorithm... " + e.getMessage(), e);
					}

					addSignerElement(verificaFirmaElement, signerName, subjectMap.get(SERIALNUMBER_KEY),
														subjectMap.get(ORGANIZATION_KEY), subjectMap.get(COUNTRY_KEY),
														issuerMap.get(ORGANIZATION_KEY), signTime, digestAlg, certFrom, certTo);

				} 
				catch (Exception e) {
					log.error("VerificaFirmaDigitale.extractSignatureInfoFromP7M(): got exception on a signer... " + e.getMessage(), e);
				}
			}
		} 
		catch (Exception e) {
			log.error("VerificaFirmaDigitale.extractSignatureInfoFromP7M(): got exception... " + e.getMessage(), e);

			Element errorEl = verificaFirmaElement.addElement("error");
			errorEl.addCDATA("Riscontrato errore nella verifica della firma: " + e.getMessage());
		}

		return verificaFirmaElement;
	}

	/**
	 * Recupero della data di firma digitale dalle informazioni di firma
	 * estratte dal P7M
	 */
	private static Date getSignatureTimeP7M(SignerInformation signer) {
		Date signatureTime = null;

		try {
			AttributeTable signedAttr = signer.getSignedAttributes();
			Attribute signingTime = signedAttr.get(CMSAttributes.signingTime);
			if (signingTime != null) {
				Enumeration<?> en = signingTime.getAttrValues().getObjects();
				while (en.hasMoreElements()) {
					Object obj = en.nextElement();
					if (obj instanceof ASN1UTCTime) {
						ASN1UTCTime asn1Time = (ASN1UTCTime) obj;
						log.debug("VerificaFirmaDigitale.getSignatureTimeP7M(): ASN1UTCTime = " + asn1Time.getDate());

						signatureTime = asn1Time.getDate();
					} else if (obj instanceof DERUTCTime) {
						DERUTCTime derTime = (DERUTCTime) obj;
						log.debug("VerificaFirmaDigitale.getSignatureTimeP7M(): DERUTCTime = " + derTime.getDate());

						signatureTime = derTime.getDate();
					}
				}
			} else {
				log.error("VerificaFirmaDigitale.getSignatureTimeP7M(): No signature time found!");
			}
		} 
		catch (Exception e) {
			log.error("VerificaFirmaDigitale.getSignatureTimeP7M(): got exception... " + e.getMessage(), e);
		}

		return signatureTime;
	}

	/**
	 * Estrazione dei dettagli di firma da file PDF
	 */
	private Element extractSignatureInfoFromPDF(Element verificaFirmaElement) {
		try {
			PdfReader reader = new PdfReader(this.content);
			AcroFields af = reader.getAcroFields();

			// Search of the whole signature
			ArrayList<?> names = af.getSignatureNames();

			// For every signature :
			for (int k = 0; k < names.size(); ++k) {
				try {
					String name = (String) names.get(k);
	
					PdfPKCS7 pk = af.verifySignature(name);
					Calendar cal = pk.getSignDate();
					cal.setTimeZone(TimeZone.getTimeZone("UTC"));
					String signTime = this.sdf.format(cal.getTime());
					log.debug("VerificaFirmaDigitale.extractSignatureInfoFromP7M(): signTime = " + signTime);
	
					//log.debug("VerificaFirmaDigitale.extractSignatureInfoFromP7M(): Algoritmo Digest = " + pk.getDigestAlgorithm());
	
					Principal subject = pk.getSigningCertificate().getSubjectDN();
					log.debug("VerificaFirmaDigitale.extractSignatureInfoFromPDF(): subject = " + subject);
					Map<String, String> subjectMap = signInfoToMap(subject.toString());
					X500Principal issuer = pk.getSigningCertificate().getIssuerX500Principal();
					log.debug("VerificaFirmaDigitale.extractSignatureInfoFromPDF(): issuer = " + issuer);
					Map<String, String> issuerMap = signInfoToMap(issuer.toString());
	
					//log.debug("VerificaFirmaDigitale.extractSignatureInfoFromP7M(): Documento modificato = " + !pk.verify());
	
					String signerName = "";
					if (subjectMap.containsKey(CN_KEY))
						signerName = subjectMap.get(CN_KEY);
					else if (subjectMap.containsKey(SURNAME_KEY) && subjectMap.containsKey(GIVENNAME_KEY))
						signerName = subjectMap.get(SURNAME_KEY) + " " + subjectMap.get(GIVENNAME_KEY);
					
					String digestAlg = "";
					try {
						digestAlg = getDigestAlgorithm(pk.getDigestAlgorithmOid());
					}
					catch (Exception e) {
						log.error("VerificaFirmaDigitale.extractSignatureInfoFromPDF(): can't get digest algorithm... " + e.getMessage(), e);
					}
	
					addSignerElement(verificaFirmaElement, signerName, subjectMap.get(SERIALNUMBER_KEY),
															subjectMap.get(ORGANIZATION_KEY), subjectMap.get(COUNTRY_KEY), issuerMap.get(ORGANIZATION_KEY),
															signTime, digestAlg, null, null);
				} 
				catch (Exception e) {
					log.error("VerificaFirmaDigitale.extractSignatureInfoFromPDF(): got exception on a signer... " + e.getMessage(), e);
				}
			}
		} 
		catch (Exception e) {
			log.error("VerificaFirmaDigitale.extractSignatureInfoFromPDF(): got exception... " + e.getMessage(), e);

			Element errorEl = verificaFirmaElement.addElement("error");
			errorEl.addCDATA("Riscontrato errore nella verifica della firma: " + e.getMessage());
		}

		return verificaFirmaElement;
	}

	/**
	 * Converte le informazioni di firma (firmatario o ente certificatore) in
	 * una mappa di tipo chiave, valore. Esempi di testi da convertire:
	 * C=IT,O=ENI SPA/00484960588,OU=E&P,SURNAME=SALMASO,GIVENNAME=NICOLA,SERIALNUMBER=IT:SLMNCL60S01G224P,CN=NICOLA SALMASO,DN=12-1372417147977 
	 * C=IT,O=Actalis S.p.A./03358520967,OU=Qualified Certification Service Provider,CN=Actalis Qualified Certificates CA G1
	 */
	private Map<String, String> signInfoToMap(String info) {
		Map<String, String> map = new HashMap<String, String>();

		if (info != null && !info.isEmpty()) {
			String[] values = info.split("\\,");
			if (values != null && values.length > 0) {
				for (int i = 0; i < values.length; i++) {
					if (values[i] != null) {
						int index = values[i].indexOf("=");
						if (index != -1) {
							map.put(values[i].substring(0, index).trim().toUpperCase(), values[i].substring(index + 1));
						}
					}
				}
			}
		}
		return map;
	}
	
	/**
	 * Dato l'OID restituisce il nome dell'algoritmo digest
	 */
	private String getDigestAlgorithm(String oid) throws Exception {
		if (oid != null && !oid.isEmpty()) {
			MessageDigest md = MessageDigest.getInstance(oid);
			return md.getAlgorithm();
		}
		else 
			return null;
	}

	/**
	 * Aggiunta di un firmatario del documento all'XML di ritorno di verifica
	 * firma
	 */
	private void addSignerElement(Element verificaFirmaElement, String name, String cf, String organization,
			String country, String issuer, String signatureDate, String digestAlg, String dateFrom, String dateTo)
					throws Exception {

		addSignerElement(verificaFirmaElement, name, cf, organization, country, issuer, signatureDate, digestAlg,
				dateFrom, dateTo, null, null, null, null, null);
	}

	/**
	 * Aggiunta di un firmatario del documento all'XML di ritorno di verifica
	 * firma
	 */
	private void addSignerElement(Element verificaFirmaElement, String name, String cf, String organization,
			String country, String issuer, String signatureDate, String digestAlg, String certFrom, String certTo,
			Boolean isQualified, Boolean isTrusted, Boolean isValid, Boolean isRevoked, String serial)
					throws Exception {
		if (verificaFirmaElement != null) {
			Element signerEl = verificaFirmaElement.addElement("signer");

			if (isValid != null)
				signerEl.addAttribute("valid", String.valueOf(isValid.booleanValue()));

			if (digestAlg != null && !digestAlg.isEmpty()) {
				Element el = signerEl.addElement("digestAlg");
				el.setText(digestAlg);
			}

			if (signatureDate != null && !signatureDate.isEmpty()) {
				Element el = signerEl.addElement("signatureDate");
				el.setText(signatureDate);
			}

			if (name != null) {
				Element el = signerEl.addElement("name");
				el.addCDATA(name);
			}

			if (cf != null && !cf.isEmpty()) {
				int index = cf.indexOf(":");
				if (index != -1)
					cf = cf.substring(index + 1);
				Element el = signerEl.addElement("cf");
				el.setText(cf);
			}

			if (organization != null) {
				Element el = signerEl.addElement("organization");
				el.addCDATA(organization);
			}

			if (country != null && !country.isEmpty()) {
				Element el = signerEl.addElement("country");
				el.addCDATA(country);
			}

			if (issuer != null) {
				Element el = signerEl.addElement("issuer");
				el.addCDATA(issuer);
			}

			if (isQualified != null)
				signerEl.addAttribute("qualified", String.valueOf(isQualified.booleanValue()));

			if (certFrom != null && !certFrom.isEmpty()) {
				Element el = signerEl.addElement("certFrom");
				el.setText(certFrom);
			}

			if (certTo != null && !certTo.isEmpty()) {
				Element el = signerEl.addElement("certTo");
				el.setText(certTo);
			}

			if (isRevoked != null)
				signerEl.addAttribute("revoked", String.valueOf(isRevoked.booleanValue()));

			if (isTrusted != null)
				signerEl.addAttribute("trusted", String.valueOf(isTrusted.booleanValue()));

			if (serial != null && !serial.isEmpty()) {
				Element el = signerEl.addElement("serial");
				el.addCDATA(serial);
			}
		}
	}

}
