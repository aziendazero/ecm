package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

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

import it.tredi.ecm.dao.entity.EventoPianoFormativo;
import it.tredi.ecm.dao.entity.PianoFormativo;

@Service
public class ExportPianoFormativoServiceImpl implements ExportPianoFormativoService {
	private static Logger LOGGER = LoggerFactory.getLogger(ExportPianoFormativoServiceImpl.class);

	@Autowired private MessageSource messageSource;

	//formatters
	/*
	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private NumberStyleFormatter intFormatter = new NumberStyleFormatter("0");
	private NumberStyleFormatter longFormatter = new NumberStyleFormatter("0");
	private NumberStyleFormatter floatFormatter = new NumberStyleFormatter("0.0#");
	private NumberStyleFormatter valutaFormatter = new NumberStyleFormatter("0.00");
	*/
    //tipi font
	//private int sizeBigBold = 17;
	private int sizeTitolo = 15;
	private int sizeParTitolo = 13;
	//private int sizeNomeCampo = 11;
	//private int sizeValoreCampo = 10;
	//private int sizeTitoloSubTable = 9;
	//private int sizeEtichettaSubTable = 8;
	private int sizeNomeCampoSubTable = 9;
	private int sizeValoreCampoSubTable = 9;
	private Font.FontFamily fontFamily = Font.FontFamily.TIMES_ROMAN;
	private Font fontTitolo = new Font(fontFamily, sizeTitolo, Font.BOLD);
	private Font fontParTitolo = new Font(fontFamily, sizeParTitolo, Font.BOLD);
	//private Font fontNomeCampo = new Font(fontFamily, sizeNomeCampo, Font.BOLD);
	//private Font fontValoreCampo = new Font(fontFamily, sizeValoreCampo, Font.NORMAL);
	//private Font blueFont = new Font(fontFamily, sizeTitolo, Font.BOLD, new BaseColor(31,204,169));
	//private Font greyItalicFont = new Font(fontFamily, sizeParTitolo, Font.BOLDITALIC, new BaseColor(85,85,85));
	//private Font fontNomeCampoBoldItalic = new Font(fontFamily, sizeNomeCampo, Font.BOLDITALIC);
	//private Font fontBigBold = new Font(fontFamily, sizeBigBold, Font.BOLD);
	//Formati delle sotto tabelle
	//private Font fontTitoloSubTable = new Font(fontFamily, sizeTitoloSubTable, Font.BOLD);
	//private Font fontEtichettaSubTable = new Font(fontFamily, sizeEtichettaSubTable, Font.BOLD);
	private Font fontNomeCampoSubTable = new Font(fontFamily, sizeNomeCampoSubTable, Font.BOLD);
	private Font fontValoreCampoSubTable = new Font(fontFamily, sizeValoreCampoSubTable, Font.NORMAL);
	private Font fontValoreCampoSubTableBold = new Font(fontFamily, sizeValoreCampoSubTable, Font.BOLD);
	//private float cellPadding = 5F;
	private float cellPaddingSubTable = 2F;
	private float spacingBefore = 10F;
	private float spacingAfter = 10F;

	@Override
	public ByteArrayOutputStream creaOutputSteramPdfExportPianoFormativo(PianoFormativo pf) throws Exception {
		LOGGER.debug("Inizio procedura scrittura PDF del piano formativo: " + pf.getId());

		ByteArrayOutputStream byteArrayOutputStreamPdf = new ByteArrayOutputStream();
		writePdfExportPianoFormativo(byteArrayOutputStreamPdf, pf);

        return byteArrayOutputStreamPdf;
	}
	
	
	@Override
	public ByteArrayOutputStream creaOutputSteramCsvExportPianoFormativo(PianoFormativo pf) throws Exception {
		LOGGER.debug("Inizio procedura scrittura CSV del piano formativo: " + pf.getId());

		ByteArrayOutputStream byteArrayOutputStreamCsv = new ByteArrayOutputStream();
		
		
		try (PrintWriter p = new PrintWriter(new OutputStreamWriter(byteArrayOutputStreamCsv, StandardCharsets.UTF_8))) {
		    // write BOM
			p.print('\ufeff');
			p.println("Titolo;Codice;Tipo");
			int anno = pf.getAnnoPianoFormativo();
			for(EventoPianoFormativo evt : pf.getEventiPianoFormativo()) {
				
				p.format("\"%s\";\"%s\";\"%s\"\r\n",
						evt.getTitolo(),
						evt.getCodiceIdentificativo(),
						evt.getInfoProcedureFormativa(anno)
				);
			}
			
		} catch (Exception e1) {
		    e1.printStackTrace();
		}

		return byteArrayOutputStreamCsv;
	}

	

