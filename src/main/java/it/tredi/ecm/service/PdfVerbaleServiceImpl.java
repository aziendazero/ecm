package it.tredi.ecm.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDate;
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
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import it.tredi.ecm.dao.entity.Account;
import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.DelegatoValutazioneSulCampo;
import it.tredi.ecm.dao.entity.FieldValutazioneAccreditamento;
import it.tredi.ecm.dao.entity.Persona;
import it.tredi.ecm.dao.entity.PersonaEvento;
import it.tredi.ecm.dao.entity.Sede;
import it.tredi.ecm.dao.entity.VerbaleValutazioneSulCampo;
import it.tredi.ecm.dao.enumlist.ContenutiEventoEnum;

@Service
public class PdfVerbaleServiceImpl implements PdfVerbaleService {
	private static Logger LOGGER = LoggerFactory.getLogger(PdfVerbaleServiceImpl.class);

	@Override
	public ByteArrayOutputStream creaOutputSteramPdfVerbale(Accreditamento accreditamento) throws Exception {
		ByteArrayOutputStream byteArrayOutputStreamPdf = new ByteArrayOutputStream();
	    writePdfVerbale(byteArrayOutputStreamPdf, accreditamento);
		return byteArrayOutputStreamPdf;
	}

	@Autowired
	private MessageSource messageSource;

	private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	private NumberStyleFormatter intFormatter = new NumberStyleFormatter("0");
	private NumberStyleFormatter longFormatter = new NumberStyleFormatter("0");
	private NumberStyleFormatter floatFormatter = new NumberStyleFormatter("0.0#");
	private NumberStyleFormatter valutaFormatter = new NumberStyleFormatter("0.00");

    //tipi font
	private int sizeTitolo = 14;
	private int sizeNomeCampo = 11;
	private int sizeValoreCampo = 11;
	private int sizeTitoloSubTable = 9;
	private int sizeEtichettaSubTable = 8;
	private int sizeNomeCampoSubTable = 9;
	private int sizeValoreCampoSubTable = 9;
	private Font.FontFamily fontFamily = Font.FontFamily.TIMES_ROMAN;
	private Font fontTitolo = new Font(fontFamily, sizeTitolo, Font.BOLD);
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

