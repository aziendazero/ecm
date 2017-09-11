package it.tredi.ecm.pdf;

import java.net.URL;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;

public class FooterWithInfo extends PdfPageEventHelper {
	private int sizeFooter = 9;
	private Font.FontFamily fontFamily = Font.FontFamily.TIMES_ROMAN;
	private Font footerFont = new Font(fontFamily, sizeFooter, Font.NORMAL, BaseColor.DARK_GRAY);
	private String nomeLogo = "";

	public FooterWithInfo(){
		super();
	}

	public FooterWithInfo(String nomeLogo) throws Exception{
		super();
		if(nomeLogo == null || nomeLogo.isEmpty())
			throw new Exception("nomeLogo non specificato");

		setNomeLogo(nomeLogo);
	}

	@Override
	public void onStartPage(PdfWriter writer, Document document) {
		PdfContentByte cb = writer.getDirectContent();
		Image img = createLogo(getNomeLogo());
		img.setAbsolutePosition((document.getPageSize().getWidth() - img.getScaledWidth()) / 2, document.top());
		try {
			cb.addImage(img);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

    public void onEndPage(PdfWriter writer, Document document) {
    	PdfContentByte cb = writer.getDirectContent();
        int altezza = 10;
        int lineOffset = 6 * altezza;//6: numeroRighe-2
        float center = (document.right() / 2);
        float bottom = 25;
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("Area Sanità e Sociale", footerFont), center, bottom + lineOffset, 0);
        lineOffset -= altezza;
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("Direzione Risorse Strumentali SSR - CRAV", footerFont), center, bottom + lineOffset, 0);
        lineOffset -= altezza;
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("Unità Organizzativa Personale e Professioni SSR", footerFont), center, bottom + lineOffset, 0);
        lineOffset -= altezza;
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("Palazzo Molin – S. Polo, 2514 – 30125 Venezia Tel. 0412793488- 3550 – 3434 – Fax 041/2793503", footerFont), center, bottom + lineOffset, 0);
        lineOffset -= altezza;
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("E-mail provvisoria: controlligovernopersonaleSSR@regione.veneto.it", footerFont), center, bottom + lineOffset, 0);
        lineOffset -= altezza;
        ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, new Phrase("PEC: area.sanitasociale@pec.regione.veneto.it", footerFont), center, bottom + lineOffset, 0);
        lineOffset -= altezza;
        ColumnText.showTextAligned(cb, Element.ALIGN_LEFT, new Phrase("Cod. Fisc. 800007580279", footerFont), document.left() + 2, bottom + lineOffset, 0);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase("P.IVA 02392630279", footerFont), document.right() - 2, bottom + lineOffset, 0);
    }

    public static Image createLogo(String nomeLogo){
		//Creazione immagine
        Image img = null;
        //LogoRegioneVeneto.png
		//String pathImgFile = "C:\\__Progetti\\ECM\\Doc da produrre in pdf\\LogoRegioneVeneto.png";
		try {
			URL url = Thread.currentThread().getContextClassLoader().getResource(nomeLogo);
			img = Image.getInstance(url);
			//img = Image.getInstance(pathImgFile);
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

	public String getNomeLogo() {
		return nomeLogo;
	}

	public void setNomeLogo(String nomeLogo) {
		this.nomeLogo = nomeLogo;
	}
}
