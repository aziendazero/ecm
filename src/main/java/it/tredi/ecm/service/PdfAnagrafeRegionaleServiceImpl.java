package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
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
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import it.tredi.ecm.dao.entity.AnagrafeRegionaleCrediti;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;

@Service
public class PdfAnagrafeRegionaleServiceImpl implements PdfAnagrafeRegionaleService {
	private static final Logger LOGGER = LoggerFactory.getLogger(PdfAnagrafeRegionaleServiceImpl.class);

	@Autowired private MessageSource messageSource;
	@Autowired private AnagrafeRegionaleCreditiService anagrafeRegionaleCreditiService;

	//formatters
	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private NumberStyleFormatter intFormatter = new NumberStyleFormatter("0");
	private NumberStyleFormatter longFormatter = new NumberStyleFormatter("0");
	private NumberStyleFormatter floatFormatter = new NumberStyleFormatter("0.0#");
	private NumberStyleFormatter valutaFormatter = new NumberStyleFormatter("0.00");

    //tipi font
	private int sizeTitolo = 15;
	private int sizeParTitolo = 13;
	private int sizeNomeCampo = 11;
	private int sizeValoreCampo = 11;
	private int sizeTitoloSubTable = 9;
	private int sizeEtichettaSubTable = 8;
	private int sizeNomeCampoSubTable = 9;
	private int sizeValoreCampoSubTable = 9;
	private Font.FontFamily fontFamily = Font.FontFamily.TIMES_ROMAN;
	private Font fontTitolo = new Font(fontFamily, sizeTitolo, Font.BOLD);
	private Font fontParTitolo = new Font(fontFamily, sizeParTitolo, Font.BOLD);
	private Font fontNomeCampo = new Font(fontFamily, sizeNomeCampo, Font.BOLD);
	private Font fontValoreCampo = new Font(fontFamily, sizeValoreCampo, Font.NORMAL);
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
	public ByteArrayOutputStream creaOutputStreamPdfAnagrafeRegionale(String codiceFiscale, Integer annoRiferimento) throws Exception {
		LOGGER.debug("Inizio procedura scrittura PDF dell'Anagrafe Regionale: " + codiceFiscale + " per l'anno" + annoRiferimento);

		//recupero tutte le registrazioni riguardanti il professionista selezionato
		Set<AnagrafeRegionaleCrediti> lista = anagrafeRegionaleCreditiService.getAllByCodiceFiscale(codiceFiscale, annoRiferimento);

		//recupero crediti totali maturati nell'anno di riferimento
		BigDecimal totaleCrediti = anagrafeRegionaleCreditiService.getSumCreditiByCodiceFiscale(codiceFiscale, annoRiferimento);

		ByteArrayOutputStream byteArrayOutputStreamPdf = new ByteArrayOutputStream();
        writePdfAnagrafeRegionaleByAnno(byteArrayOutputStreamPdf, lista, totaleCrediti, annoRiferimento, codiceFiscale);

        return byteArrayOutputStreamPdf;
	}

	private void writePdfAnagrafeRegionaleByAnno(OutputStream outputStream, Set<AnagrafeRegionaleCrediti> lista, BigDecimal totaleCrediti, Integer annoRiferimento, String codiceFiscale) throws Exception {
		Document document = new Document();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            //Info documento
            document.addAuthor("Ecm");
            document.addCreationDate();
            document.addCreator("Ecm");
           	document.addTitle("Anagrafe Regionale | Codice Fiscale: " + codiceFiscale + " - Anno: " + annoRiferimento);
            writePdf(document, lista, totaleCrediti, annoRiferimento, codiceFiscale);
        } catch (Exception e) {
        	LOGGER.error("Impossibile creare il PDF per l'Anagrafe Regionale, codice fiscale: " + codiceFiscale, e);
            throw e;
        } finally {
            if(document.isOpen())
            	document.close();
            try {
            	outputStream.close();
            } catch (IOException ex) {}
		}
	}

	private void writePdf(Document document, Set<AnagrafeRegionaleCrediti> lista, BigDecimal totaleCrediti, Integer annoRiferimento, String codiceFiscale) throws Exception {

		//TITOLO
		Object[] values = {codiceFiscale, intFormatter.print(annoRiferimento, Locale.getDefault())};
        Paragraph parTitolo = new Paragraph();
        parTitolo.setAlignment(Element.ALIGN_LEFT);
        parTitolo.setFont(fontTitolo);
        parTitolo.add(messageSource.getMessage("label.scheda_riepilogativa_crediti_codiceFiscale_annoRiferimento", values, Locale.getDefault()));
        document.add(parTitolo);

        document.add(Chunk.NEWLINE);
        document.add(Chunk.NEWLINE);

        Paragraph totalePar = new Paragraph();
        Object[] totCrediti = {intFormatter.print(totaleCrediti, Locale.getDefault())};
        totalePar.setAlignment(Element.ALIGN_LEFT);
        totalePar.setFont(fontParTitolo);
        totalePar.add(messageSource.getMessage("label.totale_crediti", totCrediti, Locale.getDefault()));
        document.add(totalePar);

        document.add(Chunk.NEWLINE);

        if(lista != null) {
	        Paragraph riepilogoPar = new Paragraph();
	        riepilogoPar.setAlignment(Element.ALIGN_LEFT);
	        riepilogoPar.setFont(fontParTitolo);
	        riepilogoPar.add(messageSource.getMessage("label.riepilogo", null, Locale.getDefault()));
	        document.add(riepilogoPar);
	        PdfPTable table = getTableAnagrafeRegionaleCrediti();
	        addTableAnagrafeRegionaleCrediti(lista, table);
	        document.add(table);
        }
	}

	private PdfPTable getTableAnagrafeRegionaleCrediti() throws DocumentException {
		PdfPTable tableFields = new PdfPTable(4);
		tableFields.setWidthPercentage(100);
		tableFields.setWidths(new float[]{0.5f, 2, 1, 0.5f});
		tableFields.setSpacingBefore(spacingBefore);
		tableFields.setSpacingAfter(spacingAfter);
		addCellIntestaSubTableByString(messageSource.getMessage("label.data", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.titolo_evento", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.crediti_acquisiti", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
		addCellIntestaSubTableByString(messageSource.getMessage("label.ruolo", null, Locale.getDefault()), tableFields, BaseColor.GRAY, true, null);
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

	private void addTableAnagrafeRegionaleCrediti(Set<AnagrafeRegionaleCrediti> lista, PdfPTable table) throws Exception {
		for(AnagrafeRegionaleCrediti arc : lista) {
			addCellSubTable(dateTimeFormatter.format(arc.getData()), table);
			addCellSubTable(arc.getEvento().getTitolo(), table);
			addCellSubTable(valutaFormatter.print(arc.getCrediti(), Locale.getDefault()), table);
			addCellSubTable(arc.getRuolo(), table);
		}
	}

	private void addCellSubTable(String valoreCampo, PdfPTable table) {
		addCellSubTable(valoreCampo, table, null, true, null, false);
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

}
