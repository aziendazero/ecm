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

}
