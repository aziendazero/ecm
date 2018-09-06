package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.format.number.NumberStyleFormatter;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.EventoFAD;
import it.tredi.ecm.dao.entity.EventoFSC;
import it.tredi.ecm.dao.entity.EventoRES;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.pdf.PdfPartecipanteInfo;
import it.tredi.ecm.pdf.PdfRiepilogoPartecipantiInfo;
import it.tredi.ecm.utils.Utils;

@Service
public class PdfRiepilogoPartecipantiServiceImpl implements PdfRiepilogoPartecipantiService {
	private static Logger LOGGER = LoggerFactory.getLogger(PdfRiepilogoPartecipantiServiceImpl.class);

	@Autowired private MessageSource messageSource;

	//formatters
	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private NumberStyleFormatter intFormatter = new NumberStyleFormatter("0");
	private NumberStyleFormatter longFormatter = new NumberStyleFormatter("0");
	private NumberStyleFormatter floatFormatter = new NumberStyleFormatter("0.0#");
	private NumberStyleFormatter valutaFormatter = new NumberStyleFormatter("0.00");

    //tipi font
	private int sizeBigBold = 17;
	private int sizeTitolo = 15;
	private int sizeParTitolo = 13;
	private int sizeNomeCampo = 11;
	private int sizeValoreCampo = 10;
	private int sizeTitoloSubTable = 9;
	private int sizeEtichettaSubTable = 8;
	private int sizeNomeCampoSubTable = 9;
	private int sizeValoreCampoSubTable = 9;
	private Font.FontFamily fontFamily = Font.FontFamily.TIMES_ROMAN;
	private Font fontTitolo = new Font(fontFamily, sizeTitolo, Font.BOLD);
	private Font fontParTitolo = new Font(fontFamily, sizeParTitolo, Font.BOLD);
	private Font fontNomeCampo = new Font(fontFamily, sizeNomeCampo, Font.BOLD);
	private Font fontValoreCampo = new Font(fontFamily, sizeValoreCampo, Font.NORMAL);
	private Font blueFont = new Font(fontFamily, sizeTitolo, Font.BOLD, new BaseColor(31,204,169));
	private Font greyItalicFont = new Font(fontFamily, sizeParTitolo, Font.BOLDITALIC, new BaseColor(85,85,85));
	private Font fontNomeCampoBoldItalic = new Font(fontFamily, sizeNomeCampo, Font.BOLDITALIC);
	private Font fontBigBold = new Font(fontFamily, sizeBigBold, Font.BOLD);
	//Formati delle sotto tabelle
	private Font fontTitoloSubTable = new Font(fontFamily, sizeTitoloSubTable, Font.BOLD);
	private Font fontEtichettaSubTable = new Font(fontFamily, sizeEtichettaSubTable, Font.BOLD);
	private Font fontNomeCampoSubTable = new Font(fontFamily, sizeNomeCampoSubTable, Font.BOLD);
	private Font fontValoreCampoSubTable = new Font(fontFamily, sizeValoreCampoSubTable, Font.NORMAL);
	private Font fontValoreCampoSubTableBold = new Font(fontFamily, sizeValoreCampoSubTable, Font.BOLD);
	private float cellPadding = 5F;
	private float cellPaddingSubTable = 2F;
	private float spacingBefore = 10F;
	private float spacingAfter = 10F;

	@Override
	public ByteArrayOutputStream creaOutputSteramPdfRiepilogoPartecipanti(PdfRiepilogoPartecipantiInfo pdfRiepilogoPartecipantiInfo, String codiceEvento) throws Exception {
		LOGGER.debug("Inizio procedura scrittura PDF del riepilogo dei Partecipanti all'Evento: " + codiceEvento);

		ByteArrayOutputStream byteArrayOutputStreamPdf = new ByteArrayOutputStream();
        writePdfRiepilogoPartecipanti(byteArrayOutputStreamPdf, pdfRiepilogoPartecipantiInfo, codiceEvento);

        return byteArrayOutputStreamPdf;
	}

