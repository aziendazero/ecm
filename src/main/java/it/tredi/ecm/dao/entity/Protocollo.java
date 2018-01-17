package it.tredi.ecm.dao.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import it.tredi.ecm.dao.enumlist.ActionAfterProtocollaEnum;
import it.tredi.ecm.service.enumlist.ProtocolloServiceVersioneEnum;
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
	private String oggetto;
	private ProtocolloServiceVersioneEnum protocolloServiceVersion;

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

	//FIXME una volta gestita meglio si può lasciare il setter di lombok
	// introdotto perchè il service di protocollazione della regione non accetta oggetti
	// con caratteri superiori a 200 caratteri
	public void setOggetto(String oggetto) {
		if(oggetto.length() > 199)
			this.oggetto = oggetto.substring(0, 199);
	}
}
