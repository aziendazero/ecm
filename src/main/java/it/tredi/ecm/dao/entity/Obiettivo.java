package it.tredi.ecm.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import it.tredi.ecm.dao.enumlist.CategoriaObiettivoNazionale;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Obiettivo extends BaseEntity{
	private String nome;
	private boolean nazionale;
	@Enumerated(EnumType.STRING)
	private CategoriaObiettivoNazionale categoria; 
	
	@Column(name ="codice_cogeaps")
	private String codiceCogeaps;
	
	public Obiettivo(){}
	public Obiettivo(String nome, boolean nazionale, CategoriaObiettivoNazionale categoria){
		this(nome, nazionale, categoria, null);
	}
	public Obiettivo(String nome, boolean nazionale, CategoriaObiettivoNazionale categoria, String codiceCogeaps){
		this.nome = nome;
		this.nazionale = nazionale;
		this.categoria = categoria;
		this.codiceCogeaps = codiceCogeaps;
	}
	
	//obiettivo regionale -> Non rientra in uno degli obiettivi regionali
	public boolean isNonRientraTraObiettiviRegionali(){
		if(!nazionale){
			return codiceCogeaps.equalsIgnoreCase("1");
		}
		return false;
	}
}
