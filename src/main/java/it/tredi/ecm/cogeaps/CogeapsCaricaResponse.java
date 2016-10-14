package it.tredi.ecm.cogeaps;

import lombok.Setter;
import lombok.Getter;

@Getter
@Setter
public class CogeapsCaricaResponse extends CogeapsResponse {
	
	private String nomeFile;
	private int errCode;
	private String errMsg;

}
