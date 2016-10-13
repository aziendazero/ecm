package it.tredi.ecm.dao.entity;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import it.tredi.ecm.dao.enumlist.FileEnum;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="file")
@Getter
@Setter
public class File extends BaseEntity{
	@JsonView(JsonViewModel.Integrazione.class)
	private String nomeFile;

	@JsonView(JsonViewModel.Integrazione.class)
	@JsonIgnore
	private byte[] data;

	@JsonView(JsonViewModel.Integrazione.class)
	@JsonIgnore
	@Column(name = "creato")
	private LocalDate dataCreazione;

	@JsonView(JsonViewModel.Integrazione.class)
	@Enumerated(EnumType.STRING)
	private FileEnum tipo;

	public File(){
		this.tipo = null;
		this.nomeFile = "";
		this.dataCreazione = LocalDate.now();
	}

	public File(FileEnum tipo){
		this.tipo = tipo;
		this.nomeFile = "";
		this.dataCreazione = LocalDate.now();
	}

	public void setId(Long id){
		this.id = id;
	}

	@JsonIgnore	public boolean isCV(){
		return this.tipo.equals(FileEnum.FILE_CV);
	}
	@JsonIgnore	public boolean isDELEGA(){
		return this.tipo.equals(FileEnum.FILE_DELEGA);
	}
	@JsonIgnore	public boolean isATTONOMINA(){
		return this.tipo.equals(FileEnum.FILE_ATTO_NOMINA);
	}
	@JsonIgnore public boolean isESTRATTOBILANCIOCOMPLESSIVO(){
		return this.tipo.equals(FileEnum.FILE_ESTRATTO_BILANCIO_COMPLESSIVO);
	}
	@JsonIgnore	public boolean isESTRATTOBILANCIOFORMAZIONE(){
		return this.tipo.equals(FileEnum.FILE_ESTRATTO_BILANCIO_FORMAZIONE);
	}
	@JsonIgnore	public boolean isFUNZIONIGRAMMA(){
		return this.tipo.equals(FileEnum.FILE_FUNZIONIGRAMMA);
	}
	@JsonIgnore	public boolean isORGANIGRAMMA(){
		return this.tipo.equals(FileEnum.FILE_ORGANIGRAMMA);
	}
	@JsonIgnore	public boolean isATTOCOSTITUTIVO(){
		return this.tipo.equals(FileEnum.FILE_ATTO_COSTITUTIVO);
	}
	@JsonIgnore	public boolean isESPERIENZAFORMAZIONE(){
		return this.tipo.equals(FileEnum.FILE_ESPERIENZA_FORMAZIONE);
	}
	@JsonIgnore	public boolean isUTILIZZO(){
		return this.tipo.equals(FileEnum.FILE_UTILIZZO);
	}
	@JsonIgnore	public boolean isSISTEMAINFORMATICO(){
		return this.tipo.equals(FileEnum.FILE_SISTEMA_INFORMATICO);
	}
	@JsonIgnore	public boolean isPIANOQUALITA(){
		return this.tipo.equals(FileEnum.FILE_PIANO_QUALITA);
	}
	@JsonIgnore	public boolean isDICHIARAZIONELEGALE(){
		return this.tipo.equals(FileEnum.FILE_DICHIARAZIONE_LEGALE);
	}
	@JsonIgnore public boolean isDICHIARAZIONEESCLUSIONE() {
		return this.tipo.equals(FileEnum.FILE_DICHIARAZIONE_ESCLUSIONE);
	}
	@JsonIgnore public boolean isREPORTPARTECIPANTI() {
		return this.tipo.equals(FileEnum.FILE_REPORT_PARTECIPANTI);
	}

	//ENGINEERING TEST FILE
	@JsonIgnore	public boolean isFILEDAFIRMARE(){
		return this.tipo.equals(FileEnum.FILE_DA_FIRMARE);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		//int result = super.hashCode();
		int result = 1;
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		File other = (File) obj;
		if (tipo == other.tipo)
			return true;
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		File cloned = (File) super.clone();
		cloned.setId(null);
		return clone();
	}

}
