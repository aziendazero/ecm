package it.tredi.ecm.pdf;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.FieldIntegrazioneAccreditamento;
import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.dao.entity.ValutazioneCommissione;
import it.tredi.ecm.utils.Utils;


public class PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo {
	private PdfProviderInfo providerInfo = null;
	private LocalDate accreditamentoDataValidazione = null;
	private LocalDate accreditamentoDataVisita = null;
	private LocalDate accreditamentoDataSeduta = null;
	private List<String> listaCriticita = new ArrayList<String>();
	private String noteSedutaDomanda = null;
	private Long giorniIntegrazionePreavvisoRigetto = null;

	public PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo(Accreditamento accreditamento, Seduta seduta, List<String> listaCriticita) {
		this.providerInfo = new PdfProviderInfo(accreditamento.getProvider());
		this.accreditamentoDataValidazione = accreditamento.getDataInvio();

		this.accreditamentoDataSeduta = seduta.getData();
		for(ValutazioneCommissione valutazione : seduta.getValutazioniCommissione()) {
			if(valutazione.getAccreditamento().getId() == accreditamento.getId()) {
				//this.noteSedutaDomanda = valutazione.getOggettoDiscussione() + " - " + valutazione.getValutazioneCommissione();
				this.noteSedutaDomanda = valutazione.getValutazioneCommissione();
			}
		}
		this.listaCriticita = listaCriticita;
	}

	public PdfAccreditamentoProvvisorioIntegrazionePreavvisoRigettoInfo(String providerDenominazione,
		String providerIndirizzo,
		String providerCap,
		String providerComune,
		String providerProvincia,
		String providerNomeLegaleRappresentante,
		String providerCognomeLegaleRappresentante,
		String providerPec,
		LocalDate accreditamentoDataValidazione,
		LocalDate accreditamentoDataSeduta,
		List<String> listaCriticita,
		String noteSedutaDomanda) {
		this.providerInfo = new PdfProviderInfo(providerDenominazione, providerIndirizzo, providerCap, providerComune, providerProvincia, providerNomeLegaleRappresentante, providerCognomeLegaleRappresentante, providerPec);
		this.accreditamentoDataValidazione = accreditamentoDataValidazione;
		this.accreditamentoDataSeduta = accreditamentoDataSeduta;
		this.listaCriticita = listaCriticita;
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

	public LocalDate getAccreditamentoDataSeduta() {
		return accreditamentoDataSeduta;
	}

	public void setAccreditamentoDataSeduta(LocalDate accreditamentoDataSeduta) {
		this.accreditamentoDataSeduta = accreditamentoDataSeduta;
	}

	public List<String> getListaCriticita() {
		return listaCriticita;
	}

	public void setListaCriticita(List<String> listaCriticita) {
		this.listaCriticita = listaCriticita;
	}

	public String getNoteSedutaDomanda() {
		return noteSedutaDomanda;
	}

	public void setNoteSedutaDomanda(String noteSedutaDomanda) {
		this.noteSedutaDomanda = noteSedutaDomanda;
	}

	public Long getGiorniIntegrazionePreavvisoRigetto() {
		return giorniIntegrazionePreavvisoRigetto;
	}

	public void setGiorniIntegrazionePreavvisoRigetto(Long giorniIntegrazionePreavvisoRigetto) {
		this.giorniIntegrazionePreavvisoRigetto = giorniIntegrazionePreavvisoRigetto;
	}

	public LocalDate getAccreditamentoDataVisita() {
		return accreditamentoDataVisita;
	}

	public void setAccreditamentoDataVisita(LocalDate accreditamentoDataVisita) {
		this.accreditamentoDataVisita = accreditamentoDataVisita;
	}
}