	private void writePdfExportPianoFormativo(OutputStream outputStream, PianoFormativo pf) throws Exception {
		Document document = new Document();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            //Info documento
            document.addAuthor("Ecm");
            document.addCreationDate();
            document.addCreator("Ecm");
            document.addTitle(String.format("Piano formativo %d : %s", pf.getAnnoPianoFormativo(), pf.getProvider().getDenominazioneLegale()));
        	writePdfExportPianoFormativo(document, pf);
        } catch (Exception e) {
        	LOGGER.error(String.format("Impossibile creare il PDF del piano formativo: %s del %d", pf.getProvider().getDenominazioneLegale(), pf.getAnnoPianoFormativo()), e);
            throw e;
        } finally {
            if(document.isOpen())
            	document.close();
            try {
            	outputStream.close();
            } catch (IOException ex) {}
        }
	}

	

	private void writePdfExportPianoFormativo(Document document, PianoFormativo pf) throws DocumentException {
		//TITOLO
		Paragraph parTitolo = new Paragraph();
        parTitolo.setAlignment(Element.ALIGN_LEFT);
        parTitolo.setFont(fontTitolo);
        parTitolo.add(String.format("Piano formativo %d : %s", pf.getAnnoPianoFormativo(), pf.getProvider().getDenominazioneLegale()));
        document.add(parTitolo);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        Paragraph par = new Paragraph();
        par.setAlignment(Element.ALIGN_LEFT);
        par.setFont(fontParTitolo);
        par.add(messageSource.getMessage("label.eventi_inseriti", new Object[] { pf.getEventiPianoFormativo().size()}, Locale.getDefault()));
        document.add(par);
        PdfPTable table = getTableFieldsPiani();
        
        //Load data
        
        addTablePartecipanti(pf, table);
        document.add(table);
	}

	

	private PdfPTable getTableFieldsPiani() throws DocumentException {
		PdfPTable tableFields = new PdfPTable(3);
		tableFields.setWidthPercentage(100);
		tableFields.setWidths(new float[]{1,0.2f,0.2f});
		tableFields.setSpacingBefore(spacingBefore);
		tableFields.setSpacingAfter(spacingAfter);
		addCellIntestaSubTableByString(messageSource.getMessage("label.titolo", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.codice_identificativo", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.procedure_formative_tipologia", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
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

	private void addTablePartecipanti(PianoFormativo pf, PdfPTable table) {
		int anno = pf.getAnnoPianoFormativo();
		for(EventoPianoFormativo evt : pf.getEventiPianoFormativo()) {
			addCellSubTable(evt.getTitolo(), table);
			addCellSubTable(evt.getCodiceIdentificativo(), table);
			addCellSubTable(evt.getInfoProcedureFormativa(anno), table);
		}
	}

	private void addCellSubTable(String valoreCampo, PdfPTable table) {
		addCellSubTable(valoreCampo, table, null, true, null, false);
	}

	/*
	private void addCellSubTableProfessioni(Set<String> professioni, PdfPTable table) {
		addCellSubTable(professioni, table, null, true, null, false);
	}
	*/

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

	/*
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
	*/

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
}