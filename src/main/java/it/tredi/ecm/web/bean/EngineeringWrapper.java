package it.tredi.ecm.web.bean;

import java.util.HashSet;
import java.util.Set;

import it.tredi.ecm.dao.entity.Evento;
import it.tredi.ecm.dao.entity.File;
import it.tredi.ecm.dao.entity.Provider;
import it.tredi.ecm.dao.enumlist.FileEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EngineeringWrapper {

	private Provider provider;

	private File fileDaFirmare;
	private Set<Evento> listaEventiDaPagare = new HashSet<Evento>();

	public EngineeringWrapper() {
		setFileDaFirmare(new File(FileEnum.FILE_DA_FIRMARE));
	}

	public void setFileDaFirmare(File file){
		fileDaFirmare = file;
		if(provider != null)
			provider.addFile(fileDaFirmare);
	}
}


