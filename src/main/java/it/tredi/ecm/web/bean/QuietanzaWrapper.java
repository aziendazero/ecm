package it.tredi.ecm.web.bean;

import it.tredi.ecm.dao.entity.File;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class QuietanzaWrapper {
	private File quietanzaPagamento;

	private boolean submitError = false;
}
