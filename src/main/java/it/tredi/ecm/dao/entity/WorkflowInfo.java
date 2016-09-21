package it.tredi.ecm.dao.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
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
}
