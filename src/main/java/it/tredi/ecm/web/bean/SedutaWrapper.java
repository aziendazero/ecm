package it.tredi.ecm.web.bean;

import java.util.Map;
import java.util.Set;

import it.tredi.ecm.dao.entity.Accreditamento;
import it.tredi.ecm.dao.entity.Seduta;
import it.tredi.ecm.dao.entity.ValutazioneCommissione;
import it.tredi.ecm.dao.enumlist.AccreditamentoStatoEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SedutaWrapper {
	private Seduta seduta;
	private boolean canEdit;
	private boolean canValidate;
	private boolean canConfirmEvaluation;
	private String motivazioneDaInserire;
	private Long idAccreditamentoDaInserire;
	private Set<Accreditamento> domandeSelezionabili;
	private Set<Seduta> seduteSelezionabili;
	private Seduta sedutaTarget;
	private ValutazioneCommissione valutazioneTarget;
	private Map<Long, Set<AccreditamentoStatoEnum>> mappaStatiValutazione;
	private boolean canBloccaSeduta;
}
