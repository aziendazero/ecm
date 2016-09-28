package it.tredi.ecm.pdf;

import java.time.LocalDate;

public class PdfInfoIntegrazioneRigetto {
	private String numeroProtocollo = null;
	private LocalDate dataProtocollo = null;
	private String verbaleNumero = null;
	private LocalDate dataSedutaCommissione = null;
	private boolean eseguitaDaProvider = false;

	public PdfInfoIntegrazioneRigetto(String numeroProtocollo,
			LocalDate dataProtocollo,
		String verbaleNumero,
		LocalDate dataSedutaCommissione,
		boolean eseguitaDaProvider) {
		this.numeroProtocollo = numeroProtocollo;
		this.dataProtocollo = dataProtocollo;
		this.verbaleNumero = verbaleNumero;
		this.dataSedutaCommissione = dataSedutaCommissione;
		this.eseguitaDaProvider = eseguitaDaProvider;
	}

	public String getNumeroProtocollo() {
		return numeroProtocollo;
	}

	public void setNumeroProtocollo(String numeroProtocollo) {
		this.numeroProtocollo = numeroProtocollo;
	}

	public LocalDate getDataProtocollo() {
		return dataProtocollo;
	}

	public void setDataProtocollo(LocalDate dataProtocollo) {
		this.dataProtocollo = dataProtocollo;
	}

	public String getVerbaleNumero() {
		return verbaleNumero;
	}

	public void setVerbaleNumero(String verbaleNumero) {
		this.verbaleNumero = verbaleNumero;
	}

	public LocalDate getDataSedutaCommissione() {
		return dataSedutaCommissione;
	}

	public void setDataSedutaCommissione(LocalDate dataSedutaCommissione) {
		this.dataSedutaCommissione = dataSedutaCommissione;
	}

	public boolean isEseguitaDaProvider() {
		return eseguitaDaProvider;
	}

	public void setEseguitaDaProvider(boolean eseguitaDaProvider) {
		this.eseguitaDaProvider = eseguitaDaProvider;
	}

}
