package it.tredi.ecm.service.bean;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EngineeringProperties {
	private String ipa;
	private String password;
	private String servizio;
	private  String endpointPagamenti;
	private String datiSpecificiRiscossione;
	private String tipoDovuti;
	private boolean useProxy;
	private String proxyHost;
	private String proxyPort;
	private String proxyUsername;
	private String proxyPassword;
}
