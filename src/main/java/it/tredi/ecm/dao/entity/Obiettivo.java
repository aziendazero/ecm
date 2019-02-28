package it.tredi.ecm.dao.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.javers.core.metamodel.annotation.TypeName;

import it.tredi.ecm.dao.enumlist.CategoriaObiettivoNazionale;
import lombok.Getter;
import lombok.Setter;

@Entity
@TypeName("Obiettivo")
@Getter
@Setter
public class Obiettivo extends BaseEntityDefaultId{
	@Column(columnDefinition = "text")
	private String nome;
	private boolean nazionale;
	@Enumerated(EnumType.STRING)
	private CategoriaObiettivoNazionale categoria;

	// ERM015189
	private int versione;

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
			if( (versione == 3 && codiceCogeaps.equalsIgnoreCase("0")) || (versione == 1 && codiceCogeaps.equalsIgnoreCase("1")))
				return true;
		}
		return false;
	}

	public int getIntCogeaps() {
		return Integer.parseInt(codiceCogeaps);
	}
}
