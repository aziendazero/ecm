package it.tredi.ecm.service.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EcmProperties {
	private int accountExpiresDay;
	private int multipartMaxFileSize;
	private int sedutaValidationMinutes;
	private String applicationBaseUrl;
	private String emailSegreteriaEcm;
	private boolean debugTestMode;
	private int giorniIntegrazioneMin;
	private int giorniIntegrazioneMax;
	private int numeroReferee;
	private String fileRootPath;
	private int giorniMinEventoProviderA;
	private int giorniMinEventoProviderB;
	private int numeroMassimoResponsabiliEvento;
}
