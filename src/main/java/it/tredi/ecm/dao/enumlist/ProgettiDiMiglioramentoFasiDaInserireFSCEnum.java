package it.tredi.ecm.dao.enumlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;

@Getter
public enum ProgettiDiMiglioramentoFasiDaInserireFSCEnum {
	ABCDE(1,"ABCDE",Arrays.asList(FaseDiLavoroFSCEnum.ANALISI_DEL_PROBLEMA,FaseDiLavoroFSCEnum.INDIVIDUAZIONE_DELLE_SOLUZIONI,FaseDiLavoroFSCEnum.CONFRONTO_E_CONDIVISIONE,FaseDiLavoroFSCEnum.IMPLEMENTAZIONE_CAMBIAMENTO_E_MONITORAGGIO,FaseDiLavoroFSCEnum.VALUTAZIONE_IMPATTO_CAMBIAMENTO)),
	ABC(2,"ABC",Arrays.asList(FaseDiLavoroFSCEnum.ANALISI_DEL_PROBLEMA,FaseDiLavoroFSCEnum.INDIVIDUAZIONE_DELLE_SOLUZIONI,FaseDiLavoroFSCEnum.CONFRONTO_E_CONDIVISIONE)),
	DE(3,"DE",Arrays.asList(FaseDiLavoroFSCEnum.IMPLEMENTAZIONE_CAMBIAMENTO_E_MONITORAGGIO,FaseDiLavoroFSCEnum.VALUTAZIONE_IMPATTO_CAMBIAMENTO));
	
	private int id;
	private String nome;
	List<FaseDiLavoroFSCEnum> fasiAbilitate = new ArrayList<FaseDiLavoroFSCEnum>();

	private ProgettiDiMiglioramentoFasiDaInserireFSCEnum(int id, String nome, List<FaseDiLavoroFSCEnum> fasiAbilitate){
		this.id = id;
		this.nome = nome;
		this.fasiAbilitate = fasiAbilitate;
	}
	
	public static boolean faseAbilitata(ProgettiDiMiglioramentoFasiDaInserireFSCEnum selectProgettoDiMiglioramento, FaseDiLavoroFSCEnum fase){
		if(selectProgettoDiMiglioramento == null || fase == null)
			return false;
		return selectProgettoDiMiglioramento.getFasiAbilitate().contains(fase);
	}
	
}
