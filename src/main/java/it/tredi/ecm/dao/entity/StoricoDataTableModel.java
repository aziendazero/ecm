package it.tredi.ecm.dao.entity;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;

@Data
public class StoricoDataTableModel {

	  /**
	   * The data to be displayed in the table. This is an array of data source objects, one for each
	   * row, which will be used by DataTables. Note that this parameter's name can be changed using the
	   * ajaxDT option's dataSrc property.
	   */
	  @JsonView(View.class)
	  private List<StoricoDataModel> data;

	  /**
	   * Optional: If an error occurs during the running of the server-side processing script, you can
	   * inform the user of this error by passing back the error message to be displayed using this
	   * parameter. Do not include if there is no error.
	   */
	  @JsonView(View.class)
	  private String error;

	  public interface View {
	}
	
}
