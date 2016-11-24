package it.tredi.ecm.web.bean;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorsAjaxWrapper {

	//mappa errori <identificatore html, msg errore>
	Map<String, String> mappaErrori = new HashMap<String, String>();

}


