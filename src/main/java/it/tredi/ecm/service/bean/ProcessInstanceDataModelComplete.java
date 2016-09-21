package it.tredi.ecm.service.bean;

import java.util.List;

import it.tredi.bonita.api.model.ActivityDataModel;
import it.tredi.bonita.api.model.ProcessInstanceDataModel;
import it.tredi.bonita.api.model.TaskInstanceDataModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProcessInstanceDataModelComplete  {
	private ProcessInstanceDataModel processInstanceDataModel;
	private List<ActivityDataModel> activitieDataModels;
	private List<TaskInstanceDataModel> taskInstanceDataModels;
}
