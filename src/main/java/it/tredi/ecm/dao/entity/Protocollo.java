package it.tredi.ecm.dao.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import it.tredi.ecm.dao.enumlist.ActionAfterProtocollaEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Protocollo extends BaseEntityDefaultId {

	private LocalDate data;
	private Integer numero;
	private String idProtoBatch;
	private String statoSpedizione;

	@Enumerated(EnumType.STRING)
	@Column(name="action_after_protocollo")
	private ActionAfterProtocollaEnum actionAfterProtocollo;

	@ManyToOne @JoinColumn(name = "file_id")
	private File file;

	@ManyToOne @JoinColumn(name = "accreditamento_id")
	private Accreditamento accreditamento;

	public void setFile(File file){
		if(file != null){
			this.file = file;
			file.setProtocollo(this);
		}else{
			this.file.setProtocollo(null);
			this.file = null;
		}
	}
}
