package it.tredi.ecm.dao.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.aspectj.weaver.NewFieldTypeMunger;
import org.javers.core.metamodel.annotation.DiffIgnore;
import org.javers.core.metamodel.annotation.ShallowReference;
import org.javers.core.metamodel.annotation.TypeName;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import it.tredi.ecm.dao.enumlist.FileEnum;
import lombok.Getter;
import lombok.Setter;

@TypeName("File")
@ShallowReference
@Entity
@Table(name="file")
@Getter
@Setter
public class File extends BaseEntityDefaultId{
	@JsonView(JsonViewModel.Integrazione.class)
	private String nomeFile;

	@JsonView(JsonViewModel.Integrazione.class)
	@JsonIgnore
	@Column(name = "creato")
	private LocalDate dataCreazione;

	@JsonView(JsonViewModel.Integrazione.class)
	@JsonIgnore
	@OneToOne
	private Protocollo protocollo;

	@JsonView(JsonViewModel.Integrazione.class)
	@JsonIgnore
	private LocalDate dataDelibera;

	@JsonView(JsonViewModel.Integrazione.class)
	@JsonIgnore
	private String numeroDelibera;

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

	@JsonView(JsonViewModel.Integrazione.class)
	@JsonIgnore
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	@JoinColumn(name="file_id")
	@DiffIgnore
	private List<FileData> fileData;

	@JsonIgnore public void setData(byte[] dataArray){
		if(fileData == null || fileData.isEmpty()){
			fileData = new ArrayList<FileData>();
			fileData.add(new FileData());
		}
		fileData.get(0).setData(dataArray);
	}

	@JsonIgnore	public byte[] getData(){
		if(this.getFileData() != null && !fileData.isEmpty())
			return fileData.get(0).getData();
		return new byte[0];
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
	@JsonIgnore public boolean isEVENTIPIANOFORMATIVO() {
		return this.tipo.equals(FileEnum.FILE_EVENTI_PIANO_FORMATIVO);
	}
	@JsonIgnore public boolean isRELAZIONEATTIVITAFORMATIVA() {
		return this.tipo.equals(FileEnum.FILE_RELAZIONE_ATTIVITA_FORMATIVA);
	}
	@JsonIgnore public boolean isRICHIESTAACCREDITAMENTOSTANDARD() {
		return this.tipo.equals(FileEnum.FILE_RICHIESTA_ACCREDITAMENTO_STANDARD);
	}

	//ENGINEERING TEST FILE
	@JsonIgnore	public boolean isFILEDAFIRMARE(){
		return this.tipo.equals(FileEnum.FILE_DA_FIRMARE);
	}

	@JsonIgnore	public boolean isProtocollato(){
		if(protocollo != null && !protocollo.isNew()){
			if(protocollo.getNumero() != null && protocollo.getData() != null)
				return true;
		}
		return false;
	}

	@JsonIgnore	public boolean isDeliberato(){
		if(dataDelibera != null && numeroDelibera != null){
				return true;
		}
		return false;
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
	@JsonIgnore
	public Object clone() throws CloneNotSupportedException {
		File cloned = (File) super.clone();

		cloned.setId(null);
		cloned.setFileData(null);
		cloned.setDataCreazione(this.getDataCreazione());
		cloned.setData(this.getData());
		cloned.setProtocollo(null);
		cloned.setNumeroDelibera("");
		cloned.setDataDelibera(null);

		return cloned;
	}
}
