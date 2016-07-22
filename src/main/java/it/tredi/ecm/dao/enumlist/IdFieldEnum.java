package it.tredi.ecm.dao.enumlist;

import lombok.Getter;

@Getter
public enum IdFieldEnum {
	PROVIDER__DENOMINAZIONE_LEGALE ("provider.denominazioneLegale",2,null, SubSetFieldEnum.PROVIDER),
	PROVIDER__PARTITA_IVA ("provider.partitaIva",3, null, SubSetFieldEnum.PROVIDER),
	LEGALE_RAPPRESENTANTE__COGNOME ("persona.anagrafica.cognome",22,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE),
	LEGALE_RAPPRESENTANTE__NOME ("persona.anagrafica.nome",23,Ruolo.LEGALE_RAPPRESENTANTE, SubSetFieldEnum.LEGALE_RAPPRESENTANTE),
	RESPONSABILE_SEGRETERIA__SOSTITUISCI ("persona.anagrafica",50,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA),
	RESPONSABILE_SEGRETERIA__COGNOME ("persona.anagrafica.cognome",50,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA),
	RESPONSABILE_SEGRETERIA__NOME ("persona.anagrafica.nome",51,Ruolo.RESPONSABILE_SEGRETERIA, SubSetFieldEnum.RESPONSABILE_SEGRETERIA),
	COMPONENTE_COMITATO_SCIENTIFICO__COGNOME ("persona.anagrafica.cognome",65,Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO),
	COMPONENTE_COMITATO_SCIENTIFICO__NOME ("persona.anagrafica.nome",66,Ruolo.COMPONENTE_COMITATO_SCIENTIFICO, SubSetFieldEnum.COMPONENTE_COMITATO_SCIENTIFICO),
	PIANO_FORMATIVO("pianoFormativo",-1,null, SubSetFieldEnum.PIANO_FORMATIVO);//gestiamo la possibilità di modificare o meno il piano formativo dentro l'accreditamento
	
	private int idEcm;
	private String key;
	private Ruolo ruolo;
	private SubSetFieldEnum subSetField;

	private IdFieldEnum(String key, int idEcm, Ruolo ruolo, SubSetFieldEnum subSetField){
		this.key = key;
		this.idEcm = idEcm;
		this.ruolo = ruolo;
		this.subSetField = subSetField;
	}

	public static int getIdEcm(String key){
		for(IdFieldEnum e : IdFieldEnum.values()){
			if(e.getKey().equals(key))
				return e.getIdEcm();
		}
		return 0;
	}
	
	public static int getIdEcm(String key, Ruolo ruolo){
		for(IdFieldEnum e : IdFieldEnum.values()){
			if(e.getRuolo() == ruolo && e.getKey().equals(key))
				return e.getIdEcm();
		}
		return 0;
	}
}
