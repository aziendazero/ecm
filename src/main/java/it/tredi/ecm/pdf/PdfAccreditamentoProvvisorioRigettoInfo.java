package it.tredi.ecm.pdf;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PdfAccreditamentoProvvisorioRigettoInfo {
	private PdfProviderInfo providerInfo = null;
	private LocalDate accreditamentoDataValidazione = null;
	private PdfInfoIntegrazioneRigetto integrazioneInfo = null;
	private PdfInfoIntegrazioneRigetto rigettoInfo = null;
	//private List<String> listaMotivazioni = new ArrayList<String>();
	private String noteSedutaDomanda = null;
	
	public PdfAccreditamentoProvvisorioRigettoInfo(String providerDenominazione,
		String providerIndirizzo,
		String providerCap,
		String providerComune,
		String providerProvincia,
		String providerNomeLegaleRappresentante,
		String providerCognomeLegaleRappresentante,
		String providerPec,
		LocalDate accreditamentoDataValidazione,
		
		String numeroProtocolloIntegrazione,
		LocalDate dataProtocolloIntegrazione,
		String verbaleNumeroIntegrazione,
		LocalDate dataSedutaCommissioneIntegrazione,
		boolean eseguitaDaProviderIntegrazione,
		
		String numeroProtocolloRigetto,
		LocalDate dataProtocolloRigetto,
		String verbaleNumeroRigetto,
		LocalDate dataSedutaCommissioneRigetto,
		boolean eseguitaDaProviderRigetto,
		
		String noteSedutaDomanda) {
		this.providerInfo = new PdfProviderInfo(providerDenominazione, providerIndirizzo, providerCap, providerComune, providerProvincia, providerNomeLegaleRappresentante, providerCognomeLegaleRappresentante, providerPec);
		this.accreditamentoDataValidazione = accreditamentoDataValidazione;
		
		this.integrazioneInfo = new PdfInfoIntegrazioneRigetto(numeroProtocolloIntegrazione, dataProtocolloIntegrazione, verbaleNumeroIntegrazione, dataSedutaCommissioneIntegrazione, eseguitaDaProviderIntegrazione);
		this.rigettoInfo = new PdfInfoIntegrazioneRigetto(numeroProtocolloRigetto, dataProtocolloRigetto, verbaleNumeroRigetto, dataSedutaCommissioneRigetto, eseguitaDaProviderRigetto);
		
		//this.listaMotivazioni = listaMotivazioni;
		this.noteSedutaDomanda = noteSedutaDomanda;
	}

	public PdfProviderInfo getProviderInfo() {
		return providerInfo;
	}

	public void setProviderInfo(PdfProviderInfo providerInfo) {
		this.providerInfo = providerInfo;
	}

	public LocalDate getAccreditamentoDataValidazione() {
		return accreditamentoDataValidazione;
	}

	public void setAccreditamentoDataValidazione(
			LocalDate accreditamentoDataValidazione) {
		this.accreditamentoDataValidazione = accreditamentoDataValidazione;
	}

	public PdfInfoIntegrazioneRigetto getIntegrazioneInfo() {
		return integrazioneInfo;
	}

	public void setIntegrazioneInfo(PdfInfoIntegrazioneRigetto integrazioneInfo) {
		this.integrazioneInfo = integrazioneInfo;
	}

	public PdfInfoIntegrazioneRigetto getRigettoInfo() {
		return rigettoInfo;
	}

	public void setRigettoInfo(PdfInfoIntegrazioneRigetto rigettoInfo) {
		this.rigettoInfo = rigettoInfo;
	}

	public String getNoteSedutaDomanda() {
		return noteSedutaDomanda;
	}

	public void setNoteSedutaDomanda(String noteSedutaDomanda) {
		this.noteSedutaDomanda = noteSedutaDomanda;
	}

	/*
	public List<String> getListaMotivazioni() {
		return listaMotivazioni;
	}

	public void setListaMotivazioni(List<String> listaMotivazioni) {
		this.listaMotivazioni = listaMotivazioni;
	}
	*/
}
