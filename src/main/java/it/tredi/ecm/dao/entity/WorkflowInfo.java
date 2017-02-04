package it.tredi.ecm.dao.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import it.tredi.ecm.dao.enumlist.StatoWorkflowEnum;
import it.tredi.ecm.dao.enumlist.TipoWorkflowEnum;
import lombok.Getter;
import lombok.Setter;

@Embeddable
@Getter
@Setter
public class WorkflowInfo {
	//data = "20160429" operatore = "regione" ora = "10:11:58" processdefinition_id = "6857662985868768081"
	//processdefinition_name = "Autorizzazione" processdefinition_version = "1.5_test_02" processinstance_id = "26001"
	@Column(name = "wf_data_avvio")
	private LocalDate dataAvvio;
	@Column(name = "wf_process_definition_id")
	private Long processDefinitionId;
	@Column(name = "wf_process_definition_name")
	private String processDefinitionName;
	@Column(name = "wf_process_definition_version")
	private String processDefinitionVersion;
	@Column(name = "wf_process_instance_id")
	private Long processInstanceId;
	@Column(name = "wf_tipo")
	@Enumerated(EnumType.STRING)
	private TipoWorkflowEnum tipo;
	@Column(name = "wf_stato")
	@Enumerated(EnumType.STRING)
	private StatoWorkflowEnum stato;

	@Column(name = "wf_integrazione_eseguita_da_provider")
	private Boolean integrazioneEseguitaDaProvider;
	@Column(name = "wf_giorni_integrazione")
	private Long giorniIntegrazione;

	//Da aggiungere se viene richiesta la creazione di file nei procedimenti di "Variazione Dati" e "Conclusione Procedimento"
//	@OneToOne
//	@JoinColumn(name = "wf_richiesta_variazione_dati_id")
//	private File richiestaVariazioneDati;

}