	private void writePdfVerbale(OutputStream outputStream, Accreditamento accreditamento) throws Exception {
		if(accreditamento.getVerbaleValutazioneSulCampo() == null)
			throw new Exception("Nessun Verbale trovato!");

        Document document = new Document();
        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();
            //Info documento
            document.addAuthor("Ecm");
            document.addCreationDate();
            document.addCreator("Ecm");
            document.addTitle("Valutazione formale della domanda di accreditamento standard del Provider: " + accreditamento.getProvider().getDenominazioneLegale());
            //TODO sostituire
        	writePdf(document, accreditamento);
        } catch (Exception ex) {
        	LOGGER.error("writePdfVerbale impossibile creare il pdf", ex);
            throw ex;
        } finally {
            if(document.isOpen())
            	document.close();
            try {
            	outputStream.close();
            } catch (IOException ex) {

            }
		}
	}

	private void writePdf(Document document, Accreditamento accreditamento) throws Exception {

        Object[] values = {accreditamento.getProvider().getCodiceIdentificativoUnivoco(), accreditamento.getProvider().getDenominazioneLegale()};
        VerbaleValutazioneSulCampo verbale = accreditamento.getVerbaleValutazioneSulCampo();

        //primo paragrafo INFO
        Paragraph parTitolo = new Paragraph();
        parTitolo.setAlignment(Element.ALIGN_LEFT);
        parTitolo.setFont(fontTitolo);
        parTitolo.add(messageSource.getMessage("label.dati_verbale", values, Locale.getDefault()));
        document.add(parTitolo);

		PdfPTable tableFields = getTableFields();
		addCellLabelCampoValoreData("label.data", verbale.getGiorno(), tableFields);
		addCellLabelCampoValoreString("label.sede", verbale.getSede().getAddressNameFull(), tableFields);
		addCellLabelCampoValoreString("label.componente_crec_team_leader", verbale.getTeamLeader().getFullNameBase(), tableFields);
		addCellLabelCampoValoreString("label.osservatore_regionale", verbale.getOsservatoreRegionale().getFullNameBase(), tableFields);
		int counter = 1;
		for(Account a : verbale.getComponentiSegreteria()) {
			addCellLabelCampoValoreStringWithParam("label.componente_segreteria_numero", new Object[]{(Object)counter}, a.getFullNameBase(), tableFields);
		}
		addCellLabelCampoValoreString("label.referente_informatico", verbale.getReferenteInformatico().getFullNameBase(), tableFields);
		if(verbale.getIsPresenteLegaleRappresentante())
			addCellLabelCampoValoreLegaleRappr("label.sottoscrivente", accreditamento.getProvider().getLegaleRappresentante(), tableFields);
		else
			addCellLabelCampoValoreDelegato("label.sottoscrivente", verbale.getDelegato(), tableFields);
		document.add(tableFields);

		//secondo paragrafo VALUTAZIONI
		Paragraph par2Titolo = new Paragraph();
		par2Titolo.setAlignment(Element.ALIGN_LEFT);
        par2Titolo.setFont(fontTitolo);
        par2Titolo.add(messageSource.getMessage("label.esito_valutazioni", values, Locale.getDefault()));
        document.add(par2Titolo);

        PdfPTable table2Fields = getTableFields();
        for(FieldValutazioneAccreditamento field : verbale.getDatiValutazioneSulCampo().getValutazioniSulCampo()) {
        	addCellLabelCampoValoreFieldValutazione(field.getIdField().getNameRef(), field.getEsito(), table2Fields);
        }

		document.add(table2Fields);


	}

	private void addCellLabelCampoValoreFieldValutazione(String nameRef, Boolean esito, PdfPTable table) {
		if(esito == true)
			addCellLabelCampoValoreString("label." + nameRef, messageSource.getMessage("label.esito_positivo", null, Locale.getDefault()), table);
		else
			addCellLabelCampoValoreString("label." + nameRef, messageSource.getMessage("label.esito_negativo", null, Locale.getDefault()), table);
	}

	private void addCellLabelCampoValoreDelegato(String labelCampo, DelegatoValutazioneSulCampo delegato, PdfPTable table) {
		addCellLabelCampoValoreString(labelCampo, delegato.getNome() + " " + delegato.getCognome(), table);
	}

	private void addCellLabelCampoValoreLegaleRappr(String labelCampo, Persona legaleRappresentante, PdfPTable table) {
		addCellLabelCampoValoreString(labelCampo, legaleRappresentante.getAnagrafica().getNome() + " " + legaleRappresentante.getAnagrafica().getCognome(), table);
	}

	private void addCellLabelCampoValoreStringWithParam(String labelCampo, Object[] counter, String valoreCampo, PdfPTable table) {
		addCellCampoValore(messageSource.getMessage(labelCampo, counter, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValoreData(String labelCampo, LocalDate valoreDateCampo, PdfPTable table) {
		String valoreCampo = null;
		if(valoreDateCampo != null)
			valoreCampo = valoreDateCampo.format(dateTimeFormatter);
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellLabelCampoValoreString(String labelCampo, String valoreCampo, PdfPTable table) {
		addCellCampoValore(messageSource.getMessage(labelCampo, null, Locale.getDefault()), valoreCampo, table);
	}

	private void addCellCampoValore(String nomeCampo, String valoreCampo, PdfPTable table) {
		PdfPCell cell = getCellLabel(nomeCampo);
		table.addCell(cell);
		if(valoreCampo == null || valoreCampo.isEmpty())
			cell = getCellValore(messageSource.getMessage("label.dato_non_inserito", null, Locale.getDefault()));
		else
			cell = getCellValore(valoreCampo);
		table.addCell(cell);
    }

	private PdfPCell getCellLabel(String nomeCampo) {
    	PdfPCell cell = new PdfPCell(new Phrase(nomeCampo, fontNomeCampo));
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
		cell.setPadding(cellPadding);
		return cell;
    }

	private PdfPCell getCellValore(String valoreCampo) {
    	PdfPCell cell = new PdfPCell(new Phrase(valoreCampo, fontValoreCampo));
		cell.setBorder(PdfPCell.NO_BORDER);
		cell.setHorizontalAlignment(Element.ALIGN_JUSTIFIED);
		cell.setPadding(cellPadding);
		return cell;
    }

	private PdfPTable getTableFields() throws DocumentException {
		PdfPTable tableFields = new PdfPTable(2);
		tableFields.setWidthPercentage(100);
		tableFields.setWidths(new float[]{1, 3});
		tableFields.setSpacingBefore(spacingBefore);
		tableFields.setSpacingAfter(spacingAfter);
		return tableFields;
	}

}
