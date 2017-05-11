package it.tredi.ecm.pdf;

import java.time.LocalDate;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.enumlist.MotivazioneDecadenzaEnum;
import it.tredi.ecm.web.bean.ImpostazioniProviderWrapper;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PdfAccreditamentoProvvisorioDecretoDecadenzaInfo {
	private PdfProviderInfo providerInfo = null;
	private LocalDate accreditamentoDataValidazione = null;
	private Integer numeroDecretoDecadenza;
	private LocalDate dataDecretoDecadenza;
	private LocalDate dataComunicazioneProviderDecadenza;
	private MotivazioneDecadenzaEnum motivazioneDecadenza;

	public PdfAccreditamentoProvvisorioDecretoDecadenzaInfo(Accreditamento accreditamento) {
		this.providerInfo = new PdfProviderInfo(accreditamento.getProvider());
		this.accreditamentoDataValidazione = accreditamento.getDataInvio();
	}

	public PdfAccreditamentoProvvisorioDecretoDecadenzaInfo(Accreditamento accreditamento, ImpostazioniProviderWrapper wrapper) {
		this.providerInfo = new PdfProviderInfo(accreditamento.getProvider());
		this.accreditamentoDataValidazione = accreditamento.getDataInvio();
		this.numeroDecretoDecadenza = wrapper.getNumeroDecreto();
		this.dataDecretoDecadenza = wrapper.getDataDecreto();
		this.dataComunicazioneProviderDecadenza = wrapper.getDataComunicazioneDecadenza();
		this.motivazioneDecadenza = wrapper.getMotivazioneDecadenza();
	}
}
