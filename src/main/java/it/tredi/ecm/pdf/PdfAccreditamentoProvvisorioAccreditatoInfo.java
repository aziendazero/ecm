package it.tredi.ecm.pdf;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.dao.entity.ValutazioneCommissione;
import it.tredi.ecm.dao.enumlist.AccreditamentoTipoEnum;

public class PdfAccreditamentoProvvisorioAccreditatoInfo {
	private PdfProviderInfo providerInfo = null;
	private LocalDate accreditamentoDataValidazione = null;
	private LocalDate accreditamentoDataVisita = null;
	private PdfInfoIntegrazioneRigetto integrazioneInfo = null;
	private PdfInfoIntegrazioneRigetto rigettoInfo = null;
	private PdfInfoIntegrazioneRigetto accreditamentoInfo = null;
	//private List<String> listaMotivazioni = new ArrayList<String>();
	private LocalDate dataCommissioneAccreditamento = null;

	public PdfAccreditamentoProvvisorioAccreditatoInfo(Accreditamento accreditamento, Seduta sedutaAccreditamento, Seduta sedutaIntegrazione, Seduta sedutaPreavvisoRigetto) {
		this.providerInfo = new PdfProviderInfo(accreditamento.getProvider());
		this.accreditamentoDataValidazione = accreditamento.getDataInvio();
		if(sedutaAccreditamento != null)
			this.dataCommissioneAccreditamento = sedutaAccreditamento.getData();

		if(accreditamento.getTipoDomanda() == AccreditamentoTipoEnum.STANDARD) {
			if(accreditamento.getVerbaleValutazioneSulCampo() != null)
				this.accreditamentoDataVisita = accreditamento.getVerbaleValutazioneSulCampo().getGiorno();
		}

		if(sedutaAccreditamento != null) {
			accreditamentoInfo = new PdfInfoIntegrazioneRigetto();
			accreditamentoInfo.setDataSedutaCommissione(sedutaAccreditamento.getData());
			accreditamentoInfo.setVerbaleNumero(sedutaAccreditamento.getNumeroVerbale());
			//per ora viene messo sempre a true perche' il documento di cui si parla e' obbligatorio
			accreditamentoInfo.setSottoscrizioneAutocertificazione(true);
			//TODO
			//integrazioneInfo.setEseguitaDaProvider(eseguitaDaProvider);
			//integrazioneInfo.setDataProtocollo(dataProtocollo);
			//integrazioneInfo.setNumeroProtocollo(numeroProtocollo);
		}

		if(sedutaIntegrazione != null) {
			integrazioneInfo = new PdfInfoIntegrazioneRigetto();
			integrazioneInfo.setDataSedutaCommissione(sedutaIntegrazione.getData());
			integrazioneInfo.setVerbaleNumero(sedutaIntegrazione.getNumeroVerbale());
			integrazioneInfo.setEseguitaDaProvider(accreditamento.getIntegrazioneEseguitaDaProvider());
			//TODO
			//integrazioneInfo.setDataProtocollo(dataProtocollo);
			//integrazioneInfo.setNumeroProtocollo(numeroProtocollo);
		}

		if(sedutaPreavvisoRigetto != null) {
			rigettoInfo = new PdfInfoIntegrazioneRigetto();
			rigettoInfo.setDataSedutaCommissione(sedutaPreavvisoRigetto.getData());
			rigettoInfo.setVerbaleNumero(sedutaPreavvisoRigetto.getNumeroVerbale());
			//TODO
			//rigettoInfo.setEseguitaDaProvider(eseguitaDaProvider);
			//rigettoInfo.setDataProtocollo(dataProtocollo);
			//rigettoInfo.setNumeroProtocollo(numeroProtocollo);
		}

	}


	public PdfAccreditamentoProvvisorioAccreditatoInfo(String providerDenominazione,
		String providerIndirizzo,
		String providerCap,
		String providerComune,
		String providerProvincia,
		String providerNomeLegaleRappresentante,
		String providerCognomeLegaleRappresentante,
		String providerPec,
		String providerId,
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

		LocalDate dataCommissioneAccreditamento) {
		this.providerInfo = new PdfProviderInfo(providerDenominazione, providerIndirizzo, providerCap, providerComune, providerProvincia, providerNomeLegaleRappresentante, providerCognomeLegaleRappresentante, providerPec, providerId);
		this.accreditamentoDataValidazione = accreditamentoDataValidazione;

		this.integrazioneInfo = new PdfInfoIntegrazioneRigetto(numeroProtocolloIntegrazione, dataProtocolloIntegrazione, verbaleNumeroIntegrazione, dataSedutaCommissioneIntegrazione, eseguitaDaProviderIntegrazione);
		this.rigettoInfo = new PdfInfoIntegrazioneRigetto(numeroProtocolloRigetto, dataProtocolloRigetto, verbaleNumeroRigetto, dataSedutaCommissioneRigetto, eseguitaDaProviderRigetto);
		this.dataCommissioneAccreditamento = dataCommissioneAccreditamento;

		//this.listaMotivazioni = listaMotivazioni;
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

	/*
	public List<String> getListaMotivazioni() {
		return listaMotivazioni;
	}

	public void setListaMotivazioni(List<String> listaMotivazioni) {
		this.listaMotivazioni = listaMotivazioni;
	}
	*/

	public LocalDate getDataCommissioneAccreditamento() {
		return dataCommissioneAccreditamento;
	}

	public void setDataCommissioneAccreditamento(
			LocalDate dataCommissioneAccreditamento) {
		this.dataCommissioneAccreditamento = dataCommissioneAccreditamento;
	}


	public PdfInfoIntegrazioneRigetto getAccreditamentoInfo() {
		return accreditamentoInfo;
	}


	public void setAccreditamentoInfo(PdfInfoIntegrazioneRigetto accreditamentoInfo) {
		this.accreditamentoInfo = accreditamentoInfo;
	}


	public LocalDate getAccreditamentoDataVisita() {
		return accreditamentoDataVisita;
	}


	public void setAccreditamentoDataVisita(LocalDate accreditamentoDataVisita) {
		this.accreditamentoDataVisita = accreditamentoDataVisita;
	}

}
