package it.tredi.ecm.web.bean;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScadenzaPagamentoProviderWrapper {
	
	@DateTimeFormat (pattern = "dd/MM/yyyy")
	private LocalDate dataScadenzaPagamento;
	
	private boolean submitScadenzePagamentoProviderError = false;
	
	private String returnLink;
}