	@Override
	public ByteArrayOutputStream creaOutputSteramPdfAttestatiPartecipanti(PdfRiepilogoPartecipantiInfo pdfRiepilogoPartecipantiInfo, Evento evento) throws Exception {
		LOGGER.debug("Inizio procedura scrittura PDF degli attestati dei Partecipanti all'Evento: " + evento.getCodiceIdentificativo());

		ByteArrayOutputStream byteArrayOutputStreamPdf = new ByteArrayOutputStream();
        writePdfAttestatiPartecipanti(byteArrayOutputStreamPdf, pdfRiepilogoPartecipantiInfo, evento);

        return byteArrayOutputStreamPdf;
	}

	private void writePdfRiepilogoPartecipanti(OutputStream outputStream, PdfRiepilogoPartecipantiInfo pdfRiepilogoPartecipantiInfo, String codiceEvento) throws Exception {
		Document document = new Document();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            //Info documento
            document.addAuthor("Ecm");
            document.addCreationDate();
            document.addCreator("Ecm");
            document.addTitle("Riepilogo dei Partecipanti all'Evento:" + codiceEvento);
        	writePdfRiepilogoPartecipanti(document, pdfRiepilogoPartecipantiInfo, codiceEvento);
        } catch (Exception e) {
        	LOGGER.error("Impossibile creare il PDF del Riepilogo dei Partecipanti all'Evento:  " + codiceEvento, e);
            throw e;
        } finally {
            if(document.isOpen())
            	document.close();
            try {
            	outputStream.close();
            } catch (IOException ex) {}
        }
	}

	private void writePdfAttestatiPartecipanti(OutputStream outputStream, PdfRiepilogoPartecipantiInfo pdfRiepilogoPartecipantiInfo, Evento evento) throws Exception {
		Document document = new Document();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            //Info documento
            document.addAuthor("Ecm");
            document.addCreationDate();
            document.addCreator("Ecm");
            document.addTitle("Attestati dei Partecipanti all'Evento:" + evento.getCodiceIdentificativo());
            writePdfAttestatiPartecipanti(document, pdfRiepilogoPartecipantiInfo, evento);
        } catch (Exception e) {
        	LOGGER.error("Impossibile creare il PDF degli Attestati dei Partecipanti all'Evento:  " + evento.getCodiceIdentificativo(), e);
            throw e;
        } finally {
            if(document.isOpen())
            	document.close();
            try {
            	outputStream.close();
            } catch (IOException ex) {}
        }
	}

	private void writePdfRiepilogoPartecipanti(Document document, PdfRiepilogoPartecipantiInfo pdfRiepilogoPartecipantiInfo, String codiceEvento) throws DocumentException {
		//TITOLO
		Object[] values = {codiceEvento};
        Paragraph parTitolo = new Paragraph();
        parTitolo.setAlignment(Element.ALIGN_LEFT);
        parTitolo.setFont(fontTitolo);
        parTitolo.add(messageSource.getMessage("label.report_rendicontazione", null, Locale.getDefault()));
        document.add(parTitolo);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        Paragraph par = new Paragraph();
        par.setAlignment(Element.ALIGN_LEFT);
        par.setFont(fontParTitolo);
        par.add(messageSource.getMessage("label.partecipanti_evento_codEvento", values, Locale.getDefault()));
        document.add(par);
        PdfPTable table = getTableFieldsPartecipanti();
        addTablePartecipanti(pdfRiepilogoPartecipantiInfo, table);
        document.add(table);
	}

	private void writePdfAttestatiPartecipanti(Document document, PdfRiepilogoPartecipantiInfo pdfRiepilogoPartecipantiInfo, Evento evento) throws DocumentException {

		Provider provider = evento.getProvider();

		for(PdfPartecipanteInfo partecipante : pdfRiepilogoPartecipantiInfo.getPartecipanti()) {
			document.newPage();

			//IMMAGINE
			Paragraph parImage = new Paragraph();
			
	        
			parImage.setAlignment(Element.ALIGN_CENTER);
			Image logoProvider=null;
			Image img1 = getLogoAziendaZero();
			if ((evento.getProvider().getProviderFile() !=null)) {
				logoProvider = getLogoProvider(evento.getProvider().getProviderFile().getData());
			}
			
			PdfPTable table = new PdfPTable(3);
			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
		    table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			//table.setWidthPercentage(100);
	        //table.setWidths(new int[]{1, 2});
			PdfPCell cellOne = new PdfPCell(img1);
			PdfPCell cellx = new PdfPCell();
			PdfPCell cellTwo = new PdfPCell(logoProvider);
			cellOne.setBorder(0);
			cellTwo.setBorder(0);
			cellx.setBorder(0);
	        table.addCell(cellOne);
	        table.addCell(cellx);
	        table.addCell(cellTwo);
	       	document.add(table);
			document.add(Chunk.NEWLINE);

			//TITOLO
	        //Paragraph parTitolo = new Paragraph();
	        //Chunk title = new Chunk(messageSource.getMessage("label.sistema_regionale_ecm", null, Locale.getDefault()).toUpperCase(), blueFont);
	        //parTitolo.setAlignment(Element.ALIGN_CENTER);
	        //parTitolo.add(title);
	        //document.add(parTitolo);

	        document.add(Chunk.NEWLINE);
	        //document.add(Chunk.NEWLINE);

	        //SOTTOTITOLO
	        Paragraph parSubTitle = new Paragraph();
	        Chunk subtitle = new Chunk("Programma regionale per la formazione continua dei Professionisti della Sanità", greyItalicFont);
	        parSubTitle.setAlignment(Element.ALIGN_CENTER);
	        parSubTitle.add(subtitle);
	        document.add(parSubTitle);

	        document.add(Chunk.NEWLINE);

	        //PRIMO PARAGRAFO
	        Paragraph par1 = new Paragraph();
	        Chunk c1 = new Chunk("Premesso che la ", fontNomeCampo);
	        Chunk c2 = new Chunk("Commissione Regionale per l’Educazione Continua in Medicina ", fontNomeCampoBoldItalic);
	        Chunk c3 = new Chunk("ha accreditato il Provider " + provider.getDenominazioneLegale() + " con accreditamento n. " + provider.getId() + ".", fontNomeCampo);
	        par1.setAlignment(Element.ALIGN_JUSTIFIED);
	        par1.add(c1);
	        par1.add(c2);
	        par1.add(c3);
	        document.add(par1);

	        document.add(Chunk.NEWLINE);

	        //SECONDO PARAGRAFO
	        Paragraph par2 = new Paragraph();
	        Chunk c4 = new Chunk("Premesso che il Provider ha organizzato l’evento formativo n. " + evento.getCodiceIdentificativo(), fontNomeCampo);
	        Chunk c5 = new Chunk(" edizione " + evento.getEdizione(), fontNomeCampo);
	        Chunk c6 = new Chunk(", dal titolo: " , fontNomeCampo);
	        Chunk c61 = new Chunk(evento.getTitolo(), fontNomeCampo);
	        Chunk c7 = new Chunk("", fontNomeCampo);
	        //if(!(evento instanceof EventoFAD)) {
	        	if(evento instanceof EventoRES)
	        		c7.append(" e tenutosi a " + ((EventoRES) evento).getSedeEvento().getLuogo());
	        	else if(evento instanceof EventoFSC)
	        		c7.append(" e tenutosi a " + ((EventoFSC) evento).getSedeEvento().getLuogo());
	        //}
	        Chunk c8 = new Chunk( " dal " + dateTimeFormatter.format(evento.getDataInizio()) + " al " , fontNomeCampo);
	        Chunk c81 = new Chunk(dateTimeFormatter.format(evento.getDataFine()), fontNomeCampo);
	        Chunk c9 = new Chunk(" avente come obiettivo formativo: " + evento.getObiettivoNazionale().getNome(), fontNomeCampo);
	        par2.setAlignment(Element.ALIGN_JUSTIFIED);
	        par2.add(c4);
	        par2.add(c5);
	        par2.add(c6);
	        par2.add(Chunk.NEWLINE);
	        par2.add(c61);
	        par2.add(c7);
	        par2.add(c8);
	        par2.add(c81);
	        par2.add(c9);
	        par2.add(Chunk.NEWLINE);
	        document.add(par2);

	        document.add(Chunk.NEWLINE);

	        //CREDITI EVENTO
	        //Paragraph par3 = new Paragraph();
	        //Chunk c11 = new Chunk("N. " + evento.getCrediti() + " Crediti Formativi E.C.M.", fontNomeCampo);
	        //par3.setAlignment(Element.ALIGN_CENTER);
	        //par3.add(c11);
	        //document.add(par3);
	        //document.add(Chunk.NEWLINE);
	        document.add(Chunk.NEWLINE);

	        //VERIFICA RAPPRESENTANTE
	        Paragraph par4 = new Paragraph();
	        Chunk c12 = new Chunk("il/la sottoscritto/a", fontNomeCampo);
	        Chunk c13 = new Chunk(provider.getLegaleRappresentante().getAnagrafica().getFullName(), fontNomeCampo);
	        Chunk c14 = new Chunk("Rappresentante Legale del Provider", fontNomeCampo);
	        Chunk c15 = new Chunk("(o suo delegato ovvero Responsabile scientifico dell’evento, su delega del Rappresentante Legale del Provider)", fontNomeCampo);
	        par4.setAlignment(Element.ALIGN_CENTER);
	        par4.add(c12);
	        par4.add(Chunk.NEWLINE);
	        par4.add(c13);
	        par4.add(Chunk.NEWLINE);
	        par4.add(c14);
	        par4.add(Chunk.NEWLINE);
	        par4.add(c15);
	        document.add(par4);
	        document.add(Chunk.NEWLINE);
	        document.add(Chunk.NEWLINE);

	        //ATTESTA
	        String varShow="";
	        if (partecipante.getReclutato()=="SI"){
	        	varShow="Partecipante Reclutato";
	        }
	        if (partecipante.getReclutato()=="NO"){
	        
	        	varShow="Partecipante non reclutato";
	        }
	        Paragraph par5 = new Paragraph();
	        Chunk c16 = new Chunk("ATTESTA", fontBigBold);
	        Chunk c17 = new Chunk("che il/la", fontNomeCampo);
	        Chunk c18 = new Chunk("Prof./Prof.ssa/Dott./Dott.ssa/Sig./Sig.ra " + partecipante.getNome() + " " + partecipante.getCognome(), fontNomeCampo);
	        Chunk c20 = new Chunk("C.F. " + partecipante.getCodiceFiscale(), fontNomeCampo);
	        Chunk c21 = new Chunk(" in qualità di " + partecipante.getTipologiaPartecipante() + " il " + partecipante.getDataCreditiAcquisiti() + "\n"
	        			+ " come " + varShow + " ha acquisito:", fontNomeCampo);
	        par5.setAlignment(Element.ALIGN_CENTER);
	        par5.add(c16);
	        par5.add(Chunk.NEWLINE);
	        par5.add(c17);
	        par5.add(Chunk.NEWLINE);
	        par5.add(c18);
	        par5.add(Chunk.NEWLINE);
	        par5.add(c20);
	        par5.add(c21);
	        document.add(par5);

	        document.add(Chunk.NEWLINE);
	        document.add(Chunk.NEWLINE);

	        String[] listNumeroCrediti = splitNumeroCrediti(partecipante.getNumeroCrediti());

	        //CREDITI PARTECIPANTE
	        Paragraph par6 = new Paragraph();
	        Chunk c22 = new Chunk(partecipante.getNumeroCrediti()+" (" + Utils.convert(Integer.parseInt(listNumeroCrediti[0]))  + "/" + Utils.convert(Integer.parseInt(listNumeroCrediti[1])) + ") Crediti Formativi E.C.M", fontNomeCampo);
	        Chunk c27 = new Chunk("(secondo i parametri stabiliti dai " + "‘‘" +  "Criteri per l’assegnazione dei crediti\n" +
	        " alle attivita ECM" + "’’" + " Allegati all’Accordo Stato Regioni del 02/02/2017)", fontNomeCampo);
	        Chunk c28 = new Chunk("Nella professione ", fontNomeCampo);

//	        Phrase phrase = new Phrase();
//	        for(String professione : partecipante.getProfessioni()) {
//				Chunk chunkProfessione = new Chunk(professione, fontNomeCampo);
//				phrase.add(chunkProfessione);
//				phrase.add(",");
//			}
//	        //document.add(phrase);
//
//	        Chunk c29 = new Chunk(" disciplina" + "xxx", fontNomeCampo);
//
	        par6.setAlignment(Element.ALIGN_CENTER);
	        par6.add(c22);
	        par6.add(Chunk.NEWLINE);
	        par6.add(c27);
	        par6.add(Chunk.NEWLINE);
	        par6.add(Chunk.NEWLINE);
	        par6.add(c28);

	        for (Map.Entry<String, Set<String>> entry : partecipante.getProfessioni_discipline().entrySet()) {
	            String professione = entry.getKey();

	            Phrase phrase = new Phrase();
	            Chunk chunkProfessione = new Chunk(professione, fontNomeCampo);
				phrase.add(chunkProfessione);

				Chunk c29 = new Chunk(" disciplina " , fontNomeCampo);
				phrase.add(c29);
				Set<String> discipline = entry.getValue();
	            for(String disciplina :  discipline) {
	            	Chunk d = new Chunk(disciplina + " ", fontNomeCampo);
	            	phrase.add(d);
	            }
	            par6.add(phrase);
	        }

	        document.add(par6);

	        document.add(Chunk.NEWLINE);

	        //FOOTER CONTAINER
	        Paragraph container = new Paragraph();
	        container.setKeepTogether(true);

	        //LUOGO DATA FIRMA
	        Paragraph par8 = new Paragraph();
	        Chunk c24 = new Chunk("__________________, lì _______________", fontNomeCampo);
	        par8.add(c24);
	        container.add(par8);

	        container.add(Chunk.NEWLINE);

	        //LEGALE RAPPRESENTANTE
	        Paragraph par7 = new Paragraph();
	        Chunk c23 = new Chunk("Il RAPPRESENTANTE LEGALE\n" +
	        		"(o suo delegato ovvero il Responsabile scientifico dell’evento, su delega del Rappresentante Legale del Provider)", fontNomeCampo);
	        Chunk c25 = new Chunk("FIRMA___________________________", fontNomeCampo);
	        par7.setAlignment(Element.ALIGN_CENTER);
	        par7.add(c23);
	        par7.add(Chunk.NEWLINE);
	        par7.add(c25);
	        container.add(par7);

	        container.add(Chunk.NEWLINE);

	        //IMMAGINE
	        Paragraph par9 = new Paragraph();
			Image img2 = getLogoFooter();
			Chunk c26 = new Chunk(img2, 0, 0, true);
			par9.setAlignment(Element.ALIGN_CENTER);
			par9.add(c26);
			container.add(par9);

	        //FOOTER IMG
	        document.add(container);
		}
	}

	private PdfPTable getTableFieldsPartecipanti() throws DocumentException {
		PdfPTable tableFields = new PdfPTable(9);
		tableFields.setWidthPercentage(100);
		tableFields.setWidths(new float[]{1,1,1.5f,0.4f,1,1,0.5f,0.8f,1});
		tableFields.setSpacingBefore(spacingBefore);
		tableFields.setSpacingAfter(spacingAfter);
		addCellIntestaSubTableByString(messageSource.getMessage("label.nome", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.cognome", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.codice_fiscale", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.reclutato_short", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.sponsor", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.tipologia_partecipante", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.nCrediti", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.data_crediti_acquisiti", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.professione", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		return tableFields;
	}

	private void addCellIntestaSubTableByString(String stringLabelCampo, PdfPTable table, BaseColor baseColor, boolean border, Integer elementAlign) {
		PdfPCell cell = new PdfPCell(new Phrase(stringLabelCampo, fontNomeCampoSubTable));
		if(!border)
			cell.setBorder(PdfPCell.NO_BORDER);
		if(baseColor != null)
			cell.setBackgroundColor(baseColor);
		if(elementAlign != null)
			cell.setHorizontalAlignment(elementAlign);
		else
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setPadding(cellPaddingSubTable);
		table.addCell(cell);
	}

	private void addTablePartecipanti(PdfRiepilogoPartecipantiInfo pdfRiepilogoPartecipantiInfo, PdfPTable table) {
		//ordinamento per cognome
		List<PdfPartecipanteInfo> listPartecipanti = new ArrayList<PdfPartecipanteInfo>();
		listPartecipanti.addAll(pdfRiepilogoPartecipantiInfo.getPartecipanti());
		listPartecipanti.sort((p1, p2) -> p1.getCognome().compareTo(p2.getCognome()));
		for(PdfPartecipanteInfo partecipante : listPartecipanti) {
			addCellSubTable(partecipante.getNome(), table);
			addCellSubTable(partecipante.getCognome(), table);
			addCellSubTable(partecipante.getCodiceFiscale(), table);
			addCellSubTable(partecipante.getReclutato(), table);
			addCellSubTable(partecipante.getSponsor(), table);
			addCellSubTable(partecipante.getTipologiaPartecipante(), table);
			addCellSubTable(partecipante.getNumeroCrediti(), table);
			addCellSubTable(partecipante.getDataCreditiAcquisiti(), table);
			addCellSubTableProfessioni(partecipante.getProfessioni(), table);
		}
	}

	private void addCellSubTable(String valoreCampo, PdfPTable table) {
		addCellSubTable(valoreCampo, table, null, true, null, false);
	}

	private void addCellSubTableProfessioni(Set<String> professioni, PdfPTable table) {
		addCellSubTable(professioni, table, null, true, null, false);
	}

	private void addCellSubTable(String valoreCampo, PdfPTable table, BaseColor baseColor, boolean border, Integer elementAlign, boolean bold) {
		PdfPCell cell = new PdfPCell(new Phrase(valoreCampo, bold ? fontValoreCampoSubTableBold : fontValoreCampoSubTable));
		if(!border)
			cell.setBorder(PdfPCell.NO_BORDER);
		if(baseColor != null)
			cell.setBackgroundColor(baseColor);
		if(elementAlign != null)
			cell.setHorizontalAlignment(elementAlign);
		else
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setPadding(cellPaddingSubTable);
		table.addCell(cell);
	}

	private void addCellSubTable(Set<String> professioni, PdfPTable table, BaseColor baseColor, boolean border, Integer elementAlign, boolean bold) {
		Phrase phrase = new Phrase();
		for(String professione : professioni) {
			Chunk chunkProfessione = new Chunk(professione, bold ? fontValoreCampoSubTableBold : fontValoreCampoSubTable);
			phrase.add(chunkProfessione);
			phrase.add(Chunk.NEWLINE);
		}
		PdfPCell cell = new PdfPCell();
		cell.setPhrase(phrase);
		if(!border)
			cell.setBorder(PdfPCell.NO_BORDER);
		if(baseColor != null)
			cell.setBackgroundColor(baseColor);
		if(elementAlign != null)
			cell.setHorizontalAlignment(elementAlign);
		else
			cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setPadding(cellPaddingSubTable);
		table.addCell(cell);
	}

	public static Image getLogoAttestato(){
		//Creazione immagine
        Image img = null;
		URL url = Thread.currentThread().getContextClassLoader().getResource("LogoAttestato.png");
		try {
			img = Image.getInstance(url);
			Float scala = 1.2F;
			Float width = 400F/scala;
			Float height = 85F/scala;
			img.scaleToFit(width, height);
            img.setAlignment(Element.ALIGN_CENTER);
		} catch(Exception e) {
			//Non mostro l'immagine
		}
		return img;
	}

	public static Image getLogoFooter(){
		//Creazione immagine
        Image img = null;
		URL url = Thread.currentThread().getContextClassLoader().getResource("ImgECM.png");
		try {
			img = Image.getInstance(url);
			Float scala = 2f;
			Float width = 400F/scala;
			Float height = 85F/scala;
			img.scaleToFit(width, height);
            img.setAlignment(Element.ALIGN_CENTER);
		} catch(Exception e) {
			//Non mostro l'immagine
		}
		return img;
	}

	//get logo of Provider to display in to .pdf document
	public static Image getLogoProvider(byte[] imgb){
		//Creazione immagine
		Image img = null;
		try {
			img = Image.getInstance(imgb);
			Float scala = 1.2F;
			Float width = 400F/scala;
			Float height = 85F/scala;
			img.scaleToFit(width, height);
            img.setAlignment(Element.ALIGN_CENTER);
		} catch(Exception e) {
			//Non mostro l'immagine
		}
		return img;
	}

	//get image of LogoAziendaZero to display in to .pdf document
	public static Image getLogoAziendaZero(){
		//Creazione immagine
        Image img = null;
		URL url = Thread.currentThread().getContextClassLoader().getResource("LogoAziendaZero.png");
		try {
			img = Image.getInstance(url);
			Float scala = 1.2F;
			Float width = 400F/scala;
			Float height = 85F/scala;
			img.scaleToFit(width, height);
            img.setAlignment(Element.ALIGN_CENTER);
		} catch(Exception e) {
			//Non mostro l'immagine
		}
		return img;
	}

	private String[] splitNumeroCrediti(String numeroCrediti)
	{
		numeroCrediti=numeroCrediti.replace(".", " ");
		String[] nCrediti = numeroCrediti.split(" ");
		return nCrediti;
	}




}