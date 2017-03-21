package it.tredi.ecm.audit;

public class AuditObjectValueInfo {
	private AuditObjectInfoTypeEnum auditChangeInfoTypeEnum;
	private String value;
	private String entity;
	private Long id;
	private String valueObjectFragment;

	public AuditObjectInfoTypeEnum getAuditChangeInfoTypeEnum() {
		return auditChangeInfoTypeEnum;
	}
	public void setAuditChangeInfoTypeEnum(AuditObjectInfoTypeEnum auditChangeInfoTypeEnum) {
		this.auditChangeInfoTypeEnum = auditChangeInfoTypeEnum;
	}
	public String getEntity() {
		return entity;
	}
	public void setEntity(String entity) {
		this.entity = entity;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getValueObjectFragment() {
		return valueObjectFragment;
	}
	public void setValueObjectFragment(String valueObjectFragment) {
		this.valueObjectFragment = valueObjectFragment;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}

}
